// Service worker de Crystal Recargas.
//
// Estrategia: RED PRIMERO, siempre. Esta página maneja dinero —montos con firma de céntimos,
// números de destino, estado de una acreditación— y servir una versión guardada sería mostrarle al
// usuario un monto viejo. La copia en caché existe solo para que la app abra sin conexión y pueda
// decir que no hay red, no para ahorrar peticiones.

// Subir el número purga lo guardado por la versión anterior al activarse.
const CACHE = "crystal-recargas-v2";
const ESENCIALES = ["/", "/icon-192.png"];

self.addEventListener("install", (e) => {
  e.waitUntil(
    caches.open(CACHE)
      .then((c) => c.addAll(ESENCIALES))
      .catch(() => {})
      .then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (e) => {
  e.waitUntil(
    caches.keys()
      .then((ks) => Promise.all(ks.filter((k) => k !== CACHE).map((k) => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

self.addEventListener("fetch", (e) => {
  const req = e.request;
  if (req.method !== "GET") return;

  // Firebase, Firestore y el SDK de gstatic van SIEMPRE a la red, sin pasar por aquí: interceptar
  // sus respuestas rompería los listeners en vivo y la autenticación.
  const url = new URL(req.url);
  if (url.origin !== self.location.origin) return;

  // "Red primero" no basta por sí solo: esa red pasa por la caché del navegador, y con una
  // cabecera de caducidad la copia vieja responde sin llegar al servidor. Para la página en sí se
  // fuerza a saltarse esa caché, que es lo que deja al usuario mirando una versión que ya no existe.
  const esPagina = req.mode === "navigate" || url.pathname === "/" || url.pathname.endsWith(".html");
  const alaRed = esPagina
    ? fetch(req.url, { cache: "reload", credentials: "same-origin" })
    : fetch(req);

  e.respondWith(
    alaRed
      .then((res) => {
        const copia = res.clone();
        caches.open(CACHE).then((c) => c.put(req, copia)).catch(() => {});
        return res;
      })
      .catch(() => caches.match(req).then((r) => r || caches.match("/")))
  );
});
