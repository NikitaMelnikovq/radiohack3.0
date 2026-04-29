import type { HTMLAttributes, ReactNode } from "react";
import { cn } from "../../lib/cn";

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode;
  padded?: boolean;
}

export function Card({ children, className, padded = true, ...props }: CardProps) {
  return (
    <section className={cn("premium-surface rounded-[1.75rem]", padded && "p-5 sm:p-6", className)} {...props}>
      {children}
    </section>
  );
}
