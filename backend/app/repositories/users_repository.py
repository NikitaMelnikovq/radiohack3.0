from app.models.user import User
from app.repositories.csv_loader import CsvLoader, parse_financial_segment, parse_int


class UsersRepository:
    filename = "Users.csv"
    required_fields = ("id", "email", "phone_number", "full_name", "financial_segment")

    def __init__(self, loader: CsvLoader) -> None:
        self.loader = loader
        self._cache: list[User] | None = None

    def list_all(self) -> list[User]:
        if self._cache is None:
            self._cache = self._load_users()
        return list(self._cache)

    def get_by_id(self, user_id: int) -> User | None:
        return next((user for user in self.list_all() if user.id == user_id), None)

    def _load_users(self) -> list[User]:
        rows = self.loader.load_rows(self.filename, self.required_fields)
        users: list[User] = []
        for row_number, row in enumerate(rows, start=2):
            users.append(
                User(
                    id=parse_int(row["id"], "id", self.filename, row_number),
                    email=row["email"],
                    phone_number=row["phone_number"],
                    full_name=row["full_name"],
                    financial_segment=parse_financial_segment(
                        row["financial_segment"],
                        self.filename,
                        row_number,
                    ),
                )
            )
        return users
