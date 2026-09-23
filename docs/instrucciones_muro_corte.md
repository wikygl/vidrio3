# Corrección de cálculos en MuroCortina con recortes

## Contexto del problema

En la actividad **MuroCortina** de Crystal, cuando una ventana muro cortina tiene **recortes** (áreas rectangulares quitadas de la ventana), el cálculo de materiales (marco, tubo, aluminio marco, aluminio tubo y vidrios) y la representación visual del vidrio recortado no se están generando correctamente.

A continuación describo el comportamiento esperado mediante un caso de referencia, seguido de los dos escenarios concretos donde se ven los errores.

---

## Datos generales del ejemplo de referencia

- **Ancho ventana:** 240 cm
- **Alto ventana:** 150 cm
- **Marco:** 2.5 cm
- **Tubo:** 3.8 cm
- **Gruña (holgura):** 0.8 cm
- **Filas:** 2
- **Columnas:** 3
- **Altura de vidrio primera fila:** 110 cm

Diseño base sin recorte:
- Ancho de cada vidrio (3 columnas iguales): **79.2 cm**
- Alto vidrio primera fila: **110 cm**
- Alto vidrio segunda fila: **38.4 cm**

---

## CASO 1 — Recorte 100 × 30

### Descripción geométrica del recorte
El recorte (100 cm de ancho × 30 cm de alto) se ubica de forma que **corta completamente el alto del primer módulo de la segunda fila y primera columna**, y **parcialmente el segundo módulo de la segunda fila y segunda columna**.

### Resultado correcto que debe arrojar el cálculo

**Marco:**
- 150 = 1
- 120 = 1
- 100 = 1
- 32.5 = 1
- 135 = 1

**Tubo:**
- 235 = 1
- 145 = 1
- 115 = 1
- 75.6 = 2
- 76.2 = 1

**Aln Marco (aluminio marco):**
- 106.4 = 2
- 76.2 = 2

**Aln Tubo (aluminio tubo):**
- 104.4 = 2
- 74.2 = 2

**Vidrios:**
- 79.2 × 110 = 3
- 79.2 × 38.4 = 2
- 79.2 × 8.4 = 1

### Error visual actual en el modo "vista vidrio"
En CASO 1 las medidas del vidrio se muestran correctas, **pero en el vidrio recortado el texto del rótulo es demasiado pequeño**. Hay que ajustar el tamaño/escala del texto para que sea legible aunque el módulo sea pequeño (8.4 cm de alto).

---

## CASO 2 — Recorte 100 × 50

### Descripción geométrica del recorte
El recorte (100 cm × 50 cm) **elimina por completo el módulo 1 de la segunda fila de la primera columna**, **corta el alto del primer módulo de la primera fila y primera columna**, **corta el ancho del segundo módulo de la segunda fila y segunda columna**, y **corta parcialmente el segundo módulo de la primera fila y segunda columna**.

### Resultado correcto que debe arrojar el cálculo

**Marco:**
- 150 = 1
- 100 = 1
- 100 = 1
- 52.5 = 1
- 135 = 1

**Tubo:**
- 235 = 1
- 145 = 1
- 95 = 1
- 75.6 = 1
- 55.6 = 1

**Aln Marco:**
- 106.4 = 2
- 76.2 = 2

**Aln Tubo:**
- 104.4 = 2
- 74.2 = 2

**Vidrios:**
- 79.2 × 110 = 2
- 79.2 × 99.2 = 1
- 79.2 × 38.4 = 1
- 59.2 × 38.4 = 1

### Errores visuales actuales en el modo "vista vidrio"
1. La medida del vidrio recortado de la primera fila se muestra como **79.4**, cuando debe ser **79.2**.
2. La medida del vidrio de la segunda fila y segunda columna: el ancho es correcto, pero el alto se muestra como **38.9** cuando debe ser **38.4**.

> Estos errores parecen venir de un redondeo o de una resta mal hecha en el cálculo del módulo cortado (probablemente sumando el ancho/alto del corte al módulo original en lugar de restarlo, o usando la medida del corte en bruto sin descontar marco/tubo).

---

## Reglas geométricas que el algoritmo debe respetar

### Regla 1 — Marco interno tras un corte
Después de cualquier corte, **siempre se debe dibujar un marco en la parte interna de la ventana cortada** (el corte queda enmarcado por todos sus lados expuestos al interior de la ventana).

### Regla 2 — Descuento de marco
En el cálculo de marco, **siempre se descuenta el horizontal, no el vertical**. Es decir: los marcos verticales corren de extremo a extremo, y los horizontales se acortan para encajar entre ellos.

### Regla 3 — Cortes en ángulo obtuso interior
Cuando el ángulo obtuso interior de la ventana implique un marco con corte en ángulo, la medida del marco se calcula así:

- **Marco horizontal de corte (eje X):** = ancho del corte
- **Marco vertical de corte:** = alto del corte + 1 marco
- **Marco horizontal "m" (el que cierra la fila tras el corte):** = ancho de ventana − ancho de corte − 2 × marco

---

## Lo que necesito que se corrija en el código

1. **Lógica de cálculo de piezas (marco, tubo, aln marco, aln tubo, vidrios)** cuando hay uno o más recortes activos en la actividad MuroCortina, ajustándose al patrón de los dos casos descritos arriba. Las cantidades y medidas de salida deben coincidir exactamente con las listas "Como debe ser" de cada caso.

2. **Cálculo del módulo de vidrio recortado:** la medida resultante debe ser la del vidrio original menos lo que el recorte le quita, **descontando correctamente marco y gruña** (no debe aparecer 79.4 cuando debe ser 79.2, ni 38.9 cuando debe ser 38.4). Verificar la fórmula usada — sospecho que está sumando en lugar de restar el espesor del marco interno agregado por el corte, o redondeando mal.

3. **Renderizado del rótulo de medida en vidrios pequeños** (modo vista vidrio): cuando el módulo es muy pequeño (ej. 8.4 cm de alto), el texto se vuelve ilegible. Ajustar el tamaño de fuente para que se mantenga legible, o moverlo fuera del módulo con una línea guía si no cabe dentro.

4. **Aplicar las tres reglas geométricas** (marco interno tras corte, descuento horizontal del marco, corte en ángulo obtuso) de forma consistente en todos los casos de recorte.

---

## Sugerencia de cómo abordar la implementación

1. Localizar la clase/función que actualmente calcula los materiales en MuroCortina **sin** recortes y validar que sigue dando el resultado correcto (caso base sin recorte).
2. Identificar dónde se aplica la lógica de recortes y comparar el resultado actual con las tablas "Como debe ser" de los casos 1 y 2.
3. Escribir tests unitarios que reproduzcan exactamente los dos casos del PDF (entradas: dimensiones de ventana + recorte; salida esperada: las listas de marco/tubo/aln/vidrios). Esto fija el comportamiento correcto antes de tocar código de producción.
4. Corregir la lógica iterando contra esos tests.
5. Una vez que los tests pasen, revisar el render del modo vista vidrio para los dos puntos visuales (79.4 → 79.2, 38.9 → 38.4, tamaño de texto en módulo de 8.4).
