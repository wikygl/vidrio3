# Reglas de Nova corrediza

El documento de referencia de Nova, **para todas sus variantes**. Todo lo que se programe, se
corrija o se diseñe en Nova tiene que salir de aquí; si algo no está, primero se escribe aquí.

**Estado de cada regla:**

| Marca | Significa |
|---|---|
| ✅ | **Dictada por el usuario.** Es la regla. |
| ⚙️ | **Así lo hace el código hoy**, pero nadie la confirmó. |
| ❓ | **Falta el dato** o hay una duda sobre lo dictado (sección 17). |
| 🐞 | **El código no cumple la regla** (sección 16). |

Cada regla lleva un número (**N-xx**; las generales **G-xx**) para dictarla o corregirla por
número. Lo que cambia en el código sale de aquí, con su prueba.

**Fuentes:** el documento del usuario *Revisión de reglas* (Google Docs, 2026-09-25), las respuestas
en conversación, `diseño simbólico nova.txt` y la revisión del código del 2026-09-23.

Última revisión: 2026-09-28 (N-100 y N-101, el diseño sugerido del escalonado). Antes, 2026-09-25 (se incorporaron las respuestas de *Revisión de reglas*; después, el
puente preestablecido por modelo y por lado, N-03, y la modulación por tramo, N-92).

**Vocabulario.** *Modelo* = dónde van las mochetas (normal, invertido, bandera, compuesto); en el
código se llama `modeloRemate`. *Modulación* = el patrón de hojas (clásico, full corredizas…); en el
código, `textoModelo`. *Forma* = rectangular, circular, poligonal. *Mocheta* = el **espacio**; sus
vidrios, U y tees son otra cosa (G-05).

---

## 0. El flujo en el que vive Nova ✅

1. **Medida** en casa del cliente: medida exacta (→ MedidaActivity → proforma en MainActivity), o
   medida para proforma que, si hay contrato, vuelve a MedidaActivity como medida técnica.
2. **Entrada a la calculadora**: a mano, desde el carrito de MainActivity (simple) o desde
   MedidaActivity (forma, volumen, descuadres, cantidades, alféizar…).
3. **Cálculo** y **archivo** en el proyecto (local; a Firebase al compartir por la mensajería).
4. Lo archivado lo leen los **optimizadores** y, en el futuro, las **guías de fabricación e
   instalación** (meta: la mecanización).

---

## 1. Reglas generales (todas las calculadoras) ✅

- **G-01 Orden: ancho, diagonal, alto.** Vale para el ingreso de medidas, la presentación de
  resultados, los cálculos y la jerarquía: primero lo horizontal, después lo diagonal, al final lo
  vertical.
  - Rectángulo 50 × 50 sin parante, U-3/8: `50 = 2`, `48 = 2` (los horizontales enteros; los
    verticales, alto − 2 × alto de U).
  - Triángulo 70 (horizontal) × 99 (diagonal) × 70 (vertical), U-3/8: `70 = 1`, `98 = 1`, `68 = 1`.
- **G-02 Perfiles U** (ancho y alto de la U, en cm):

  | U | Ancho | Alto | Espesor máximo del vidrio |
  |---|---|---|---|
  | U-3/8 | 1 | 1 | 8 mm |
  | U-13 | 1 | 1.5 | 8 mm |
  | U-1/2 | 1.2 | 1.2 | |
  | U-3/4 | 1.2 | 1.9 | |

  En Nova y en todas las calculadoras se descuenta el **alto** de la U (1.5 en U-13). El **ancho**
  solo se usa en INA, en las esquinas **en ángulo** de L, C y serie (G-09). La lista debe ser
  editable, para que el usuario agregue perfiles (N-40).
- **G-03 La decisión del usuario manda.** Lo automático (regla de los 60, máximo por tramo,
  repartos, cruce) es solo el valor preestablecido. Si el usuario pone una división de 5 m, 10
  hojas en un tramo o un tramo de 1000 cm, se respeta.
- **G-04 La forma decide qué código se usa.** Rectangular, circular o poligonal no es un detalle
  del dibujo: elige la lógica de cálculo.
- **G-05 La mocheta es un espacio; sus materiales son otra cosa.** La mocheta mide lo mismo en APA
  y en INA (N-31). Lo que cambia entre acabados es cómo se calculan sus vidrios y sus U.
- **G-06 Nulos.**
  - No pueden ser nulos: ancho, alto, volumen, geometría.
  - La altura de puente **puede entrar en 0** (activa los 5/7), **pero no puede salir en 0**: sale
    la altura calculada. "Sin puente" es un sí/no: cuando la hoja es mayor o igual al alto.
  - Material y accesorios pueden ser nulos (N-79).
- **G-07 Cada calculadora es independiente** y guarda su propia configuración (SharedPreferences
  propias). Nada de estado compartido entre calculadoras.
- **G-08 Altura de puente por ergonomía** (con alféizar): el puente debe quedar **entre 70 y 120
  cm del piso; 90 por defecto**. Altura de puente = 90 − alféizar (alféizar 20 → puente a 70 de la
  base de la ventana). Ver N-90 para cuándo el modelo pasa a invertido o bandera.
- **G-09 Esquinas.** APA descuenta y INA **aumenta** (el ancho de la U) en cada esquina en ángulo;
  en una esquina **curva** no se aumenta ni se descuenta. Aparte de eso, APA e INA calculan igual
  (N-83).
- **G-11 Todo el cálculo en float; `df1` solo al presentar** (dictado el 2026-09-25). Ninguna medida
  se trunca a mitad de camino: se suma, se resta y se reparte con la medida exacta, y solo la línea
  que se muestra o se archiva pasa por `df1` (N-10). "Que salga lo que salga." Ejemplo: la U de
  la Dora en inaparente, 53.257 + 1.25 + 157.5 = 212.007 → `212`, no 211.9. Aplicado en el motor
  (2026-09-25); las rutas viejas de respaldo se retiran en la fase 4 y no se tocaron.
- **G-10 Configuración guardada.** El usuario podrá guardar alturas, cruces, materiales, etc. y
  usarlos en los cálculos siguientes. Existe en otra calculadora; en Nova falta.

---

## 2. Las variantes: los ejes

| Eje | En el código | Valores | Qué decide |
|---|---|---|---|
| **Acabado** | `tipoNova` | `a` aparente · `i` inaparente | Descuentos y fórmulas de vidrio |
| **Tipología** | | `c` corrediza (pivotante, batiente y fija sin desarrollar) | El mecanismo |
| **Volumen** | `texto` | plano · esquinero (L, C, serie) · curvo | Cuántos lados y cómo se unen |
| **Forma** | (mezclada en `textoModelo` como `nci`) | rectangular · circular · poligonal | Qué código de cálculo se usa (G-04) |
| **Encuentro** | máscara de lados | muro · acople · libre, por lado | Parante, falso puente o acople (N-78) |
| **Modelo** | `modeloRemate` | normal · invertido · bandera (doble puente) · compuesto | Dónde van las mochetas |
| **Modulación** | `textoModelo` | clásico · full corredizas · full fijos · ncfc · doble corrediza · triple corrediza | El patrón de hojas |

- N-01 ✅ **Circular es una forma, no una modulación.** Admite cualquier modelo y cualquier
  modulación, tramos, franjas y módulos. Su encuentro **solo puede ser muro**. 🐞 Hoy está metida
  en la modulación (`nci`) y se dibuja siempre como bandera.
- N-01b ✅ **Poligonal** es todo lo que no es rectangular ni circular: escalonado, tramo
  inclinado, vano triangular, etc. Lo que se puede hacer en rectangular o circular se puede hacer
  en poligonal, según el caso. En volúmenes distintos de plano, **cada lado** puede tener su propia
  forma poligonal. Hoy solo se diseña; los materiales se desarrollarán con pruebas (sección 12).
- N-02 ✅ **Pivotante es una tipología**, no un acabado. Pivotante, Nova mampara y Nova puerta
  **quedan sin desarrollar** por ahora (mampara y puerta, versión 2). 🐞 El código que trata
  pivotante como acabado (`PIV`) se elimina (C-01).
- N-03 ✅ **Puente preestablecido** (dictado el 2026-09-25):
  - **Aparente normal: Múltiple.**
  - **Todo lo demás: tubo 2⅜ x 1** (hecho, C-02; antes bandera y full corredizas usaban tubo 2 x 1).
  - **En L, C, serie y demás volúmenes se decide por lado.** Cada lado normal en aparente, o cada
    tramo normal de ese lado, lleva Múltiple como preestablecido, aunque los otros lados o tramos
    lleven tubo. 🐞 Hoy el puente es uno para toda la ventana (C-24).
  - Invertido y bandera **no admiten Múltiple**. Vale también para un tramo invertido o en bandera
    de un diseño editado: al volver de novaDiseño el puente pasa al tubo (hecho el 2026-09-25).
  - Excepciones por resistencia que siguen: INA ncfc → paflón 1; full corredizas con más de 4 por
    tramo → paflón 1.
- N-04 ✅ Mampara y puerta Nova: versión 2.

---

## 3. Qué entra (las medidas)

| Origen | Qué trae |
|---|---|
| A mano | ancho, alto, altura de hoja, divisiones, cruce, U, tipo de puente |
| Carrito de MainActivity | ancho, alto, cantidad, cliente, producto |
| MedidaActivity | lo anterior **más** contorno del vano, esquina (lados, ángulos, puente y parantes por lado), alféizar, boceto |

- N-05 ✅ **Esquina con descuadre**: manda la medida **mayor** y se avisa. En versiones futuras
  cada lado se calculará con su propia medida.
- N-06 ✅ **Descuadre**: si pasa de **2.5 cm (una pulgada)**, la ventana pasa a **forma poligonal**
  y cada lado se trata con su medida; si no, manda la medida mayor.
- N-07 ✅ **Contorno** (escalón, inclinado, triángulo): es forma poligonal; dentro se puede hacer
  todo (tramos, franjas…). Se desarrolla después de cerrar las reglas.
- N-08 ✅ **Curva**: el ancho es el **arco o desarrollo**; con cuerda, flecha o ambas se calcula
  lo demás.
- N-09 ✅ **Alféizar y dintel**: el alféizar ya llega de Medidas; hay que incorporar el **dintel,
  0 por defecto**, en Nova y en todas las calculadoras. El alféizar fija la altura de puente por
  ergonomía (G-08). El dintel es información para el usuario y para su configuración (G-10).
- N-78 ✅ **Encuentro** (transición): llega de Medidas, **por lado**, y en la calculadora activa la
  lógica de máscaras (los checks del diálogo de ivDiseño), que decide puente o parante:
  - **Libre a un lado** (lo más común: puerta-ventana sin puerta todavía): lleva **parante** para
    sostener el puente.
  - **Acople** (la puerta ya existe, del material que sea): la ventana se **acopla** a la puerta.
  - **Libre arriba**: lleva **falso puente**.
  - Nunca libre en los cuatro lados; **como máximo tres**.

---

## 4. Formato de números

- N-10 ✅ `df1` **trunca** a un decimal: 119.99 → 119.9.
- N-11 ✅ Entero sin decimales (120.0 → `120`).
- N-12 ✅ Líneas de material: `medida = cantidad`, una por línea, en el orden de G-01.
- N-13 ✅ Material con un decimal; dinero con dos; dos decimales solo en cotas de plano.
- N-14 ✅ **Vidrios siempre ancho x alto.** El vidrio de mocheta de INA salía alto x ancho;
  corregido (C-03).

