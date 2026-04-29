import type { ReactNode } from "react";
import { Card } from "./Card";

interface MetricCardProps {
  label: string;
  value: ReactNode;
  description?: string;
}

export function MetricCard({ label, value, description }: MetricCardProps) {
  return (
    <Card className="min-h-32">
      <p className="text-sm font-medium text-muted">{label}</p>
      <div className="mt-3 text-2xl font-bold tracking-normal sm:text-3xl">{value}</div>
      {description ? <p className="mt-2 text-sm text-muted">{description}</p> : null}
    </Card>
  );
}
