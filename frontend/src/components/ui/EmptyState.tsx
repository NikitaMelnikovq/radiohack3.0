import { CircleDashed } from "lucide-react";
import { Card } from "./Card";

interface EmptyStateProps {
  title: string;
  description: string;
}

export function EmptyState({ title, description }: EmptyStateProps) {
  return (
    <Card className="flex min-h-48 flex-col items-center justify-center text-center">
      <CircleDashed className="mb-4 h-9 w-9 text-t-yellow" aria-hidden />
      <h3 className="text-lg font-semibold">{title}</h3>
      <p className="mt-2 max-w-md text-sm text-muted">{description}</p>
    </Card>
  );
}
