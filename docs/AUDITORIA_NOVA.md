# Revisión del código de Nova contra las reglas

Fecha: 2026-09-25. Se revisó el código de Nova corrediza completo contra `docs/REGLAS_NOVA.md`:
`NovaCorrediza.kt` (4875 líneas), `NovaCalculos.kt`, `NovaInaCalculos.kt`, `NovaPerfilesHelper.kt`,
`NovaVidriosHelper.kt`, `NovaCalculosDesiguales.kt` y `NovaUIHelper.kt`. Solo lectura: nada de lo
que aquí se describe está corregido todavía, salvo lo marcado como hecho el 2026-09-25.

---

## 1. El problema de fondo: cada pieza se calcula en varios lugares

Hoy Nova no tiene **un** cálculo, tiene **cinco rutas** que calculan las mismas piezas cada una a su
manera:

| Ruta | Dónde está | Cuándo se usa |
|---|---|---|
| A. Aparente automático | `NovaCorrediza.uTexto/otrosAluminios/vidriosTexto` (rama APA) + `NovaPerfilesHelper` + `NovaVidriosHelper` + `NovaCalculos` | Ventana aparente calculada en Nova |
| B. Inaparente automático | las mismas funciones (rama INA) + `NovaInaCalculos` | Ventana inaparente calculada en Nova |
| C. Parches por modulación | bloques `if (textoModelo == "nfc" / "ncfc" / "nff")` dentro de las rutas A y B | Full corredizas, ncfc, full fijos |
| D. Nova desigual | `NovaCorrediza.calcularDesigual` + `NovaCalculosDesiguales` | Toda ventana que vuelve del diseño |
| E. Esquinas (L, C) | `calcularMaterialesNl/Nu`: escriben cada lado en los campos de la pantalla, corren A o B, **leen el resultado de los textos de la pantalla** y lo suman | Ventanas en L y en C |

Consecuencias, y por qué los arreglos de uno en uno no alcanzan:

1. **Una regla se corrige en una ruta y sigue mal en las otras.** Ejemplo real: la U parante de
   aparente se calcula `hoja − 2u` en la ruta A y `hoja − (u + 0.2)` en la ruta D. La misma ventana
   da 97 en una y 98.3 en la otra.
2. **Quedan tablas por número de divisiones** (6, 8, 10, 12, 14) en puentes, rieles, U y vidrios de
   mocheta: no escalan y usted ya las dio por abandonadas (N-50).
3. **El cálculo está dentro de la pantalla.** Se arman textos y después se "operan" con búsquedas
   de líneas (quitar la línea de U que coincide con el puente, insertar la de mocheta después de la
   del parante). Es frágil y no se puede probar sin abrir la pantalla.
4. **Se pasa un eje donde se espera otro**: en varias llamadas va la geometría (`texto`) donde se
   espera la modulación (así se perdió el doble puente en la ruta D).

## 2. Cuadro: cada pieza, en cada ruta

✔ cumple la regla · ✗ no cumple · — no aplica. Las rutas A y B incluyen sus parches (C).

