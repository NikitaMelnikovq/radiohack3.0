import type { AIInsight } from "../../api/types";
import { getConfidenceLabel } from "../../lib/formatters";
import { confidenceBadgeClass } from "../../lib/segment";
import { Badge } from "../ui/Badge";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";
import { EvidenceList } from "./EvidenceList";

export function AiInsightsPanel({ insights, limit }: { insights: AIInsight[]; limit?: number }) {
  const visibleInsights = typeof limit === "number" ? insights.slice(0, limit) : insights;

  return (
    <div className="grid gap-3 lg:grid-cols-3">
      {visibleInsights.map((insight) => (
        <Card key={insight.insight_id} className="flex h-full flex-col">
          <div className="mb-4 flex items-center justify-between gap-3">
            <Badge>{insight.type}</Badge>
            <Badge className={confidenceBadgeClass(insight.confidence)}>{getConfidenceLabel(insight.confidence)}</Badge>
          </div>
          <h3 className="text-lg font-bold">{insight.title}</h3>
          <p className="mt-3 text-sm leading-6 text-muted">{insight.description}</p>
          <p className="mt-4 text-xs leading-5 text-muted">{insight.reason}</p>
          <div className="mt-4 flex-1">
            <EvidenceList evidence={insight.evidence} />
          </div>
          <Button className="mt-5 w-full" variant="secondary" type="button">
            {insight.cta_label}
          </Button>
        </Card>
      ))}
    </div>
  );
}
