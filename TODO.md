# Android Native TODO

## Setup inicial

- [ ] Crear `android-native/.env.dev` a partir de `.env.example`
- [ ] Confirmar que `adb` este instalado y disponible en `PATH`
- [ ] Documentar el primer emparejamiento de wireless debugging del dispositivo
- [x] Definir `applicationId`, `minSdk` y `targetSdk`
- [x] Crear el proyecto Compose dentro de `android-native/`
- [x] Confirmar el `package name` final del modulo app

## Flujo CLI

- [x] Implementar `android-native/gradlew` y el proyecto Gradle real
- [x] Conectar `scripts/run-android-dev.sh` con el proyecto Gradle
- [x] Inyectar `BASE_URL` y `GOOGLE_SERVER_CLIENT_ID` en `BuildConfig`
- [x] Definir `applicationId` para poder lanzar la app por `adb shell am start`
- [x] Soportar `ANDROID_SERIAL` cuando haya multiples dispositivos conectados

## Auth

- [x] Integrar Credential Manager + Google ID
- [x] Implementar `POST /register` con `email` + `idToken`
- [ ] Mapear errores HTTP de register
- [x] Persistir `apiKey` con `EncryptedSharedPreferences`
- [x] Restaurar sesion usando `apiKey`

## UI y assets

- [ ] Incorporar `Space Grotesk` al proyecto Android nativo
- [ ] Portar `assets/brand/cleep.png`
- [ ] Migrar strings `es` y `en`
- [x] Reproducir la base visual dark + cian del cliente Flutter

## Fases recuperadas

- [x] Fase 3: shell autenticada con bottom navigation
- [x] Fase 4: crear cleep
- [x] Fase 5: listar cleeps con refresh, empty y error state
- [x] Fase 6: borrado con confirmacion y feedback real en dispositivo
- [x] Fase 7: settings final con datos completos y pulido visual
