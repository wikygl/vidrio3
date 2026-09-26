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

Última revisión: 2026-09-25 (se incorporaron las respuestas de *Revisión de reglas*; después, el
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
- N-22 ✅ **Orden preestablecido**: 2 `fc` · 3 `fcf` · 4 `fccf` · 5 `fcfcf` · 6 `fcffcf` · 7
  `fcfcfcf` · 8 `fccffccf`…; lo demás depende de la modulación.
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
  | Esquina curva | 0 | 0 |

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
  reparte entre las hojas como el cruce (`(120 + 0.7 + 1) / 2 = 60.8`). Los fijos seguidos van en
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
- N-45 ✅ **U de mocheta** (APA): `altoMocheta − 2u` por tramos; con Múltiple o gorrito 1 por
  tramo, con los demás 2. (La mocheta es una superficie: ancho de mocheta × alto de mocheta.)
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

- En esquinero **cada lado es otro tramo**, unido por un parante de esquina `P<90>` (N-84).
- En una esquina curva, al empezar y al terminar el arco van parantes: el arco es otro tramo (N-84).

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
- N-96 ✅ **Estrategia de cálculo de las formas: partir en rectángulos** (dictado el 2026-09-26). Un
  tramo que no es rectángulo se parte por sus franjas hasta que cada trozo lo sea; cada rectángulo
  se calcula con las reglas que ya existen, identificando sus módulos; al final los materiales que
  coinciden se suman y los que no, se agrupan, y eso es lo que se muestra. Por ahora solo aparente.
  En Formas: tramo 1 (102.1 × 77.2), el sistema del tramo 2 (233.7 × 109.4) y su mocheta de abajo
  (151.7 × 35.1). Todos los datos mostrados se actualizan solos con cada cambio.
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
| C-13 | Un solo lector del diseño simbólico | N-81 |
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
| C-24 | Puente por lado y por tramo: en L, C y serie, el lado o tramo normal en aparente con Múltiple y el resto con tubo. Hoy el motor usa un solo puente para toda la ventana | N-03 |
| C-25 | Un solo escritor del diseño, desde el motor: mocheta como espacio sin el puente, cada módulo con su ancho; el editor mantiene la regla al agregar o quitar franjas y al cambiar alturas; el motor deja de leer "como proporción"; Referencias con la misma mocheta en APA e INA (hoy `mocheta sup: 40` en normal y 20 en bandera INA) | N-31, N-34, N-70, N-81 |

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
- **Faltan**: listas de materiales completas de ventanas reales (plana APA, plana INA, la L
  dictada en sección 7, una bandera) y pruebas del camino desigual. Cada cambio de sección 16 entra con su
  prueba.
