# Reglas de Nova corrediza

Extraído del código (`app/src/main/java/crystal/crystal/taller/nova/`) para tener las reglas en un
solo sitio y dejar de deducirlas en cada cambio.

**Cómo leerlo:**

- ✔ = leído directamente en el código; es lo que la app hace hoy.
- ❓ = no está claro si lo que hace es lo correcto, o falta el dato. **Estas son las que hay que
  corregir primero.**

Que algo esté marcado ✔ significa que *así está programado*, no que esté bien. Si una regla ✔ está
equivocada, corregirla aquí y luego en el código.

---

## 1. Los tres ejes

Son **independientes** y se confunden con facilidad porque los tres se guardan como cadenas
parecidas. Pasar uno donde se espera otro hace que una rama entera no se ejecute nunca, sin error
visible. ✔

| Eje | Variable | Valores | Qué decide |
|---|---|---|---|
| **Acabado** | `tipoNova` | `APA`, `INA`, `PIV` | Aparente / inaparente / pivotante |
| **Modulación** | `texto` / `textoModelo` | `nn`, `nfc`, `nff`, `ncfc`, `ncc`, `n3c`, `nl`, `nu`, `ns`, `ncu`, `nci` | El patrón de hojas y la geometría |
| **Remate** | `modeloRemate` | `nn`, `nr`, `np` | Las mochetas (normal, invertido, doble puente) |

Varias funciones reciben los dos últimos por separado: `modelo` = modulación, `remate` = mochetas.

En el eje de modulación se mezclan dos cosas distintas: el **patrón de hojas** (`nn`, `nfc`, `nff`,
`ncfc`, `ncc`, `n3c`) y la **geometría** (`nl` en L, `nu` en C, `ns` serie, `ncu` curvo, `nci`
circular). ❓ *¿Deberían ser dos ejes separados?*

---

## 2. Formato de números

- `df1` **trunca** a un decimal, no redondea: `floor(x * 10) / 10`. 119.99 → 119.9. ✔
- Si el resultado es entero se muestra sin decimales: 120.0 → `120`. ✔
- Las líneas de material son siempre `medida = cantidad`, una por línea. ✔
- Material con un decimal; dinero con dos. (Regla general de la app.)

---

## 3. Divisiones (cuántas hojas)

**Regla de los 60:** `divisionesAuto = ceil(ancho / 60)`, mínimo 1. Un ancho de 120 da 2; 120.1 da
3. ✔

`divisiones(ancho, divisManual, tipo)`: ✔

- Si `divisManual > 0`, **manda ese valor** y el ancho no participa.
- Si es 0, se aplica la regla de los 60.
- `tipo` conocido: `nn`, `nl`, `nff`, `nfc` → automático o manual; `ncc` → 2; `n3c` y `ncfc` → 3.
- **Trampa:** cualquier otro tipo devuelve **0**. Por eso no se le puede pasar `textoModelo` a
  ciegas: `nu`, `ns`, `ncu` y `nci` darían 0 divisiones. Hoy `NovaCorrediza` nunca le pasa el tipo.
  ❓ *¿Debería el `else` devolver el automático en vez de 0?*

**En L y en C:** la cantidad de hojas sale de la regla de los 60 sobre la **medida que se midió**,
no sobre el ancho útil. El descuento de esquina cambia las medidas de corte, no cuántas hojas lleva
la ventana. Si corresponden menos, se escriben a mano. Lo fija `conDivisionesDeLaMedidaReal`. ✔
*(cambiado el 2026-09-07; antes se contaba sobre el útil)*

**Desde el diseño:** al volver de novaDiseño, el diseño escribe la cantidad en el campo
(`etPartes`), así que pasa a ser un valor manual y manda sobre todo lo demás. ✔

**Límite:** full corredizas inaparente admite máximo 6 divisiones. ✔

---

## 4. Fijos y corredizas

El patrón clásico (`nn`, `nl`) es una tabla fija por número de divisiones: ✔

| div | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| fijos | 1 | 1 | 2 | 2 | 3 | 4 | 4 | 4 | 5 | 6 | 6 | 6 | 7 | 8 | 8 |
| corredizas | 0 | 1 | 1 | 2 | 2 | 2 | 3 | 4 | 4 | 4 | 5 | 6 | 6 | 6 | 7 |

