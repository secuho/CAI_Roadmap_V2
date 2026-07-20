import { chromium } from "playwright";
import { readFileSync } from "node:fs";

const accessToken = readFileSync(process.env.CAI_ACCESS_FILE, "utf-8").trim();
const refreshToken = readFileSync(process.env.CAI_REFRESH_FILE, "utf-8").trim();

const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 390, height: 800 } });
await page.addInitScript(
  ({ accessToken, refreshToken }) => {
    localStorage.setItem("cai_access_token", accessToken);
    localStorage.setItem("cai_refresh_token", refreshToken);
  },
  { accessToken, refreshToken }
);
await page.goto("http://localhost:5173/", { waitUntil: "domcontentloaded", timeout: 15000 });
await page.waitForTimeout(1500);
await page.screenshot({ path: "scripts/dashboard-mobile.png" });

// also test scroll behavior for sticky checklist overlap on a taller tab
await page.setViewportSize({ width: 1400, height: 900 });
await page.goto("http://localhost:5173/?tab=courses", { waitUntil: "domcontentloaded", timeout: 15000 });
await page.waitForTimeout(1000);
await page.mouse.wheel(0, 600);
await page.waitForTimeout(300);
await page.screenshot({ path: "scripts/dashboard-scrolled.png" });

await browser.close();
console.log("done");
