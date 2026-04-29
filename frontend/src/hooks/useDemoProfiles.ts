import { useCallback, useEffect, useState } from "react";
import { loyaltyApi } from "../api/loyaltyApi";
import type { DemoProfile } from "../api/types";

export function useDemoProfiles() {
  const [profiles, setProfiles] = useState<DemoProfile[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  const refetch = useCallback(() => setReloadToken((token) => token + 1), []);

  useEffect(() => {
    const controller = new AbortController();
    setIsLoading(true);
    setError(null);

    loyaltyApi
      .demoProfiles(controller.signal)
      .then((response) => setProfiles(response.profiles))
      .catch((requestError: unknown) => {
        if (requestError instanceof DOMException && requestError.name === "AbortError") {
          return;
        }
        setError(requestError instanceof Error ? requestError.message : "Не удалось загрузить демо-профили.");
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setIsLoading(false);
        }
      });

    return () => controller.abort();
  }, [reloadToken]);

  return { profiles, isLoading, error, refetch };
}
