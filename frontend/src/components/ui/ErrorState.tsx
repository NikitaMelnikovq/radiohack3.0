import { AlertTriangle } from "lucide-react";
import { Button } from "./Button";
import { Card } from "./Card";

interface ErrorStateProps {
  title?: string;
  message: string;
  onRetry?: () => void;
}

export function ErrorState({ title = "Не удалось загрузить данные", message, onRetry }: ErrorStateProps) {
  return (
    <Card className="flex min-h-56 flex-col items-start justify-center">
      <div className="mb-4 rounded-2xl bg-t-danger/[0.12] p-3 text-t-danger">
        <AlertTriangle className="h-6 w-6" aria-hidden />
      </div>
      <h2 className="text-xl font-semibold">{title}</h2>
      <p className="mt-2 max-w-2xl text-sm leading-6 text-muted">{message}</p>
      {onRetry ? (
        <Button className="mt-5" onClick={onRetry}>
          Повторить
        </Button>
      ) : null}
    </Card>
  );
}
