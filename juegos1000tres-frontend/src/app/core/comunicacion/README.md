# Sistema de Comunicación para Juegos - Frontend

Este es el equivalente en TypeScript/Angular del sistema de comunicación del backend Java. Implementa un patrón robusto y extensible para la comunicación entre cliente y servidor.

## Estructura

### Clases Generales (`src/app/core/comunicacion/`)

Estas clases definen el patrón base y pueden reutilizarse para cualquier juego:

- **`Enviable`**: Clase abstracta que define objetos que pueden ser enviados/recibidos por la red.
  - Método `out()`: Serializa el objeto para enviar
  - Método `in()`: Deserializa datos recibidos

- **`Envio<PAYLOAD>`**: Estrategia de conversión de Enviable a un formato específico (String, JSON, etc).
  - Traductor entre Enviable y formato de transmisión

- **`Recibo<PAYLOAD>`**: Estrategia para procesar payloads recibidos.
  - Enrutamiento por comando: Extrae el comando del payload y ejecuta el evento correspondiente
  - Procesador personalizado: Aplica lógica personalizada a cada payload

- **`Evento<PAYLOAD>`**: Interfaz para la lógica que se ejecuta al recibir un comando.
  - `hacer(payload)`: Procesa el payload
  - **Nota**: En el frontend, los eventos son unidireccionales. Si necesitas enviar una respuesta, usa `Traductor.enviar()` como una acción independiente (no sincrónica al evento).

- **`Conexion<PAYLOAD>`**: Interfaz que define el contrato de comunicación de red.
  - `conectar()`, `desconectar()`, `enviar()`, `recibir()`

- **`ContextoEvento`**: Contexto pasado a cada evento.
  - Permite al evento establecer una respuesta que será retornada al cliente

- **`Traductor<PAYLOAD>`**: Orquestador central que coordina todo.
  - Integra Conexion, Envio, Recibo
  - Maneja el flujo completo de comunicación

### Implementaciones (`src/app/core/comunicacion/implementaciones/`)

- **`ApiConexion`**: Implementación usando HTTP/REST
- **`WebSocketConexion`**: Implementación usando WebSocket

## Cómo Usar para un Juego Específico

### 1. Crear el Enviable del Juego

```typescript
// src/features/games/miJuego/comunicacion/miJuegoEstadoEnviable.ts

export class MiJuegoEstadoEnviable extends Enviable {
  private estado: Map<string, unknown> = new Map();

  override out(): string {
    // Serializar a JSON
    const objeto: Record<string, unknown> = {};
    this.estado.forEach((value, key) => {
      objeto[key] = value;
    });
    return JSON.stringify(objeto);
  }

  override in(entrada: unknown): void {
    // Deserializar desde JSON
    const objeto = JSON.parse(entrada as string);
    this.estado.clear();
    Object.entries(objeto).forEach(([key, value]) => {
      this.estado.set(key, value);
    });
  }

  // Métodos auxiliares
  getEstado(): Record<string, unknown> { ... }
  setEstado(clave: string, valor: unknown): void { ... }
}
```

### 2. Crear los Eventos del Juego

```typescript
// src/features/games/miJuego/comunicacion/miComandoMiJuegoEvento.ts

export class MiComandoMiJuegoEvento implements Evento<string> {
  constructor(private juego: any) {}

  hacer(payload: string): void {
    const datos = JSON.parse(payload);
    
    // Lógica del juego: procesar los datos
    this.juego.procesarComando(datos);
    
    // Si necesitas enviar una respuesta al servidor:
    // Usa Traductor.enviar() como una acción INDEPENDIENTE
    // (no es una respuesta sincrónica al evento)
  }
}
```

### 3. Configurar el Traductor en el Componente/Servicio

```typescript
// src/features/games/miJuego/miJuego.component.ts

import { WebSocketConexion, Envio, Recibo, Traductor } from '@app/core/comunicacion';
import { MiJuegoEstadoEnviable, MiComandoMiJuegoEvento } from './comunicacion';

export class MiJuegoComponent {
  private traductor: Traductor<string>;

  constructor() {
    // 1. Crear la conexión
    const conexion = new WebSocketConexion(
      'mi-sala-id',
      'ws://localhost:8080/api/sala/mi-sala-id'
    );

    // 2. Crear la estrategia de envío
    const envio = Envio.paraStringDesdeOut();

    // 3. Crear la estrategia de recepción
    const recibo = new Recibo<string>(String, Recibo.extractorComandoDesdeJson())
      .conEvento('miComando', new MiComandoMiJuegoEvento(this))
      .conEvento('otroComando', new OtroComandoMiJuegoEvento(this));

    // 4. Crear el traductor
    this.traductor = new Traductor(conexion, envio, recibo);
    
    // 5. Conectar
    this.traductor.conectar();
  }

  // Enviar un mensaje
  enviarAccion(datos: any): void {
    const enviable = new MiJuegoEstadoEnviable(datos);
    this.traductor.enviar(enviable);
  }

  // Procesar un mensaje recibido
  procesarMensaje(payload: string): void {
    this.traductor.procesar(payload); // Los eventos se ejecutan automáticamente
  }

  // Métodos del juego (llamados desde eventos)
  procesarComando(datos: any): Record<string, unknown> {
    // Lógica del juego
    return { exito: true, nuevoEstado: {} };
  }
}
```

## Flujo de Comunicación

### Envío de datos:

```
Componente/Servicio
    ↓
Crea Enviable (ej: MiJuegoEstadoEnviable)
    ↓
Traductor.enviar(enviable)
    ↓
Envio.traducirEnviableAFormato() → String (JSON)
    ↓
Conexion.enviar(payload)
    ↓
WebSocket/HTTP envía al servidor
```

### Recepción de datos:

```
Servidor envía payload (String JSON)
    ↓
Conexion.recibir()
    ↓
Traductor.procesar(payload)
    ↓
Recibo extrae comando del payload
    ↓
Recibo busca Evento registrado para ese comando
    ↓
Evento.hacer(payload) ejecuta lógica
    ↓
(Si necesitas enviar algo de vuelta, usa Traductor.enviar() como acción independiente)
```

## Ventajas del Patrón

✅ **Separación de responsabilidades**: Cada clase tiene un propósito específico  
✅ **Extensible**: Fácil agregar nuevos eventos, estrategias o implementaciones  
✅ **Reutilizable**: Las clases generales sirven para cualquier juego  
✅ **Type-safe**: TypeScript valida tipos en tiempo de compilación  
✅ **Testeable**: Cada componente es independiente y fácil de testear  
✅ **Consistencia**: Mismo patrón que el backend Java  

## Ejemplo Completo: Juego Preguntas

Mira las carpetas:
- `src/app/core/comunicacion/` - Clases generales
- `src/features/games/preguntas/comunicacion/` - Implementación específica para Preguntas
