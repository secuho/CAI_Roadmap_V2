const SLIDES = ["/images/bg1.jpg", "/images/bg2.jpg", "/images/bg3.jpg"];

/**
 * Full-bleed campus photography, crossfading slowly behind a warm scrim.
 * The photos carry the warmth the brand actually has — no invented backdrop needed.
 */
export function BackgroundSlideshow() {
  return (
    <div className="absolute inset-0 overflow-hidden bg-ink">
      {SLIDES.map((src, i) => (
        <div
          key={src}
          className="bg-slide absolute inset-0 bg-cover bg-center opacity-0"
          style={{ backgroundImage: `url(${src})`, animationDelay: `${i * 6}s` }}
        />
      ))}
      <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-black/35 to-black/20" />
    </div>
  );
}
