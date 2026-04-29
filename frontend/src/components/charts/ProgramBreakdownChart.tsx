import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";
import type { ProgramBreakdownItem } from "../../api/types";
import { formatCurrencyAmount, formatPercent } from "../../lib/formatters";
import { getCurrencyColor } from "../../lib/currency";
import { Card } from "../ui/Card";
import { EmptyState } from "../ui/EmptyState";

export function ProgramBreakdownChart({ items }: { items: ProgramBreakdownItem[] }) {
  if (!items.length) {
    return <EmptyState title="Нет разбивки" description="Разбивка по программам появится после выплат." />;
  }

  return (
    <Card className="h-[360px]">
      <h3 className="mb-5 text-lg font-bold">Вклад программ</h3>
      <div className="grid h-[86%] gap-4 md:grid-cols-[1fr_0.9fr]">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie data={items} dataKey="amount" nameKey="loyalty_program" innerRadius="58%" outerRadius="82%" paddingAngle={4}>
              {items.map((item) => (
                <Cell key={`${item.loyalty_program}-${item.currency}`} fill={getCurrencyColor(item.currency)} />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{
                background: "#151518",
                border: "1px solid rgba(255,255,255,0.1)",
                borderRadius: 18,
                color: "#fff",
              }}
              formatter={(value, _, entry) => {
                const item = entry.payload as ProgramBreakdownItem;
                return [formatCurrencyAmount(item.currency, Number(value)), item.loyalty_program];
              }}
            />
          </PieChart>
        </ResponsiveContainer>
        <div className="flex flex-col justify-center gap-3">
          {items.map((item) => (
            <div key={`${item.loyalty_program}-${item.currency}`} className="rounded-2xl bg-white/[0.06] p-3 light:bg-black/5">
              <div className="flex items-center justify-between gap-3">
                <div className="min-w-0">
                  <p className="truncate font-semibold">{item.loyalty_program}</p>
                  <p className="text-xs text-muted">{formatCurrencyAmount(item.currency, item.amount)}</p>
                </div>
                <p className="text-sm font-bold text-t-yellow">{formatPercent(item.share_percent)}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Card>
  );
}
