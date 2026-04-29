from datetime import date

from pydantic import BaseModel


class LoyaltySummaryUser(BaseModel):
    id: int
    full_name: str
    financial_segment: str


class LoyaltyAccountSummary(BaseModel):
    account_id: int
    loyalty_program: str
    cashback_currency: str
    current_balance: float
    total_cashback: float
    transactions_count: int


class CurrencyAmount(BaseModel):
    currency: str
    amount: float


class LoyaltySummary(BaseModel):
    user: LoyaltySummaryUser
    accounts: list[LoyaltyAccountSummary]
    totals_by_currency: list[CurrencyAmount]
    total_transactions: int
    last_payout_date: date | None


class MonthlyDynamicItem(BaseModel):
    month: str
    currency: str
    amount: float


class ProgramBreakdownItem(BaseModel):
    loyalty_program: str
    currency: str
    amount: float
    share_percent: float


class BestProgram(BaseModel):
    loyalty_program: str
    currency: str
    amount: float


class LoyaltyAnalytics(BaseModel):
    monthly_dynamics: list[MonthlyDynamicItem]
    program_breakdown: list[ProgramBreakdownItem]
    best_program: BestProgram | None
    average_monthly_cashback: list[CurrencyAmount]


class ForecastItem(BaseModel):
    currency: str
    predicted_amount: float
    confidence: str


class LoyaltyForecast(BaseModel):
    forecast_period_days: int
    method: str
    items: list[ForecastItem]
    explanation: str
