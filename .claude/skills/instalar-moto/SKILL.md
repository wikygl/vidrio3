---
name: instalar-moto
description: Compila Crystal (directoDebug) y lo instala en el moto g04 de pruebas sin desinstalar. Usar al terminar cada cambio o cuando el usuario diga "instala", "al moto", "pásalo al celular".
---

# Instalar en el moto

1. Compilar:
   ```bash
   cd /f/crystal && ./gradlew :app:assembleDirectoDebug -q
   ```
2. Conectar (adb no está en el PATH):
   ```bash
   ADB="$LOCALAPPDATA/Android/Sdk/platform-tools/adb.exe"
   MSYS_NO_PATHCONV=1 "$ADB" connect 192.168.101.8:5555
   ```
   Si no responde, la IP cambió (DHCP): buscar el puerto 5555 en 192.168.101.x y confirmar el modelo con
   `adb -s <ip>:5555 shell getprop ro.product.model` (moto g04). Si el adb inalámbrico se cayó, pedir al usuario
   que lo reactive (código de emparejamiento 8341).
3. Instalar por serial, **siempre con -r**:
   ```bash
   MSYS_NO_PATHCONV=1 "$ADB" -s 192.168.101.8:5555 install -r app/build/outputs/apk/directo/debug/app-directo-debug.apk
   ```

## Nunca
- **Nunca desinstalar** Crystal (`uninstall`, `pm clear`) en un equipo con datos. Si la firma no coincide, avisar y preguntar.
- No enviar ningún comando a 192.168.101.100 (es la cámara).
- No correr pruebas en el celular salvo que el usuario lo pida o lo permita: las pruebas van en la PC (skill `probar-pc`).
  Si las pide: `./gradlew :app:assembleDirectoDebugAndroidTest`, instalar el APK de androidTest con -r y
  `adb shell am instrument -w -e class <Clase> crystal.crystal.test/androidx.test.runner.AndroidJUnitRunner`.
