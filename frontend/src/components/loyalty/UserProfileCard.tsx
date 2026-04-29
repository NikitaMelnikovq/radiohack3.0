import type { ReactNode } from "react";
import { CalendarDays, CreditCard, ReceiptText } from "lucide-react";
import type { DashboardResponse } from "../../api/types";
import { formatDate, getSegmentLabel } from "../../lib/formatters";
import { segmentBadgeClass } from "../../lib/segment";
import { Badge } from "../ui/Badge";
import { Card } from "../ui/Card";

export function UserProfileCard({ dashboard }: { dashboard: DashboardResponse }) {
  return (
    <Card className="h-full">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm text-muted">Профиль клиента</p>
          <h3 className="mt-2 text-2xl font-bold">{dashboard.user.full_name}</h3>
        </div>
        <Badge className={segmentBadgeClass(dashboard.user.financial_segment)}>
          {getSegmentLabel(dashboard.user.financial_segment)}
        </Badge>
      </div>
      <div className="mt-6 grid gap-3">
        <ProfileMetric icon={<CreditCard className="h-5 w-5" />} label="Счетов" value={dashboard.user.accounts_count} />
        <ProfileMetric icon={<ReceiptText className="h-5 w-5" />} label="Операций" value={dashboard.loyalty_summary.total_transactions} />
        <ProfileMetric icon={<CalendarDays className="h-5 w-5" />} label="Последняя выплата" value={formatDate(dashboard.loyalty_summary.last_payout_date)} />
      </div>
    </Card>
  );
}

function ProfileMetric({ icon, label, value }: { icon: ReactNode; label: string; value: ReactNode }) {
  return (
    <div className="flex items-center gap-3 rounded-2xl bg-white/[0.06] p-3 light:bg-black/5">
      <div className="grid h-10 w-10 place-items-center rounded-xl bg-t-yellow/15 text-t-yellow">{icon}</div>
      <div>
        <p className="text-xs text-muted">{label}</p>
        <p className="font-semibold">{value}</p>
      </div>
    </div>
  );
}
