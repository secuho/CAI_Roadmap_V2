type IconProps = { className?: string };

const base = "h-4.5 w-4.5";

export function RecommendIcon({ className = base }: IconProps) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className={className}>
      <path
        d="M12 2.5l2.24 5.6 6.02.49-4.6 3.92 1.4 5.87L12 15.3l-5.06 3.08 1.4-5.87-4.6-3.92 6.02-.49L12 2.5z"
        strokeLinejoin="round"
      />
    </svg>
  );
}

export function TreeIcon({ className = base }: IconProps) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className={className}>
      <circle cx="6" cy="6" r="2.2" />
      <circle cx="6" cy="18" r="2.2" />
      <circle cx="17" cy="12" r="2.2" />
      <path d="M8 6h4a4 4 0 0 1 4 4M8 18h4a4 4 0 0 0 4-4" strokeLinecap="round" />
    </svg>
  );
}

export function ClockIcon({ className = base }: IconProps) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className={className}>
      <circle cx="12" cy="12" r="8.5" />
      <path d="M12 7.5V12l3 2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

export function CheckBadgeIcon({ className = base }: IconProps) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className={className}>
      <path d="M9 12.5l2 2 4-4.5" strokeLinecap="round" strokeLinejoin="round" />
      <circle cx="12" cy="12" r="8.5" />
    </svg>
  );
}

export function ChartIcon({ className = base }: IconProps) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className={className}>
      <path d="M4 20V10M12 20V4M20 20v-7" strokeLinecap="round" />
    </svg>
  );
}

export function CalendarIcon({ className = base }: IconProps) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" className={className}>
      <rect x="3.5" y="5" width="17" height="15.5" rx="2" />
      <path d="M3.5 9.5h17M8 3v3.5M16 3v3.5" strokeLinecap="round" />
    </svg>
  );
}
