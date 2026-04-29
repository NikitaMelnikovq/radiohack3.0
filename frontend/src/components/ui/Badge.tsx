import type { HTMLAttributes, ReactNode } from "react";
import { cn } from "../../lib/cn";

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  children: ReactNode;
}

export function Badge({ children, className, ...props }: BadgeProps) {
  return (
    <span
      className={cn(
        "inline-flex min-h-7 items-center rounded-full border border-white/10 bg-white/[0.08] px-3 py-1 text-xs font-semibold text-white/80 light:border-black/10 light:bg-black/5 light:text-black/70",
        className,
      )}
      {...props}
    >
      {children}
    </span>
  );
}
