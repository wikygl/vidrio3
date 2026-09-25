# Motor nuevo de Nova contra la pantalla de hoy (fase 2)

Fecha: 2026-09-25. Se calcularon **672 ventanas** con la pantalla actual de Nova y con el motor
nuevo, en el moto g04, y se compararon pieza por pieza. Variantes probadas: aparente e inaparente;
modelo normal, invertido y bandera; las 6 modulaciones (clásico, full corredizas, full fijos,
ncfc, doble y triple corrediza); anchos de 100 a 700; con y sin mocheta. Ambas rutas usan la misma
cantidad de hojas.

La pantalla no falló en ningún caso. Todas las diferencias que salieron están en esta lista: cada
una se explica por una regla, salvo dos que necesitan su decisión (sección C).

La pantalla **todavía no usa** el motor: nada de esto cambió aún lo que usted ve ni lo que se archiva.

---

## A. Donde el motor cumple la regla y la pantalla no

| # | Qué | Regla | Ejemplo (pantalla → motor) |
|---|---|---|---|
| 1 | Riel = puente − 0.06 | N-51 | 100 → **99.9** |
| 2 | Fijo corredizo, U felpero y doble corrediza = medida del riel | N-52 | 100 → **99.9** |
| 3 | U parante de aparente = hoja − (u + 0.2) | N-43 | hoja 100: 97 → **98.3** |
| 4 | **Doble y triple corrediza**: solo corredizas. La pantalla las calculaba como el clásico, con un fijo | N-23 | APA doble corrediza de 100: la pantalla sacaba U de fijo, U parante, vidrio fijo 49.8 × 98.3, 1 hache, 3 portafelpas; el motor: 2 corredizas, **2 haches, 4 portafelpas, 2 vidrios 48.9 × 96.5** |
| 5 | Doble y triple corrediza: 2 y 3 rieles, y el perfil doble corrediza (1 cada 2 corredizas) | N-51, N-52b | riel `100 = 1` → **`99.9 = 2`**; doble corrediza **`99.9 = 1`** |
| 6 | Doble corrediza 2 hojas, triple 3, ncfc 3 por defecto (la pantalla usaba la regla de los 60) | N-20 | 145 de ancho en doble corrediza: 3 hojas → **2** |
| 7 | Full corredizas inaparente: máximo 5 por tramo en automático | N-19 | 330 (6 hojas): puente `330 = 1` → **`163.7 = 2`**, 1 parante, 4 topes |
| 8 | Full corredizas y doble corrediza inaparente: la mocheta va sobre las corredizas juntas | N-64 | `49 x 46` (como si hubiera un fijo) → **`99.4 x 46`** |
| 9 | Full fijos inaparente: solo fijos; la pantalla sacaba vidrio de corrediza y de mocheta | N-23, N-87 | → **`49.6 x 143.3 = 2`** |
| 10 | **Bandera inaparente**: la pantalla la calculaba como si fuera de 1 división: sin U de fijos y sin U parante de mocheta | N-44, N-46 | ventana de 100: `100 = 2; 142 = 2` → **`100 = 2` (U superior), `50.3 = 1` (U de fijo), `142 = 1`, `22.5 = 1` (U parante de mocheta arriba), `21 = 1` (abajo)** |
| 11 | Bandera con full corredizas aparente: U vertical de mocheta en las dos mochetas | N-45 | `17 = 2` → **`17 = 4`** |
| 12 | Bandera ncfc inaparente: dos corridas de puente | N-35 | `100 = 1` → **`100 = 2`** |
| 13 | ncfc aparente con Múltiple: no lleva fijo corredizo (el Múltiple ya lo trae) | N-52 | la pantalla lo mostraba → no va |
| 14 | Líneas sin sentido en inaparente sin mocheta | — | la pantalla mostraba `0 = 1` y `-1.5 = 1` → no salen |
| 15 | U de una racha de fijos en inaparente: se suma la medida exacta y se trunca al final | N-42 | 55.4 + 55.4 = 110.8 → **110.9** (la medida real es 110.9) |

Además, lo que se corrigió en el formato de números afecta ya a la pantalla actual (sin esperar al
motor). Por la cuenta en coma flotante, 48.8 − 1.4 daba 47.39999, y al truncar salía **47.3 en
lugar de 47.4**: vidrios un milímetro cortos. Está corregido en `df1` para todas las listas.

## B. Esquinas y Nova desigual