| Pieza | Regla | A. Aparente | B. Inaparente | D. Desigual |
|---|---|---|---|---|
| U de fijos | N-42 | ✔ | ✔ | ✔ |
| U de fijos, aumento de esquina INA | G-09, N-42 | — | ✗ no existe | ✗ no existe |
| U parante, medida | N-43 | ✗ `hoja − 2u` (debe ser `hoja − (u + 0.2)`) | ✔ | ✔ |
| U parante, cantidad | N-43 | ✔ | ✔ (corregido hoy) | ✔ |
| U parante de mocheta INA | N-44 | — | ✗ en bandera no sale | ✗ invertido suma 1.5 |
| U de mocheta APA | N-45 | ✔ | — | ✔ |
| U superior INA | N-46 | — | ✗ en bandera no sale (bandera INA se calcula como si fuera de 1 división) | ✗ bandera: 1 en vez de 2 |
| Puente, medida | N-49 | ✔ (las tablas por divisiones quedan en una rama que no se muestra) | ✔ (ídem) | ✔ |
| Puente, bandera × 2 | N-35, N-49 | ✔ | ✔ | ✗ (N-73) |
| Riel | N-51 | ✗ con mocheta sale **igual al puente**, sin el −0.06 (sin mocheta sí lo resta) | ✗ igual | ✗ igual |
| Riel doble / triple corrediza | N-51 | ✗ | ✗ | ✗ |
| U felpero / fijo corredizo | N-52 | ✗ igual al puente (debe ser el riel, puente − 0.06) | ✗ igual | ✗ igual |
| Perfil doble corrediza | N-52b | solo full corredizas | solo full corredizas | ✗ |
| Tubo APA | N-53 | ✗ siempre 1 (bandera tiene 2 mochetas) | — | ✗ igual |
| Hache | N-54 | ✔ | ✔ | ✔ |
| Portafelpa | N-55 | ✔ | ✔ | ✔ |
| Tope | N-56 | ✔ (corregido hoy) | ✔ (corregido hoy) | ✔ |
| Tee APA, medida | N-57 | ✔ | — | ✔ |
| Tee APA, cantidad | N-57 | ✗ recibe la geometría en lugar de la modulación | — | ✔ |
| Vidrio fijo | N-60, N-61 | ✔ (corregido hoy) | ✔ (corregido hoy) | ✔ (corregido hoy) |
| Vidrio corrediza | N-58, N-59 | ✔ | ✔ | ✔ |
| Vidrio mocheta APA | N-63 | ✔ (corregido hoy) | — | ✔ (corregido hoy) |
| Vidrio mocheta INA | N-64 | — | ✗ tabla por divisiones pares; bandera: anchos distintos arriba y abajo | ✔ ancho x alto; bandera ✗ |
| Mochetas del diseño (alto, bandera, orden) | N-33, N-34 | — | — | ✗ el lector no las lee (N-74) |
| Esquina en L, C | G-09, N-38, N-84 | ✔ descuento APA | ✗ falta el aumento | ✗ lee la L como fila plana |

Además, sin pieza propia:

- Cruce en INA: usa la tabla vieja de divisiones y aborta si "Partes" está vacío (N-83).
- Estado compartido en `NovaCalculos` que leen otras calculadoras (G-07).
- Pivotante como acabado (N-02).
- Tope automático de 6 en full corredizas INA, que además solo sabe hacer un tramo (N-19).

## 3. Propuesta: un solo motor de cálculo

En lugar de seguir corrigiendo cinco rutas, **un motor único**, sin pantalla, que todas usen:

1. **Una sola descripción de la ventana.** Acabado, modelo, lados; en cada lado sus tramos, franjas
   y módulos (fijo o corrediza, con su ancho), sus esquinas (en ángulo o curva) y su encuentro por
   lado (muro, acople, libre). Además, los perfiles (U con ancho y alto, puente con altura y
   esquinero) y el cruce.
2. **Dos maneras de llenarla, un solo cálculo.** Nova con ceil la llena con las reglas automáticas
   (60 cm, máximo 5 por tramo, repartos, orden). Nova desigual la llena desde el diseño, con **un
   solo lector** del diseño simbólico (N-81). Desde ahí las dos son la misma ventana y se calculan
   igual: la diferencia entre "ceil" y "desigual" desaparece del cálculo.
3. **Una función por pieza**, que aplica su regla mirando la ventana: con qué colinda cada fijo y
   cada corrediza (pared, acople, libre, parante, esquina, otro módulo), cuántas mochetas hay, en
   qué acabado. Sin tablas por número de divisiones.
4. **Salida en piezas**, no en texto: perfil, medida, cantidad y orientación (horizontal, diagonal,
   vertical, G-01). El texto de pantalla y el del archivo se arman al final, igual que hoy, para
   no romper los proyectos guardados ni los optimizadores.
5. **Las esquinas dentro del motor**: una L o una C es una sola ventana con varios lados; no se
   vuelve a pasar por la pantalla.

### Cómo se hace sin romper lo que funciona

| Fase | Qué | Cómo se controla |
|---|---|---|
| 1 | Motor nuevo al lado del código actual, con pruebas por regla (N-xx) | Las pruebas de reglas |
| 2 | Comparar motor nuevo y código actual en muchas ventanas (cada acabado × modelo × modulación, L, C, desigual) | Cada diferencia tiene que estar explicada por una regla de este documento; si no, se investiga |
| 3 | La pantalla usa el motor nuevo; se ve en los celulares | Pruebas en el moto g04 |
| 4 | Se retiran las rutas viejas; la lógica escrita a mano queda como comentario de consulta | Suite completa en verde. **Hecha el 2026-09-30** (ver abajo) |

