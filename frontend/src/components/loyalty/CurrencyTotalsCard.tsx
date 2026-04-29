import { ArrowUpRight } from "lucide-react";
import type { CurrencyAmount } from "../../api/types";
import { formatCurrencyAmount } from "../../lib/formatters";
import { getCurrencyColor, getCurrencyLabel } from "../../lib/currency";
import { EmptyState } from "../ui/EmptyState";

export function CurrencyTotalsCard({ totals }: { totals: CurrencyAmount[] }) {
  if (!totals.length) {
    return <EmptyState title="Выгода ещё не накоплена" description="После первых выплат здесь появятся рубли, мили и баллы Браво." />;
  }

  return (
    <div className="grid gap-3 sm:grid-cols-3">
      {totals.map((item) => (
        <div key={item.currency} className="relative overflow-hidden rounded-[1.5rem] border border-white/10 bg-white/[0.07] p-5 backdrop-blur light:border-black/10 light:bg-black/5">
          <div
            className="absolute -right-8 -top-8 h-24 w-24 rounded-full blur-2xl"
            style={{ backgroundColor: `${getCurrencyColor(item.currency)}33` }}
          />
          <div className="relative">
            <div className="mb-6 flex h-10 w-10 items-center justify-center rounded-2xl bg-white/10 light:bg-black/5">
              <ArrowUpRight className="h-5 w-5" style={{ color: getCurrencyColor(item.currency) }} aria-hidden />
            </div>
            <p className="text-sm text-muted">{getCurrencyLabel(item.currency)}</p>
            <p className="mt-2 text-2xl font-black sm:text-3xl">{formatCurrencyAmount(item.currency, item.amount)}</p>
          </div>
        </div>
      ))}
    </div>
  );
}
