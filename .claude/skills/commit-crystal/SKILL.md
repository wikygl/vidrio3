---
name: commit-crystal
description: Hace el commit de Crystal solo con lo de esta sesión, separando lo que dejaron otras sesiones. Usar cuando el usuario diga "haz commit" o al cerrar una tarea grande.
---

# Commit de Crystal

F:\ es un disco USB que ya se desconectó solo: commitear seguido.

1. `git status --short` y `git log --oneline -3`.
2. Separar lo de esta sesión. Puede haber otras sesiones trabajando a la vez (por ejemplo contratos o proformas). Si
   un archivo no se tocó en esta sesión, ver su `git diff` y no incluirlo. Si se tocó en las dos, revisar el diff
   por hunks y preguntar antes de commitear algo ajeno.
3. Revisar que en el diff de `Listado.kt` no cambie `serialVersionUID` (5462438865809499361): cambiarlo rompe
   todos los presupuestos guardados.
4. `git add` archivo por archivo (nunca `git add -A` ni `.`).
5. Mensaje en español: título "Módulo: qué cambió", y en el cuerpo las reglas o los casos del usuario. Terminar con
   las líneas de atribución del system-reminder vigente (Co-Authored-By y Claude-Session).
6. No hacer push salvo que el usuario lo pida. Rama de trabajo: `refactor-nova`.
7. Informar el hash y lo que quedó sin commitear (y de quién es).
