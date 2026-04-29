import { Moon, Sun } from "lucide-react";
import { Button } from "../ui/Button";
import { Badge } from "../ui/Badge";
import type { UserPreview } from "../../api/types";
import { getSegmentLabel } from "../../lib/formatters";
import { segmentBadgeClass } from "../../lib/segment";
import type { Theme } from "../../hooks/useTheme";

interface TopBarProps {
  user?: UserPreview;
  theme: Theme;
  onToggleTheme: () => void;
}

export function TopBar({ user, theme, onToggleTheme }: TopBarProps) {
  return (
    <header className="mb-5 flex flex-col gap-3 rounded-[1.5rem] border border-white/10 bg-white/[0.045] px-4 py-4 backdrop-blur-xl light:border-black/10 light:bg-white/75 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <p className="text-sm text-muted">Моя выгода</p>
        <h2 className="text-lg font-bold sm:text-xl">{user?.full_name ?? "Загрузка профиля"}</h2>
      </div>
      <div className="flex items-center gap-2">
        {user ? <Badge className={segmentBadgeClass(user.financial_segment)}>{getSegmentLabel(user.financial_segment)}</Badge> : null}
        <Button
          variant="secondary"
          aria-label={theme === "dark" ? "Включить светлую тему" : "Включить тёмную тему"}
          onClick={onToggleTheme}
          className="h-11 w-11 px-0"
        >
          {theme === "dark" ? <Sun className="h-5 w-5" aria-hidden /> : <Moon className="h-5 w-5" aria-hidden />}
        </Button>
      </div>
    </header>
  );
}
