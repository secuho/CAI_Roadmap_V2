/** Decodes the payload of a JWT for display purposes only — no signature check. */
export function decodeJwtSubject(token: string): string | null {
  try {
    const [, payload] = token.split(".");
    const json = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    const parsed = JSON.parse(decodeURIComponent(escape(json)));
    return typeof parsed.sub === "string" ? parsed.sub : null;
  } catch {
    return null;
  }
}
