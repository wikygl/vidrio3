# Tienda de anuncios del rubro — plan

Dictado por el usuario el 2026-10-12. Una tienda dentro de Crystal donde **cualquiera** publica
anuncios del rubro (vidrio, aluminio, melamina, accesorios, herramientas, retazos… y servicios), y
cada anuncio **se paga con el saldo del wallet**, como un adicional prepago a los planes.

**Por qué:** con Claude Code, Codex y compañía cualquiera clona una app; lo que no se clona es la
red de vidrieros, proveedores y compradores que ya están dentro. La tienda es un ingreso que crece
con esa red.

---

## Decisiones

| Tema | Decisión | Por qué |
|---|---|---|
| Quién publica | Cualquier cuenta de Crystal, con plan o sin plan | Lo pidió el usuario: "todo aquel que quiera publicar". |
| Terminales | **No** publican: lo hace la cuenta del patrón | El cobro sale del wallet de quien llama; una terminal no tiene saldo propio y el chat de contacto llegaría a una bandeja que nadie mira. |
| Qué se publica | Productos (nuevos o usados) y servicios | Los servicios llevan la marca "Servicio" y no tienen estado nuevo/usado. El "trato diferente" de los servicios (por zona, por hora) queda para la fase 2. |
| Quién lo ve | Solo usuarios de Crystal con sesión | Lo pidió el usuario: por ahora, solo desde Crystal. |
| Precio | **S/ 3.00 por 30 días**, cambiable sin desplegar en `config/anuncios` (`precio_cent`, `dias`) | El costo real de un anuncio en Firebase es de S/ 0.02 a 0.05 (ver "Costos"). |
| Cobro | Cloud Function `publicarAnuncio` descuenta del wallet en una transacción y deja el movimiento en `wallet_movs` | Mismo patrón que `activarPlan`: el cliente nunca decide precio ni vencimiento. |
| Boleta | **No** se emite al publicar | La boleta ya se emitió al recargar el saldo (`encolarComprobante` en la acreditación). Gastar saldo no es una venta nueva. |
| Build de Play | Se ve la tienda y se contacta, pero **no se publica** dentro de la app | Play exige Play Billing para lo digital; es la misma regla por la que esa build no vende planes (`CanalPagos.DISPONIBLE_EN_LA_APP`). |
| Contacto | Por el **chat de Crystal**, con el mensaje ya escrito | Reutiliza `ChatConversationRepository.getOrCreateDirectChat`; no se exponen teléfonos. |
| Fotos | Hasta 5; cada una se sube en dos tamaños: 1280 px (~150 KB) y miniatura de 400 px (~25 KB) | La salida de datos de las fotos es lo único caro; la lista solo baja miniaturas. |
| Renovar | Cobra de nuevo y suma los días desde el vencimiento si sigue vigente | Mismo criterio que extender un plan: no se pierden días. |
| Moderación | Botón **Denunciar** (una vez por usuario). A las 3 denuncias el anuncio pasa a `en_revision` y se avisa al panel (`admins_soporte`) | Revisión manual al principio; la automática cuesta y aún no hace falta. |
| Reglas de la tienda | Se aceptan antes del primer anuncio; queda guardado en cada anuncio (`reglasVersion`) | Si publica cualquiera, hay que poder retirar lo engañoso o prohibido sin devolución. |

---

## Datos

`anuncios/{id}` (lo crea solo el servidor):

| Campo | Tipo | Nota |
|---|---|---|
| `uid`, `vendedorNombre` | string | El vendedor; el nombre se copia del perfil al publicar. |
| `tipo` | `producto` \| `servicio` | |
| `categoria` | string | Una de la lista fija (`CategoriasAnuncio`). |
| `titulo`, `descripcion` | string | 80 y 1500 caracteres como máximo. |
| `precioCent` | number | 0 = "a tratar". |
| `unidad` | string | c/u, m², ml, plancha, kg, servicio, hora… |
| `condicion` | `nuevo` \| `usado` \| "" | Vacío en servicios. |
| `ciudad` | string | Donde está el producto o donde se atiende el servicio. |
| `fotos`, `miniaturas` | list<string> | URLs de descarga, en el mismo orden. |
| `palabras` | list<string> | Palabras normalizadas para buscar (`array-contains`). |
| `estado` | `activo` \| `pausado` \| `vencido` \| `retirado` \| `en_revision` \| `bloqueado` | |
| `publicadoEn`, `venceEn` | timestamp | Los pone el servidor. |
| `vistas`, `denuncias` | number | `vistas` sube de uno en uno desde la app; `denuncias` la cuenta el servidor. |
| `pagadoCent`, `dias`, `reglasVersion` | number | Lo cobrado y lo aceptado. |

