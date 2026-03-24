---
name: commit
description: Flujo rapido para revisar cambios, validar este proyecto Android/Gradle y crear un commit de git con un mensaje conciso en ingles. Usar cuando el usuario pida hacer un commit o cerrar cambios en este repo.
metadata:
  short-description: Commit para repo Android
---

# Commit

Usa este skill cuando el usuario quiera cerrar cambios con un commit en este repo.

## Flujo

1. No ejecutes `npm run build` en este proyecto. Este repo no usa `package.json`.
2. Valida el proyecto con `./gradlew build`. Si falla por sandbox, red o permisos, reinténtalo con permisos escalados. No continúes hasta entender si el fallo es nuevo o preexistente.
3. Revisa los cambios con `git diff --staged` y, si no hay nada staged, con `git status --short` y `git diff`.
4. Si el build falla por un problema introducido en el cambio actual, corrígelo antes de seguir. Si el fallo es preexistente y el usuario igualmente quiere commitear, explícalo explícitamente antes de decidir.
5. Redacta un mensaje de commit corto en inglés, imperativo y específico. Usa Conventional Commits cuando aplique.
6. Ejecuta `git add -A` y luego `git commit -m "<message>"`.
7. Muestra al usuario el hash del commit, el mensaje y una validación breve.

## Criterios

- No asumas workflows de Node ni npm.
- Prioriza `./gradlew build` como chequeo principal.
- No hagas commit si agregaste cambios inesperados o no revisados.
- Si el árbol ya estaba roto, dilo explícitamente en la respuesta final.
