import type { Confidence, FinancialSegment } from "../api/types";
import { cn } from "./cn";

export function segmentBadgeClass(segment?: FinancialSegment | string): string {
  return cn(
    "border",
    segment === "HIGH" && "border-t-yellow/60 bg-black text-t-yellow",
    segment === "MEDIUM" && "border-t-yellow/30 bg-t-yellow text-black",
    segment === "LOW" && "border-white/10 bg-white/[0.08] text-white",
  );
}

export function confidenceBadgeClass(confidence?: Confidence | string): string {
  return cn(
    confidence === "high" && "border-t-success/30 bg-t-success/[0.12] text-t-success",
    confidence === "medium" && "border-t-yellow/30 bg-t-yellow/15 text-t-yellow",
    confidence === "low" && "border-t-warning/30 bg-t-warning/[0.12] text-t-warning",
  );
}