`anuncios/{id}/denuncias/{uid}`: una por usuario, con `motivo` y `detalle`.

Fotos en Storage: `anuncios/{uid}/{anuncioId}/{n}.jpg` y `{n}_mini.jpg`.

**El dueño puede editar** el texto, el precio, las fotos, la categoría y la ciudad, y pasar entre
`activo`, `pausado` y `retirado`. **No puede** tocar `venceEn`, `uid`, `pagadoCent`, `denuncias` ni
reactivar lo `vencido`, `en_revision` o `bloqueado`: eso es del servidor y del panel.

---

## Fase 1 — hecha el 2026-10-12 (commit bc3f71a)

1. **Servidor** (`functions/index.js`)
   - `publicarAnuncio`: valida, cobra, crea el anuncio con su vencimiento y su movimiento de wallet.
   - `renovarAnuncio`: cobra y extiende.
   - `expirarAnuncios` (diario): pasa a `vencido` lo activo con `venceEn` pasado.
   - `contarDenunciaAnuncio` (trigger): suma la denuncia; a la 3.ª pasa a `en_revision` y avisa al panel.
2. **Reglas** de `anuncios` y `denuncias`, e **índices** para la lista por fecha, por categoría, por
   palabra y "mis anuncios".
3. **App** (`crystal.crystal.tienda`)
   - Botón **Tienda** en la cabecera de la pantalla principal, junto a Catálogo.
   - Lista en dos columnas con miniatura, precio y ciudad; buscador, filtro por categoría y por
     producto/servicio; "Mis anuncios" con su estado y vencimiento.
   - Detalle con fotos, descripción, vendedor, **Contactar por chat** y **Denunciar**.
   - Publicar/editar: tipo, categoría, título, precio (o a tratar), unidad, estado, ciudad,
     descripción y hasta 5 fotos; muestra el precio y el saldo antes de cobrar; si no alcanza,
     lleva a Recargar.
   - El dueño: editar, pausar/reactivar, renovar y retirar.
4. **Pruebas**: unitarias de la parte pura (palabras de búsqueda, formato, validación) y prueba en
   frío del cobro en el emulador de Firestore antes de desplegar.

## Fases 2 y 3 — plan de implementación (2026-10-12)

Todo sigue el mismo patrón de la fase 1: **el servidor cobra** del wallet en una transacción, deja el
movimiento en `wallet_movs` y no emite boleta (ya salió al recargar). Los precios viven en
`config/anuncios` con un valor por defecto y un tope, como `precio_cent`. La build de Play ve todo,
pero no paga nada dentro de la app (`CanalPagos.DISPONIBLE_EN_LA_APP`). Las terminales no pagan: lo
hace la cuenta del patrón.

### Tarifas nuevas (`config/anuncios`)

| Campo | Por defecto | Tope | Qué es |
|---|---|---|---|
| `destacar_precio_cent`, `destacar_dias` | S/ 2.00, 7 días | S/ 50, 60 días | Destacar un anuncio |
| `servicio_precio_cent`, `servicio_dias` | las del producto | S/ 50, 365 días | Publicar/renovar un servicio, si se quiere otra tarifa |
| `proveedor_precio_cent`, `proveedor_dias` | S/ 10.00, 30 días | S/ 200, 365 días | Vitrina de proveedores |
| `pedido_precio_cent` | S/ 1.00 | S/ 20 | Tomar un pedido de un cliente de la web |
| `pedido_max_talleres` | 3 | 10 | Cuántos talleres pueden tomar el mismo pedido |
| `web_publica` | true | — | `false` apaga la web pública sin desplegar |

### Fase 2

**1. Destacar** (`destacarAnuncio`). Un anuncio activo y vigente sale primero, con la marca
"Destacado", durante `destacar_dias`. Suma desde el destacado vigente, si lo hay. **No se puede
destacar más allá del vencimiento del anuncio**: si vence antes, se pide renovar primero (cobrar 7
días y dar 2 sería un robo). Movimiento `DESTACA_ANUNCIO`. En la lista: la primera página pide aparte
hasta 10 destacados vigentes (`estado`, `destacadoHasta`), los filtra con lo elegido y los pone
arriba sin repetirlos abajo.

