import { Badge } from "../ui/Badge";

export function EvidenceList({ evidence, limit = 4 }: { evidence: string[]; limit?: number }) {
  return (
    <div className="flex flex-wrap gap-2">
      {evidence.slice(0, limit).map((item) => (
        <Badge key={item} className="border-white/10 bg-white/[0.06] text-white/70 light:border-black/10 light:bg-black/5 light:text-black/[0.65]">
          {item}
        </Badge>
      ))}
    </div>
  );
}
