import { Navigate, Outlet, useParams } from "react-router-dom";
import { Sidebar } from "./Sidebar";
import { TopBar } from "./TopBar";
import { MobileNav } from "./MobileNav";
import { useDashboard } from "../../hooks/useDashboard";
import { useTheme } from "../../hooks/useTheme";
import type { DashboardResponse } from "../../api/types";

export interface DashboardOutletContext {
  userId: string;
  dashboard: DashboardResponse | null;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
}

export function AppShell() {
  const { userId } = useParams<{ userId: string }>();
  const { theme, toggleTheme } = useTheme();
  const dashboardState = useDashboard(userId);

  if (!userId) {
    return <Navigate to="/" replace />;
  }

  return (
    <div className="min-h-screen lg:flex">
      <Sidebar />
      <main className="min-w-0 flex-1">
        <div className="page-shell">
          <TopBar user={dashboardState.data?.user} theme={theme} onToggleTheme={toggleTheme} />
          <Outlet
            context={
              {
                userId,
                dashboard: dashboardState.data,
                isLoading: dashboardState.isLoading,
                error: dashboardState.error,
                refetch: dashboardState.refetch,
              } satisfies DashboardOutletContext
            }
          />
        </div>
      </main>
      <MobileNav />
    </div>
  );
}