**2. Servicios con trato propio.** Un servicio lleva **zonas de atención** (`zonas`, hasta 5,
además de la ciudad): entran en las palabras de búsqueda, así que buscar "Chilca" encuentra a quien
atiende Chilca. Sus unidades son las de un servicio (servicio, hora, día, m², ml, pie²). El botón
del comprador dice **Pedir cotización** y el mensaje ya escrito pide cotizar en la ciudad del
comprador. Tarifa propia opcional (`servicio_precio_cent`).

**3. Vitrina de proveedores** (`suscribirProveedor`, `guardarProveedor`). Suscripción por 30 días,
cobrada del saldo; renovar suma desde el vencimiento. Ficha en `proveedores/{uid}`: nombre
comercial, ciudad, zonas, rubros (las categorías de la tienda), descripción y logo. **Solo el
servidor escribe** la ficha (valida y arma `zonasClave`), para que nadie se ponga en todas las
ciudades. Dónde aparecen:
- **Al pedir materiales**: cuando se abre el chat para enviar medidas, una lista de cortes, un
  plano de planchas o un proyecto (`ListChatActivity` con algo que enviar), arriba sale
  "Proveedores en {ciudad}"; tocar uno abre (o crea) el chat con él y le pasa lo que se iba a enviar.
  Primero los que venden el rubro de lo enviado (cortes → aluminio; planchas → vidrio y melamina).
- **En la Tienda**, la sección Proveedores: los de la ciudad del taller (o todos si no hay), con su
  ficha, sus anuncios activos y "Escribir".
- La ciudad del taller se pregunta una vez ("Mi ciudad") y se guarda en el teléfono.

**4. Moderación en crystalAdmin** (`moderarAnuncio`, `moderarProveedor`, `resolverPedidoCliente`).
Una pantalla Tienda en el panel con tres listas: anuncios **en revisión**, anuncios **con
denuncias** aún activos y **pedidos reportados**. Cada anuncio muestra fotos, texto, vendedor y las
denuncias con su motivo. Acciones: **Reactivar** (desestima y pone las denuncias en 0; si ya venció,
queda vencido), **Bloquear** y **Retirar sin devolución**. Cada decisión queda en
`anuncios/{id}/moderacion` y al vendedor le llega el motivo por el chat de **Soporte Crystal** (el
mismo canal de `enviarMensajeAdmin`). El aviso 🚩 del panel abre ese anuncio.

**5. Endurecer Storage — PENDIENTE.** Las reglas de Storage no están en el repositorio y no se
pudieron leer las publicadas desde esta sesión (permiso denegado). Sin conocerlas, publicar otras
podría romper los vouchers, los archivos del chat o las fotos de perfil. Para hacerlo: bajar las
reglas vigentes de la consola de Firebase (Storage → Reglas), guardarlas en `storage.rules`,
añadir `"storage": {"rules": "storage.rules"}` a `firebase.json`, agregar
`match /anuncios/{uid}/{todo=**}` y `match /proveedores/{uid}/{todo=**}` con escritura solo si
`request.auth.uid == uid`, imagen y < 2 MB, probar en el emulador y desplegar
`firebase deploy --only storage`.

### Fase 3

**1. Fotos a Cloudflare R2 — NO se hace todavía.** Es "cuando la salida pase de ~100 GB/mes" y
necesita una cuenta de Cloudflare con su tarjeta. Queda preparado: todas las subidas pasan por
`TiendaRepositorio.subirFoto` y las URLs se validan en un solo lugar (`fotoValida` en el servidor y
`fotoPropia` en las reglas). Para cambiar: un bucket R2 con dominio propio, una función que firme
subidas (URL prefirmada por anuncio), aceptar ese dominio en `fotoValida`/`fotoPropia` y cambiar
`subirFoto`. Las fotos viejas pueden quedarse en Firebase.

**2. Web pública** en `crystal-4f306.web.app/tienda` (misma hosting que la web de recarga).
- La lee una función HTTP (`tiendaWeb`, por `/api/tienda/**`), **no** las reglas: así solo salen los
  campos públicos (nada de `uid`, lo pagado ni las denuncias) y la respuesta se guarda en la CDN de
  Hosting 5 minutos, que es lo que la hace barata.
- Lista con buscador y categorías, detalle con fotos. Como el visitante no tiene Crystal, el
  contacto es **"Quiero que me contacten"**: deja nombre y teléfono, y el vendedor recibe un pedido
  dirigido, **gratis** (ya pagó su anuncio), con aviso en el teléfono.
