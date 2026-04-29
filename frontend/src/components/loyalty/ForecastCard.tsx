import type { LoyaltyForecast } from "../../api/types";
import { formatCurrencyAmount, getConfidenceLabel } from "../../lib/formatters";
import { confidenceBadgeClass } from "../../lib/segment";
import { Badge } from "../ui/Badge";
import { Card } from "../ui/Card";
import { EmptyState } from "../ui/EmptyState";

export function ForecastCard({ forecast }: { forecast: LoyaltyForecast }) {
  if (!forecast.items.length) {
    return <EmptyState title="Прогноз появится позже" description="Нужна история выплат, чтобы рассчитать ожидаемую выгоду." />;
  }

  return (
    <Card>
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm text-muted">Прогноз на {forecast.forecast_period_days} дней</p>
          <h3 className="mt-2 text-xl font-bold">Ожидаемая выгода</h3>
        </div>
        <Badge>{forecast.method}</Badge>
      </div>
      <div className="mt-5 grid gap-3">
        {forecast.items.map((item) => (
          <div key={item.currency} className="rounded-2xl bg-white/[0.06] p-4 light:bg-black/5">
            <div className="flex items-center justify-between gap-3">
              <div className="text-xl font-black">{formatCurrencyAmount(item.currency, item.predicted_amount)}</div>
              <Badge className={confidenceBadgeClass(item.confidence)}>{getConfidenceLabel(item.confidence)}</Badge>
            </div>
            <p className="mt-1 text-sm text-muted">{item.currency}</p>
          </div>
        ))}
      </div>
      <p className="mt-4 text-sm leading-6 text-muted">{forecast.explanation}</p>
    </Card>
  );
}
