import { chromium } from "playwright";

const url = process.argv[2] ?? "http://localhost:5173/login";
const outPath = process.argv[3] ?? "screenshot.png";

const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
const errors = [];
page.on("console", (msg) => {
  if (msg.type() === "error") errors.push(msg.text());
});
page.on("pageerror", (err) => errors.push(String(err)));

await page.goto(url, { waitUntil: "networkidle" });
await page.waitForTimeout(300);
await page.screenshot({ path: outPath, fullPage: true });
await browser.close();

console.log("Saved:", outPath);
if (errors.length) {
  console.log("Console errors:");
  errors.forEach((e) => console.log(" -", e));
} else {
  console.log("No console errors.");
}
