from app.models.offer import Offer
from app.repositories.csv_loader import CsvLoader, parse_financial_segment, parse_float, parse_int


class OffersRepository:
    filename = "Offers.csv"
    required_fields = (
        "partner_id",
        "partner_name",
        "short_description",
        "logo_url",
        "brand_color_hex",
        "cashback_percent",
        "financial_segment",
    )

    def __init__(self, loader: CsvLoader) -> None:
        self.loader = loader
        self._cache: list[Offer] | None = None

    def list_all(self) -> list[Offer]:
        if self._cache is None:
            self._cache = self._load_offers()
        return list(self._cache)

    def list_by_segment(self, financial_segment: str) -> list[Offer]:
        return [
            offer
            for offer in self.list_all()
            if offer.financial_segment == financial_segment
        ]

    def _load_offers(self) -> list[Offer]:
        rows = self.loader.load_rows(self.filename, self.required_fields)
        offers: list[Offer] = []
        for row_number, row in enumerate(rows, start=2):
            offers.append(
                Offer(
                    partner_id=parse_int(row["partner_id"], "partner_id", self.filename, row_number),
                    partner_name=row["partner_name"],
                    short_description=row["short_description"],
                    logo_url=row["logo_url"],
                    brand_color_hex=row["brand_color_hex"],
                    cashback_percent=parse_float(
                        row["cashback_percent"],
                        "cashback_percent",
                        self.filename,
                        row_number,
                    ),
                    financial_segment=parse_financial_segment(
                        row["financial_segment"],
                        self.filename,
                        row_number,
                    ),
                )
            )
        return offers
