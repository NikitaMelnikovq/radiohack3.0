import { BarChart3, Gift, Home, MessageSquareText, Route, UserRoundPlus } from "lucide-react";
import { NavLink } from "react-router-dom";
import { cn } from "../../lib/cn";

const navigation = [
  { label: "Главная", to: "dashboard", icon: Home },
  { label: "Аналитика", to: "analytics", icon: BarChart3 },
  { label: "Офферы", to: "offers", icon: Gift },
  { label: "Ассистент", to: "assistant", icon: MessageSquareText },
  { label: "Путь выгоды", to: "gamification", icon: Route },
];

export function Sidebar() {
  return (
    <aside className="sticky top-0 hidden h-screen w-72 shrink-0 border-r border-white/[0.08] bg-black/20 px-4 py-5 backdrop-blur-xl light:border-black/[0.08] light:bg-white/70 lg:block">
      <div className="mb-8 flex items-center gap-3 px-2">
        <div className="grid h-11 w-11 place-items-center rounded-2xl bg-t-yellow text-lg font-black text-black">T</div>
        <div>
          <p className="text-sm text-muted">T-Loyalty Hub</p>
          <h1 className="font-bold">Моя выгода</h1>
        </div>
      </div>
      <nav className="space-y-2" aria-label="Основная навигация">
        {navigation.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              cn(
                "flex min-h-12 items-center gap-3 rounded-2xl px-4 text-sm font-semibold text-white/[0.66] transition light:text-black/60",
                "hover:bg-white/[0.08] hover:text-white light:hover:bg-black/5 light:hover:text-black",
                isActive && "bg-t-yellow text-black hover:bg-t-yellow light:bg-t-yellow light:text-black",
              )
            }
          >
            <item.icon className="h-5 w-5" aria-hidden />
            {item.label}
          </NavLink>
        ))}
      </nav>
      <NavLink
        to="/"
        className="mt-6 flex min-h-12 items-center gap-3 rounded-2xl px-4 text-sm font-semibold text-white/[0.66] transition hover:bg-white/[0.08] hover:text-white light:text-black/60 light:hover:bg-black/5 light:hover:text-black"
      >
        <UserRoundPlus className="h-5 w-5" aria-hidden />
        Сменить пользователя
      </NavLink>
    </aside>
  );
}
