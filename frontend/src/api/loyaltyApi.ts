import { apiGet } from "./client";
import type {
  AIInsightsResponse,
  CrossSellResponse,
  DashboardResponse,
  DemoProfilesResponse,
  GamificationResponse,
  LoyaltyAnalytics,
  MissedBenefitResponse,
  OffersResponse,
} from "./types";

export const loyaltyApi = {
  demoProfiles: (signal?: AbortSignal) => apiGet<DemoProfilesResponse>("/api/demo/profiles", signal),
  dashboard: (userId: number | string, signal?: AbortSignal) =>
    apiGet<DashboardResponse>(`/api/users/${userId}/dashboard`, signal),
  analytics: (userId: number | string, signal?: AbortSignal) =>
    apiGet<LoyaltyAnalytics>(`/api/users/${userId}/loyalty/analytics`, signal),
  offers: (userId: number | string, signal?: AbortSignal) =>
    apiGet<OffersResponse>(`/api/users/${userId}/offers`, signal),
  aiInsights: (userId: number | string, signal?: AbortSignal) =>
    apiGet<AIInsightsResponse>(`/api/users/${userId}/ai-insights`, signal),
  gamification: (userId: number | string, signal?: AbortSignal) =>
    apiGet<GamificationResponse>(`/api/users/${userId}/gamification`, signal),
  crossSell: (userId: number | string, signal?: AbortSignal) =>
    apiGet<CrossSellResponse>(`/api/users/${userId}/cross-sell`, signal),
  missedBenefit: (userId: number | string, signal?: AbortSignal) =>
    apiGet<MissedBenefitResponse>(`/api/users/${userId}/missed-benefit`, signal),
};
