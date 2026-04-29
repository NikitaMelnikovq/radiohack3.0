import { Link } from "react-router-dom";
import { Card } from "../components/ui/Card";

export function NotFoundPage() {
  return (
    <main className="grid min-h-screen place-items-center px-4">
      <Card className="max-w-lg text-center">
        <p className="text-sm text-muted">404</p>
        <h1 className="mt-2 text-3xl font-black">Страница не найдена</h1>
        <p className="mt-3 text-sm leading-6 text-muted">Вернитесь к выбору демо-профиля и откройте раздел «Моя выгода».</p>
        <Link
          to="/"
          className="mt-6 inline-flex min-h-10 items-center justify-center rounded-2xl bg-t-yellow px-4 py-2 text-sm font-semibold text-black transition hover:bg-t-yellow-hover active:bg-t-yellow-active"
        >
          К выбору клиента
        </Link>
      </Card>
    </main>
  );
}
