# Las demás calculadoras y las formas

Revisión del 2026-09-29, pedida por el usuario: "hacer exactamente lo mismo que con Nova para las
demás calculadoras, que puedan reconocer formas". Las reglas generales G-01 a G-11 de
`docs/REGLAS_NOVA.md` valen para todas; aquí va lo de cada calculadora frente a las formas.

Marcas como en REGLAS_NOVA: ✅ dictado · ⚙️ así lo hace el código · ❓ falta el dato · 🐞 no cumple.

## 1. Qué llega de Medidas ✅

La misma ficha para todas (`ColaCalculadoras.MedidaCalc`): producto, cliente, cantidad, ancho y alto,
y además el contorno del vano, la esquina (lados, ángulos, curvas) y la forma circular. Hasta el
2026-09-29 solo Nova y el ropero miraban lo que no es ancho y alto; las otras doce calculadoras
recibían el rectángulo y calculaban en silencio.

## 1b. Una medida con varias figuras ✅ (dictado el 2026-10-01)

Si el apunte de una medida tiene más de una figura suelta (rectángulos, triángulos, círculos o
formas cerradas que no están unas dentro de otras), al enviarla a las calculadoras se pregunta qué
hacer con ellas:

1. **Enviar todas a su calculadora**: cada figura es una medida aparte en la cola.
2. **Enviar cada una a su calculadora**: se elige la calculadora de cada figura (o "No enviar").
3. **Elegir una sola** y enviarla.

Cada figura se lleva lo que tiene dentro o más cerca (cotas, puentes, textos, símbolos) y se mide
con el camino de siempre (contorno, esquina, círculo). Una ventana de esquina o un apunte con vistas
es una sola ventana aunque tenga varias piezas: no se parte. Antes la cola juntaba todas las figuras
en una medida, con el ancho mayor y el alto mayor de todas. Caso: Fide, 214.5 × 157.5, 204 × 21 y
200 × 23 (`FigurasDeUnaMedidaTest`). En el moto hay 10 medidas así (Fide, Pipa).

## 2. Reconocer la forma (hecho el 2026-09-29)

`taller/FormaDeLaMedida.kt` dice qué es el vano, con las reglas generales:

| Forma | Cuándo | Ejemplo real |
|---|---|---|
| Rectangular | sin contorno, o todos los vértices en las esquinas de su caja | la mayoría |
| Descuadre | un vértice se aparta menos de 2.5 (N-06): sigue siendo rectangular, no avisa | Ele 229.5 × 223.4, dintel que baja 0.6 |
| Escalonado | rectángulos de distinto alto lado a lado (N-66) | ventana de Pipa 182.4 × 70.8: 156 × 70.8 + 26.1 × 60 |
| Poligonal | lados inclinados, o algo que entra por un costado | puerta de Isabel 97.8 × 255.2 (dintel en diagonal), muro de Pipa 201.4 × 232 |
| Circular | el círculo o la elipse de Medidas | — |
| Esquina | L, C, serie o curva | — |

Todas las calculadoras que usan `ControladorColaMedidas` (Puerta, Mampara FC, Mampara Paflón,
Mampara Vidrio, Ventana Aluminio, Vitroven, Muro, Pivot, Puerta Ducha, Rejas, División de baño,
Drywall, Ropero) avisan al abrir una medida con forma: qué es y que calculan el rectángulo que la
contiene. Un vano escalonado se puede **partir** en sus rectángulos (botón "Partir en N" del aviso):
cada uno entra a la cola como una medida propia y se calcula con las reglas ya verificadas de esa
calculadora (N-96: partir en rectángulos, calcular cada uno con lo que existe). Lo decide el vidriero
(G-03). Pruebas: `FormaDeLaMedidaTest` y `FormaEnLasCalculadorasTest` (en el celular).

## 3. Revisión de las medidas del celular de pruebas

`RevisionMedidasCalculadorasTest` recorre en el moto todas las medidas que no son Nova, por la cola,
y deja `revision_medidas_calculadoras.txt`. Resultado del 2026-09-29 (42 medidas distintas):

| Calculadora | Medidas | Estado |
|---|---|---|
| Puerta (Mari) | 15 | ⚙️ Todas salen con el modelo Mari de fábrica; sus pruebas de corte pasan (`VidriosMariDHolgurasTest`), pero no revisé a mano cada número. Con alto menor que la hoja (Abel, 69.8 × 195) la fila "Ensayos", que no se archiva, muestra un vidrio de −2.7. |
| Vitroven | 7 | ✅ Fórmulas por modelo del usuario; su prueba (`VitrovenFormulasTest`) pasa. La de Jorge se archivó como 92.6 × 44.5 con una versión vieja de Medidas; su figura mide 57.3 × 44.5 (arriba 19.5 + 37.8, abajo 51). |
| Muro cortina | 6 | ⚙️ Sin reglas dictadas; el de 201.4 × 232 es poligonal y ahora avisa. |
| Ventana Aluminio | 5 | ⚙️ Sin reglas dictadas; la de 182.4 × 70.8 es escalonada y ahora avisa y se puede partir. |
| Mampara Paflón | 2 | ✅ Cuadra con lo dictado (zócalos, parantes, portafelpa, topes). |
| Mampara FC | 2 | ⚙️ Sin reglas dictadas. (La prueba no encontraba su botón, `btn_calcular`; la calculadora está bien.) |
| Puerta Ducha | 1 | ⚙️ Modelo P2 Alutemp: solo vidrios. |
| División de baño | 1 | Pide cubículos, profundidad, ancho de puerta y desagüe, que Medidas no trae: avisa "Ingrese datos válidos". |
| Otro techo, baranda | 3 | Sin calculadora. |

## 4. Lo que falta dictar ❓

Nada de esto se inventó: sin regla, la calculadora avisa y calcula el rectángulo.

- **C-01 Escalón, ¿una pieza o varias?** Partir da unidades separadas, cada una con su marco. ¿Vale
  así para ventana de aluminio, vitroven y muro cortina, o el escalón va en una sola pieza con un
  parante o un travesaño, como en Nova?
- **C-02 Muro cortina con lados inclinados o una muesca** (Pipa 201.4 × 232): ¿cómo se cortan los
  perfiles del lado inclinado y los vidrios de la muesca?
- **C-03 Puerta con el dintel en diagonal** (Isabel 97.8 × 255.2): ¿la hoja es el rectángulo y la
  sobreluz toma la forma?
- **C-04 Vitroven con una esquina cortada** (Jorge 57.3 × 44.5): ¿lamas de largo distinto?
- **C-05 Esquinas (L, C) y círculo** fuera de Nova: ¿qué calculadoras los admiten?
- **C-06 División de baño desde Medidas**: ¿valores por defecto de cubículos, profundidad, puerta y
  desagüe, o se escriben siempre?
