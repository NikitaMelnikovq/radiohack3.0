import { useOutletContext } from "react-router-dom";
import type { DashboardOutletContext } from "../components/layout/AppShell";
import { GamificationPanel } from "../components/loyalty/GamificationPanel";
import { Card } from "../components/ui/Card";
import { ErrorState } from "../components/ui/ErrorState";
import { DashboardSkeleton } from "../components/ui/Skeleton";
import { SectionHeader } from "../components/ui/SectionHeader";

export function GamificationPage() {
  const { dashboard, isLoading, error, refetch } = useOutletContext<DashboardOutletContext>();

  if (isLoading) {
    return <DashboardSkeleton />;
  }

  if (error || !dashboard) {
    return <ErrorState message={error ?? "Путь выгоды недоступен."} onRetry={refetch} />;
  }

  return (
    <div className="space-y-6">
      <SectionHeader title="Путь выгоды" description="Игровой прогресс по накопленной выгоде и персональные челленджи." />
      <GamificationPanel gamification={dashboard.gamification} />
      <Card>
        <h2 className="text-xl font-bold">Как считается уровень</h2>
        <p className="mt-3 text-sm leading-6 text-muted">
          Уровень считается по накопленной выгоде без конвертации валют. Это игровой показатель, не кредитный скоринг.
        </p>
      </Card>
    </div>
  );
}
