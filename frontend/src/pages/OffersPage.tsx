import { useOutletContext } from "react-router-dom";
import type { DashboardOutletContext } from "../components/layout/AppShell";
import { OffersStack } from "../components/loyalty/OffersStack";
import { ErrorState } from "../components/ui/ErrorState";
import { DashboardSkeleton } from "../components/ui/Skeleton";
import { SectionHeader } from "../components/ui/SectionHeader";
import { Badge } from "../components/ui/Badge";
import { getSegmentLabel } from "../lib/formatters";
import { segmentBadgeClass } from "../lib/segment";

export function OffersPage() {
  const { dashboard, isLoading, error, refetch } = useOutletContext<DashboardOutletContext>();

  if (isLoading) {
    return <DashboardSkeleton />;
  }

  if (error || !dashboard) {
    return <ErrorState message={error ?? "Офферы недоступны."} onRetry={refetch} />;
  }

  return (
    <div className="space-y-6">
      <SectionHeader
        title="Офферы партнёров"
        description="Подборка соответствует financial_segment пользователя. Порядок сохраняет backend-сортировку по cashback_percent."
        action={<Badge className={segmentBadgeClass(dashboard.offers.user_segment)}>{getSegmentLabel(dashboard.offers.user_segment)}</Badge>}
      />
      <OffersStack offers={dashboard.offers.offers} />
    </div>
  );
}