Por encima de 15 se calcula: corredizas = `div/2` (menos 1 si `div ≥ 6` y `div % 4 == 2`). ✔

El orden de las hojas sale de `ordenDivis`: 2 → `fc`, 3 → `fcf`, 4 → `fccf`, 5 → `fcfcf`,
6 → `fcffcf`, 7 → `fcfcfcf`, 8 → `fccffccf`… ✔

Por modelo: ✔

| modelo | fijos | corredizas |
|---|---|---|
| `ncc` (doble corrediza) | 0 | 2 |
| `n3c` (triple corrediza) | 0 | 3 |
| `nff` (full fijos) | todas | 0 |
| `nfc` (full corredizas) | 0 | todas |
| `ncfc` | el inverso del clásico (fijo ↔ corrediza); con 1 división es fijo | |

---

## 5. Descuentos: aparente sí, inaparente no

**Regla dura: en INA no se descuenta nada.** Ni el parante ni los puentes. ✔

| Descuento | Función | APA | INA / PIV |
|---|---|---|---|
| Lado lateral al vacío | `descuentoAnchoLateralApa` | resta el parante por cada lado al vacío | 0 |
| Sin pared arriba/abajo | `descuentoAltoVertical` | resta el falso puente | 0 |
| Esquina en L y en C | `medidasCalculoNlApa` | resta el esquinero | 0 *(corregido 2026-09-07)* |
| Altura de mocheta | `alturasMochetasPorModelo` | descuenta el perfil de puente | no descuenta |

**Descuento de esquina en APA** (`medidasCalculoNlApa`): el perfil lo define el **esquinero**, no el
puente; "ninguno" = sin descuento. Reparto **invertido**: la ventana más ancha recibe la parte
**menor** del aluminio y la más angosta la mayor. Si los dos valores del perfil son iguales, cada
lado descuenta lo mismo. ✔

**Encuentro en ángulo en INA:** en vez de restar hay que **sumar**. La U de abajo y del techo de
cada lado debe ser `medida + u` (la u normalmente 1, la u-3/8 que va por defecto de marco): con
lados de 120 y 150, 121 y 151, para que una monte sobre la otra. ❓ **Sin implementar.** Aplica al
caso de **doble puente**, donde no hay división por corrediza. Falta decidir en qué líneas de U
va (ver §9) y con qué cantidad.

---

## 6. Remate y mochetas

`siNoMoch` = 1 si `hoja < alto` (hay mocheta), 0 si no. ✔

`altoHoja(alto, hoja)`: si `hoja == 0` → `alto / 7 * 5`; si `hoja >= alto` → `alto`; si no,
`hoja`. ✔

`altoMocheta(alto, altoHoja, tubo)` = `alto - (altoHoja + tubo)`. ✔

| Remate | Mochetas |
|---|---|
| `nn` normal | una mocheta arriba: `s(sistema); m(mocheta)` |
| `nr` invertido | la mocheta ocupa `alto - altoHoja` y va delante del sistema |
| `np` doble puente | dos mochetas, arriba y abajo: `m(inferior); s(sistema); m(superior)` |

**Doble puente (`np`):** ✔

- El disponible se reparte en dos mitades iguales; si se escribe una **mocheta inferior** > 0, esa
  se respeta y la superior toma el resto.
- En **APA** el disponible descuenta los DOS puentes (`altoMocheta` con `2 × alturaPuente`); en
  **INA** el disponible es `alto - altoHoja` completo.
- Lleva **dos corridas de puente**. Se duplica **solo la medida horizontal**: el parante vertical
  entre tramos va de piso a techo y no se duplica, y el **riel y la U felpero siguen en 1** por
  tramo (el riel va en el puente de abajo y el felpero en el de arriba).
- El ×2 se hace con `escalarCantidadesTexto` (texto sin parantes verticales) o con el parámetro
  `filasPuente` dentro de `NovaInaCalculos.puentes` (texto que sí los trae). Hay tests que fijan
  la frontera: `PuenteDobleEscaladoTest`.