- Apagable con `config/anuncios.web_publica = false`. Las reglas de la tienda pasan a la versión 2
  con el punto 7 ("los anuncios activos también se ven en la web"); el servidor acepta la 1 y la 2,
  para no romper las apps ya instaladas.

**3. Pedidos de clientes finales a talleres cercanos.**
- En la web, **"Pida cotización a talleres de su ciudad"**: qué necesita (ventana, puerta, mampara,
  vidrio o espejo, ropero, reja, otro), ciudad, medidas aproximadas, detalle, nombre y teléfono.
  Lo recibe `tiendaWeb` con trampa para robots, tope por IP (3 por hora, 10 por día) y por teléfono
  (3 abiertos).
- `pedidos_clientes/{id}` guarda lo público; el nombre completo y el teléfono van aparte
  (`privado/contacto`), que ninguna app lee.
- Los talleres de esa ciudad reciben un aviso (tema FCM `pedidos_{ciudad}`, al que la app se
  suscribe cuando se fija "Mi ciudad") y lo ven en la Tienda → **Pedidos de clientes**.
- **Tomar el pedido** (`tomarPedidoCliente`) cobra `pedido_precio_cent` y devuelve el contacto, que
  queda en `pedidos_clientes/{id}/tomas/{uid}` (solo lo lee quien lo tomó). Hasta
  `pedido_max_talleres`; el último que entra lo cierra. Movimiento `TOMA_PEDIDO`. Llamar o WhatsApp
  desde la misma pantalla.
- **Reportar pedido falso**: avisa al panel; el panel puede **devolver lo cobrado a todos los que lo
  tomaron y bloquearlo** (`DEVUELVE_PEDIDO`).
- Los pedidos vencen a los 15 días (`expirarAnuncios` también los cierra).

**4. Precios de referencia por ciudad** (`preciosReferencia`). Para quien tiene **plan FULL**
vigente: se escribe qué se busca ("templado 8") y la ciudad; el servidor junta los anuncios de los
últimos 180 días que casan (no los bloqueados ni en revisión, solo con precio), por unidad, y
devuelve mínimo, mediana y máximo. **Solo con 3 vendedores distintos o más** por grupo, para que
nadie pueda deducir el precio de un competidor. Si en la ciudad no alcanza, muestra el del país.
Sin plan FULL, la invitación de siempre (`Suscripcion.invitarFull`).

### Pruebas y despliegue

- Servidor: pruebas `node --test` de lo puro (precios, validaciones de ficha y pedido, destacado,
  estadística con el mínimo de vendedores) y prueba en frío en el emulador de cada cobro antes de
  desplegar (saldo justo, doble toque, tope de talleres a la vez).
- App: pruebas unitarias de lo puro (palabras con zonas idénticas al servidor, clave de ciudad, tema
  FCM) y las 646 de siempre en verde.
- Desplegar **por nombre** cada función nueva, más reglas, índices y hosting.

---

## Costos (aprox., plan Blaze, 1 US$ ≈ S/ 3.75)

| Escenario | Usuarios | Anuncios | Al mes |
|---|---|---|---|
| Arranque | 500 | 300 | S/ 0 – 10 |
| Crece | 5 000 | 3 000 | ~S/ 500 con Firebase Storage; < S/ 5 en fotos con R2 |

Por anuncio: S/ 0.02 – 0.05. Lo caro es la **salida** de las fotos (~US$ 0.12/GB pasados los
~100 GB gratis); por eso las miniaturas.

## Reglas de la tienda (versión 2)

Se muestran antes del primer anuncio y se aceptan una vez (la versión 2 suma el punto 7; quien
aceptó la 1 la vuelve a ver al publicar otra vez):

1. Solo productos y servicios del rubro: vidrio, aluminio, melamina, carpintería metálica,
   accesorios, herramientas, maquinaria, retazos e instalación.
2. Nada robado, falsificado, ilegal, ni anuncios engañosos (precio o fotos que no son del producto).
3. Fotos propias o con permiso; nada de fotos con la marca de otra empresa.
4. Un anuncio por producto; no repetir el mismo anuncio.
5. El trato, el pago y la entrega son entre comprador y vendedor; Crystal no interviene en la venta.
6. Un anuncio que incumpla puede retirarse **sin devolución**.
7. Los anuncios activos también se ven en la web pública de Crystal, sin datos de contacto.
