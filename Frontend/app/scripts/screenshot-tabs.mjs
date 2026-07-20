import { chromium } from "playwright";
import { readFileSync } from "node:fs";

const accessToken = readFileSync(process.env.CAI_ACCESS_FILE, "utf-8").trim();
const refreshToken = readFileSync(process.env.CAI_REFRESH_FILE, "utf-8").trim();
const outDir = process.argv[2] ?? ".";

const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 1400, height: 1000 } });
const errors = [];
page.on("console", (msg) => {
  if (msg.type() === "error") errors.push(msg.text());
});
page.on("pageerror", (err) => errors.push(String(err)));

await page.addInitScript(
  ({ accessToken, refreshToken }) => {
    localStorage.setItem("cai_access_token", accessToken);
    localStorage.setItem("cai_refresh_token", refreshToken);
  },
  { accessToken, refreshToken }
);

await page.goto("http://localhost:5173/", { waitUntil: "networkidle" });
await page.waitForTimeout(400);

const tabs = ["이수체계도", "현재 수강 강의", "수강 완료 강의", "나의 학점", "학사일정"];
for (const tab of tabs) {
  await page.getByRole("button", { name: tab, exact: true }).click();
  await page.waitForTimeout(500);
  await page.screenshot({ path: `${outDir}/tab-${tab}.png`, fullPage: true });
}

await page.goto("http://localhost:5173/mypage", { waitUntil: "networkidle" });
await page.waitForTimeout(500);
await page.screenshot({ path: `${outDir}/mypage.png`, fullPage: true });

await page.goto("http://localhost:5173/signup", { waitUntil: "networkidle" });
await page.waitForTimeout(300);
await page.screenshot({ path: `${outDir}/signup.png`, fullPage: true });

await browser.close();
console.log("Done.");
if (errors.length) {
  console.log("Console errors:");
  errors.forEach((e) => console.log(" -", e));
} else {
  console.log("No console errors across all pages.");
}
