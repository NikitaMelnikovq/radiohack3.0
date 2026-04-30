import { useState } from "react";
import { ChevronDown } from "lucide-react";
import { useOutletContext } from "react-router-dom";
import type { DashboardOutletContext } from "../components/layout/AppShell";
import { AiInsightsPanel } from "../components/loyalty/AiInsightsPanel";
import { Badge } from "../components/ui/Badge";
import { Card } from "../components/ui/Card";
import { ErrorState } from "../components/ui/ErrorState";
import { DashboardSkeleton } from "../components/ui/Skeleton";
import { SectionHeader } from "../components/ui/SectionHeader";
import { cn } from "../lib/cn";
import { getTechnicalLabel, localizeTechnicalText } from "../lib/formatters";

export function AssistantPage() {
  const { dashboard, isLoading, error, refetch } = useOutletContext<DashboardOutletContext>();
  const [openQuestion, setOpenQuestion] = useState<string | null>(null);

  if (isLoading) {
    return <DashboardSkeleton />;
  }

  if (error || !dashboard) {
    return <ErrorState message={error ?? "Ассистент недоступен."} onRetry={refetch} />;
  }

  return (
    <div className="space-y-6">
      <SectionHeader
        title="AI-ассистент выгоды"
        description="Инсайты рассчитаны по вашей истории лояльности и офферам без внешней нейросети."
        action={<Badge className="border-t-yellow/30 bg-t-yellow/15 text-t-yellow">{getTechnicalLabel(dashboard.ai_insights.method)}</Badge>}
      />
      <Card>
        <h2 className="text-2xl font-black">{dashboard.ai_insights.title}</h2>
        <p className="mt-3 text-sm leading-6 text-muted">{dashboard.ai_insights.summary}</p>
      </Card>
      <AiInsightsPanel insights={dashboard.ai_insights.insights} />
      <Card>
        <h2 className="mb-4 text-xl font-bold">Быстрые вопросы</h2>
        <div className="space-y-2">
          {dashboard.ai_insights.quick_questions.map((item) => {
            const isOpen = openQuestion === item.question;
            return (
              <button
                key={item.question}
                type="button"
                className="w-full rounded-2xl bg-white/[0.06] p-4 text-left transition hover:bg-white/[0.09] light:bg-black/5 light:hover:bg-black/10"
                onClick={() => setOpenQuestion(isOpen ? null : item.question)}
                aria-expanded={isOpen}
              >
                <div className="flex items-center justify-between gap-3">
                  <span className="font-semibold">{localizeTechnicalText(item.question)}</span>
                  <ChevronDown className={cn("h-5 w-5 transition", isOpen && "rotate-180")} aria-hidden />
                </div>
                {isOpen ? <p className="mt-3 text-sm leading-6 text-muted">{localizeTechnicalText(item.answer)}</p> : null}
              </button>
            );
          })}
        </div>
      </Card>
    </div>
  );
}
