import { useState } from "react";
import type { Offer } from "../../api/types";
import { formatPercent, localizeTechnicalText } from "../../lib/formatters";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";
import { EmptyState } from "../ui/EmptyState";

interface OffersStackProps {
  offers: Offer[];
  limit?: number;
}

export function OffersStack({ offers, limit }: OffersStackProps) {
  const visibleOffers = typeof limit === "number" ? offers.slice(0, limit) : offers;

  if (!visibleOffers.length) {
    return <EmptyState title="Офферов пока нет" description="Для этого сегмента нет подходящих предложений партнёров." />;
  }

  return (
    <div className="grid gap-3">
      {visibleOffers.map((offer) => (
        <Card key={offer.partner_id} className="relative overflow-hidden">
          <div className="absolute inset-x-0 top-0 h-1" style={{ backgroundColor: offer.brand_color_hex }} />
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div className="flex min-w-0 gap-4">
              <OfferLogo offer={offer} />
              <div className="min-w-0">
                <h3 className="text-lg font-bold">{offer.partner_name}</h3>
                <p className="mt-1 text-sm leading-6 text-muted">{offer.short_description}</p>
                <p className="mt-2 text-xs leading-5 text-muted">{localizeTechnicalText(offer.reason)}</p>
              </div>
            </div>
            <div className="flex shrink-0 items-center justify-between gap-4 sm:flex-col sm:items-end">
              <div className="text-right">
                <div className="text-3xl font-black text-t-yellow">{formatPercent(offer.cashback_percent)}</div>
                <p className="text-xs text-muted">кэшбэк</p>
              </div>
              <Button type="button">Активировать</Button>
            </div>
          </div>
        </Card>
      ))}
    </div>
  );
}

function OfferLogo({ offer }: { offer: Offer }) {
  const [hasError, setHasError] = useState(false);
  const firstLetter = offer.partner_name.trim().charAt(0).toUpperCase();

  return (
    <div className="grid h-14 w-14 shrink-0 place-items-center overflow-hidden rounded-2xl bg-white/10 text-xl font-black text-t-yellow light:bg-black/5">
      {!hasError && offer.logo_url ? (
        <img
          src={offer.logo_url}
          alt=""
          className="h-full w-full object-cover"
          onError={() => setHasError(true)}
          loading="lazy"
        />
      ) : (
        firstLetter
      )}
    </div>
  );
}
