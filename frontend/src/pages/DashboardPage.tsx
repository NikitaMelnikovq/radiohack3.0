import { ArrowRight } from "lucide-react";
import { useOutletContext } from "react-router-dom";
import type { DashboardOutletContext } from "../components/layout/AppShell";
import { DashboardSkeleton } from "../components/ui/Skeleton";
import { ErrorState } from "../components/ui/ErrorState";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { SectionHeader } from "../components/ui/SectionHeader";
import { UserProfileCard } from "../components/loyalty/UserProfileCard";
import { CurrencyTotalsCard } from "../components/loyalty/CurrencyTotalsCard";
import { DashboardScoreCard } from "../components/loyalty/DashboardScoreCard";
import { ForecastCard } from "../components/loyalty/ForecastCard";
import { NextBestActionCard } from "../components/loyalty/NextBestActionCard";
import { OffersStack } from "../components/loyalty/OffersStack";
import { CrossSellCards } from "../components/loyalty/CrossSellCards";
import { AiInsightsPanel } from "../components/loyalty/AiInsightsPanel";
import { GamificationPanel } from "../components/loyalty/GamificationPanel";

export function DashboardPage() {
  const { dashboard, isLoading, error, refetch } = useOutletContext<DashboardOutletContext>();

  if (isLoading) {
    return <DashboardSkeleton />;
  }

  if (error || !dashboard) {
    return <ErrorState message={error ?? "Dashboard не найден."} onRetry={refetch} />;
  }

  return (
    <div className="space-y-8">
      <Card className="relative overflow-hidden bg-[linear-gradient(135deg,#0B0B0C_0%,#151518_48%,#2C270E_100%)] p-6 text-white sm:p-8">
        <div className="absolute right-0 top-0 h-60 w-60 rounded-full bg-t-yellow/15 blur-3xl" />
        <div className="relative grid gap-6 lg:grid-cols-[1fr_0.85fr] lg:items-end">
          <div>
            <p className="text-sm font-semibold text-t-yellow">Моя выгода от Т-Банка</p>
            <h1 className="mt-3 text-4xl font-black tracking-normal sm:text-5xl">Т-Банк уже вернул вам</h1>
            <p className="mt-4 max-w-xl text-base leading-7 text-white/70">
              Собрали кэшбэк, мили, Браво, офферы и прогноз в одном разделе.
            </p>
          </div>
          <div className="grid gap-3">
            <CurrencyTotalsCard totals={dashboard.loyalty_summary.totals_by_currency} />
          </div>
        </div>
      </Card>

      <div className="grid gap-4 lg:grid-cols-12">
        <div className="lg:col-span-4">
          <UserProfileCard dashboard={dashboard} />
        </div>
        <div className="lg:col-span-5">
          <DashboardScoreCard score={dashboard.dashboard_score} />
        </div>
        <div className="lg:col-span-3">
          <NextBestActionCard action={dashboard.dashboard_score.next_best_action} />
        </div>
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <ForecastCard forecast={dashboard.forecast} />
        <GamificationPanel gamification={dashboard.gamification} compact />
      </div>

      <section>
        <SectionHeader
          title="Персональные офферы"
          description="Лучшие предложения от партнёров"
          action={<Button variant="secondary" icon={<ArrowRight className="h-4 w-4" aria-hidden />}>Все офферы</Button>}
        />
        <OffersStack offers={dashboard.offers.offers} limit={3} />
      </section>

      <section>
        <SectionHeader title="Рекомендуемые продукты экосистемы" description="Что ещё может приносить выгоду" />
        <CrossSellCards recommendations={dashboard.cross_sell.recommendations} limit={3} />
      </section>

      <section>
        <SectionHeader title="AI-ассистент выгоды" description="Короткие инсайты по вашей истории лояльности" />
        <AiInsightsPanel insights={dashboard.ai_insights.insights} limit={3} />
      </section>
    </div>
  );
}
