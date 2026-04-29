import type { CrossSellRecommendation } from "../../api/types";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";
import { EvidenceList } from "./EvidenceList";

export function CrossSellCards({ recommendations, limit }: { recommendations: CrossSellRecommendation[]; limit?: number }) {
  const visibleRecommendations = typeof limit === "number" ? recommendations.slice(0, limit) : recommendations;

  return (
    <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
      {visibleRecommendations.map((recommendation) => (
        <Card key={recommendation.product_code} className="flex h-full flex-col">
          <div className="mb-5 flex items-center justify-between gap-3">
            <p className="text-sm font-semibold text-muted">{recommendation.product_name}</p>
            <div className="rounded-full bg-t-yellow px-3 py-1 text-sm font-black text-black">{recommendation.score}</div>
          </div>
          <h3 className="text-lg font-bold">{recommendation.title}</h3>
          <p className="mt-3 flex-1 text-sm leading-6 text-muted">{recommendation.description}</p>
          <p className="mt-4 text-xs leading-5 text-muted">{recommendation.reason}</p>
          <div className="mt-4">
            <EvidenceList evidence={recommendation.evidence} limit={3} />
          </div>
          <Button className="mt-5 w-full" type="button">
            {recommendation.cta_label}
          </Button>
        </Card>
      ))}
    </div>
  );
}