---

## 5. Divisiones, orden de hojas y tramos

- N-15 ✅ **Regla de los 60**: `ceil(ancho / 60)`, mínimo 1. 120 → 2; 120.1 → 3.
- N-16 ✅ Si el usuario las escribe, **mandan** (G-03).
- N-17 ✅ En L y en C se cuentan sobre la medida medida; el usuario puede dejarlo así o editarlo.
- N-18 ✅ Al volver de novaDiseño se respeta el diseño que eligió el usuario.
- N-19 ✅ **Máximo automático: 5 hojas por tramo**, en APA y en INA, también en full corredizas.
  Si el usuario pone 10 o 20, se respeta. 🐞 Hoy full corredizas INA tiene tope 6 (C-04).
- N-20 ✅ Por modulación: doble corrediza 2, triple 3, ncfc 3; el resto automático o manual. En
  volúmenes distintos de plano, la misma lógica **por lado**. En circular, la misma lógica con la
  de la forma. Lo que falte se completa.
- N-21 ✅ 189.6 con 3 divisiones era error del ejemplo o decisión del usuario (G-03).
- **Un vano con forma desde Nova** (2026-10-01, Fide: dintel inclinado 214.5 × 180, 148.8 a la
  derecha). Respuestas del usuario:
  1. **En Nova se cambian la hoja y las divisiones**, con hojas iguales como en la regla de los 60
     (N-15, N-16): el diseño se rehace solo desde la forma, sin preguntar. Lo que no es hojas
     iguales —Nova desigual, anchos distintos, el reparto entre varios tramos—, aunque el vano tenga
     un solo tramo, se hace en novaDiseño. (Una primera lectura bloqueaba también la hoja y la
     cantidad de divisiones; corregido el mismo día.)
  2. **El ancho y el alto de un vano con lados desiguales** (más de 2.5, N-06, que es cuando viaja el
     contorno) no se editan en Nova: se cambian en novaDiseño. Dentro del descuadre el vano es
     rectangular y se edita normal.
  Si se cambian el ancho o el alto, Calcular ya no ofrece "descartar" (la ventana automática no
  conoce el contorno y salía rectangular): ofrece abrir novaDiseño o conservar el diseño. Pendiente en el motor único: la ventana automática
  (`VentanaAutomatica`) no conoce el contorno; el diseño desde la forma sale de `ContornoEnTramos`
  y entra al motor por el lector único.
- **La mocheta bajo un dintel inclinado** (dictado el 2026-10-01, Fide): la **U de arriba** es el
  dintel, el largo que trae el diseño de la medida (√(214.5² + 31.2²) = 216.7, truncado como todo,
  aceptado por el usuario; si el tramo se parte, cada parte lleva lo suyo, como un recto); la de
  abajo, sobre el puente, recta (con Múltiple no va). **Cada U de costado con su propia medida**: la
  mocheta menos lo que baja el dintel de ese lado (47.4 y 16.2). En INA, la U de cada borde con la
  mocheta de su lado. **Cada tee mide lo que baja el dintel donde queda**: con dos paños, el lado
  alto menos la mitad de lo que baja (Fide: 48.9 − 1.5 − 31.2 / 2 = 31.8). **Los vidrios de mocheta**:
  cada paño es el rectángulo que lo contiene (N-99), mismo ancho y el alto de su lado más alto
  (Fide: 106.7 × 48.4 y 106.7 × 32.8). **En INA** lo que llega al dintel mide lo suyo en su lugar:
  U parante de cada costado (177 y 145.8), la U de arriba por el dintel (216.7), cada fijo con el alto
  de su lado más alto (178.3 y 154.9) y la mocheta, el rectángulo que la contiene (52.4 − 7.8 = 44.6).
  **La U parante bajo la U inclinada** (dictado el mismo día): se corta en ángulo y mide su canto
  más largo, el que queda más alto en el ancho de la U, menos una U. Del lado bajo el dintel sube
  hacia adentro y la pieza sale más larga que restando dos U: INA 146 (antes 145.8), mocheta APA 16.4
  (antes 16.2). Del lado alto el canto largo es el de la pared y queda lo de siempre: INA 177
  (corregido por el usuario; contando la U inclinada más gruesa en vertical, u / coseno, salía 176.9).
  **El alféizar inclinado** (el vano de Fide al revés, 261.6 × 172, 115.2 a la derecha; dictado el
  mismo día). El sistema es un rectángulo (N-66): con el dintel recto y el alféizar inclinado cuelga
  del dintel y la mocheta va abajo, invertido, con tubo (N-03). Antes iba abajo, sobre el alféizar
  inclinado. La mocheta de abajo lleva lo mismo que la de arriba bajo el dintel: abajo la U inclinada
  (267.4), arriba la recta sobre el puente, cada costado su medida (el de la punta no lleva), paños
  del mismo ancho y distinto alto, tee en su lugar; en INA, la U de abajo por el alféizar, cada fijo y
  cada U parante con lo suyo. **El puente y la U recta de la mocheta miden su canto largo**, el que
  toca el sistema: 261.6, el ancho entero (corregido por el usuario: se medían 0.05 adentro de la
  pendiente y daban 261.3). Lo mismo para todo lo que el vano mide en el borde de una franja contra
  una pendiente (`HerramientasDiseno.enElBorde`); en un escalón no cambia nada. Prueba `DintelInclinadoTest`.
  **Los dos bordes inclinados** (2026-10-02, 88.4 × 174.2 con 114.4 a la derecha): sin borde recto
  donde apoyar el sistema va en **bandera** con tubo: mocheta abajo con lo que sube el alféizar
  (27.1), el sistema sobre lo más alto del alféizar y arriba la mocheta con el resto (32.7). Cada
  mocheta sigue su borde como las de arriba y abajo; donde una llega en punta al costado no lleva U.
  ✅ Respuesta del usuario (mismo día): **el sistema se cuadra donde cabe entero un rectángulo,
  dejando de lado la regla de bandera** (el reparto mitad y mitad de N-34). Los 110 de su dibujo los
  hizo a mano en novaDiseño, bajando la mocheta de abajo y subiendo la de arriba hasta que el sistema
  cupo; no salen de la regla. Con la hoja de siempre el rectángulo es lo que mide el lado bajo
  (114.4); si la hoja escrita es menor, se apoya sobre lo más alto del alféizar y lo que sobra va a la
  mocheta de arriba, que también cumple.
  **La casilla de mocheta inferior** (dictado el 2026-10-02: así se tiene más control): cuando la
  forma queda en bandera aparece, con la mocheta inferior del diseño medida como en N-34 (sin el
  puente). Cambiarla y calcular rehace el diseño desde la forma con esa mocheta, sin sacar al sistema
  de su rectángulo: abajo, por lo menos lo que sube el alféizar; arriba, lo que baja el dintel. Si lo
  pedido no cabe queda en el límite y Nova lo dice.
  ✅ La mocheta INA bajo un borde inclinado es el rectángulo que la contiene (N-99), y el vano al
  revés (261.6) lleva las 5 hojas de la regla de los 60: confirmados por el usuario el 2026-10-02.
  Prueba `DintelInclinadoTest`.
- N-22 ✅ **Orden preestablecido**: 2 `fc` · 3 `fcf` · 4 `fccf` · 5 `fcfcf` · 6 `fcffcf` · 7
  `fcfcfcf` · 8 `fccffccf`…; lo demás depende de la modulación.
- N-103 ✅ **En INA una corrediza nunca va en una esquina** (dictado el 2026-10-02). En una L en
  ángulo de dos hojas por lado salía `fc P fc`, con la corrediza del lado 1 contra la esquina; va
  `cf P fc`. El tramo que toca una esquina en ángulo con una corrediza se da vuelta, si así queda un
  fijo en la esquina sin poner una corrediza en la otra punta. ❓ Si ni dado vuelta se puede (dos
  hojas entre dos esquinas, en el lado del medio de una C; full corredizas), hoy queda como estaba.
  Código: `DisenoAutomatico.sinCorredizaEnLaEsquina`, prueba en `MedidasDeObraTest`.
- N-23 ✅ Por modulación (fijos / corredizas): clásico según el orden · full fijos todas/0 · full
  corredizas 0/todas · doble corrediza 0/2 · triple 0/3 · ncfc el inverso del clásico. Las
  combinaciones de nfc, n3c, etc. están en el diálogo.
- N-92 ✅ **La modulación se lee por tramo** (dictado el 2026-09-25). Un tramo de dos o más hojas,
  todas corredizas, es **full corredizas** aunque el resto de la ventana sea clásica (un diseño
  editado con un tramo `fccf` y otro `ccc`). El mismo tramo calcula igual venga de donde venga: el
  motor es uno. Ese tramo lleva:
  - el cruce del clásico con el mismo número de hojas (N-42): `ccc` cruza como `fcf`;
  - su vidrio de corrediza sin el −1.4 (N-59);
  - 1 riel por corrediza y el perfil doble corrediza, 1 por cada 2 corredizas, en lugar de U
    felpero o fijo corredizo (N-51, N-52b).

  Si la ventana lleva a la vez tramos clásicos y de solo corredizas, la doble corrediza sale en su
  propia fila, aparte del fijo corredizo o la U felpero, y se archiva con las demás. Hecho el
  2026-09-25 (`CompuestoTest`).
- N-24 ✅ **Máximo 5 módulos por tramo**, en automático. Un módulo grande no parte la ventana, y dos
  tramos de 10 cm siguen siendo dos tramos (G-03).
- N-25 ✅ Repartos iniciales: 6 → [3,3] · 8 → [4,4] · 10 → [5,5] · 12 → [4,4,4] · 14 → [5,4,5].
  Deslizando ivModelo se eligen otros.
- N-26 ✅ doble / triple corrediza y full corredizas: igual en APA e INA; los tramos por ventana se
  eligen en el diálogo de ivModelo. Las limitaciones de INA las dictará el usuario más adelante.
- N-27 ✅ **Parante entre tramos: 2.5 por defecto**, pero depende del puente elegido. El ancho útil
  de los tramos es `ancho − parante × (tramos − 1)`.
- N-28 ✅ **Tramos dentro del tramo**: se pueden poner parantes que parten **solo la mocheta o solo
  el sistema**, nunca de piso a techo (eso sería otro tramo).
- N-85 ✅ **Tramo bloqueado / libre.** Con tramos libres, al agregar o quitar módulos pueden variar
  las medidas de los tramos y de sus módulos. Un tramo **bloqueado** (congelado) no cambia por lo
  que se haga en otro tramo; y lo que se cambie dentro de él solo afecta a sus módulos, nunca a su
  ancho, su alto ni a nada de fuera. Ejemplo: 360 de ancho, dos tramos de 178.7.

---

## 6. Modelo y mochetas

- N-29 ✅ **Altura de hoja**: si no se escribe, `alto × 5/7`; si es mayor o igual al alto, no hay
  mocheta.
- N-30 ✅ Hay mocheta si `hoja < alto`.
- N-31 ✅ **La mocheta** = `alto − (altoHoja + puente)`, **igual en APA e INA**. Solo cambian el
  cálculo de sus vidrios y el dibujo.
