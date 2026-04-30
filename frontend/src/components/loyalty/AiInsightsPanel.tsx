import type { AIInsight } from "../../api/types";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { getConfidenceLabel, getTechnicalLabel, localizeTechnicalText } from "../../lib/formatters";
import { confidenceBadgeClass } from "../../lib/segment";
import { Badge } from "../ui/Badge";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";
import { EvidenceList } from "./EvidenceList";

export function AiInsightsPanel({ insights, limit }: { insights: AIInsight[]; limit?: number }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { userId } = useParams<{ userId: string }>();
  const visibleInsights = typeof limit === "number" ? insights.slice(0, limit) : insights;

  function handleInsightAction(insight: AIInsight) {
    if (!userId) {
      return;
    }

    if (insight.insight_id === "best_program_focus") {
      navigate(`/users/${userId}/analytics`);
      return;
    }

    if (insight.insight_id === "forecast_explanation") {
      if (location.pathname.endsWith("/dashboard")) {
        document.getElementById("expected-benefit")?.scrollIntoView({ behavior: "smooth", block: "start" });
        return;
      }
      navigate(`/users/${userId}/dashboard#expected-benefit`);
      return;
    }

    if (insight.insight_id === "offer_activation_tip" || insight.insight_id === "segment_explanation") {
      navigate(`/users/${userId}/offers`);
    }
  }

  return (
    <div className="grid gap-3 lg:grid-cols-3">
      {visibleInsights.map((insight) => (
        <Card key={insight.insight_id} className="flex h-full flex-col">
          <div className="mb-4 flex items-center justify-between gap-3">
            <Badge>{getTechnicalLabel(insight.type)}</Badge>
            <Badge className={confidenceBadgeClass(insight.confidence)}>{getConfidenceLabel(insight.confidence)}</Badge>
          </div>
          <h3 className="text-lg font-bold">{insight.title}</h3>
          <p className="mt-3 text-sm leading-6 text-muted">{localizeTechnicalText(insight.description)}</p>
          <p className="mt-4 text-xs leading-5 text-muted">{localizeTechnicalText(insight.reason)}</p>
          <div className="mt-4 flex-1">
            <EvidenceList evidence={insight.evidence} />
          </div>
          <Button className="mt-5 w-full" variant="secondary" type="button" onClick={() => handleInsightAction(insight)}>
            {insight.cta_label}
          </Button>
        </Card>
      ))}
    </div>
  );
}
