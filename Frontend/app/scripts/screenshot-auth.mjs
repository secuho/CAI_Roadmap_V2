import { chromium } from "playwright";
import { readFileSync } from "node:fs";

const url = process.argv[2] ?? "http://localhost:5173/";
const outPath = process.argv[3] ?? "screenshot.png";

const accessToken = readFileSync(process.env.CAI_ACCESS_FILE ?? "/tmp/fe_access.txt", "utf-8").trim();
const refreshToken = readFileSync(process.env.CAI_REFRESH_FILE ?? "/tmp/fe_refresh.txt", "utf-8").trim();

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

await page.goto(url, { waitUntil: "networkidle" });
await page.waitForTimeout(600);
await page.screenshot({ path: outPath, fullPage: true });
await browser.close();

console.log("Saved:", outPath);
if (errors.length) {
  console.log("Console errors:");
  errors.forEach((e) => console.log(" -", e));
} else {
  console.log("No console errors.");
}
