import { cn } from "../../lib/cn";

export function Skeleton({ className }: { className?: string }) {
  return <div className={cn("animate-pulse rounded-3xl bg-white/10 light:bg-black/10", className)} />;
}

export function DashboardSkeleton() {
  return (
    <div className="grid gap-4 lg:grid-cols-12">
      <Skeleton className="h-72 lg:col-span-8" />
      <Skeleton className="h-72 lg:col-span-4" />
      <Skeleton className="h-56 lg:col-span-4" />
      <Skeleton className="h-56 lg:col-span-4" />
      <Skeleton className="h-56 lg:col-span-4" />
    </div>
  );
}
