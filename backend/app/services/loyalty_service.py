from collections import defaultdict
from datetime import date

from app.core.errors import CsvParsingError, NotFoundError
from app.models.analytics import CashbackRecord
from app.models.loyalty_history import LoyaltyHistory
from app.models.loyalty_program import LoyaltyProgram
from app.models.user import User
from app.repositories.accounts_repository import AccountsRepository
from app.repositories.loyalty_history_repository import LoyaltyHistoryRepository
from app.repositories.loyalty_programs_repository import LoyaltyProgramsRepository
from app.repositories.users_repository import UsersRepository
from app.schemas.loyalty import (
    CurrencyAmount,
    LoyaltyAccountSummary,
    LoyaltySummary,
    LoyaltySummaryUser,
)

CURRENCY_ORDER = {"rub": 0, "miles": 1, "bravo-points": 2}


def currency_sort_key(currency: str) -> tuple[int, str]:
    return CURRENCY_ORDER.get(currency, 100), currency


def round_amount(value: float) -> float:
    return round(value, 2)


class LoyaltyService:
    def __init__(
        self,
        users_repository: UsersRepository,
        accounts_repository: AccountsRepository,
        programs_repository: LoyaltyProgramsRepository,
        history_repository: LoyaltyHistoryRepository,
    ) -> None:
        self.users_repository = users_repository
        self.accounts_repository = accounts_repository
        self.programs_repository = programs_repository
        self.history_repository = history_repository

    def get_summary(self, user_id: int) -> LoyaltySummary:
        user = self._get_user_or_raise(user_id)
        accounts = self.accounts_repository.list_by_user_id(user_id)
        histories = self.history_repository.list_by_account_ids([account.account_id for account in accounts])
        histories_by_account: dict[int, list[LoyaltyHistory]] = defaultdict(list)
        for history in histories:
            histories_by_account[history.account_id].append(history)

        totals_by_currency: dict[str, float] = defaultdict(float)
        account_summaries: list[LoyaltyAccountSummary] = []
        last_payout_date: date | None = None

        for account in accounts:
            program = self._get_program_or_raise(account.loyalty_program_id)
            account_histories = histories_by_account.get(account.account_id, [])
            total_cashback = sum(history.cashback_amount for history in account_histories)

            if account_histories:
                totals_by_currency[program.cashback_currency] += total_cashback
                account_last_payout_date = max(history.payout_date for history in account_histories)
                if last_payout_date is None or account_last_payout_date > last_payout_date:
                    last_payout_date = account_last_payout_date

            account_summaries.append(
                LoyaltyAccountSummary(
                    account_id=account.account_id,
                    loyalty_program=program.loyalty_program_name,
                    cashback_currency=program.cashback_currency,
                    current_balance=round_amount(account.current_balance),
                    total_cashback=round_amount(total_cashback),
                    transactions_count=len(account_histories),
                )
            )

        return LoyaltySummary(
            user=LoyaltySummaryUser(
                id=user.id,
                full_name=user.full_name,
                financial_segment=user.financial_segment,
            ),
            accounts=account_summaries,
            totals_by_currency=self._build_totals_by_currency(totals_by_currency),
            total_transactions=len(histories),
            last_payout_date=last_payout_date,
        )

    def get_cashback_records(self, user_id: int) -> list[CashbackRecord]:
        self._get_user_or_raise(user_id)
        accounts = self.accounts_repository.list_by_user_id(user_id)
        if not accounts:
            return []

        programs_by_account_id: dict[int, LoyaltyProgram] = {}
        for account in accounts:
            programs_by_account_id[account.account_id] = self._get_program_or_raise(
                account.loyalty_program_id
            )

        histories = self.history_repository.list_by_account_ids([account.account_id for account in accounts])
        records: list[CashbackRecord] = []
        for history in histories:
            program = programs_by_account_id[history.account_id]
            records.append(
                CashbackRecord(
                    account_id=history.account_id,
                    loyalty_program=program.loyalty_program_name,
                    currency=program.cashback_currency,
                    amount=history.cashback_amount,
                    payout_date=history.payout_date,
                )
            )
        return records

    def _build_totals_by_currency(self, totals_by_currency: dict[str, float]) -> list[CurrencyAmount]:
        return [
            CurrencyAmount(currency=currency, amount=round_amount(amount))
            for currency, amount in sorted(
                totals_by_currency.items(),
                key=lambda item: currency_sort_key(item[0]),
            )
        ]

    def _get_user_or_raise(self, user_id: int) -> User:
        user = self.users_repository.get_by_id(user_id)
        if user is None:
            raise NotFoundError("User not found")
        return user

    def _get_program_or_raise(self, loyalty_program_id: int) -> LoyaltyProgram:
        program = self.programs_repository.get_by_id(loyalty_program_id)
        if program is None:
            raise CsvParsingError(f"loyalty program id {loyalty_program_id} was referenced but not found")
        return program
