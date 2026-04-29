import { Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "../components/layout/AppShell";
import { AnalyticsPage } from "../pages/AnalyticsPage";
import { AssistantPage } from "../pages/AssistantPage";
import { DashboardPage } from "../pages/DashboardPage";
import { DemoProfilesPage } from "../pages/DemoProfilesPage";
import { GamificationPage } from "../pages/GamificationPage";
import { NotFoundPage } from "../pages/NotFoundPage";
import { OffersPage } from "../pages/OffersPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<DemoProfilesPage />} />
      <Route path="/users/:userId" element={<AppShell />}>
        <Route index element={<Navigate to="dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="analytics" element={<AnalyticsPage />} />
        <Route path="offers" element={<OffersPage />} />
        <Route path="assistant" element={<AssistantPage />} />
        <Route path="gamification" element={<GamificationPage />} />
      </Route>
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
