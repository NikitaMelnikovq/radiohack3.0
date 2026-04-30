import { useOutletContext } from "react-router-dom";
import type { DashboardOutletContext } from "../components/layout/AppShell";
import { MonthlyDynamicsChart } from "../components/charts/MonthlyDynamicsChart";
import { ProgramBreakdownChart } from "../components/charts/ProgramBreakdownChart";
import { ErrorState } from "../components/ui/ErrorState";
import { DashboardSkeleton } from "../components/ui/Skeleton";
import { MetricCard } from "../components/ui/MetricCard";
import { SectionHeader } from "../components/ui/SectionHeader";
import { formatCurrencyAmount } from "../lib/formatters";
import { getCurrencyLabel } from "../lib/currency";

export function AnalyticsPage() {
  const { dashboard, isLoading, error, refetch } = useOutletContext<DashboardOutletContext>();

  if (isLoading) {
    return <DashboardSkeleton />;
  }

  if (error || !dashboard) {
    return <ErrorState message={error ?? "Аналитика недоступна."} onRetry={refetch} />;
  }

  return (
    <div className="space-y-6">
      <SectionHeader title="Аналитика выгоды" description="Динамика выплат, вклад программ и средний кэшбэк по валютам." />
      <div className="grid gap-4 xl:grid-cols-2">
        <MonthlyDynamicsChart items={dashboard.analytics.monthly_dynamics} />
        <ProgramBreakdownChart items={dashboard.analytics.program_breakdown} />
      </div>
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        {dashboard.analytics.best_program ? (
          <MetricCard
            label="Лучшая программа"
            value={dashboard.analytics.best_program.loyalty_program}
            description={formatCurrencyAmount(dashboard.analytics.best_program.currency, dashboard.analytics.best_program.amount)}
          />
        ) : (
          <MetricCard label="Лучшая программа" value="—" description="Недостаточно истории выплат" />
        )}
        {dashboard.analytics.average_monthly_cashback.map((item) => (
          <MetricCard
            key={item.currency}
            label={`Среднее в месяц: ${getCurrencyLabel(item.currency)}`}
            value={formatCurrencyAmount(item.currency, item.amount)}
          />
        ))}
      </div>
    </div>
  );
}