Las piezas que hoy salen mal (cuadro de la sección 2) cambiarán de medida o cantidad al pasar al
motor. Esa lista de diferencias se le mostrará antes de la fase 3.

### Fase 4 hecha (2026-09-30)

Pedido del usuario: "no quiero ningún motor aparte del que está en desarrollo". Desde entonces
**todo sale del motor único** (`taller/nova/motor`):

- **Materiales**: `MotorNova`, sin respaldo. Si el motor no puede con un caso, la pantalla dice "el
  motor todavía no calcula este caso" y no muestra materiales (antes se quedaba en silencio con lo
  de las rutas viejas, y la misma ventana podía salir con uno u otro).
- **Referencias**: `ReferenciasNova`, con el formato de siempre (la primera línea `An: … x Al: …`
  es la que se relee al reabrir un producto).
- **Diseño simbólico**: `EscritorNova` escribe el reparto (tramos, hojas, alturas) desde la misma
  ventana que se calcula; la pantalla solo pone los adornos de la medida (esquina, panza, círculo,
  silueta). Antes lo decidía `NovaUIHelper.generarTramosConsolidado` por su cuenta.
- **Retirado**: `uTexto`, `otrosAluminios`, `vidriosTexto`, `referencias`, `calcularMaterialesNl/Nu`,
  `calcularDesigual` y 28 funciones que solo ellas usaban (1 305 líneas de `NovaCorrediza`), y los
  objetos `NovaCalculosDesiguales`, `NovaInaCalculos`, `NovaVidriosHelper`, `NovaPerfilesHelper` y
  casi todo `NovaUIHelper`. Quedan comentados en `taller/nova/viejo/` para leerlos, y enteros en la
  etiqueta git `rutas-viejas-nova` (commit e020bc2) para volver a correrlos y comparar.
- **Pruebas**: las reglas dictadas que fijaban las pruebas del cálculo viejo pasaron al motor
  (`ReglasDictadasEnElMotorTest`); las viejas quedan comentadas en `test/.../nova/viejo/`.

## 4. Decisiones del usuario (2026-09-25)

1. **U felpero, fijo corredizo y doble corrediza miden igual que el riel (puente − 0.06).** Hoy
   las tres rutas los sacan igual al puente: es otra pieza a corregir en el motor.
2. **Plan de la sección 3 aprobado.** Se empieza por la fase 1.
3. **(2026-09-28) El plan se amplía al flujo completo**, de la medida a los optimizadores, para
   todas las calculadoras: sección 5.

## 5. El flujo completo: una sola entrada, una sola salida (2026-09-28)

El motor único de la sección 3 resuelve el medio del camino, el cálculo de Nova. Los fallos del
27 y 28 de septiembre salieron en los extremos: la misma medida cambió de tamaño al abrirse en otro
celular (×1.571), y el dibujo, el panel, la 3D y el motor leían el diseño cada uno a su manera.
Usted lo dijo así: **todo en un solo motor, desde el principio del flujo hasta el final.**

### El flujo

```
 Medidas (general) ─┐
 Carrito (Main)  ───┼─►  una medida  ─►  calculadora  ─►  piezas  ─►  proyecto  ─►  optimizadores
 Ingreso manual  ───┘     (entrada)      (sus reglas)     (salida)                   compartir (JSON)
```

1. **Tres puertas de entrada.** Medidas, que sirve a todas las calculadoras (Nova, mampara, puerta,
   ropero…); el carrito de MainActivity; y el ingreso escrito en la misma calculadora.
2. **Todas las calculadoras leen lo mismo.** Cada una solo interpreta los datos según su producto y
   hace sus propios cálculos. Ninguna vuelve a medir, a adivinar ni a calcular desde el dibujo.
3. **Todas entregan el resultado en la misma forma**, que leen los optimizadores y que viaja como
   JSON (formato Crystal) a cualquier versión de la app y a cualquier celular, de cualquier tamaño.

### La regla que une todo

