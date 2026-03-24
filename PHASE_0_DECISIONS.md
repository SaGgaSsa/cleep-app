# Fase 0 - Decisiones base para Android nativo

## Estado

Fase 0 cerrada a nivel repo.

Este documento congela lo que ya se puede afirmar desde `flutter-deprecado/` y deja definido el flujo CLI-first para el nuevo proyecto Android nativo.

## Decisiones cerradas

- La nueva app Android nativa vive en este mismo repo, separada del codigo Flutter deprecado.
- El proyecto Flutter de `flutter-deprecado/` se usa como especificacion funcional, visual y de copy; no como base tecnica.
- La V1 mantiene localizacion `es` + `en`.
- La sesion autenticada se define por la presencia local de `apiKey`.
- La primera implementacion nativa guarda `apiKey` en `EncryptedSharedPreferences`.
- El alcance V1 no crece respecto del plan original: login, sesion persistida, crear/listar/borrar cleeps y settings basico.
- El flujo principal de desarrollo y prueba sera por linea de comando, sin Android Studio.
- La configuracion local del proyecto nativo usa `android-native/.env.dev`, cargado por un script del repo.
- El destino principal de prueba es un dispositivo fisico conectado por `adb`, incluyendo wireless debugging.

## Fuente de verdad usada

Referencia relevada en este repo:

- Auth y registro: `flutter-deprecado/lib/features/auth/...`
- Networking y auth header: `flutter-deprecado/lib/shared/providers/dio_provider.dart`, `flutter-deprecado/lib/shared/dio/auth_interceptor.dart`
- CRUD de cleeps: `flutter-deprecado/lib/features/cleeps/...`
- Navegacion base: `flutter-deprecado/lib/core/router/...`
- Tema visual: `flutter-deprecado/lib/core/theme/...`
- Localizacion: `flutter-deprecado/lib/l10n/...`

## Contratos confirmados desde el cliente actual

### Auth

Flujo actual observado:

1. El usuario entra a login.
2. Hace sign-in con Google.
3. La app obtiene `idToken`.
4. La app llama `POST /register` con `email` + `idToken`.
5. El backend devuelve `apiKey`.
6. La app persiste `apiKey`.
7. Los requests autenticados usan `Authorization: Bearer <apiKey>`.

Contrato observado:

```json
POST /register
{
  "email": "user@example.com",
  "idToken": "<google-id-token>"
}
```

Respuesta minima esperada:

```json
{
  "apiKey": "<api-key>"
}
```

Regla de sesion a preservar:

- Si existe `apiKey` persistida, la app considera al usuario autenticado.
- La restauracion silenciosa de Google es best-effort y solo sirve para poblar datos de usuario en Settings.

### Cleeps

Contrato observado para listar:

```json
GET /cleeps
```

Respuesta minima esperada:

```json
{
  "cleeps": [
    {
      "id": "string",
      "userId": "string",
      "content": "string",
      "createdAt": "ISO-8601"
    }
  ]
}
```

Contrato observado para crear:

```json
POST /cleeps
{
  "content": "texto del cleep"
}
```

La respuesta actual del cliente se parsea directo como un `Cleep`.

Contrato observado para borrar:

```json
DELETE /cleeps
{
  "ids": ["cleep-id"]
}
```

### Header de autenticacion

Regla actual observada:

```http
Authorization: Bearer <apiKey>
```

## Contratos confirmados desde backend

Definiciones cerradas para la implementacion nativa:

### Auth

- `POST /register` mantiene el flujo `email + idToken -> apiKey`.
- El backend solo requiere Google login por ahora; no hay validaciones adicionales documentadas.
- Si register responde OK, el cliente persiste `apiKey` y habilita el resto de la app.
- `GOOGLE_SERVER_CLIENT_ID` sigue siendo requerido del lado cliente para obtener el `idToken`.
- No se planifican validaciones extra de issuer, audience o expiracion en cliente para V1.

Pendiente de precision operacional:

- codigos HTTP de exito y error esperados en `POST /register`
- payload exacto de error para mapear mensajes uniformes

### Cleeps