- N-32 ✅ En INA, los **vidrios** y las U de la mocheta se calculan directo desde `alto − altoHoja`
  (atajo equivalente); la mocheta sigue siendo la misma (G-05, N-86).
- N-33 ✅ Orden de franjas, de abajo arriba: normal → sistema, mocheta · invertido → mocheta,
  sistema · bandera → mocheta, sistema, mocheta. **Compuesto (`x`)**: cualquier otro orden por tramo
  (mocheta abajo, otra mocheta, sistema…); se usará mucho en poligonales, casi siempre diseñadas a
  mano.
- N-34 ✅ **Bandera (doble puente)**: la mocheta inferior más la altura de hoja definen la
  superior; si la inferior es 0, se reparte en dos mitades. Igual en APA e INA.
  - Alto 160, puente 2.5, hoja 110, inferior 0 → cada mocheta `(160 − 110 − 2 × 2.5) / 2 = 22.5`.
  - Vidrio APA: con tubo, `22.5 − (u + 0.3)` (N-63; el dictado original decía "22.5 − alto de la
    U", corregido el 2026-09-25).
  - Vidrio INA: `(160 − 110) / 2 + 1` arriba, `− 0.5` abajo.
- N-100 ✅ **Crear mochetas en novaDiseño** (dictado el 2026-09-28, aplica N-29 y N-34):
  1. Si la medida trae altura de puente, se respeta.
  2. La primera mocheta que se crea deja el sistema en 5/7 del alto y la mocheta con el resto.
     También si el sistema llenaba el tramo (diseños armados antes del 2026-09-28, cuando la
     calculadora mandaba el escalonado así; desde entonces llega con N-29, ver N-101).
  3. Si antes de editar esa mocheta se crea otra, es la bandera: el sistema se queda como está y
     las mochetas se reparten el resto a partes iguales.
  4. Si antes de crear se editó una altura, lo editado se conserva: el cambio solo lo absorben las
     mochetas, aunque alguna haya sido editada.
  - El mismo reparto (sistema 5/7 y mochetas a partes iguales cuando nada está escrito) vale en el
    panel, el paquete, el motor, el dibujo y la vista en 3D: `HerramientasDiseno.repartirAlto`.
- N-35 ✅ Bandera lleva dos corridas de puente (solo la horizontal se duplica); el parante vertical
  entre tramos no se duplica; riel y U felpero siguen en 1 por tramo.
- N-36 ✅ **El dibujo** muestra la mocheta con los puentes descontados y dibuja cada puente con su
  valor: lo que hace hoy es correcto. (Nova todavía no genera planos.)
- N-80 ✅ El dibujo es solo visual; basta con aproximar.
- N-90 ✅ **Qué modelo sale en automático** (con alféizar y altura); siempre prevalece lo que decida
  el usuario:
  - Alféizar **dentro del rango** (el puente cae entre 70 y 120 del piso): **normal**, mocheta
    arriba sin límite de altura.
  - Alféizar **por debajo del rango**: hay que subir la corrediza (G-08): **invertido** si la ventana
    mide hasta 180, **bandera** (partida en dos) si mide más de 180.
  - Casos difíciles (muy alta con alféizar en el rango): decide el usuario. Compuesto: según el
    diseño.

---

## 7. Descuentos y esquinas

- N-37 ✅ **INA no descuenta** parante, puentes ni esquina. En esquinas **en ángulo** INA
  **aumenta** el ancho de la U (G-09). Sería bueno indicar el ángulo de corte de esas U (sin
  urgencia). En esquina curva, ni aumento ni descuento.

  | Descuento | APA | INA |
  |---|---|---|
  | Lado al vacío | resta el parante por cada lado | 0 |
  | Sin pared arriba o abajo | resta el falso puente | 0 |
  | Esquina en ángulo (L, C, serie) | resta el **esquinero** | **suma** el ancho de la U |
  | Esquina curva | cada pared resta **entero** el parante de su punta de curva (N-104); la curva, 0 | 0 |

- N-104 ✅ **APA en L con esquina curva: el ancho útil de cada lado** (dictado el 2026-10-02). Cada
  pared descuenta el parante de la esquina (el del esquinero, su medida normal: 2.5 con el tubo 2⅜,
  3.8 con el paflón 1½)
  en la punta donde empieza la curva, y todo el cálculo de ese lado (hojas, U, puente, riel, vidrios)
  se hace con ese ancho útil. La curva se queda con su desarrollo. Sin esquinero ("ninguno") no hay
  parante ni descuento. En Ele (150 + curva de 90 + 120): las paredes calculan con 147.5 y 117.5
  (`MedidasDeObraTest`); con paflón 1½, 146.2 y 116.2. INA sigue sin descontar (N-37).
  - **La curva**: su U de desarrollo no descuenta nada; el vidrio sí descuenta 0.5 por lado contra el
    parante, así que el vidrio curvo tiene un poco menos de desarrollo, flecha y cuerda que el
    aluminio (casi no se siente).

- N-38 ✅ **Esquina en APA**: el perfil lo define el esquinero. Reparto invertido: la ventana más
  ancha recibe la parte menor del aluminio.
- N-39 ✅ **El aumento de INA en esquina**: misma cantidad de piezas; no se muestra en las
  referencias (es interno). 🐞 Sin implementar (C-09).
- N-75 ✅ **En INA la estructura descuenta los parantes de cada tramo, incluido el de esquina; el
  vidrio no.** Se entiende así: el reparto de los tramos (su ancho en el diseño) resta los
  parantes, igual que en APA (N-27); lo que no se descuenta en INA son los vidrios y las U de
  fijos (N-37, N-42).
- ⚙️✅ Algunos perfiles se retiran en el parante; Nova ya lo hace.

**Ejemplo dictado: L inaparente con U-13** (G-02, N-42, N-43, N-44, N-46). Es incompleto, solo
para entender: lado 1 de 120, lado 2 de 40, alto 100 en ambos, hoja 80, modelo normal, modulación
`c f P<90> f`, mocheta `m(f)`, cruce 0.7. La corrediza del lado 1 topa la pared; el fijo del lado 1
va a la esquina; el fijo del lado 2 topa la pared.

| Pieza | Cálculo | Resultado |
|---|---|---|
| U de fijo, lado 1 | (120 + 0.7 cruce + 1 ancho de U) / 2 | 60.8 = 1 |
| U superior, lado 1 | 120 + 1 ancho de U (entera; con mocheta es 1, en bandera sería 2) | 121 = 1 |
| U del fijo, lado 2 | 40 + 1 | 41 = 2 |
| U parante | 100 − 2 × 1.5; solo el fijo del lado 2 topa pared | 97 = 1 |
| U parante de mocheta | (100 − 80) − 1.5 + 1.5; la corrediza del lado 1 topa pared | 20 = 1 |

Con U-3/8 la U parante de mocheta sería (20 − 1) + 1.5 = 20.5. El aumento de esquina usa el
**ancho** de la U (1), por eso 121 y no 121.5.

---

## 8. Perfiles

### 8.1 Puentes ⚙️

| Puente | Altura (cm) | Esquinero (cm) |
|---|---|---|
| Múltiple | 1.5 | 5 |
| tubo 2 x 1 | 2.5 | 5 |
| **tubo 2⅜ x 1** (por defecto, N-03) | 2.5 | 6 |
| tubo.c 1½ | 3.8 | 3.8 |
| tubo.c 1 | 2.5 | 2.5 |
| paflón 1½ | 3.8 | 8.25 |
| paflón 1 | 2.5 | 8.25 |
| tubo 2 x 2 | 5.0 | 5 |
| gorrito | 2.5 | 2.5 |

- N-40 ✅ **U**: la tabla de G-02, como **lista editable** donde el usuario agregue perfiles (C-10).
  El aumento de esquina en INA usa el **ancho** de la U elegida (U-13 → 1).
- N-41 ✅ **Cruce: 0.7 por defecto** (el espesor de la portafelpa); decide el usuario.
- N-71 ✅ El cruce es el mismo para fijos y corredizas: 0.7, o el que ponga el usuario, para todos.
- N-83 ✅ Ya no hay tablas de cruce ni de divisiones distintas por acabado: INA es igual a APA salvo
  las esquinas. 🐞 `cruce()` de INA todavía usa la tabla vieja (C-15).

### 8.2 U

- N-42 ✅ **U de fijos**: APA `(ancho − parantes + cruces × cruce) / divisiones`; INA
  `(ancho + cruces × cruce) / divisiones`, **más un ancho de U por cada esquina** en ángulo, que se
  reparte entre las hojas como el cruce (`(120 + 0.7 + 1) / 2 = 60.8`). **La hache y los vidrios
  también lo llevan**: salen de la U de fijo, como antes del motor único (dictado el 2026-10-02: en la
  L la hache salía 60.3 y el vidrio 59.8, 1 cm menos que la U, cuando la holgura es 0.5). **En la
  esquina se encuentran los dos fijos: el del lado más ancho descuenta 0.5 y el otro 0.2**, el de
  vidrio con vidrio (con los dos iguales, da lo mismo cuál: el primero lleva el 0.5): 60.3 y 60.6.
  **La mocheta de INA sobre las corredizas también lo lleva**, o su vidrio queda corto (59.5, no 59).
  El ancho de U de la esquina modifica todo lo que sale de las hojas. Los fijos seguidos van en
  una sola U. ✅ (2026-09-25) **Con tramos automáticos y ninguno bloqueado, todos los módulos son
  iguales**: el cruce se reparte en toda la ventana, aunque los tramos tengan distinto número de hojas
  (420 en 4 + 3 hojas: hache `60 = 3`). ✅ (2026-09-25) **Los cruces se cuentan por tramo**: un tramo
  de solo corredizas cruza como el clásico con el mismo número de hojas (N-92). Dora editada, 370,
  tramos `fccf` y `ccc`: 4 cruces → `(370 − 2.5 + 4 × 0.7) / 7 = 52.9`, igual que con `fcf`.
- N-43 ✅ **U parante**: APA `altoHoja − (u + 0.2)`; INA `alto − 2u`. **Cantidad**:
  - APA: una por cada colindancia de fijo con pared o parante.
  - INA: una por cada fijo que colinda con **pared o con otro material de acople**; **no** con
    parante ni en la esquina (ahí colinda con otro vidrio). Una L con un fijo contra pared en cada
    lado, de igual altura, lleva 2. Cuenta también el lado libre. Corregido el 2026-09-25: antes
    contaba por tramo, con los parantes (12 hojas en tres tramos daba 6 en lugar de 2).
- N-44 ✅ **U parante de mocheta** (INA), **una por cada corrediza que topa pared o acople**, cuando
  hay mocheta:
  - Normal (mocheta arriba): `(alto − altoHoja) − u + 1.5`. Con U-13 el −1.5 y el +1.5 se anulan:
    100 − 80 → 20; con U-3/8, (20 − 1) + 1.5 = 20.5.
  - Invertido: `(alto − altoHoja) − u` (sin el +1.5). Hecho (C-21).
  - Bandera: la mocheta de arriba como normal (+1.5) y la de abajo como invertido (sin él).
- N-45 ✅ **U de mocheta** (APA). La horizontal, el ancho de la mocheta: con Múltiple o gorrito 1
  por tramo (la de abajo la trae el puente), con los demás 2. (La mocheta es una superficie: ancho
  de mocheta × alto de mocheta.)
  - **Los costados** (dictado de nuevo el 2026-10-01): con **Múltiple o gorrito**, `mocheta − u`
    (solo topa la U de arriba; el canal de abajo lo trae el puente); con los **demás puentes**,
    `mocheta − 2u` (U arriba y abajo). Es lo que hacía el código original del usuario (NovaApa:
    `altoMocheta − us`) y el mismo criterio del tee (N-57). Un costado inclinado, igual sobre su
    diagonal (Vna4: 48.1 − 1 = 47). El motor restaba 2u con todos los puentes hasta ese día.
    145 × 140, hoja 100, U-13: Múltiple `37 = 2`, tubo `34.5 = 2`.
- N-46 ✅ **U superior** (INA): el ancho, 1 pieza; **en bandera, 2**. Con esquina: ancho del lado +
  ancho de la U. Es un caso de N-93.
- N-93 ✅ **La U de cada borde en inaparente** (dictado el 2026-09-25). La U horizontal sigue al vidrio
  a lo largo del borde de arriba y del de abajo; **todo lo que apoya seguido en ese borde va en una
  sola U**. U de fijos, U superior y U inferior son esa misma pieza:
  - Un tramo **con mocheta en ese borde** lleva U de punta a punta: su mocheta, sus fijos y el paso
    de sus corredizas.
  - **Arriba**, un tramo sin mocheta arriba (invertido, o normal sin puente) lleva **fijo
    corredizo, que ya incluye su U**: ahí no hay U y se corta.
  - **Abajo**, un tramo sin mocheta abajo: cada racha de fijos apoya en su U; las corredizas la
    cortan, porque van sobre el riel.
  - **El parante no la corta**: el vidrio pasa por delante (N-75). La U de un fijo llega hasta el
    **centro del parante**, y un tramo cubierto se mide con su ancho de tramo, que empieza al otro
    lado del parante. Por eso el parante suma entero entre dos tramos cubiertos (210 + 2.5 + 157.5 =
    370), **la mitad** entre un fijo y un tramo cubierto, y nada entre dos fijos, porque cada U ya
    lleva su mitad (420 en `fccf P fcf`: 60.4 + 60.4 = 120.8).

  Dora editada en inaparente (370: `fccf` normal, P 2.5, `ccc` en bandera): arriba `370 = 1`; abajo
  `53.2 = 1` (fijo izquierdo) y `212 = 1` (fijo derecho 53.257 + medio parante 1.25 + tramo 157.5 =
  212.007; con el 53.2 ya truncado daría 211.9, ver G-11). Normal:
  abajo una U por racha de fijos, arriba de lado a lado. Invertido: abajo de lado a lado, arriba
  nada. **Bandera: una U de lado a lado en cada borde (`2`), sin U de fijos aparte**; hasta el
  2026-09-25 el motor sacaba además la U de fijos (bandera de 180 en `fcf`: `60.4 = 2`, que ya no
  va). Hecho en el motor (`CompuestoTest`).
- N-47 ✅ **Full fijos**: U horizontal = ancho del tramo, 2 piezas; con esquina en INA, más el ancho
  de la U.
- N-48 ✅ Con 1 división: U de fijos × 2 y U parante × 2.

### 8.3 Puente, riel, felpero y los demás

- N-49 ✅ **Puente = ancho del tramo.** Una corrida si no es bandera ni compuesto; 2 en bandera; en
  compuesto, según el diseño.
- N-50 ✅ Cantidad de puentes según el modelo y la cantidad de tramos. (Se abandonó la función que
  solo llegaba a 15 divisiones.)
- N-51 ✅ **Riel** = puente − 0.06; sin mocheta, `ancho − 0.06`. Solo si hay corredizas. En APA e
  INA, **doble corrediza (ncc) lleva 2 rieles, triple corrediza (n3c) 3**, y así sucesivamente.
  **Full corredizas: 1 riel por corrediza**, también en el tramo de solo corredizas de un diseño
  editado (N-92).
- N-52 ✅ **U felpero** (INA) y **fijo corredizo** (APA) = la medida del riel. U felpero en INA con
  mocheta; fijo corredizo en APA cuando el puente superior no es Múltiple o no hay puente superior.
  (Revisar en código, C-19.)
- N-52b ✅ **Perfil doble corrediza**: en **full corredizas**, doble y triple corrediza **reemplaza**
  a la U felpero (INA) y al fijo corredizo (APA); en un diseño editado, en cada tramo de solo
  corredizas (N-92). Cantidad: **1 por cada 2 corredizas** (3 corredizas
  → 2). Doble corrediza, fijo corredizo y U felpero son **rieles superiores**: miden **igual que
  el riel** (puente − 0.06; confirmado el 2026-09-25). Ejemplo dictado, solo para entender: en la L, el lado de 120 (el mayor) resta el ancho
  del parante de esquina (2.5 por defecto), en APA y en INA → `117.5 = 2`. 🐞 C-22.
- N-53 ✅ **Tubo** (APA con mocheta) = el ancho, **1 por mocheta**.
- N-54 ✅ **Hache** = la U de fijos, **1 por corrediza**. En Nova desigual hay una excepción que ya
  funciona: hay que ubicarla y documentarla aquí (C-11).
- N-55 ✅ **Portafelpa** = `altoHoja − 1.6`; 2 por corrediza + 1 por cada lado de fijo que toca una
  corrediza.
- N-56 ✅ **Tope** = `altoHoja − 0.9`, **1 por cada corrediza que colinda con pared o parante** (ncfc
  → 2). Hecho (C-05); antes solo salía con 2 divisiones.
- N-57 ✅ **Tee** (APA, mocheta) = `altoMocheta − u` (Múltiple o gorrito) o `− 2u`; una por unión
  entre paños de mocheta.

---

## 9. Vidrios

### 9.1 Corrediza

- N-58 ✅ `hache − 1.4` x `altoHoja − 3.5`.
- N-59 ✅ **Full corredizas**: `U de fijos` x `altoHoja − 3.5`, sin el −1.4, porque se coloca en otro
  sentido (contra el polvo y la lluvia). También el tramo de solo corredizas de un diseño editado
  (N-92). Doble y triple corrediza siguen con el −1.4.

### 9.2 Fijo

- N-60 ✅ **Alto**: `alto − (u + holgura)`, holgura 0.2 con U y 1 sin U. En APA el alto es la hoja;
  en INA el alto completo.
- N-61 ✅ **Ancho** = U de fijos menos la colindancia de cada lado: **pared o parante 0.5** · otro fijo
  0.2 · corrediza 0. Igual en APA e INA. Hecho (C-06); antes pared o parante descontaba 0.4.
  Full fijos y el fijo solo (1 división) no van por colindancia: cada vidrio descuenta una gruña
  (0.4, N-87, N-91).

### 9.3 Mocheta

- N-62 ✅ **Paños por tramo**: `ceil(ancho del tramo / 180)`; el ancho del tramo se reparte.
- N-63 ✅ **APA**: ancho del paño − 0.5; alto = mocheta − 0.5 (Múltiple o gorrito) o **mocheta −
  (u + 0.3)** con los demás puentes, en todos los modelos, bandera incluida (dictado el 2026-09-25).
  Dora editada con U-13: mocheta 37.5 → **35.7**; mochetas de bandera 17.5 → **15.7**. Hecho en el
  motor, en Nova con ceil y en Nova desigual. (Ese mismo día se aplicó por error `mocheta − u + 0.3`.)
- N-64 ✅ **INA**: alto = `alto − altoHoja + 1` la de arriba, `− 0.5` la de abajo en bandera (N-34);
  invertido `alto − altoHoja − 0.5`. **Los vidrios de mocheta superior e inferior deben tener el
  mismo ancho** (C-08). ✅ (2026-09-25) **Ancho**: lo que dejan las U de fijos menos una gruña de
  **0.3 a cada lado**, con las U en float (G-11): 140 en fcf, U de fijo 47.133 → `140 − 2 × 47.133 −
  0.6 = 45.13` → **45.1**. (El ejemplo dictado restaba la U ya truncada, 47.1, y daba 45.2; quedó
  superado por G-11 el mismo día.) Sobre dos o más corredizas juntas, lo mismo: un solo
  vidrio con 0.3 a cada lado.
- N-86 ✅ En INA la mocheta sigue siendo un espacio; el cálculo directo desde `alto − altoHoja` es un
  atajo. En INA el **vidrio** de mocheta superior es **+ 1** y la **U parante de mocheta + 1.5**
  (N-44).
- N-87 ✅ **Ancho del módulo de mocheta**, APA: con corrediza, como sistema; solo con fijos, como
  full fijos, o la lógica que existe: descontar las **gruñas** del ancho del tramo y dividir en n
  partes. INA: como está en Nova con ceil.
- N-91 ✅ **Gruña** = el espacio de separación entre vidrios. Lo recomendable es 0.3 en ventanas
  pequeñas y 0.4 en grandes; **por ahora se sigue usando 0.4**, que es el máximo: la gruña no debe
  ser demasiada. (Es el mismo 0.2 + 0.2 de dos fijos que colindan, N-61.) En la mocheta de INA se
  usa 0.3 a cada lado (N-64).
- N-88 ✅ **Corrediza en mocheta**: solo APA (INA no la admite). Esa mocheta **se convierte en
  sistema**, con toda su lógica. No se admite sistema donde hay deformaciones (poligonal,
  circular).

---

## 10. Volumen: L, C, serie y curva

| Caso | Dibujo | Materiales |
|---|---|---|
| Plana | ✔ | ✔ |
| En L, en C, serie | ✔ | ✔ cada lado aparte; esquina según G-09 |
| Curva | ✔ | ✔ el ancho es el arco (N-08) |

- **La esquina de la L escrita a mano** (dictado el 2026-10-02): en Nova, debajo del lado 1, el
  bloque "Esquina curva" con flecha, cuerda y desarrollo. Vacío, la esquina es en ángulo; con algún
  valor, curva, por el mismo camino que la esquina curva que llega de Medidas (N-84). Con dos de las
  tres medidas el arco queda dicho; con una sola se toma un cuarto de círculo (la L dobla 90°). El
  lado que se escribe se nombra junto a sus casillas ("Lado 2"), ya no en el título; "Otros datos"
  se quitó. Prueba `EsquinaCurvaEscritaTest`.
- **La imagen de Nova de una ventana que dobla** (L, C, serie, esquina curva; pedido del usuario el
  mismo día) sale de la vista 3D en perspectiva, no del dibujo plano, que la finge doblada y se ve
  deforme. La vista 3D dibuja ahora las piezas con su medida sobre cada pared: el puente como banda
  de su alto, el parante con su ancho, la hache abajo de la corrediza y las tres rayas del vidrio.
  Correcciones del usuario (mismo día): **se mira desde adentro**, que es como se mide y se trabaja;
  **desde afuera es una opción** (diálogo de opciones de Nova, "Vista"). La imagen va **con el estilo
  del dibujo plano** (vidrio blanco, piezas en negro) y con el puente, el parante y la hache 3.5 veces
  más gruesos que su medida (con el doble se confundían con las líneas comunes), que si no en la
  imagen chica no se distinguen. También el parante entre una pared y la siguiente (la esquina, las
  puntas de la curva), que salía como la raya del marco. El lado 1 va de frente y se gira hacia el lado
  que deja la ventana más abierta. En INA se dibuja como en el plano: los fijos de piso a techo y la
  mocheta solo sobre cada racha de corredizas, como un recuadro; la estructura no se ve: sin puente y
  con los parantes como línea, y a la vista solo la hache; al cambiar el acabado, Nova recalcula y
  redibuja sola. La vista 3D de novaDiseño sigue con su azul. La ventana plana sigue
  con el dibujo de siempre.

- En esquinero **cada lado es otro tramo**, unido por un parante de esquina `P<90>` (N-84).
- En una esquina curva, al empezar y al terminar el arco van parantes: el arco es otro tramo (N-84).
  **En el motor desde el 2026-09-29**: la curva que trae la medida entra como un lado más, con su
  desarrollo de ancho, sus hojas por la regla de los 60 y un parante en cada punta. La curva no
  descuenta nada; en APA cada pared descuenta entero el parante de su punta (N-104, 2026-10-02). Antes la curva no entraba en el cálculo: en Ele (150 +
  curva de 90 + 120) faltaban sus dos hojas, su puente de 90 y sus vidrios, y salía un solo parante.
- **N-75 en L, C y serie inaparente** (aplicado el 2026-09-29): la estructura de cada lado (puente,
  riel, U felpero) llega hasta el parante de esquina, con el reparto de N-38; el vidrio y sus U no
  descuentan nada (la U superior sigue siendo el lado + el ancho de la U). En Ele (80 + 210.9) el
  puente pasó de 80 y 210.9 a 74 y 208.4, lo mismo que en aparente.
- **La cara asimétrica de una esquina** (hallado por el usuario el 2026-09-29, Formas 259 × 138.4:
  la segunda cara mide 120 abajo y 189 arriba). La esquina del botón v.esquina no traía silueta y la
  cara llegaba a novaDiseño como su rectángulo de 189. Ahora la silueta sale de sus medidas (lo que
  pasa de 2.5, N-06): lo que sobra de un ancho va a la punta libre (izquierda en la primera cara,
  derecha en la última); dos altos distintos inclinan el dintel. Nova la calcula desde el diseño y
  esa cara con las reglas de las formas (N-96, N-99).
  - **El diseño sugerido de una cara con silueta sale de su contorno** (2026-09-30), con N-101,
    como el de un vano de un solo paño. La cara de Formas 13 es un trapecio: `f c f` (60, 60 y el
    fijo inclinado de 69, ver N-99 abajo).
- **El trapecio sugerido** (2026-09-30, por analogía con Vna4, N-99): un tramo, el fijo contra el lado
  inclinado y las corredizas en el rectángulo (N-67). El rectángulo se reparte con la regla de los 60
  y el costado inclinado lleva su fijo. ❓ En Vna4 el usuario puso 3 hojas (`f c f`); con esta cuenta
  salen 4 (`f c c f`: 133.2 del rectángulo pide 3). Confirmar cuántas.
- **La altura de puente editada en cada lado** (2026-09-29): al volver de novaDiseño, las
  referencias salen lado por lado con su puente; salían como una sola ventana. Y la hoja escrita se
  conserva: si las franjas no suman el alto (la mocheta viene sin el puente), la diferencia la
  absorben las mochetas (N-100); antes se estiraba todo y una hoja de 80 salía de 81.7.
- **La ventana curva dibujada con su figura** (la plantilla de la curva vista de frente) llega a Nova
  como curva, con su desarrollo de ancho (N-08). Antes llegaba como el rectángulo de su cuerda: en
  Pipa, 120 de cuerda con flecha 40.2, todo el aluminio horizontal salía de 120 en lugar de 158.9.
- N-102 ✅ **El curvado** (dictado el 2026-10-01). Nova curva, antes de instalarse, pasa por la
  roladora: cada perfil **horizontal** lleva su **cuerda** (C) y su **flecha** (F), calculadas con
  el arco de la pieza (su medida) sobre el radio de la curva medida, y su **ampliación**: la roladora
  sujeta el perfil **5 cm en cada punta**, así que se corta la medida + 10 (108 → 118) y, curvado,
  se recorta. Lo vertical no se curva. En pantalla va en la vista completa (el ▼ de "Cálculos"):

  ```
  108 = 1
  C = 97
  F = 16.1
  Ampliación 118 = 1
  ```

  (números del ejemplo del usuario, ilustrativos). **Los vidrios también** (dictado el mismo día):
  su ancho es el arco y llevan C y F, **sin ampliación** (`60.4 x 126.9 = 4`, `C = 59.4`,
  `F = 4.5` en radio 100). Respuestas del usuario (mismo día):
  1. **El radio es el de la línea medida.** Los perfiles van como 1 cm más adentro, pero correrlos en
     paralelo no cambia nada y, si cambia, es preferible que la medida quede grande.
  2. **Se archiva con la ampliación** y con la marca **curvo**, para saber a dónde mandar la pieza
     después del corte: `118 = 1 curvo` (el vidrio, `60.4 x 126.9 = 4 curvo`, sin ampliación). Va en
     su misma lista (u-13, puente…), porque sale de la misma barra; la marca queda en la lista
     archivada y aparece en la ficha y en la etiqueta del optimizador (`Vna5 pipa,u-13 curvo`).
  3. **Las esquinas curvas de L, C y serie** se tratan igual: el lado de la curva se curva con el
     radio de su desarrollo y su cuerda; las paredes rectas, no.
  4. **En una S, cada pieza con el radio del arco donde va.** Cada arco es su tramo; el puente, el
     riel o el vidrio de un tramo toman su arco, y lo que pasa por varios (la U de lado a lado) se
     curva por trozos, cada uno con su largo, C, F y hacia dónde va la panza. Si las divisiones se
     cambiaron y los tramos ya no son los arcos, se usa la curva equivalente.

  **Lista para el curvador o el horno** (Taller → Herramientas → vidrios curvos, mismo día): junta
  los vidrios archivados como curvos de los proyectos que se elijan, con desarrollo, alto, cantidad,
  cuerda, flecha, radio, tipo de vidrio y ventana, y se comparte como texto. Por eso cada pieza curva
  se archiva también con su cuerda y su flecha (`60.4 x 126.9 = 4 curvo C59.4 F4.5`; las de varios
  arcos, solo `curvo`). Código: `optimizadores/ListaVidriosCurvos.kt`, prueba `ListaVidriosCurvosTest`.

  **Las caras del vidrio curvo** (dictado el mismo día). Un reflejante tiene color por una cara y
  espejo por la otra: al curvarlo una queda cóncava y la otra convexa, y en una S cambia de un arco
  al otro.
  1. **La dirección se toma desde adentro.** Fuera del 3D todo se mide desde adentro (en 3D la medida
     ya lo dice): una flecha negativa de Medidas es una panza hacia adentro. Escrita a mano en Nova, y
     en la curva de una esquina de L, C o serie, hacia afuera.
  2. **Las caras se preguntan al archivar**, porque Medidas no sabe nada del vidrio: si la ventana
     lleva vidrio curvo, el diálogo pregunta si tiene caras. Sin caras, el vidrio se voltea y no pasa
     nada (`[sin caras]`). Con caras, cuál es la especial (espejo) y si es la que se ve desde adentro.
  3. **Se dice cóncava o convexa** (el curvador lo entiende, y si no, lo aprende). Panza hacia afuera:
     la cara de afuera queda convexa y la de adentro cóncava; hacia adentro, al revés.
     `60 x 120 = 2 curvo C59.1 F4.4 afuera [espejo convexa]`. Dos vidrios iguales con la cara al
     revés no se suman; la lista los ordena por cara, y la etiqueta de planchas y la ficha lo llevan.
     Los perfiles llevan hacia dónde va su panza (`u-13 curvo afuera`), por la roladora.
  4. **Cambiar de cara sin recalcular** (mismo día): en vidrios curvos, el toque largo en un vidrio
     de la lista lo muestra en planta (interior abajo, exterior arriba, la panza hacia donde va).
     Encima del arco está el nombre de la cara de afuera (espejo o color); al tocarlo pasa a la otra,
     la clasificación cambia (cóncava ↔ convexa) y se guarda en la pieza archivada del proyecto.
     Código: `VistaCaraCurvo.kt`, `ListaVidriosCurvos.alternarCara`.

  Código: `motor/CurvadoNova.kt` (las piezas llevan su tramo, `LadoNova.radios`), prueba
  `CurvadoNovaTest`. Las medidas del motor no cambian: solo se marca qué se curva.

---

## 11. Forma circular (primer dictado, N-65) ✅

- **INA**: la U es el **perímetro** (la circunferencia) = 1.
- **APA** en automático: el modelo pasa a **bandera** aunque se haya elegido otro (correcto).
- **Puente** (primordial en esta forma): en un círculo de 180 × 180 se calculan las mochetas como
  en rectangular, o se usa la altura de hoja. Mocheta + puente es una **flecha**; la cuerda trazada
  en esa flecha es la **medida del puente**.
- **U felpero, fijo corredizo, riel**: dependen del puente (su misma medida).
- **U de mocheta**: el arco entre donde empiezan y terminan los puentes.
- **U de fijo**: desde donde empieza el fijo contra la pared hasta donde empieza el puente. La U de
  fijos por ceil vale solo para el vidrio. **U parante**: el arco entre el puente inferior y el
  superior. Con 5 divisiones, la U del vidrio del centro va con ceil.
- **Hache**: la división por ceil. **Portafelpa**: sin cambio.
- **Tee**: cambia de altura. Con 2 paños de mocheta vale la lógica rectangular; con 3 o más también
  (sale algo más larga y se recorta en obra), o mejor calcularla geométricamente, lo que servirá
  también para circular y compuesto.

**Regla (dictada el 2026-09-27): cada perfil se mide donde todavía toca el muro.** Su base puede
tocarlo antes; su alto sube hacia el centro, donde el círculo es más ancho, y el perfil se corta a
esa medida. Ejemplo del usuario: la U de fijo toca con la base en 33.4 y con el alto (U 3/8, alto 1)
todavía toca más afuera.

**En el motor (2026-09-26, `Circulo.kt`, `CircularTest`)**, con el ejemplo de 180 × 180 APA en
bandera con tubo (hoja 5/7 = 128.6, mochetas 23.2, flecha 25.7):

| Pieza | Cálculo | 180 × 180 |
|---|---|---|
| Puente | cuerda en la flecha (mocheta + puente) | 125.9 = 2 |
| Riel | la cuerda donde su alto (1.2) todavía toca el muro − 0.06 | 128.3 |
| Fijo corredizo | puente − 0.06 (N-51); falta su alto para medirlo como el riel | 125.9 |
| U de fijo | el fijo desde donde su perfil todavía toca el muro: la base toca en 33.4, su alto (1) sube hacia el centro | 60.5 − 26 = 34.4 = 2 |
| U de mocheta | el arco entre los extremos del puente, por su borde de afuera | 132.2 = 2 |
| U recta de mocheta (solo con tubo) | la cuerda sobre el puente, como la rectangular | 120.6 = 2 |
| U parante | el arco más largo del perfil entre los dos puentes (su borde de adentro, 143.6) − (u + 0.2) como N-43 | 142.4 = 2 |
| Hache, portafelpa, tope, vidrios corredizos | sin cambio | 60.4 · 126.9 = 4 |
| Vidrio de fijo | ancho por ceil; alto, lo que el círculo le deja (entero, se corta en obra) | 59.9 x 127.3 |
| Vidrio de mocheta | el rectángulo que contiene el segmento (N-99) | 120.1 x 21.9 = 2 |
| INA | la U es el perímetro | 565.4 = 1 |

Sin confirmar (tomado por analogía): el arco de mocheta por el borde de afuera del puente (por el
de adentro sería 139.5), la U recta con tubo, el − (u + 0.2) de la U parante, el vidrio de mocheta
por su rectángulo, y ancho ≠ alto como elipse (el dibujo usa un círculo del lado menor). Solo un
tramo; con varios tramos sigue la lógica rectangular. Pendientes: tee geométrica y "con 5
divisiones, la U del vidrio del centro va con ceil".

## 12. Forma poligonal (se desarrolla con pruebas)

- N-66 ✅ En Nova **no existe jamba** (es de ventanas estructuradas). Primero se evalúa la forma:
  - Rectangular con un solo corte en cualquier lado → lógica rectangular.
  - En la **mocheta** se acepta cualquier forma (inclinada, recortada…).
  - El **sistema** tiene que ser un rectángulo perfecto, con descuadre máximo de 2.5 en altura.
  - **Escalonado**: cada escalón es un tramo.
- N-67 ✅ Vano triangular o tramo inclinado: la corrediza va donde queda **entera**.
- N-68 ✅ Cada cálculo se aproxima al rectángulo.
- N-69 ✅ Lo que no se sabe calcular se resuelve con pruebas, caso por caso.
- **Orden de trabajo dictado**: primero dejar bien Nova con ceil y Nova desigual; después las
  formas, con pruebas.
- **Paso 1 (2026-09-25): lo diseñado en novaDiseño se conserva al volver a Nova.** Con un vano de
  contorno, el siguiente Calcular rehacía el diseño desde el contorno si la hoja del diseño editado
  no era la con que se armó. Ahora lo que vuelve del editor pasa a ser la referencia; el vano solo
  se rehace si el vidriero escribe otra hoja. Prueba en el celular: `DisenoEditadoSeConservaTest`
  (el escalonado de 446.3 × 160).
- N-94 ✅ **El parante en un escalón** (dictado el 2026-09-26). Un parante **nunca queda centrado en
  la esquina de un escalón**, salvo que el usuario lo decida. Va **dentro** (del lado del tramo bajo)
  o **fuera** (del lado del tramo alto). Cuando cae en un escalón (puesto o movido con el imán, o
  porque el diseño llegó así), novaDiseño pregunta dentro, fuera o centrado, con las medidas de cada
  opción, y no se cierra sin elegir. El parante mide el alto del vano donde queda; si pisa los dos
  lados (centrado), el mayor. Vano de Formas (338.3 × 144.5, escalón a 104.6):

  | Opción | Parante | Tramo 1 | Tramo 2 |
  |---|---|---|---|
  | Dentro (elegida por el usuario) | 77.2 | 102.1 × 77.2 | 233.7 × 109.4 |
  | Fuera | 144.4 | 104.6 × 77.2 | 231.2 × 109.4 |
  | Centrado | 144.4 | 103.3 × 77.2 | 232.4 × 109.4 |

  El alto de cada tramo es el rectángulo donde entra el sistema (N-66); con un descuadre de hasta
  2.5 manda la medida mayor (N-06): el alféizar del recorte mide 109.4 y 108.5 → 109.4.
- N-97 ✅ **El puente en un escalón** (dictado el 2026-09-26): lo mismo que el parante (N-94). Si un
  puente cae en un escalón, novaDiseño pregunta dentro, fuera o centrado con las medidas. Dentro deja
  el puente entero del lado angosto: en el tramo invertido de Formas, en la mocheta, y así el
  sistema queda un rectángulo limpio y el puente mide lo que la mocheta.

  | Opción (Formas, tramo 2) | Puente | Sistema | Mocheta (espacio) |
  |---|---|---|---|
  | Dentro (elegida por el usuario) | 151.7 | 233.7 × 109.4 | 151.7 × 32.6 |
  | Fuera | 233.7 | 233.7 × 106.9 | 151.7 × 35.1 |

  El alto del puente es el del puente elegido en Nova. Con esto, el puente del tramo 2 de Formas
  mide **151.7** (respuesta a la duda de N-49 en formas).
- N-99 ✅ **Un lado inclinado: la geometría se mide módulo por módulo** (dictado el 2026-09-26,
  "polis" Vna4: trapecio de 208.6 × 156.8, lado izquierdo inclinado de x 0 arriba a x 75.4 abajo,
  U-3/8, Múltiple). Antes se achicaba la franja entera en proporción (un fijo recto salía de 58.5
  cuando mide 65.2) y el fijo inclinado se trataba como "recortado" (78 × 35.1, tope de sobra).
  - **El fijo contra el lado inclinado lleva el vidrio entero**, el rectángulo que lo contiene, y se
    corta en obra: `56.4 x 110.8`.
  - **Su U: primero la de abajo** (su borde de abajo, "como 2 o 3 cm": `3.1`) **y sobre ella la
    diagonal**: el largo del lado menos el alto de la U (124.3 − 1 = `123.2`, como el 99 → 98 de G-01).
  - **La mocheta:** su U horizontal de arriba es la del rectángulo mayor (`208.6`; la de abajo la
    trae el Múltiple); la de su lado inclinado va en diagonal (48.1 − 1 = `47`); su vidrio, por el
    rectángulo mayor (dos paños de 104.3 → `103.8 x 42.8`).
  - **El riel:** del lado inclinado al otro, más lo que su propio alto (1.2) sube por la pendiente:
    133.2 + 1.2 × tan − 0.06 = `133.7`. *(El 1.2 y el − 0.06, a confirmar.)*
  - Un recorte tipo viga o escalón (N-96) solo si el borde que corta es horizontal.
  - **Los módulos repartidos por igual se reparten lo que el vano deja de su franja**, no el ancho
    del tramo (dibujo del usuario): el sistema mide 187.1 en su borde de arriba → 62.4 cada módulo;
    lo que queda fuera del vano (21.5) va al módulo de la punta, y el fijo inclinado abajo mide 8.5
    (133.2 − 2 × 62.4). Antes salían 69.5 cada uno y el parante caía fuera del alféizar. Una franja
    que llega de lado a lado (la mocheta: 208.6) no cambia; un borde a menos de 2.5 cuenta como recto.
  - **El ancho de un módulo (diálogo) es lo que mide dentro del vano**: 62.4, no 55.9 ni 75.2 (antes
    se achicaba en proporción a la franja). Al escribirlo, los demás se reparten el resto del hueco y
    el módulo de la punta conserva lo que tiene fuera del vano.
  - **Lo ya escrito se queda** (pedido del usuario, 2026-09-26): al escribir el ancho de otro
    módulo, los editados antes conservan su medida y solo los no editados absorben el cambio. Si no
    queda sitio para ellos, avisa y no se aplica. Vale con forma o sin ella; se olvida si la franja
    cambia de módulos o se limpia el diseño.

  Hecho en el motor y en el panel del editor (`FormasTest`).
- N-101 ✅ **El diseño sugerido de un vano escalonado sigue las reglas** (pedido el 2026-09-28: "ese
  diseño sugerido también tiene que seguir las reglas"). Cuando Nova arma el diseño desde el
  contorno de Medidas, cada escalón es un tramo (N-66) y:
  - un desnivel de hasta 2.5 es descuadre: el tramo queda recto y manda su medida mayor (N-06);
  - sin hoja escrita, el sistema es 5/7 del alto **del tramo** y la mocheta el resto (N-29, con el
    reparto único de N-100); con la hoja escrita, esa hoja, y si llega al alto del tramo no hay
    mocheta (N-30): una sola franja, **nunca una mocheta de alto 0**;
  - cada corte deja medio parante a cada lado (N-27): tramos más parantes suman el vano.

  Formas sin hoja escrita: `103.3 × 77.2` (55.1 + 22), `149.2 × 144.5` (103.1 + 41.2), `80.7 ×
  109.3` (78.1 + 31.2). Antes: el tramo 2 salía `s<144.4>; m<0>` (0.06 de dintel inclinado creaba
  una mocheta vacía), el 3 `s<103.2>; m<6.1>` (el descuadre de 0.9 se leía como pendiente) y los
  anchos no dejaban sitio a los parantes (343.3 en un vano de 338.3). El diseño sugerido y Limpiar
  sacan los escalones de la misma función (`ContornoEnTramos.escalones`). `ContornoEnTramosTest`.
  - **Cada escalón calcula su mocheta con SU alto** (corregido el 2026-09-29, `Tramo.alto` en el
    motor): en Formas el tramo 1 mide 77.2 y su vidrio de mocheta es 102.8 x 20, no 102.8 x 87.3
    (se tomaba el alto del vano entero). Lo mismo el fijo y la U parante de inaparente.
  - **Inaparente en un escalón** (2026-09-29, deducido de N-93 y N-43): la U de arriba y la de abajo
    se cortan en cada escalón (solo va en una pieza lo que apoya seguido) y cada lado se queda con medio
    parante; el fijo junto a la pared del escalón lleva U parante del alto del escalón − 2u. En Formas:
    arriba `104.5` y `233.8` en lugar de `338.4`; U parante `65.3` y `33.1`. ❓ Una corrediza junto a
    la pared de un escalón (su mocheta) todavía no lleva esa U.
  - **Full fijos en inaparente invertido** lleva su U arriba y abajo (N-47); se quedaba sin ninguna.
  - **Un costado casi vertical que se desvía hasta 2.5 es descuadre** (2026-09-29): no arma un tramo.
    Formas 11 salía con dos tramos de 1 cm (vidrios de 0.2 x 42.4 en Ele). Y dos trozos seguidos que
    solo se diferencian en un descuadre son el mismo tramo.
  - ❓ Con la hoja escrita (103.2) el recorte de Formas, que sube 35, queda con una mocheta de 6.1:
    es N-29 al pie de la letra (la hoja es menor que el alto del tramo).
  - 🐞 Los vanos con lados inclinados de verdad (triángulo, trapecio) siguen por el camino viejo:
    tampoco descuentan el parante entre sus trozos.
  - **Un tramo inclinado es su rectángulo contenedor** (N-68, N-99; arreglado el 2026-09-28). Sus
    franjas se reparten desde su dintel más alto hasta su alféizar más bajo, y la silueta las
    recorta. Esto vale en el modelo (`altoDeTramo`, `caidaDeTramo`), el dibujo, la 3D y el motor.
    Antes contaba solo el lado izquierdo. En la medida del usuario, 232.4 × 164.2 con 109.1 a la
    izquierda, sistema y mocheta se apretaban en 109.1 (72.5 + 36.6): se veían dos puentes y el
    triángulo de arriba no se podía tocar. Ahora queda así: sistema 109.1 y mocheta 55.1, con la
    mocheta de punta a punta. La corrediza va en el lado bajo, donde entra entera (N-67). Pruebas:
    `ContornoEnTramosTest` y `el_triangulo_de_un_dintel_que_sube_es_de_la_mocheta` en el celular.
- N-96 ✅ **Estrategia de cálculo de las formas: partir en rectángulos** (dictado el 2026-09-26). Un
  tramo que no es rectángulo se parte por sus franjas hasta que cada trozo lo sea; cada rectángulo
  se calcula con las reglas que ya existen, identificando sus módulos; al final los materiales que
  coinciden se suman y los que no, se agrupan, y eso es lo que se muestra. Por ahora solo aparente.
  En Formas: tramo 1 (102.1 × 77.2), el sistema del tramo 2 (233.7 × 109.4) y su mocheta de abajo
  (151.7 × 35.1). Todos los datos mostrados se actualizan solos con cada cambio.

  **El orden** (dictado el 2026-09-26):
  1. **Identificar cada tramo entero**: su modelo por el orden de sus franjas (sin puente, normal,
     invertido, bandera, compuesto) y el puente que admite por N-03. El tramo 2 de Formas es un
     **invertido con la mocheta recortada**: no admite Múltiple, lleva tubo 2⅜ x 1 aunque en Nova esté
     elegido Múltiple. El tramo 1 no tiene mocheta: sin puente.
  2. Partirlo en rectángulos (por franjas) y calcular cada uno con las reglas que ya existen.
  3. Sumar lo que coincide y agrupar lo demás.

  El editor muestra la identificación en su panel (`tramo2 = 233.7 × 109.4 · invertido, tubo 2⅜ x 1`)
  y la usa en el diálogo del puente en un escalón (N-97).

  **Hecho en el motor (2026-09-26, `FormasTest`).** El lector del diseño identifica cada tramo
  (su puente por N-03) y, en un vano con forma, le pasa al motor el alto real de cada franja, el
  rectángulo de cada una, lo que mide cada corrida de puente y cada parante. Formas en aparente,
  con Múltiple elegido en Nova:

  | Pieza | Resultado |
  |---|---|
  | U | `51.4 = 1`, `58.7 = 2`, `151.7 = 2`, `75.5 = 1`, `107.7 = 2`, `29.6 = 2` |
  | Puente (tubo 2⅜, por ser invertido) | `151.7 = 1` |
  | Riel y fijo corredizo | `102 = 1`, `233.6 = 1` |
  | Portafelpa / tope / hache | `75.6 = 3`, `107.8 = 6` / `76.3 = 1` / `51.4 = 1`, `58.7 = 2` |
  | Parante | `77.2 = 1` |
  | Vidrios | `50.9 x 75.5 = 1`, `50 x 73.7 = 1`, `58.2 x 107.7 = 2`, `57.3 x 105.9 = 2`, `151.2 x 30.8 = 1` |

  **El sistema también se parte por módulos cuando el vano lo corta** (dictado el 2026-09-26,
  medida "polis": una viga de 61.8 × 39.2 sobre el fijo de la izquierda de un tramo de 252 × 133.6):
  - el fijo bajo la viga es un rectángulo propio (94.4 de hoja): su vidrio (`61.8 x 93.2`), su U
    parante (`93.2`) y la portafelpa de su lado (`92.8`) salen de ese alto;
  - en la cara horizontal del corte, sobre el fijo, va una **U** del largo de su U de fijo (`62.3`);
  - el **fijo corredizo solo llega hasta donde empieza el corte**: 252 − 61.8 = 190.2 − 0.06 = `190.1`;
  - la corrediza de al lado colinda con la cara vertical del corte (la viga) como con una **pared**:
    lleva **tope completo** (`133.6 − 0.9 = 132.7`, N-56). El usuario hizo corrediza ese módulo a
    propósito, para equilibrar con el otro tramo; se desliza hacia el otro lado.

  Un corte de hasta 2.5 no cuenta (es descuadre, N-06). Hecho en el motor (`FormasTest`, y
  `PolisEnNovaTest` en el celular).

- N-98 ✅ **Una columna en el costado de un fijo: la U sigue la pared** (dictado el 2026-09-26,
  "polis" ventana 2: 229.5 × 223.4 en bandera, U-13; una columna de 36.4 × 97.2 entra desde la pared
  izquierda a media altura del fijo). El fijo sigue siendo uno (su vidrio, el rectángulo, se corta en
  obra) y su U parante rodea la columna:
  - **Primero las horizontales** (G-01): las caras de arriba y de abajo de la columna llevan U; en la
    esquina saliente, donde se encuentran con la vertical de la columna, el espesor de la U quedaría
    vacío, así que **suman el alto de la U**: 36.4 + 1.5 = `37.9`, 36.1 + 1.5 = `37.6`. Lo mismo en
    toda esquina saliente.
  - La cara vertical de la columna, su largo: `97.2`.
  - La pared que queda encima y debajo de la columna, dentro del sistema: la de arriba va entre el
    fijo corredizo y la horizontal, `17.4 − (1.5 + 0.2) = 15.7` (como N-43); la de abajo entre la
    horizontal y la U de fijos, `21.4 − 2 × 1.5 = 18.4`. *(Deducido de N-43 y G-01; confirmar.)*

  Hecho en el motor (`FormasTest`, `PolisEnNovaTest` en el celular).
  - **La columna que sigue más allá del puente** (2026-09-29, la columna de Ele en el diseño
    sugerido, sistema 5/7): dentro del sistema queda una sola cara; la otra la cierra el puente. La U
    sigue la pared igual: cara de abajo `37.1`, cara vertical `94.4`, pared de abajo `63.1`, y el
    puente llega hasta la columna (`193.1`). Si la columna llega al dintel es una viga (N-96).

  **Las Referencias y la cadena G<> se identifican desde el diseño** (2026-09-26): "polis" salía
  `mocheta sup: 56.1` y `G<p,r,m,n>`. Con un diseño cargado, el remate de la ventana sale de sus
  tramos (los dos invertidos → `nr`, modelo `i`; tramos distintos → `x`) y la forma es poligonal
  si el vano tiene silueta o escalón (`p`). La mocheta de las Referencias es el espacio, en
  aparente y en inaparente (N-31): `mocheta inf: 53.6`.

  **El riel y el fijo corredizo salen del ancho del sistema** (dictado el 2026-09-26): 233.7 −
  0.06 = 233.6, aunque el puente mida 151.7. En automático, compuesto y desigual coincide con
  puente − 0.06 (N-51), porque ahí el puente mide lo que el tramo.
- N-95 ✅ **El descuadre se marca en rojo** (pedido el 2026-09-26): quien mide no siempre es quien
  diseña, y 9 mm no se ven en el dibujo. Todo lado del vano casi horizontal o casi vertical cuyas
  puntas se desvían entre 0.3 y 2.5 cm se resalta en rojo con "descuadre N" (menos de 0.3 es el
  trazo del apunte; más de 2.5 ya es pendiente, forma poligonal). En Formas sale en el alféizar del
  recorte de 82: 0.9. La medida completa se ve con el chip "medida original".

---

## 13. Nova desigual y el diseño

- N-70 ✅ **El diseño manda**: cada módulo lleva el ancho exacto del dibujo.
- N-71 ✅ Ajuste por módulo: `(transiciones f↔c) × cruce / módulos`, con el mismo cruce para
  todos. Se reparte en toda la ventana, igual que en Nova con ceil (N-42, decidido el 2026-09-25):
  así el diseño automático, leído como diseño, da lo mismo. Un tramo **bloqueado** (N-85) queda
  pendiente de implementar.
- N-72 ✅ **Es momento de generar el nuevo diseño simbólico desde novaDiseño** (editarlo y
  crearlo ahí). El dibujo está bien; el simbólico hace falta para los cálculos.
- N-89 ✅ **Si el diseño se edita, el modelo pasa a compuesto (`x`) o desigual.** Para eso existe la
  edición; si no, todo se resuelve en Nova.
- N-73 ✔ Resuelto en el motor: el doble puente ya no se pierde en el camino desigual.
- N-74 ✔ Resuelto en el motor: el diseño lo lee el lector único, con mochetas, modelo y esquinas.
- N-81 ✅ **Un solo lector** del diseño simbólico (`DisenoNova`), y no solo para Nova: Medidas
  alimenta a todas las calculadoras (C-13). Nova ya lo usa (2026-09-25).

### 13.1 Diseño compuesto (2026-09-25)

Lo que ya hace el motor, sacado de las reglas N-31, N-33, N-35, N-49, N-72, N-88 y N-89:

- **Cada tramo se calcula con sus propias franjas.** La franja de sistema da la altura de hoja de
  ese tramo: un tramo con el puente a 130 junto a otro con el puente a 100, o sin puente.
- 🐞 **El diseño escribe mal la mocheta** (hallado el 2026-09-25). Debe escribirla como espacio,
  sin el puente (N-31, N-34), y cada módulo con su ancho (N-70). La Dora editada debe quedar
  `m<37.5>` en el tramo normal y `m<17.5>`, `s<100>`, `m<17.5>` en la bandera. Hoy el editor guarda
  `m<40>` (con el puente dentro), el diseño automático de bandera `m<20>` y a veces los módulos
  salen sin ancho (`fccf`). El motor lo tapaba leyendo las alturas "como proporción" (alto − hoja
  repartido como en el dibujo, menos el puente de cada mocheta): los materiales salían bien, pero
  el diseño simbólico no. Se corrige en C-25.
- **Puente: una corrida por cada mocheta del tramo.** Normal o invertido 1, bandera 2, sin mocheta
  ninguno. Ejemplo, la Dora editada (tramo de 210 normal y tramo de 157.5 en bandera): `210 = 1`,
  `157.5 = 2`.
- **Se archiva el diseño editado.** Antes se regeneraba el automático y lo editado se perdía. Si los
  tramos tienen franjas distintas, el modelo se archiva como `x`.

Dictado por el usuario el 2026-09-25:

- ✅ **Toda división entre franjas es un tubo** (una corrida de puente, del ancho del tramo). Entre
  dos mochetas, se le descuenta a la de arriba. Entre sistema y mocheta sigue N-31 (la mocheta
  pierde el puente), así normal, invertido y bandera no cambian. Ejemplo: sistema de 100 y dos
  mochetas de 30 encima, en 160 → 2 corridas y dos mochetas de 27.5.
- ✅ **La mocheta con corrediza se convierte en franja de sistema** (N-88), con un puente normal que
  se le descuenta a la de arriba. Se calcula como sistema propio: riel, riel superior, hache,
  portafelpa, tope, U y vidrios con su altura. Ejemplo: mocheta de 60 con corrediza sobre un
  sistema de 100 → hoja de 57.5, riel `199.9 = 2`, portafelpas `98.4 = 3` y `55.9 = 3`.
- ✅ **U superior e inferior de inaparente:** albergan el vidrio que va delante de la estructura, así
  que no las parten los parantes (420 en dos tramos de 4 y 3 sigue siendo 420). Precisado el mismo
  día en N-93: la U sigue al vidrio de cada borde, y se corta donde no hay vidrio que apoyar (fijo
  corredizo arriba, corredizas sobre el riel abajo). Antes se aplicaba siempre de lado a lado.

---

## 14. Lo que sale

### 14.1 Referencias

- N-76 ✅ `An:` y `Al:` muestran la medida medida; todo lo demás es interno.
- N-77 ✅ Divisiones, fijos y corredizas salen del mismo número que usan el diseño y el corte.

### 14.2 El diseño simbólico

- N-84 ✅ **Grupos, en este orden**: cliente, producto, geometría, medidas, diseño, material,
  accesorios (el que ya emite la app: `C<>-P<>-G<>-M<>-T<>-MAT<>-ACC<>`). El orden de
  `diseño simbólico nova.txt` queda superado.
  - En volumen esquinero, cada lado es otro tramo y el parante de esquina lleva su ángulo:
    **`P<90>`** (hoy se escribe `A<90>`, C-16).
  - En esquina curva, el arco es otro tramo entre parantes, con **su propia información**: su
    desarrollo y su flecha. Cada módulo curvo se escribe **`f<80,5>`**: 80 es el ancho (desarrollo o
    arco) y 5 la flecha. En los resultados de la calculadora solo se muestra el desarrollo; arco,
    flecha y cuerda se guardan para el curvado (pendiente, pero previsto). 🐞 Hoy la flecha va en
    una marca `U<flecha>` en la primera franja (C-23).
  - Poligonal y compuesto: con pruebas.
- N-79 ✅ **Material**: al archivar, un diálogo pregunta los materiales; si no se ponen, van nulos y
  la lista muestra solo el nombre del material, sin color ni espesor.
- G-06 aplica: la altura de puente nunca sale en 0 (C-16).
- La comparación entre el formato original y el de hoy está en `diseño simbólico nova actual.txt`.

### 14.3 Lo que emite hoy (V2) ⚙️

```
C<cliente>-P<V,n,acabado,tipología,nº>-G<volumen,forma,encuentro,modelo>-M<anchos…,altos…,hp,alféizar,dintel,cantidad>-T<tramos>-MAT<alu:…;vid:…>-ACC<…>
```

Dentro de `T<>`: `<`→`(`, `>`→`)`, `-`→`_`.

### 14.4 El paquete técnico ⚙️

```
{nova,<acabado>,[<ancho>,<alto>: Tl<w>(franja;franja;…) P<2.5> Tl<w>(…) A<90> Tl<w>(…)]}
```

| Pieza | Significa |
|---|---|
| `Tl<w>` / `Tb<w>` | tramo libre / bloqueado (N-85) |
| `P<2.5>` | parante entre tramos (`P<90>` en esquina, N-84) |
| `H<alto>` / `H<izq,der>` | alto propio del tramo (escalón / inclinado) |
| `s<alto>( … )` / `m<alto>( … )` | franja de sistema / mocheta, de abajo arriba, separadas por `;` |
| `f<w>` / `c<w>` | módulo fijo / corrediza |
| `U<flecha>` | curva · `O<1>` circular |

---

## 15. Trampas conocidas

1. Pasar la modulación donde se espera el modelo: la rama de bandera no se ejecuta (N-73).
2. El puente se escribe por dos caminos (por tramos con mocheta, y `NovaInaCalculos.puentes` sin
   ella): un arreglo toca los dos.
3. `divisiones(…, tipo)` devuelve 0 para un tipo que no conoce.
4. `df1` trunca: comparar con tolerancia da falsos iguales.
5. `NovaCalculos` guarda en memoria el reparto manual y "cada cuántas corredizas", y lo leen otras
   calculadoras (contra G-07, C-14).
6. Lo editado en el diseño no llega a la cadena archivada (C-16).

---

## 16. Cambios de código que salen de estas reglas (pendientes, sin hacer)

Orden dictado: **primero Nova con ceil y Nova desigual**; después diseño simbólico desde
novaDiseño; después formas.

| # | Cambio | Regla |
|---|---|---|
| C-01 | Quitar pivotante como acabado (`PIV`) | N-02 |
| C-02 | ✔ Hecho 2026-09-25. Puente por defecto tubo 2⅜ x 1 en todos los casos | N-03 |
| C-03 | ✔ Hecho 2026-09-25. Vidrio de mocheta INA en ancho x alto | N-14 |
| C-04 | Full corredizas INA: máximo automático 5 por tramo (hoy 6). No es solo un número: hoy full corredizas INA solo sabe hacer **un** tramo (puente, riel y felpero al ancho total); hay que enseñarle a partir en tramos | N-19 |
| C-05 | ✔ Hecho 2026-09-25. Tope: 1 por corrediza que colinda con pared o parante (clásico, ncfc, full, doble y triple corrediza) | N-56 |
| C-06 | ✔ Hecho 2026-09-25. Vidrio fijo: colindancia con pared o parante 0.5, en APA, INA y desigual | N-61 |
| C-07 | ✔ Hecho 2026-09-25. Vidrio de mocheta APA (puentes que no son Múltiple ni gorrito): mocheta − (u + 0.3), también en desigual y en el motor | N-63 |
| C-08 | Vidrios de mocheta superior e inferior del mismo ancho | N-64 |
| C-09 | INA: sumar el ancho de U en cada esquina en ángulo (U de fijos, U superior, full fijos) | G-09, N-39, N-42, N-46, N-47 |
| C-10 | Lista de U editable (ancho, alto, espesor máximo) | G-02, N-40 |
| C-11 | Ubicar y documentar la excepción de hache en Nova desigual | N-54 |
| C-12 | Camino desigual: doble puente, mochetas, modelo, esquinas y descuentos APA | N-73, N-74, N-75 |
| C-13 | Un solo lector del diseño simbólico. ✔ En parte (2026-09-28): el dibujo de novaDiseño dejó su lector propio y lee con el del modelo (`DisenoNova.desdePaquete`), el mismo del panel, la 3D y el motor. El modelo aprendió lo que solo leía el dibujo: formato viejo sin tramos, `s<>` sin alto, mochetas laterales `l`/`r`, segmentos `a<…>{…}`, anchos `f(50)`, y círculo o arco pegados a un módulo (`fccfO<1>`). `LectorUnicoTest` fija 305 paquetes contra lo que leía el lector viejo (304 iguales; el otro, un arco que el viejo no marcaba). Falta: la pantalla de diseño todavía rearma el paquete con texto en algunos botones (`parsearBloquesTramo`, `reconstruirPaqueteConBloques`), que es C-25 | N-81 |
| C-14 | Estado de `NovaCalculos` propio de cada calculadora | G-07 |
| C-15 | `cruce()` de INA sin la tabla vieja de divisiones | N-83 |
| C-16 | Cadena archivada: altura de puente calculada (no 0), `P<90>` en esquinas, `Tb` bloqueado. ✔ Hecho 2026-09-25: lo editado se conserva y el modelo pasa a `x` | G-06, N-84, N-85, N-89 |
| C-17 | Dintel (0 por defecto) y altura de puente por ergonomía desde el alféizar | N-09, G-08, N-90 |
| C-18 | Orden de las líneas de material: horizontal, diagonal, vertical (revisar) | G-01 |
| C-19 | Revisar U felpero en APA, cantidad de rieles en ncc/n3c y cantidad de U parante contra lo dictado | N-43, N-51, N-52 |
| C-20 | Circular como forma, no como modulación | N-01 |
| C-21 | U parante de mocheta INA. ✔ Invertido sin el +1.5 (hecho 2026-09-25). Falta bandera: hoy la U de INA en bandera se calcula como si fuera de 1 división (viene del código original, `932353b`) y no lleva U parante de mocheta | N-44 |
| C-22 | Perfil doble corrediza en ncc/n3c (1 por cada 2 corredizas) en lugar de U felpero / fijo corredizo; rieles 2 en ncc, 3 en n3c | N-51, N-52b |
| C-23 | Módulo curvo `f<desarrollo,flecha>` en el diseño simbólico; en resultados solo el desarrollo | N-84 |
| C-24 | Puente por lado y por tramo: en L, C y serie, el lado o tramo normal en aparente con Múltiple y el resto con tubo. ✔ En parte (2026-09-26): en el diseño leído (desigual, compuesto, formas) cada tramo toma el puente que admite (un invertido o bandera no lleva Múltiple). Falta el automático en L, C y serie | N-03 |
| C-25 | Un solo escritor del diseño, desde el motor: mocheta como espacio sin el puente, cada módulo con su ancho; el editor mantiene la regla al agregar o quitar franjas y al cambiar alturas; el motor deja de leer "como proporción"; Referencias con la misma mocheta en APA e INA (hoy `mocheta sup: 40` en normal y 20 en bandera INA) . ✔ En parte (2026-09-26): Referencias con la mocheta como espacio en APA e INA; alturas sin escribir resueltas con N-29 (sistema 5/7, mochetas el resto) en el dibujo, el panel y el paquete que va a Nova; agregar o quitar un módulo solo reparte su franja, las demás conservan sus anchos. Falta: mocheta sin puente en el paquete, módulos sin ancho escritos | N-31, N-34, N-70, N-81 |

---

## 17. Preguntas que quedan

Ninguna abierta. P-01 a P-11 quedaron respondidas el 2026-09-25 (cada respuesta está en su
regla). "Una" en las respuestas del usuario es **INA** (el teclado lo corrige).

Lo que falte saldrá con las pruebas de cada cambio de sección 16.

---

## Anexo A. Los ejemplos de `siglas crystal.txt`

Se compararon con el código el 2026-09-23. El usuario confirmó el 2026-09-25 que **tienen errores
y no son fuente de reglas**:

- El cruce es 0.7 (N-41), no el ≈ 1 que implicaban.
- La hache va 1 por corrediza (N-54).
- 189.6 con 3 divisiones fue error o decisión del usuario (N-21).

Mandan las reglas de este documento.

## Historia útil

- `932353b` — punto de control antes del refactor del diseño simbólico; sin descuentos de esquina.
- `64c2251` — refactor de Nova desigual; trajo los descuentos de esquina a los dos acabados
  (incorrecto) y la regresión de la mocheta de INA.
- `bdeef44` — el doble puente lleva dos corridas y en INA la mocheta no descuenta el puente.
- `3535749` — INA deja de descontar la esquina.
- `c5da2e5`, `0291240` (2026-09-08) — orden de la cadena V2 y una sola función para armarla.

## Pruebas que fijan reglas

- `ReglasDictadasVidriosYPerfilesTest` (N-14, N-44, N-56, N-61, N-63, N-87).
- `CompuestoTest` (N-35, N-46, N-88, N-89 y N-92: la Dora editada con un tramo `ccc`).
- `PuenteDobleEscaladoTest` (N-35) · `DivisionesAnchoUtilTest` (N-15, N-17) ·
  `RepartosPosiblesTest`, `RepartoSegunLosArcosTest` (N-25) · `TramosYUMochetaTest` (N-45) ·
  `ContornoEnTramosTest`, `PiezasDelEscalonTest` (sección 12).
- `MedidasDeObraTest` (2026-09-29): medidas reales de Medidas por el camino entero (escalones,
  columna, esquina curva, L inaparente). `RevisionMedidasNovaTest` en el celular recorre todas las
  medidas de Nova de `Descargas/Crystal/Medidas` como la cola y deja `revision_medidas_nova.txt`.
- **Faltan**: listas de materiales completas de ventanas reales (plana APA, plana INA, la L
  dictada en sección 7, una bandera) y pruebas del camino desigual. Cada cambio de sección 16 entra con su
  prueba.
