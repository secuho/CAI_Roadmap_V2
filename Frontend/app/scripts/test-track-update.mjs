import { chromium } from "playwright";
import { readFileSync } from "node:fs";

const accessToken = readFileSync(process.env.CAI_ACCESS_FILE, "utf-8").trim();
const refreshToken = readFileSync(process.env.CAI_REFRESH_FILE, "utf-8").trim();

const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 1000, height: 800 } });
page.on("pageerror", (err) => console.log("PAGE ERROR:", String(err)));

await page.addInitScript(
  ({ accessToken, refreshToken }) => {
    localStorage.setItem("cai_access_token", accessToken);
    localStorage.setItem("cai_refresh_token", refreshToken);
  },
  { accessToken, refreshToken }
);

await page.goto("http://localhost:5173/mypage", { waitUntil: "networkidle" });
await page.getByRole("button", { name: "수정" }).click();
await page.locator("select").selectOption("게임트랙");
await page.getByRole("button", { name: "수정 완료" }).click();
await page.waitForTimeout(800);
const trackText = await page.locator("text=선택한 트랙").locator("..").locator("p").nth(1).textContent();
console.log("Track after update:", trackText);
await page.screenshot({ path: process.argv[2] ?? "track-updated.png" });

await browser.close();
