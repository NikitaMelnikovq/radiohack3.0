from app.core.errors import NotFoundError
from app.repositories.offers_repository import OffersRepository
from app.repositories.users_repository import UsersRepository
from app.schemas.offer import OfferResponse, OffersResponse


class OffersService:
    def __init__(
        self,
        users_repository: UsersRepository,
        offers_repository: OffersRepository,
    ) -> None:
        self.users_repository = users_repository
        self.offers_repository = offers_repository

    def get_personal_offers(self, user_id: int) -> OffersResponse:
        user = self.users_repository.get_by_id(user_id)
        if user is None:
            raise NotFoundError("User not found")

        offers = sorted(
            self.offers_repository.list_by_segment(user.financial_segment),
            key=lambda offer: (-offer.cashback_percent, offer.partner_name),
        )

        return OffersResponse(
            user_segment=user.financial_segment,
            offers=[
                OfferResponse(
                    partner_id=offer.partner_id,
                    partner_name=offer.partner_name,
                    short_description=offer.short_description,
                    logo_url=offer.logo_url,
                    brand_color_hex=offer.brand_color_hex,
                    cashback_percent=offer.cashback_percent,
                    financial_segment=offer.financial_segment,
                    reason=f"Предложение подобрано под финансовый сегмент {user.financial_segment}",
                )
                for offer in offers
            ],
        )
