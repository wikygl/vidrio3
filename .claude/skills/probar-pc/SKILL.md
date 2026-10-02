---
name: probar-pc
description: Corre las pruebas unitarias de Crystal en la PC y resume las fallas. Usar después de cada cambio de cálculo (Nova, optimizadores, materiales) o cuando el usuario pida probar.
---

# Probar en la PC

1. Todas, o una clase:
   ```bash
   cd /f/crystal && ./gradlew :app:testDirectoDebugUnitTest -q
   ./gradlew :app:testDirectoDebugUnitTest --tests "crystal.crystal.taller.nova.motor.MedidasDeObraTest"
   ```
2. Contar las fallas (la salida de gradle se corta):
   ```bash
   grep -l "<failure" app/build/test-results/testDirectoDebugUnitTest/*.xml
   grep -h -A3 "<failure" app/build/test-results/testDirectoDebugUnitTest/*.xml | head -60
   ```
3. Informar cuántas pasaron y cuántas fallaron, con el mensaje de cada falla.

## Reglas
- **Un test de corte en rojo es la alarma**: el cálculo cambió. Nunca cambiar el valor esperado ni poner tolerancias
  para apagarlo. Primero buscar qué se rompió; si la regla nueva lo cambia a propósito, decírselo al usuario con
  el valor viejo y el nuevo y esperar su confirmación.
- Los valores esperados de una prueba nueva salen de las reglas (docs/REGLAS_NOVA.md) o de lo que dictó el usuario,
  no de lo que da el código.
- La PC tiene poca memoria (8 GB): no lanzar dos builds de gradle a la vez.
- No hay Python: usar bash, sed o Kotlin.