**Los centímetros que usted escribe son la única fuente.** El dibujo en píxeles sirve solo para
mirar. Ningún dato se vuelve a derivar del dibujo ni depende de la pantalla o la densidad del
celular. Una misma medida da el mismo resultado entre por la puerta que entre y en el celular que
sea.

### Cómo está hoy (revisado el 2026-09-28)

| Parte | Dónde | Estado |
|---|---|---|
| Entrada desde Medidas | `ColaCalculadoras.MedidaCalc` | La leen 14 calculadoras: buena base. Pero crece con un campo suelto por caso (`contorno`, `esquina`, `contornosLados`, `parantesLados`, `disenoRopero`, `forma`), y el contorno viaja como texto `x,y;x,y`. |
| Entrada desde el carrito | `EnrutadorPresupuesto.crearIntentCalculadora` | Otra ficha distinta: extras sueltos (`ancho`, `alto`, `cantidad`, `producto`, `color_aluminio`, `tipo_vidrio`). No usa `MedidaCalc`. |
| Ingreso manual | los campos de cada calculadora | Cada pantalla lee sus propios campos. |
| El apunte de Medidas | `SketchMedidasView.exportEditableState` | Se guarda en píxeles; los cm se reconstruían adivinando la densidad. Desde el 2026-09-28 guarda también la escala con que se dibujó. Es lo que se comparte hoy como "formato Crystal" de medidas. |
| Cálculo | cada calculadora | Nova, en camino con el motor único (`taller/nova/motor`). Las demás, cada una con su código. |
| Salida | `ListaCasilla.procesarArchivarConPrefijo` → `MapStorage` | Cada calculadora archiva los **textos de su pantalla**, por perfil: `[medida, cantidad, ventana, proyecto]`. El motor de Nova ya produce piezas (`MaterialesNova.Pieza`), pero se vuelven texto para archivar. |
| Optimizadores | `CorteListManager` y planchas | Leen ese mapa del proyecto y filtran las filas por forma (número, entero). Comparten el corte como JSON "formato Crystal". |

### Los tres contratos

Se fijan una vez, por escrito y con pruebas, y todas las calculadoras los cumplen.

1. **Entrada: una medida.** Una sola ficha para las tres puertas, en cm:
   - producto, cliente y cantidad;
   - ancho y alto;
   - la forma del vano: recto, contorno, circular o esquina con sus lados;
   - acabado, color y vidrio cuando se conocen;
   - los datos propios del producto, como el diseño del ropero.

   Se hace **ampliando `MedidaCalc`**, no con una ficha nueva al lado. El carrito y el ingreso
   manual la llenan también. El dibujo va aparte y solo para mirar.
2. **Salida: piezas.** Cada pieza con su material o perfil, medida en cm, cantidad, orientación
   (G-01), ventana y proyecto, y color o tipo de vidrio. Es lo que el mapa del proyecto ya guarda
   como texto: se ordena y se escribe desde las piezas, no desde la pantalla. Los proyectos ya
   guardados se siguen abriendo (el `serialVersionUID` de `Listado` no se toca).
3. **Transporte: JSON.** La medida y el resultado viajan como JSON con número de versión. Una app
   más nueva lee lo viejo; una más vieja ignora lo que no conoce. Lo que se comparte de Medidas
   lleva los cm, y no solo el dibujo.

### Orden

| Paso | Qué | Cómo se controla |
|---|---|---|
| 1 | Escribir los tres contratos con sus pruebas | Una prueba por campo; la misma medida escrita en JSON y leída de vuelta da idéntico |
| 2 | Las tres puertas llenan la misma ficha | La misma medida por Medidas, por el carrito y a mano da la misma ficha; el mismo apunte en dos densidades de pantalla da los mismos cm |
| 3 | Nova primero: lee la ficha, calcula con el motor y archiva piezas | La suite de Nova y la comparación de la fase 2, sin diferencias |
| 4 | Las demás calculadoras, una por una, con su propia comparación antes y después | Cada diferencia explicada por una regla; si no, se investiga |
| 5 | Optimizadores y compartir leen piezas | El mismo proyecto optimiza igual antes y después |

Mientras tanto, lo que la sección 3 deja pendiente en Nova (C-25, un solo escritor del diseño; C-20,
circular como forma) se hace dentro de estos contratos, no aparte.
