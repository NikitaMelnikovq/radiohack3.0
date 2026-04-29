from app.models.loyalty_history import LoyaltyHistory
from app.repositories.csv_loader import CsvLoader, parse_date, parse_float, parse_int


class LoyaltyHistoryRepository:
    filename = "LoyaltyHistory.csv"
    required_fields = ("transaction_id", "account_id", "cashback_amount", "payout_date")

    def __init__(self, loader: CsvLoader) -> None:
        self.loader = loader
        self._cache: list[LoyaltyHistory] | None = None

    def list_all(self) -> list[LoyaltyHistory]:
        if self._cache is None:
            self._cache = self._load_history()
        return list(self._cache)

    def list_by_account_ids(self, account_ids: set[int] | list[int] | tuple[int, ...]) -> list[LoyaltyHistory]:
        account_ids_set = set(account_ids)
        return [
            history
            for history in self.list_all()
            if history.account_id in account_ids_set
        ]

    def _load_history(self) -> list[LoyaltyHistory]:
        rows = self.loader.load_rows(self.filename, self.required_fields)
        history_items: list[LoyaltyHistory] = []
        for row_number, row in enumerate(rows, start=2):
            history_items.append(
                LoyaltyHistory(
                    transaction_id=parse_int(
                        row["transaction_id"],
                        "transaction_id",
                        self.filename,
                        row_number,
                    ),
                    account_id=parse_int(row["account_id"], "account_id", self.filename, row_number),
                    cashback_amount=parse_float(
                        row["cashback_amount"],
                        "cashback_amount",
                        self.filename,
                        row_number,
                    ),
                    payout_date=parse_date(row["payout_date"], "payout_date", self.filename, row_number),
                )
            )
        return history_items