---

## 7. Puentes, tramos y parantes

`nPuentes(divisiones)`: 1 hasta 5 divisiones; a partir de 6, `ceil(divisiones / 5)`. ✔

La medida del puente (`mPuentes1`) depende del acabado y del número de divisiones. Con divisiones
impares y hasta 5 es el ancho completo; con 6, 8 (y 10 en INA) es `(ancho - 2.5) / 2`; con 12 es
`(ancho - 5) / 3`; con 10 y 14 hay repartos propios. Por encima de 15 se reparte
`(ancho - 2.5 × (puentes - 1)) / puentes`. ✔ ❓ *La tabla parece histórica; conviene revisarla.*

**Tramos** (`gruposDivisionesMochetaPorModelo`): ✔

- `ncc` / `n3c`: un tramo cada 2 (o 3) corredizas.
- `nfc`: en INA un solo tramo; si el usuario elige "cada cuántas corredizas" en el diálogo de
  `ivModelo`, se corta cada N; si no, reparto balanceado del clásico.
- `ncfc`: en INA un solo tramo.
- El resto: reparto por ancho.

`anchMota(medida)` = `ceil(medida / 180)`: cuántos paños lleva la mocheta de un tramo (el vidrio de
mocheta no pasa de 180). ✔

**Cruce:** por defecto 0.7 si no se escribe otro valor. ✔ ❓ *En doble puente se vio 0.9; ¿el
defecto depende de algo?*

---

## 8. Perfiles U

`us` es cuánto se mete la pieza dentro de la U: 1 → `u-3/8`, 1.5 → `u-13`. ✔

**Inaparente** (`calcularTextoU`): ✔

| Línea | Fórmula | Orientación |
|---|---|---|
| U de fijos | `uFijos(ancho, divisiones, cruce)`, agrupando fijos adyacentes | horizontal, por módulo |
| U parante | `alto - 2×us` | vertical, de piso a techo |
| U parante de mocheta | `(alto - altoHoja) - us + 1.5` | vertical |
| U superior | `ancho` (1 pieza) | horizontal, ancho completo |

Con 1 división: U de fijos ×2 y U parante ×2, sin las otras. Con 2 divisiones aparece además la U
parante de mocheta ×1. ❓ *La U superior sale en **una** pieza; si es "la de abajo y la del techo"
deberían ser dos.*

**Aparente:** la U se arma por tramos con `textoUMochetaPorTramosAparente`; en full fijos la U
horizontal es el marco continuo del tramo (2 piezas por tramo, arriba y abajo). ✔

---

## 9. Vidrios

- **Corrediza:** ancho = `hache - 1.4`; alto = `altoHoja - 3.5`. ✔
- **Mocheta:** holgura de 0.5 en ancho y 0.3 en alto; el ancho del tramo se reparte entre los paños
  que dé `anchMota`. ✔
- En INA la altura del vidrio de mocheta no descuenta el puente (§5). ✔

---

## 10. Geometrías compuestas

| Geometría | Modelo | Cómo se calcula |
|---|---|---|
| En L | `nl` | 2 lados; una esquina; un parante de esquina en la lista |
| En C | `nu` | 3 lados; dos esquinas; el central descuenta las dos (en APA) |
| Serie | `ns` | N lados encadenados |
| Curvo | `ncu` | el ancho es el **arco**: con cuerda y flecha se calcula, si no el ancho escrito |
| Circular | `nci` | dibujo con tag `O<1>` |

En L y en C cada lado se calcula por separado y los materiales se **combinan** sumando las
cantidades de las medidas iguales. ✔

---

## 11. Nova desigual

Es una rama **excluyente**: si hay módulos desiguales cargados, `calcularDesigual()` reemplaza todo
el cálculo normal. ✔

- El **diseño manda**: cada módulo lleva el ancho exacto del dibujo. No hay regla de los 60 ni
  reparto por ancho.
- **Ajuste por módulo:** dentro de cada tramo, `ajuste = (nº de transiciones f↔c) × cruce / (nº de
  módulos)`, y ese mismo ajuste se suma a todos los módulos del tramo. Ejemplo: `f<100> c<50> f<50>`
  con cruce 0.9 → 2 transiciones / 3 módulos = 0.6 → U de fijos `100.6 = 1` y `50.6 = 1`. ✔
