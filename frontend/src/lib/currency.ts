import { CURRENCY_COLORS } from "./constants";

export function getCurrencyLabel(currency: string): string {
  if (currency === "rub") {
    return "Рубли";
  }
  if (currency === "miles") {
    return "Мили";
  }
  if (currency === "bravo-points") {
    return "Браво";
  }
  return currency;
}

export function getCurrencyColor(currency: string): string {
  return CURRENCY_COLORS[currency] ?? "#8A8A8E";
}
