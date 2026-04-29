import csv
from datetime import date
from pathlib import Path
from typing import Iterable

from app.core.errors import CsvParsingError


class CsvLoader:
    def __init__(self, data_dir: Path) -> None:
        self.data_dir = data_dir

    def load_rows(self, filename: str, required_fields: Iterable[str]) -> list[dict[str, str]]:
        path = self.data_dir / filename
        if not path.exists():
            raise CsvParsingError(f"file '{filename}' was not found in {self.data_dir}")

        try:
            with path.open("r", encoding="utf-8-sig", newline="") as file:
                reader = csv.DictReader(file)
                if reader.fieldnames is None:
                    return []

                original_fieldnames = [field for field in reader.fieldnames if field is not None]
                fieldnames = [field.strip() for field in original_fieldnames]
                field_mapping = dict(zip(original_fieldnames, fieldnames, strict=True))
                missing_fields = set(required_fields) - set(fieldnames)
                if missing_fields:
                    missing = ", ".join(sorted(missing_fields))
                    raise CsvParsingError(f"file '{filename}' is missing required columns: {missing}")

                rows: list[dict[str, str]] = []
                for row in reader:
                    values = [
                        value
                        for key, value in row.items()
                        if key is not None and isinstance(value, str)
                    ]
                    if not any((value or "").strip() for value in values):
                        continue
                    rows.append(
                        {
                            normalized_field: (row.get(original_field) or "").strip()
                            for original_field, normalized_field in field_mapping.items()
                        }
                    )
                return rows
        except CsvParsingError:
            raise
        except (OSError, UnicodeDecodeError, csv.Error) as exc:
            raise CsvParsingError(f"file '{filename}' could not be read: {exc}") from exc


def parse_int(value: str, field: str, filename: str, row_number: int) -> int:
    try:
        return int(value)
    except (TypeError, ValueError) as exc:
        raise CsvParsingError(
            f"file '{filename}', row {row_number}, column '{field}' must be an integer"
        ) from exc


def parse_float(value: str, field: str, filename: str, row_number: int) -> float:
    try:
        return float(value)
    except (TypeError, ValueError) as exc:
        raise CsvParsingError(
            f"file '{filename}', row {row_number}, column '{field}' must be a number"
        ) from exc


def parse_date(value: str, field: str, filename: str, row_number: int) -> date:
    try:
        return date.fromisoformat(value)
    except (TypeError, ValueError) as exc:
        raise CsvParsingError(
            f"file '{filename}', row {row_number}, column '{field}' must use YYYY-MM-DD format"
        ) from exc


def parse_financial_segment(value: str, filename: str, row_number: int) -> str:
    normalized_value = value.strip().upper()
    segment_aliases = {
        "LOW": "LOW",
        "MEDIUM": "MEDIUM",
        "MIDDLE": "MEDIUM",
        "HIGH": "HIGH",
    }
    try:
        return segment_aliases[normalized_value]
    except KeyError as exc:
        raise CsvParsingError(
            f"file '{filename}', row {row_number}, column 'financial_segment' must be LOW, MEDIUM, MIDDLE or HIGH"
        ) from exc
