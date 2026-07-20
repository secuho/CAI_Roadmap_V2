export const CATEGORY_ORDER = ["전공", "공통교양", "일반교양", "기타"] as const;

export function categoryLabel(cpdvNm: string | null): (typeof CATEGORY_ORDER)[number] {
  switch (cpdvNm) {
    case "전공":
      return "전공";
    case "공교":
      return "공통교양";
    case "일교":
      return "일반교양";
    default:
      return "기타";
  }
}

export function groupBy<T>(items: T[], keyFn: (item: T) => string): Record<string, T[]> {
  return items.reduce<Record<string, T[]>>((acc, item) => {
    const key = keyFn(item);
    (acc[key] ??= []).push(item);
    return acc;
  }, {});
}
