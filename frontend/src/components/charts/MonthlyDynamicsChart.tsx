import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import type { MonthlyDynamicItem } from "../../api/types";
import { formatCurrencyAmount } from "../../lib/formatters";
import { getCurrencyColor, getCurrencyLabel } from "../../lib/currency";
import { Card } from "../ui/Card";
import { EmptyState } from "../ui/EmptyState";

interface ChartRow {
  month: string;
  [currency: string]: string | number;
}

export function MonthlyDynamicsChart({ items }: { items: MonthlyDynamicItem[] }) {
  if (!items.length) {
    return <EmptyState title="Нет динамики" description="График появится после первых выплат кэшбэка." />;
  }

  const currencies = Array.from(new Set(items.map((item) => item.currency)));
  const rowsByMonth = new Map<string, ChartRow>();

  for (const item of items) {
    const row = rowsByMonth.get(item.month) ?? { month: item.month };
    row[item.currency] = item.amount;
    rowsByMonth.set(item.month, row);
  }

  const data = Array.from(rowsByMonth.values()).sort((a, b) => String(a.month).localeCompare(String(b.month)));

  return (
    <Card className="h-[360px]">
      <h3 className="mb-5 text-lg font-bold">Динамика выплат</h3>
      <ResponsiveContainer width="100%" height="86%">
        <LineChart data={data} margin={{ top: 12, right: 8, bottom: 0, left: -12 }}>
          <CartesianGrid stroke="rgba(255,255,255,0.08)" vertical={false} />
          <XAxis dataKey="month" stroke="rgba(255,255,255,0.45)" tickLine={false} axisLine={false} />
          <YAxis stroke="rgba(255,255,255,0.45)" tickLine={false} axisLine={false} />
          <Tooltip
            contentStyle={{
              background: "var(--chart-tooltip-bg)",
              border: "1px solid var(--chart-tooltip-border)",
              borderRadius: 14,
              color: "var(--chart-tooltip-text)",
              boxShadow: "var(--chart-tooltip-shadow)",
            }}
            itemStyle={{ color: "var(--chart-tooltip-text)" }}
            labelStyle={{ color: "var(--chart-tooltip-text)" }}
            formatter={(value, name) => [formatCurrencyAmount(String(name), Number(value)), getCurrencyLabel(String(name))]}
          />
          {currencies.map((currency) => (
            <Line
              key={currency}
              type="monotone"
              dataKey={currency}
              stroke={getCurrencyColor(currency)}
              strokeWidth={3}
              dot={{ r: 4, strokeWidth: 0 }}
              activeDot={{ r: 6 }}
            />
          ))}
        </LineChart>
      </ResponsiveContainer>
    </Card>
  );
}
