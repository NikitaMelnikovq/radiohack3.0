export type FinancialSegment = "LOW" | "MEDIUM" | "HIGH";
export type Confidence = "low" | "medium" | "high";
export type CurrencyCode = "rub" | "miles" | "bravo-points" | string;
export type DashboardStatus = "starting" | "growing" | "strong" | "top";

export interface UserPreview {
  id: number;
  full_name: string;
  email: string;
  phone_number: string;
  financial_segment: FinancialSegment;
  accounts_count: number;
  total_cashback_value: number;
}

export interface CurrencyAmount {
  currency: CurrencyCode;
  amount: number;
}

export interface LoyaltySummaryUser {
  id: number;
  full_name: string;
  financial_segment: FinancialSegment;
}

export interface LoyaltyAccountSummary {
  account_id: number;
  loyalty_program: string;
  cashback_currency: CurrencyCode;
  current_balance: number;
  total_cashback: number;
  transactions_count: number;
}

export interface LoyaltySummary {
  user: LoyaltySummaryUser;
  accounts: LoyaltyAccountSummary[];
  totals_by_currency: CurrencyAmount[];
  total_transactions: number;
  last_payout_date: string | null;
}

export interface MonthlyDynamicItem {
  month: string;
  currency: CurrencyCode;
  amount: number;
}

export interface ProgramBreakdownItem {
  loyalty_program: string;
  currency: CurrencyCode;
  amount: number;
  share_percent: number;
}

export interface BestProgram {
  loyalty_program: string;
  currency: CurrencyCode;
  amount: number;
}

export interface LoyaltyAnalytics {
  monthly_dynamics: MonthlyDynamicItem[];
  program_breakdown: ProgramBreakdownItem[];
  best_program: BestProgram | null;
  average_monthly_cashback: CurrencyAmount[];
}

export interface ForecastItem {
  currency: CurrencyCode;
  predicted_amount: number;
  confidence: Confidence;
}

export interface LoyaltyForecast {
  forecast_period_days: number;
  method: string;
  items: ForecastItem[];
  explanation: string;
}

export interface Offer {
  partner_id: number;
  partner_name: string;
  short_description: string;
  logo_url: string;
  brand_color_hex: string;
  cashback_percent: number;
  financial_segment: FinancialSegment;
  reason: string;
}

export interface OffersResponse {
  user_segment: FinancialSegment;
  offers: Offer[];
}

export interface CrossSellRecommendation {
  product_code: string;
  product_name: string;
  category: string;
  priority: number;
  score: number;
  title: string;
  description: string;
  reason: string;
  evidence: string[];
  cta_label: string;
}

export interface CrossSellResponse {
  user_id: number;
  financial_segment: FinancialSegment;
  recommendations: CrossSellRecommendation[];
}

export interface LoyaltyLevel {
  code: string;
  name: string;
  current_points: number;
  next_level: string | null;
  points_to_next_level: number;
  progress_percent: number;
}

export interface LoyaltyBadge {
  code: string;
  title: string;
  description: string;
}

export interface LoyaltyChallenge {
  challenge_id: string;
  title: string;
  description: string;
  reward_text: string;
  progress_percent: number;
  difficulty: "easy" | "medium" | "hard";
}

export interface GamificationResponse {
  user_id: number;
  level: LoyaltyLevel;
  badges: LoyaltyBadge[];
  challenges: LoyaltyChallenge[];
}

export interface AIInsight {
  insight_id: string;
  type: "optimization" | "explanation" | "forecast" | "cross_sell" | "risk" | "gamification";
  priority: number;
  title: string;
  description: string;
  reason: string;
  evidence: string[];
  confidence: Confidence;
  cta_label: string;
}

export interface QuickQuestion {
  question: string;
  answer: string;
}

export interface AIInsightsResponse {
  user_id: number;
  method: string;
  title: string;
  summary: string;
  insights: AIInsight[];
  quick_questions: QuickQuestion[];
}

export interface MissedBenefitItem {
  currency: CurrencyCode;
  average_monthly_amount: number;
  potential_extra_amount: number;
}

export interface MissedBenefitResponse {
  user_id: number;
  method: string;
  uplift_factor: number;
  items: MissedBenefitItem[];
  top_offer_cashback_percent: number | null;
  explanation: string;
}

export interface DashboardScoreFactor {
  code: string;
  label: string;
  value: number;
  max_value: number;
  explanation: string;
}

export interface DashboardNextBestAction {
  title: string;
  description: string;
  cta_label: string;
}

export interface DashboardScore {
  score: number;
  status: DashboardStatus;
  title: string;
  description: string;
  factors: DashboardScoreFactor[];
  next_best_action: DashboardNextBestAction;
}

export interface DashboardResponse {
  user: UserPreview;
  loyalty_summary: LoyaltySummary;
  analytics: LoyaltyAnalytics;
  forecast: LoyaltyForecast;
  offers: OffersResponse;
  cross_sell: CrossSellResponse;
  gamification: GamificationResponse;
  ai_insights: AIInsightsResponse;
  missed_benefit: MissedBenefitResponse;
  dashboard_score: DashboardScore;
}

export interface DemoProfile {
  user_id: number;
  label: string;
  description: string;
  financial_segment: FinancialSegment;
  highlight_metrics: string[];
  recommended_demo_flow: string[];
}

export interface DemoProfilesResponse {
  profiles: DemoProfile[];
}
