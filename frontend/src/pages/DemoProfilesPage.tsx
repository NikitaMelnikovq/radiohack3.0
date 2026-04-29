import { ArrowRight, CheckCircle2, RefreshCw } from "lucide-react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { useDemoProfiles } from "../hooks/useDemoProfiles";
import { SELECTED_USER_STORAGE_KEY } from "../lib/constants";
import { getSegmentLabel } from "../lib/formatters";
import { segmentBadgeClass } from "../lib/segment";
import { Badge } from "../components/ui/Badge";
import { Button } from "../components/ui/Button";
import { Card } from "../components/ui/Card";
import { EmptyState } from "../components/ui/EmptyState";
import { ErrorState } from "../components/ui/ErrorState";
import { Skeleton } from "../components/ui/Skeleton";

export function DemoProfilesPage() {
  const navigate = useNavigate();
  const { profiles, isLoading, error, refetch } = useDemoProfiles();
  const savedUserId = localStorage.getItem(SELECTED_USER_STORAGE_KEY);

  function openProfile(userId: number) {
    localStorage.setItem(SELECTED_USER_STORAGE_KEY, String(userId));
    navigate(`/users/${userId}/dashboard`);
  }

  return (
    <main className="min-h-screen px-4 py-6 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-7xl">
        <section className="mb-8 overflow-hidden rounded-[2rem] border border-white/10 bg-[linear-gradient(135deg,#0B0B0C_0%,#17171A_54%,#292409_100%)] p-6 shadow-soft sm:p-8 lg:p-10">
          <div className="max-w-3xl">
            <Badge className="border-t-yellow/30 bg-t-yellow/15 text-t-yellow">T-Loyalty Hub</Badge>
            <h1 className="mt-5 text-4xl font-black tracking-normal sm:text-5xl lg:text-6xl">Моя выгода от Т-Банка</h1>
            <p className="mt-5 max-w-2xl text-base leading-7 text-white/70 sm:text-lg">
              Выберите демо-клиента и покажите жюри единый раздел лояльности: cashback, мили, Браво, прогноз, офферы и персональные инсайты.
            </p>
            {savedUserId ? (
              <Button className="mt-6" icon={<ArrowRight className="h-5 w-5" aria-hidden />} onClick={() => navigate(`/users/${savedUserId}/dashboard`)}>
                Продолжить с выбранным клиентом
              </Button>
            ) : null}
          </div>
        </section>

        {isLoading ? (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {Array.from({ length: 6 }).map((_, index) => (
              <Skeleton key={index} className="h-80" />
            ))}
          </div>
        ) : error ? (
          <ErrorState message={error} onRetry={refetch} />
        ) : profiles.length === 0 ? (
          <EmptyState title="Демо-профили не найдены" description="Backend вернул пустой список профилей. Проверьте CSV-данные." />
        ) : (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {profiles.map((profile, index) => (
              <motion.div
                key={profile.user_id}
                initial={{ opacity: 0, y: 18 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.05, duration: 0.35 }}
              >
                <Card className="flex h-full flex-col transition hover:-translate-y-1 hover:border-t-yellow/35 hover:shadow-glow">
                  <div className="mb-4 flex items-start justify-between gap-3">
                    <div>
                      <Badge className={segmentBadgeClass(profile.financial_segment)}>{getSegmentLabel(profile.financial_segment)}</Badge>
                      <h2 className="mt-4 text-2xl font-black">{profile.label}</h2>
                    </div>
                    {savedUserId === String(profile.user_id) ? <CheckCircle2 className="h-6 w-6 text-t-yellow" aria-label="Выбран" /> : null}
                  </div>
                  <p className="text-sm leading-6 text-muted">{profile.description}</p>
                  <div className="mt-5 space-y-2">
                    {profile.highlight_metrics.map((metric) => (
                      <div key={metric} className="rounded-2xl bg-white/[0.06] px-3 py-2 text-sm light:bg-black/5">
                        {metric}
                      </div>
                    ))}
                  </div>
                  <div className="mt-5 flex-1">
                    <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-muted">Demo flow</p>
                    <div className="space-y-2">
                      {profile.recommended_demo_flow.map((step) => (
                        <div key={step} className="flex items-center gap-2 text-sm text-white/[0.72] light:text-black/70">
                          <span className="h-1.5 w-1.5 rounded-full bg-t-yellow" />
                          {step}
                        </div>
                      ))}
                    </div>
                  </div>
                  <Button className="mt-6 w-full" icon={<ArrowRight className="h-5 w-5" aria-hidden />} onClick={() => openProfile(profile.user_id)}>
                    Открыть Мою выгоду
                  </Button>
                </Card>
              </motion.div>
            ))}
          </div>
        )}

        <Button className="mt-6" variant="ghost" icon={<RefreshCw className="h-4 w-4" aria-hidden />} onClick={refetch}>
          Обновить профили
        </Button>
      </div>
    </main>
  );
}
