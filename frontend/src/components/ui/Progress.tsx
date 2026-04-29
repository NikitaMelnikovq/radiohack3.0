import { cn } from "../../lib/cn";

interface ProgressProps {
  value: number;
  max?: number;
  className?: string;
  indicatorClassName?: string;
  ariaLabel?: string;
}

export function Progress({ value, max = 100, className, indicatorClassName, ariaLabel }: ProgressProps) {
  const normalizedValue = Math.max(0, Math.min(100, max ? (value / max) * 100 : 0));

  return (
    <div
      className={cn("h-2.5 overflow-hidden rounded-full bg-white/10 light:bg-black/10", className)}
      role="progressbar"
      aria-label={ariaLabel}
      aria-valuenow={Math.round(normalizedValue)}
      aria-valuemin={0}
      aria-valuemax={100}
    >
      <div
        className={cn("h-full rounded-full bg-t-yellow transition-all", indicatorClassName)}
        style={{ width: `${normalizedValue}%` }}
      />
    </div>
  );
}
