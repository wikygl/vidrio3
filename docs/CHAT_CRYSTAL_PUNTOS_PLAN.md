# Plan Maestro: Chat Crystal <-> Puntos

## Objetivo

Refactorizar el modulo `red` para que `Crystal` sea el hub de mensajeria y permita:

- chat entre usuarios Crystal;
- chat operativo entre Crystal y Puntos;
- envio y apertura de presupuestos entre usuarios;
- envio y apertura de listas de medidas y materiales;
- integracion limpia con `MainActivity` para importar presupuestos y medidas;
- una base de contrato interoperable para que Puntos use la mensajeria de Crystal.

## Decisiones acordadas

1. `Crystal` mantiene la mensajeria como sistema principal.
2. `Puntos` puede usar esa mensajeria, pero la integracion debe apoyarse en un contrato comun y no en texto libre.
3. La identidad del chat debe ser estable y no depender del auth anonimo del terminal.
4. Los chats antiguos no se consideran requisito de migracion; eran pruebas.
5. La UI nueva debe preservar el flujo operativo actual de `MainActivity`.
6. Los datos importantes que use Puntos deben seguir la estructura importante que Crystal ya usa para chat y mensajes.

## Restricciones actuales

- `MainActivity` ya depende del chat para:
  - abrir `ListChatActivity`;
  - enviar presupuestos;
  - importar medidas;
  - mostrar badge de no leidos.
- Las reglas actuales de Firestore permiten uso normal del chat, pero no migracion cliente de chats historicos.
- `Puntos` puede terminar viviendo en otro proyecto Firebase; por eso el contrato debe ser explicito y desacoplado.

## Arquitectura objetivo

### 1. Crystal como hub

- `Crystal` sigue siendo el centro del chat.
- La estructura de `chats` y `messages` se mantiene compatible con lo actual en los campos criticos.
- Sobre esa base se agrega un contrato interoperable para mensajes de negocio.

### 2. Puntos como cliente interoperable

- `Puntos` debe poder leer y producir mensajes compatibles.
- En los campos importantes, Puntos usara la misma estructura que Crystal:
  - chat: `id`, `name`, `users`, `participantsKey`, `lastMsgDate`
  - mensaje: `id`, `message`, `from`, `tipo`, `nombreArchivo`, `dob`
- Sobre esos campos se agregan los metadatos nuevos:
  - `fromUid`
  - `sourceApp`
  - `targetApp`
  - `schemaVersion`
  - `payload`
  - `fileUrl`
  - `fileName`

### 3. Tipos de mensaje de negocio

- `text`
- `crystal_budget`
- `materials_request`
- `puntos_quote`
- `file`

## Flujo objetivo

### Crystal -> Crystal

1. Un tecnico genera presupuesto o medidas.
2. Lo envia por chat a otro usuario Crystal.
3. El receptor abre el mensaje y lo importa desde Crystal.

### Crystal -> Puntos

1. El tecnico genera materiales o presupuesto preliminar.
2. Lo envia por chat a un contacto operativo de Puntos.
3. Puntos abre el mensaje y lo usa para cotizar o generar respuesta.

### Puntos -> Crystal

1. Puntos responde con cotizacion o archivo de negocio.
2. Crystal recibe el mensaje en la misma conversacion.
3. El tecnico lo abre y lo usa dentro de su flujo.

## Fases de implementacion

### Fase 1. Contrato compartido

- Crear paquete nuevo de interoperabilidad.
- Definir plataformas, tipos de mensaje y payloads base.
- Definir adaptadores para mantener compatibilidad con `Chat` y `Message`.

### Fase 2. Identidad estable

- Centralizar identidad de chat en un solo proveedor.
- Mantener `participantsKey`.
- Asegurar que la app no use el auth anonimo como participante logico.

### Fase 3. Repositorios nuevos

- Separar acceso a Firestore de Activities.
- Crear repositorios para:
  - lista de chats;
  - busqueda de contactos;
  - mensajes;
  - archivos adjuntos.

### Fase 4. Integracion con MainActivity

- Extraer parsing de medidas.
- Extraer importacion de presupuestos.
- Convertir esos flujos en coordinadores reutilizables.

### Fase 5. UI nueva de chat

- Rehacer la bandeja de conversaciones.
- Rehacer la pantalla de conversacion.
- Mantener compatibilidad de acciones que hoy usa `MainActivity`.

### Fase 6. Integracion Puntos

- Hacer que Puntos consuma el mismo contrato.
- Reutilizar la estructura critica del chat de Crystal.
- Preparar capa puente para cuando Puntos opere fuera de este proyecto.

## Entregables de esta etapa

1. Plan persistente dentro del repo.
2. Contrato interoperable base.
3. Modelos y payloads iniciales.
4. Helpers para compatibilidad con el chat actual.

## Estado para retomar si hay interrupcion

### Ya acordado

- Crystal sera el hub de mensajeria.
- Puntos compartira el contrato de mensajes en los campos importantes.
- No se invertira tiempo en rescatar chats viejos.

### Siguiente paso exacto

1. Reemplazar gradualmente la logica vieja de `red` por repositorios y UI nueva.
2. Conectar el contrato nuevo con el envio de presupuestos y medidas desde `MainActivity`.
3. Preparar el canal operativo Crystal <-> Puntos sobre el mismo contrato.