**L y C (10 ventanas más, comparadas igual en el celular).**

- **Aparente:** coincide con la pantalla en todo (descuento del esquinero incluido), salvo la U
  parante (N-43) y el riel −0.06 de la sección A.
- **Inaparente:** las diferencias son las reglas nuevas:

| Qué | Regla | Ejemplo, L de 120 y 150 |
|---|---|---|
| Aumento de esquina: la U superior suma el ancho de la U; la de fijos, su parte | G-09, N-42, N-46 | U superior `120`, `150` → **`121`, `151`**; U de fijos `60.3` → **`60.8`** |
| U parante: el fijo de la esquina no la lleva | N-43 | `142 = 3` → **`142 = 2`** |
| U parante de mocheta: la corrediza de la esquina no la lleva | N-44 | `45 = 1` → no va |

Su L dictada da en el motor exactamente `60.8 = 1, 121 = 1, 41 = 2, 97 = 1, 20 = 1`.

La corrediza que llega a la esquina **sí** lleva tope, como hoy: la esquina tiene su parante de
esquina (N-84) y el tope va contra pared o parante (N-56).

**Nova desigual.** Pasa por el mismo motor: el diseño lo lee un solo lector (`DisenoNova`, el
mismo del dibujo). Prueba: 288 ventanas automáticas, escritas como diseño y leídas otra vez, dan
**exactamente** los mismos materiales. Nova con ceil y Nova desigual ya son el mismo cálculo. De
paso se corrigió que el diseño guarda los anchos truncados (143 / 3 → 47.6, que suman 142.8): el
motor los ajusta al ancho de la ventana.

## C. Dos decisiones suyas — respondidas el 2026-09-25

1. **Cruce:** con tramos automáticos y ninguno bloqueado, todos los módulos iguales. El motor
   reparte el cruce en toda la ventana: 420 en 4 + 3 hojas da hache `60 = 3`, como la pantalla.
2. **Vidrio de mocheta de inaparente:** lo que dejan las U de fijos ya cortadas, menos 0.3 a cada
   lado (máximo 0.4). 140 en fcf → `140 − 2 × 47.1 − 0.6 = 45.2`. Frente a la pantalla quedan
   diferencias de 0.1 a 0.2 en algunos anchos, todas por esta cuenta (145: pantalla 46.9 → motor
   **46.8**).

Lo que sigue es la pregunta tal como se hizo:

**1. El cruce cuando los tramos no tienen las mismas hojas.** Ventana aparente de 420, 7 hojas en
dos tramos de 4 y 3:

| | Pantalla (N-42: todo el lado junto) | Motor (N-71: cada tramo por su cuenta) |
|---|---|---|
| Hache | `60 = 3` | `59.9 = 2`, `60.1 = 1` |
| Vidrio fijo | `59.5 x 98.3 = 4` | `59.4 x 98.3 = 2`, `59.6 x 98.3 = 2` |

Con tramos iguales (3 y 3, 4 y 4…) las dos dan lo mismo. Las dos reglas están confirmadas por
usted: N-42 reparte el cruce en toda la ventana y N-71 (Nova desigual) en cada tramo. ¿Cuál vale?

**2. Vidrio de mocheta de inaparente sobre una sola corrediza: ¿− 0.5 o − 0.6?** Hoy depende de la
ruta: con divisiones impares la pantalla resta 0.5; con pares, 0.6; Nova desigual siempre 0.6. El
motor quedó en 0.5 sobre una corrediza y 0.6 sobre dos o más juntas. Ejemplo, ventana de 100:
pantalla `49 x 46` → motor `49.1 x 46`.

## D. Estado (2026-09-25)

1. ✔ Sus dos respuestas, aplicadas y con su prueba.
2. ✔ Lector único del diseño: Nova desigual pasa por el mismo motor.
3. ✔ **Fase 3 encendida en el moto g04**: los materiales de Nova salen del motor. Las referencias y
   el diseño siguen como estaban. Si el motor no pudiera con algún caso, la pantalla se queda con el
   cálculo viejo. Prueba en el celular: con el motor encendido, la pantalla muestra exactamente lo
   del motor en las 672 ventanas. El Xiaomi sigue con la versión de ayer.
4. Pendiente: que usted lo pruebe en el moto g04; después, fase 4 (retirar las rutas viejas) y lo
   que falta del motor: tramo bloqueado (N-85), bandera por tramo y compuesto (N-89), formas.
