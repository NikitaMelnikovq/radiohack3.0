import type { GamificationResponse } from "../../api/types";
import { getTechnicalLabel } from "../../lib/formatters";
import { Progress } from "../ui/Progress";
import { Card } from "../ui/Card";
import { Badge } from "../ui/Badge";

interface GamificationPanelProps {
  gamification: GamificationResponse;
  compact?: boolean;
}

export function GamificationPanel({ gamification, compact = false }: GamificationPanelProps) {
  const challenges = compact ? gamification.challenges.slice(0, 2) : gamification.challenges;

  return (
    <div className="grid gap-4 lg:grid-cols-[0.9fr_1.1fr]">
      <Card>
        <p className="text-sm text-muted">Путь выгоды</p>
        <div className="mt-3 flex items-end justify-between gap-4">
          <div>
            <h3 className="text-3xl font-black">{gamification.level.name}</h3>
            <p className="mt-1 text-sm text-muted">
              {gamification.level.next_level
                ? `До ${gamification.level.next_level}: ${gamification.level.points_to_next_level} баллов`
                : "Максимальный уровень"}
            </p>
          </div>
          <div className="text-right text-2xl font-black text-t-yellow">{gamification.level.progress_percent}%</div>
        </div>
        <Progress className="mt-5 h-3" value={gamification.level.progress_percent} ariaLabel="Прогресс уровня" />
        <div className="mt-5 flex flex-wrap gap-2">
          {gamification.badges.map((badge) => (
            <Badge key={badge.code} className="border-t-yellow/20 bg-t-yellow/10 text-t-yellow">
              {badge.title}
            </Badge>
          ))}
        </div>
      </Card>
      <div className="grid gap-3">
        {challenges.map((challenge) => (
          <Card key={challenge.challenge_id}>
            <div className="mb-3 flex items-start justify-between gap-3">
              <div>
                <h3 className="font-bold">{challenge.title}</h3>
                <p className="mt-1 text-sm leading-6 text-muted">{challenge.description}</p>
              </div>
              <Badge>{getTechnicalLabel(challenge.difficulty)}</Badge>
            </div>
            <Progress value={challenge.progress_percent} ariaLabel={challenge.title} />
            <p className="mt-3 text-sm font-semibold text-t-yellow">{challenge.reward_text}</p>
          </Card>
        ))}
      </div>
    </div>
  );
}
