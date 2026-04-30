import type { DashboardScore } from "../../api/types";
import { localizeTechnicalText } from "../../lib/formatters";
import { Progress } from "../ui/Progress";
import { Card } from "../ui/Card";
import { Badge } from "../ui/Badge";

const statusLabels: Record<string, string> = {
  starting: "старт",
  growing: "растёт",
  strong: "сильный",
  top: "топ",
};

export function DashboardScoreCard({ score }: { score: DashboardScore }) {
  return (
    <Card className="h-full">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm text-muted">Оценка вовлечённости</p>
          <h3 className="mt-2 text-xl font-bold">{score.title}</h3>
        </div>
        <Badge className="border-t-yellow/30 bg-t-yellow/15 text-t-yellow">{statusLabels[score.status]}</Badge>
      </div>
      <div className="mt-6 flex items-end gap-4">
        <div className="text-6xl font-black tracking-normal">{score.score}</div>
        <div className="pb-2 text-sm text-muted">из 100</div>
      </div>
      <p className="mt-4 text-sm leading-6 text-muted">{localizeTechnicalText(score.description)}</p>
      <div className="mt-6 space-y-4">
        {score.factors.map((factor) => (
          <div key={factor.code}>
            <div className="mb-2 flex items-center justify-between gap-3 text-sm">
              <span className="font-semibold">{factor.label}</span>
              <span className="text-muted">
                {factor.value}/{factor.max_value}
              </span>
            </div>
            <Progress value={factor.value} max={factor.max_value} ariaLabel={factor.label} />
          </div>
        ))}
      </div>
    </Card>
  );
}
