import { Sparkles } from "lucide-react";
import type { DashboardNextBestAction } from "../../api/types";
import { Button } from "../ui/Button";
import { Card } from "../ui/Card";

export function NextBestActionCard({ action }: { action: DashboardNextBestAction }) {
  return (
    <Card className="bg-t-yellow text-black light:bg-t-yellow">
      <div className="mb-5 grid h-12 w-12 place-items-center rounded-2xl bg-black/10">
        <Sparkles className="h-6 w-6" aria-hidden />
      </div>
      <p className="text-sm font-semibold opacity-70">Лучшее следующее действие</p>
      <h3 className="mt-2 text-2xl font-black">{action.title}</h3>
      <p className="mt-3 text-sm leading-6 opacity-75">{action.description}</p>
      <Button className="mt-6 min-h-12 rounded-2xl px-5 !bg-black !text-white hover:!bg-black/80" type="button">
        {action.cta_label}
      </Button>
    </Card>
  );
}
