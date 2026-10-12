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

## Fase 1 — lo que se implementa ahora

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

## Fase 2 — después

- **Destacar** un anuncio (sale primero unos días) por un pago chico: `destacadoHasta`.
- **Servicios con trato propio**: zonas de atención y precio por hora/m²; quizá otra tarifa.
- **Vitrina de proveedores** (suscripción mensual): aparecen cuando un taller pide materiales
  (`materials_request` del chat) en su ciudad.
- **Pantalla de moderación en crystalAdmin**: denuncias, bloquear, desbloquear.
- **Endurecer Storage**: hoy cualquier sesión puede escribir en cualquier ruta salvo `vouchers/`;
  limitar `anuncios/{uid}/**` a su dueño (son reglas que están fuera del repositorio).

## Fase 3 — si crece

- Fotos a **Cloudflare R2** (sin costo de salida) cuando la salida pase de ~100 GB/mes.
- **Web pública** de la tienda y pedidos de clientes finales a talleres cercanos.
- Precios de referencia por ciudad (anónimos) como servicio premium.

---

## Costos (aprox., plan Blaze, 1 US$ ≈ S/ 3.75)

| Escenario | Usuarios | Anuncios | Al mes |
|---|---|---|---|
| Arranque | 500 | 300 | S/ 0 – 10 |
| Crece | 5 000 | 3 000 | ~S/ 500 con Firebase Storage; < S/ 5 en fotos con R2 |

Por anuncio: S/ 0.02 – 0.05. Lo caro es la **salida** de las fotos (~US$ 0.12/GB pasados los
~100 GB gratis); por eso las miniaturas.

## Reglas de la tienda (versión 1)

Se muestran antes del primer anuncio y se aceptan una vez:

1. Solo productos y servicios del rubro: vidrio, aluminio, melamina, carpintería metálica,
   accesorios, herramientas, maquinaria, retazos e instalación.
2. Nada robado, falsificado, ilegal, ni anuncios engañosos (precio o fotos que no son del producto).
3. Fotos propias o con permiso; nada de fotos con la marca de otra empresa.
4. Un anuncio por producto; no repetir el mismo anuncio.
5. El trato, el pago y la entrega son entre comprador y vendedor; Crystal no interviene en la venta.
6. Un anuncio que incumpla puede retirarse **sin devolución**.