- La U de fijos combina **rachas** de fijos adyacentes en un solo perfil. En INA el parante es
  transparente y la racha lo atraviesa; en APA el parante la corta. ✔
- ❓ `calcularDesigual()` usa el ancho **crudo**: no pasa por `arcoCurvo` ni por ninguna función de
  descuento. En una L con diseño editado, los materiales no descuentan el esquinero.
- ❓ El cargador de diseños no entiende `A<90>` (el separador entre lados de L y de C): un diseño de
  L vuelve como una lista plana con los módulos de los dos lados juntos.

---

## 12. Referencias

Formato: ✔

```
An: <ancho>  x  Al: <alto>
Altura de puente: <altoHoja | "sin puente">
mocheta inf: <n>
mocheta sup: <n>
Divisiones: <n> -> fjs: <n>;czs: <n>
[Puntos: ...]        (solo con más de 4 divisiones)
```

- `An:` y `Al:` muestran **la medida que se midió**, no la útil. Es a propósito: al archivar se
  compara con lo que hay en el campo, y así no se archiva por error ni se duplica. ✔
- Los conteos (divisiones, fijos, corredizas) salen del mismo número que usan el diseño y los
  materiales. ✔
- En doble puente la mocheta se reparte entre inferior y superior; en el resto va todo en la
  inferior y la superior queda en 0. ✔

---

## 13. Diseño simbólico

El paquete se arma según `diseño simbólico nova.txt` (en la raíz del proyecto). Orden:

```
Cliente - medidas - Producto,sistema,acabado,tipología,número - volumen,forma,encuentro,modelo
       - Tramo,condición<medida>(franja<medida>(módulo<medida>…)…)… - aluminio - vidrios - accesorios
```

El diseño usa la **medida real**, no la útil: es visual, muestra cómo va a quedar la ventana y no
alimenta los materiales. ✔ *No hay que "corregirlo" para que use el útil.*

---

## 14. Trampas conocidas

1. **Pasar la modulación donde se espera el remate** hace que la rama `np` no se ejecute nunca. Ya
   pasó en `generarReferencias`.
2. **El puente se escribe por dos caminos**: la rama "por tramos" cuando `alto > altoHoja`, y
   `NovaInaCalculos.puentes` cuando no. Un arreglo tiene que tocar los dos.
3. **`divisiones(..., tipo)` devuelve 0** para los tipos que no conoce.
4. **La cuenta de hojas y el ancho de las piezas son cosas distintas** en L y en C.
5. **`df1` trunca**: comparar medidas con tolerancia da falsos iguales.

---

## 15. Pendientes por confirmar

| # | Qué | Dónde |
|---|---|---|
| 1 | El `+u` del encuentro en ángulo en INA con doble puente: en qué líneas de U y con qué cantidad | §5, §8 |
| 2 | La U superior de INA sale en 1 pieza; ¿deberían ser 2 (abajo y techo)? | §8 |
| 3 | `calcularDesigual()` trabaja con el ancho crudo, sin descuentos | §11 |
| 4 | El cargador de diseños no entiende `A<90>` en L y en C | §11 |
| 5 | La tabla de medida de puente por número de divisiones | §7 |
| 6 | El cruce por defecto (0.7); en doble puente se vio 0.9 | §7 |
| 7 | ¿Separar el eje de modulación del de geometría? | §1 |
| 8 | `divisiones(..., tipo)` con `else -> 0` | §3 |

---

## Historia útil

- `932353b` — punto de control antes del refactor del diseño simbólico. **No existía ningún
  descuento de esquina.** Es la referencia buena para comparar.
- `64c2251` — entró el refactor de nova desiguales. Trajo los descuentos de esquina (aplicados a los
  dos acabados, que era incorrecto) y la regresión de la mocheta de INA.

## Tests que fijan reglas

- `PuenteDobleEscaladoTest` — qué se duplica y qué no en el doble puente.
- `DivisionesAnchoUtilTest` — la regla de los 60 y la diferencia entre medida real y ancho útil.
