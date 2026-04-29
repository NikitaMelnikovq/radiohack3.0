from app.models.account import Account
from app.repositories.csv_loader import CsvLoader, parse_float, parse_int


class AccountsRepository:
    filename = "Accounts.csv"
    required_fields = ("account_id", "user_id", "loyalty_program_id", "current_balance")

    def __init__(self, loader: CsvLoader) -> None:
        self.loader = loader
        self._cache: list[Account] | None = None

    def list_all(self) -> list[Account]:
        if self._cache is None:
            self._cache = self._load_accounts()
        return list(self._cache)

    def list_by_user_id(self, user_id: int) -> list[Account]:
        return [account for account in self.list_all() if account.user_id == user_id]

    def _load_accounts(self) -> list[Account]:
        rows = self.loader.load_rows(self.filename, self.required_fields)
        accounts: list[Account] = []
        for row_number, row in enumerate(rows, start=2):
            accounts.append(
                Account(
                    account_id=parse_int(row["account_id"], "account_id", self.filename, row_number),
                    user_id=parse_int(row["user_id"], "user_id", self.filename, row_number),
                    loyalty_program_id=parse_int(
                        row["loyalty_program_id"],
                        "loyalty_program_id",
                        self.filename,
                        row_number,
                    ),
                    current_balance=parse_float(
                        row["current_balance"],
                        "current_balance",
                        self.filename,
                        row_number,
                    ),
                )
            )
        return accounts
