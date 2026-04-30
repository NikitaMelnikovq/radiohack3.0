import type { Confidence, CurrencyCode, FinancialSegment } from "../api/types";
import { getCurrencyLabel } from "./currency";

const ruNumber = new Intl.NumberFormat("ru-RU", {
  maximumFractionDigits: 0,
});

const ruNumberCompact = new Intl.NumberFormat("ru-RU", {
  notation: "compact",
  maximumFractionDigits: 1,
});

const ruDecimal = new Intl.NumberFormat("ru-RU", {
  maximumFractionDigits: 2,
});

export function formatMoneyRub(value?: number | null): string {
  if (!isFiniteNumber(value)) {
    return "—";
  }
  return `${ruNumber.format(value)} ₽`;
}

export function formatNumberCompact(value?: number | null): string {
  if (!isFiniteNumber(value)) {
    return "—";
  }
  return ruNumberCompact.format(value);
}

export function formatCurrencyAmount(currency: CurrencyCode, value?: number | null): string {
  if (!isFiniteNumber(value)) {
    return "—";
  }
  if (currency === "rub") {
    return formatMoneyRub(value);
  }
  if (currency === "miles") {
    return `${ruNumber.format(value)} миль`;
  }
  if (currency === "bravo-points") {
    return `${ruNumber.format(value)} Браво`;
  }
  return `${ruDecimal.format(value)} ${currency}`;
}

export function formatDate(value?: string | null): string {
  if (!value) {
    return "—";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "—";
  }
  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "long",
    year: "numeric",
  }).format(date);
}

export function formatPercent(value?: number | null): string {
  if (!isFiniteNumber(value)) {
    return "—";
  }
  return `${ruDecimal.format(value)}%`;
}

export function getSegmentLabel(segment?: FinancialSegment | string): string {
  if (segment === "LOW") {
    return "Стартовый";
  }
  if (segment === "MEDIUM") {
    return "Средний";
  }
  if (segment === "HIGH") {
    return "Премиальный";
  }
  return "—";
}

export function getConfidenceLabel(confidence?: Confidence | string): string {
  if (confidence === "low") {
    return "низкая";
  }
  if (confidence === "medium") {
    return "средняя";
  }
  if (confidence === "high") {
    return "высокая";
  }
  return "—";
}

const technicalLabels: Record<string, string> = {
  average_last_3_months: "среднее за последние 3 месяца",
  rule_based_ai_insights: "инсайты по правилам",
  average_monthly_cashback_uplift: "потенциал от среднего кэшбэка",
  optimization: "оптимизация",
  explanation: "объяснение",
  forecast: "прогноз",
  cross_sell: "следующий продукт",
  risk: "риск",
  gamification: "прогресс",
  easy: "легко",
  medium: "средне",
  hard: "сложно",
  LOW: "стартовый",
  MEDIUM: "средний",
  HIGH: "премиальный",
};

export function getTechnicalLabel(value?: string | null): string {
  if (!value) {
    return "—";
  }
  return technicalLabels[value] ?? getCurrencyLabel(value);
}

export function localizeTechnicalText(value: string): string {
  return [
    ["cashback_percent", "проценту кэшбэка"],
    ["cashback_amount", "сумме выплат"],
    ["financial_segment", "финансовому сегменту"],
    ["average_monthly_cashback", "среднего месячного кэшбэка"],
    ["total_cashback_value", "накопленную выгоду"],
    ["loyalty points", "баллы выгоды"],
    ["Forecast service", "Сервис прогноза"],
    ["Offers service", "Сервис офферов"],
    ["Gamification service", "Сервис прогресса"],
    ["Missed benefit", "Потенциальная выгода"],
    ["Product score", "Оценка продукта"],
    ["Cashback", "Кэшбэк"],
    ["cashback", "кэшбэк"],
    ["score", "оценку"],
    ["average_last_3_months", "среднее за последние 3 месяца"],
    ["rule_based_ai_insights", "инсайты по правилам"],
    ["explainable", "объяснимое"],
    ["uplift", "прирост"],
    ["LOW", "стартовый"],
    ["MEDIUM", "средний"],
    ["HIGH", "премиальный"],
    ["rub", "рубли"],
    ["miles", "мили"],
    ["bravo-points", "Браво"],
  ].reduce((text, [from, to]) => text.split(from).join(to), value);
}

function isFiniteNumber(value: unknown): value is number {
  return typeof value === "number" && Number.isFinite(value);
}
