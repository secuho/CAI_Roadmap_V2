import { chromium } from "playwright";
import { readFileSync } from "node:fs";

const url = process.argv[2] ?? "http://localhost:5173/";
const outPath = process.argv[3] ?? "screenshot.png";

const accessToken = readFileSync(process.env.CAI_ACCESS_FILE, "utf-8").trim();
const refreshToken = readFileSync(process.env.CAI_REFRESH_FILE, "utf-8").trim();

const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 1400, height: 900 } });
const errors = [];
page.on("console", (msg) => { if (msg.type() === "error") errors.push(msg.text()); });
page.on("pageerror", (err) => errors.push(String(err)));
page.on("requestfailed", (req) => errors.push("REQFAIL: " + req.url() + " " + req.failure()?.errorText));

await page.addInitScript(
  ({ accessToken, refreshToken }) => {
    localStorage.setItem("cai_access_token", accessToken);
    localStorage.setItem("cai_refresh_token", refreshToken);
  },
  { accessToken, refreshToken }
);

await page.goto(url, { waitUntil: "domcontentloaded", timeout: 15000 });
await page.waitForTimeout(2000);
await page.screenshot({ path: outPath, fullPage: true });
console.log("URL:", page.url());
console.log("Saved:", outPath);
console.log(errors.length ? "Errors:\n" + errors.join("\n") : "No errors.");
await browser.close();
