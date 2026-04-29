import type { ButtonHTMLAttributes, ReactNode } from "react";
import { cn } from "../../lib/cn";

type ButtonVariant = "primary" | "secondary" | "ghost" | "danger";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  icon?: ReactNode;
}

export function Button({ className, variant = "primary", icon, children, ...props }: ButtonProps) {
  return (
    <button
      className={cn(
        "inline-flex min-h-10 items-center justify-center gap-2 rounded-2xl px-4 py-2 text-sm font-semibold transition",
        "disabled:cursor-not-allowed disabled:opacity-50",
        variant === "primary" && "bg-t-yellow text-black shadow-glow hover:bg-t-yellow-hover active:bg-t-yellow-active",
        variant === "secondary" && "border border-white/10 bg-white/[0.08] text-white hover:bg-white/[0.12] light:border-black/10 light:bg-black/5 light:text-black",
        variant === "ghost" && "text-white/[0.72] hover:bg-white/[0.08] hover:text-white light:text-black/70 light:hover:bg-black/5 light:hover:text-black",
        variant === "danger" && "bg-t-danger text-white hover:brightness-110",
        className,
      )}
      {...props}
    >
      {icon}
      {children}
    </button>
  );
}
