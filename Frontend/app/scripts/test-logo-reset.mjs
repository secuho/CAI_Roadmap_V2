import { chromium } from "playwright";
import { readFileSync } from "node:fs";

const accessToken = readFileSync(process.env.CAI_ACCESS_FILE, "utf-8").trim();
const refreshToken = readFileSync(process.env.CAI_REFRESH_FILE, "utf-8").trim();

const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 1400, height: 900 } });
page.on("pageerror", (err) => console.log("PAGE ERROR:", String(err)));

await page.addInitScript(
  ({ accessToken, refreshToken }) => {
    localStorage.setItem("cai_access_token", accessToken);
    localStorage.setItem("cai_refresh_token", refreshToken);
  },
  { accessToken, refreshToken }
);

await page.goto("http://localhost:5173/", { waitUntil: "networkidle" });
await page.getByRole("button", { name: "나의 학점" }).click();
await page.waitForTimeout(300);
console.log("URL after clicking 나의 학점:", page.url());

await page.locator('a[href="/"] img').click();
await page.waitForTimeout(300);
console.log("URL after clicking logo:", page.url());
const activeTab = await page.getByRole("button", { name: "추천 강의" }).getAttribute("class");
console.log("추천 강의 button classes:", activeTab);

await browser.close();
