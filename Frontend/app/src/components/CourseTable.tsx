export interface CourseRow {
  code: string;
  name: string;
  professor?: string | null;
  credit: number | null;
  extra?: { label: string; value: string | number | null }[];
}

export function CourseTable({ title, rows }: { title: string; rows: CourseRow[] }) {
  const extraLabels = rows[0]?.extra?.map((e) => e.label) ?? [];

  return (
    <div>
      <h4 className="mb-2 font-semibold text-flame">{title}</h4>
      <div className="overflow-x-auto rounded-xl border border-ink/5">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-cream-2 text-left text-ink-soft">
              <th className="p-2.5 font-medium">과목코드</th>
              <th className="p-2.5 font-medium">과목명</th>
              {rows[0]?.professor !== undefined && <th className="p-2.5 font-medium">교수</th>}
              <th className="p-2.5 font-medium">학점</th>
              {extraLabels.map((label) => (
                <th key={label} className="p-2.5 font-medium">
                  {label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.map((row, i) => (
              <tr key={i} className="border-t border-ink/5">
                <td className="p-2.5">{row.code || "-"}</td>
                <td className="p-2.5 font-medium text-ink">{row.name || "-"}</td>
                {row.professor !== undefined && <td className="p-2.5">{row.professor || "-"}</td>}
                <td className="p-2.5">{row.credit ?? "-"}</td>
                {row.extra?.map((e) => (
                  <td key={e.label} className="p-2.5">
                    {e.value ?? "-"}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
