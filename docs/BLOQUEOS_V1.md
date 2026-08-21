# Funciones bloqueadas en la v1

Registro de funcionalidades que se **ocultan o bloquean** para la primera versión (v1), sin
eliminar código. Todas se controlan con interruptores en `app/src/main/java/crystal/crystal/FeaturesV1.kt`
(poner el flag en `false` las reactiva).

## 1. Crystal Ventas / Patrón / Terminales — DESBLOQUEADO (2026-07-24)
- **Flag:** `FeaturesV1.OCULTAR_VENTAS = false` (antes `true`).
- **Estado:** ABIERTO. El usuario decidió mostrar Ventas/Patrón/Terminales (los proveedores/ventas
  son ventaja competitiva). Wallet ya estaba abierto por su flag propio `OCULTAR_WALLET = false`.
- **Dónde se usa el flag (código actual):**
  - `MainActivity`: con `false`, `btnProcesarVenta`, `btnGestionProductos`, `btnGestionClientes` quedan
    visibles y funcionales (handlers en `PosManager.configurarBotonesPOS()`); `btUser`/`txUser` conservan
    su navegación. `btWallet` depende de `OCULTAR_WALLET`.
  - `TerminalLoginActivity` y `Gestiondispositivosactivity`: con `false` abren normal (el guard de
    `onCreate` solo cerraba cuando el flag era `true`).
- **Detalle:** ver memoria `project_ocultar_ventas_v1`.

## 2. Taller — enviar presupuesto a las calculadoras
- **Flag:** `FeaturesV1.BLOQUEAR_TALLER_PRESUPUESTO`
- **Qué se bloquea:** al entrar a `Taller` con un presupuesto (lista de ítems desde `MainActivity`),
  el flujo que enruta cada ítem a su calculadora (color → vidrio → calculadora) está **incompleto**.
- **Dónde:** `taller/Taller.kt`:
  - `onCreate`: en vez de mostrar el card y procesar, llama a `guardarPresupuestoBloqueado(...)`,
    que **guarda en silencio** (sin diálogo) y deja entrar directo a la actividad.
  - `iniciarProcesamientoAcumulado()`: retorna con aviso si el flag está activo (solo se alcanza por
    acción explícita, p. ej. desde el Baúl).
- **Qué se guarda para ver después:** el presupuesto entrante (cliente, fecha, ítems: producto,
  cantidad, escala) se serializa a JSON en el almacenamiento interno de la app:
  `filesDir/taller_pendientes/pendiente_<fecha>.json`. Así no se pierde y se puede revisar/retomar
  cuando se complete la función.

---

### Cómo reactivar al completar
1. Poner el flag correspondiente en `FeaturesV1` en `false`.
2. Para el Taller, además, revisar/usar los pendientes guardados en `taller_pendientes/`.
3. Quitar este bloqueo de la lista cuando la función quede terminada y probada.
