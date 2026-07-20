import type { ReactNode } from "react";

export function AuthCard({ children }: { children: ReactNode }) {
  return (
    <div className="overflow-hidden rounded-2xl bg-cream shadow-[0_20px_60px_-15px_rgba(42,26,13,0.5)]">
      <div className="gradient-flame h-1.5 w-full" />
      <div className="px-8 py-9 sm:px-10">{children}</div>
    </div>
  );
}
