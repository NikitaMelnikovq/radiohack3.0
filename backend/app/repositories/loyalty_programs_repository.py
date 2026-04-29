from app.models.loyalty_program import LoyaltyProgram
from app.repositories.csv_loader import CsvLoader, parse_int


class LoyaltyProgramsRepository:
    filename = "LoyaltyPrograms.csv"
    required_fields = ("loyalty_program_id", "loyalty_program_name", "cashback_currency")

    def __init__(self, loader: CsvLoader) -> None:
        self.loader = loader
        self._cache: list[LoyaltyProgram] | None = None

    def list_all(self) -> list[LoyaltyProgram]:
        if self._cache is None:
            self._cache = self._load_programs()
        return list(self._cache)

    def get_by_id(self, loyalty_program_id: int) -> LoyaltyProgram | None:
        return next(
            (
                program
                for program in self.list_all()
                if program.loyalty_program_id == loyalty_program_id
            ),
            None,
        )

    def _load_programs(self) -> list[LoyaltyProgram]:
        rows = self.loader.load_rows(self.filename, self.required_fields)
        programs: list[LoyaltyProgram] = []
        for row_number, row in enumerate(rows, start=2):
            programs.append(
                LoyaltyProgram(
                    loyalty_program_id=parse_int(
                        row["loyalty_program_id"],
                        "loyalty_program_id",
                        self.filename,
                        row_number,
                    ),
                    loyalty_program_name=row["loyalty_program_name"],
                    cashback_currency=row["cashback_currency"],
                )
            )
        return programs
