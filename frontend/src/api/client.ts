export const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL || "/api").replace(/\/$/, "");

export class ApiError extends Error {
  constructor(
    message: string,
    public readonly status?: number,
    public readonly detail?: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

export async function apiGet<T>(path: string, signal?: AbortSignal): Promise<T> {
  const url = buildApiUrl(path);

  try {
    const response = await fetch(url, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
      signal,
    });

    if (!response.ok) {
      const detail = await readErrorDetail(response);
      throw new ApiError(detail || `Backend вернул HTTP ${response.status}`, response.status, detail);
    }

    return (await response.json()) as T;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    if (error instanceof DOMException && error.name === "AbortError") {
      throw error;
    }
    throw new ApiError(
      `Не удалось подключиться к backend. Проверьте, что FastAPI запущен на ${API_BASE_URL}.`,
    );
  }
}

function buildApiUrl(path: string): string {
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;

  if (API_BASE_URL.endsWith("/api") && normalizedPath.startsWith("/api/")) {
    return `${API_BASE_URL}${normalizedPath.slice("/api".length)}`;
  }

  return `${API_BASE_URL}${normalizedPath}`;
}

async function readErrorDetail(response: Response): Promise<string | undefined> {
  try {
    const payload = (await response.json()) as { detail?: string };
    return payload.detail;
  } catch {
    return undefined;
  }
}
