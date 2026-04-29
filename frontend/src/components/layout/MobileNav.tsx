import { BarChart3, Gift, Home, MessageSquareText, Route } from "lucide-react";
import { NavLink } from "react-router-dom";
import { cn } from "../../lib/cn";

const navigation = [
  { label: "Главная", to: "dashboard", icon: Home },
  { label: "Аналитика", to: "analytics", icon: BarChart3 },
  { label: "Офферы", to: "offers", icon: Gift },
  { label: "AI", to: "assistant", icon: MessageSquareText },
  { label: "Путь", to: "gamification", icon: Route },
];

export function MobileNav() {
  return (
    <nav
      className="fixed inset-x-3 bottom-3 z-50 grid grid-cols-5 rounded-[1.4rem] border border-white/10 bg-t-black/[0.88] p-2 shadow-soft backdrop-blur-xl light:border-black/10 light:bg-white/90 lg:hidden"
      aria-label="Мобильная навигация"
    >
      {navigation.map((item) => (
        <NavLink
          key={item.to}
          to={item.to}
          className={({ isActive }) =>
            cn(
              "flex min-h-12 flex-col items-center justify-center gap-1 rounded-2xl text-[0.7rem] font-semibold text-white/[0.58] transition light:text-black/[0.55]",
              isActive && "bg-t-yellow text-black",
            )
          }
        >
          <item.icon className="h-5 w-5" aria-hidden />
          {item.label}
        </NavLink>
      ))}
    </nav>
  );
}