- `GET /cleeps` devuelve al menos un objeto con clave `cleeps`.
- `POST /cleeps` recibe `content`.
- `DELETE /cleeps` recibe `{ "ids": [...] }`.

Pendiente de precision operacional:

- metadata adicional de `GET /cleeps`, si existe
- shape exacto de respuesta de `POST /cleeps`
- respuesta exacta de `DELETE /cleeps`
- payloads de error normalizados

## Configuracion requerida para la app nativa

Entradas obligatorias desde el arranque del proyecto:

- `BASE_URL`
- `GOOGLE_SERVER_CLIENT_ID`

Formato local definido:

- archivo local `android-native/.env.dev`
- plantilla versionada en `android-native/.env.example`

Reglas iniciales:

- `BASE_URL` no debe quedar hardcodeada en codigo fuente productivo.
- `GOOGLE_SERVER_CLIENT_ID` debe inyectarse por configuracion para `debug` y `release`.
- `android-native/.env.dev` no se versiona.
- el script del repo carga este archivo y pasa los valores a Gradle.

## Flujo CLI-first definido

Comando objetivo para desarrollo:

- `./scripts/run-android-dev.sh`

Comportamiento esperado del script:

1. Carga `android-native/.env.dev`.
2. Valida `BASE_URL` y `GOOGLE_SERVER_CLIENT_ID`.
3. Verifica `adb`.
4. Selecciona dispositivo usando `ANDROID_SERIAL` o el unico device conectado.
5. Ejecuta instalacion debug con Gradle.
6. Abre la app instalada con `adb shell am start`.

Reglas operativas:

- si no hay devices, falla con instruccion clara
- si hay varios y no existe `ANDROID_SERIAL`, falla pidiendo seleccion explicita
- el flujo soporta dispositivo fisico por USB o wireless debugging
- Android Studio no es un prerequisito del workflow diario

## Baseline funcional a preservar

Pantallas a reconstruir:

- Login
- Home / Nuevo cleep
- Lista de cleeps
- Settings

Navegacion base a preservar:

- pantalla de login publica
- shell autenticada
- bottom navigation con tabs `Nuevo`, `Cleeps`, `Settings`

Comportamientos clave:

- redirect a login si no hay sesion
- restore session al abrir app si existe `apiKey`
- logout borra `apiKey` local y cierra la sesion Google si esta activa

## Baseline visual a preservar

Tokens visuales relevados:

- tema oscuro
- color primario `#8FF5FF`
- color primario container `#00EEFC`
- fondo principal `#0E0E0E`
- tipografia objetivo `Space Grotesk`
- layout sobrio, bajo ruido visual

Archivo de referencia visual:

- `flutter-deprecado/lib/core/theme/app_colors.dart`
- `flutter-deprecado/lib/core/theme/app_theme.dart`

## Assets y copy a reutilizar

Assets confirmados en repo:

- logo: `flutter-deprecado/assets/brand/cleep.png`

Localizacion confirmada:

- `flutter-deprecado/lib/l10n/app_es.arb`
- `flutter-deprecado/lib/l10n/app_en.arb`

Pendiente a resolver antes del bootstrap visual:

- definir de donde se incorpora `Space Grotesk` para Android nativo

## Implicancias para Fase 1

Con este documento, Fase 1 debe arrancar asumiendo:

- proyecto Compose limpio dentro de `android-native/`
- estructura por features
- DI manual inicialmente
- placeholders de navegacion equivalentes al producto actual
- tema y strings importados desde estos insumos
- script CLI de desarrollo en `scripts/run-android-dev.sh`
- inyeccion de config desde `.env.dev` hacia build debug

Lo unico que sigue abierto para la implementacion es el detalle fino de payloads de error y respuestas auxiliares del backend.

## Checklist de salida de Fase 0

- [x] decidir ubicacion del proyecto nativo
- [x] decidir alcance de idiomas V1
- [x] decidir estrategia inicial de storage para `apiKey`
- [x] congelar el flujo de auth observado
- [x] congelar contratos cliente observados para cleeps
- [x] inventariar assets y copy reutilizable
- [x] definir flujo CLI-first sin Android Studio
- [x] definir formato local de configuracion
- [ ] completar detalles exactos de errores y respuestas auxiliares del server
