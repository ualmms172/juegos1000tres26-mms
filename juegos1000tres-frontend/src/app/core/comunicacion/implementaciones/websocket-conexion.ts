import { Conexion } from '../conexion';

/**
 * Implementación de Conexion usando WebSocket.
 */
export class WebSocketConexion implements Conexion<string> {
  readonly TIPO_COMPROBAR_HOMOLOGO: string = 'ComprobarHomologo';

  private webSocket: WebSocket | null = null;
  private salaId: string;
  private juego: string | null = null;
  private canalSala: string;
  private conectado: boolean = false;
  // tercer parametro opcional: juego -> si se proporciona y canalSala es host/base,
  // construye ws://host:port/ws/salas/{sala}/{juego}
  constructor(salaId: string, canalSala: string, juego?: string) {
    if (!salaId || salaId.trim() === '') {
      throw new Error('El salaId es obligatorio');
    }
    if (!canalSala || canalSala.trim() === '') {
      throw new Error('El canalSala es obligatorio');
    }

    this.salaId = salaId;
    this.juego = juego && juego.trim() ? juego.trim() : null;

    if (this.juego) {
      // si canalSala no contiene /ws/salas/ asumimos que es host base
      if (!canalSala.includes('/ws/salas/')) {
        const base = canalSala.replace(/\/?$/, '');
        this.canalSala = `${base}/ws/salas/${encodeURIComponent(this.salaId)}/${encodeURIComponent(this.juego)}`;
      } else {
        // intenta insertar juego después del segmento de sala
        this.canalSala = canalSala.replace(`${encodeURIComponent(this.salaId)}`, `${encodeURIComponent(this.salaId)}/${encodeURIComponent(this.juego)}`);
      }
    } else {
      this.canalSala = canalSala;
    }
  }

  conectar(): void {
    if (this.conectado) {
      return;
    }

    try {
      this.webSocket = new WebSocket(this.canalSala);

      this.webSocket.onopen = () => {
        this.conectado = true;
        console.log('WebSocket conectado:', this.canalSala);
      };

      this.webSocket.onerror = (error) => {
        console.error('Error en WebSocket:', error);
        this.conectado = false;
      };

      this.webSocket.onclose = () => {
        this.conectado = false;
        console.log('WebSocket desconectado');
      };
    } catch (error) {
      console.error('Error al conectar WebSocket:', error);
      throw new Error(`No se pudo conectar al WebSocket: ${this.canalSala}`);
    }
  }

  desconectar(): void {
    if (this.webSocket) {
      this.webSocket.close();
      this.webSocket = null;
      this.conectado = false;
    }
  }

  enviar(payload: string): void {
    if (!this.conectado) {
      throw new Error('WebSocket no está conectado');
    }

    if (!payload || payload.trim() === '') {
      throw new Error('El payload es obligatorio');
    }

    if (!this.webSocket) {
      throw new Error('WebSocket no está inicializado');
    }

    this.webSocket.send(payload);
  }

  recibir(): Promise<string> {
    if (!this.conectado) {
      return Promise.reject(new Error('WebSocket no está conectado'));
    }

    return new Promise((resolve, reject) => {
      if (!this.webSocket) {
        reject(new Error('WebSocket no está inicializado'));
        return;
      }

      let temporizador: ReturnType<typeof setTimeout> | undefined;

      const limpiar = (): void => {
        if (temporizador !== undefined) {
          clearTimeout(temporizador);
          temporizador = undefined;
        }

        this.webSocket?.removeEventListener('message', manejadorMensaje);
        this.webSocket?.removeEventListener('close', manejadorClose);
        this.webSocket?.removeEventListener('error', manejadorError);
      };

      const manejadorMensaje = (evento: Event) => {
        if (evento instanceof MessageEvent) {
          limpiar();
          resolve(evento.data);
        }
      };

      const manejadorClose = (): void => {
        limpiar();
        reject(new Error('WebSocket desconectado'));
      };

      const manejadorError = (): void => {
        limpiar();
        reject(new Error('Error en WebSocket'));
      };

      this.webSocket.addEventListener('message', manejadorMensaje);
      this.webSocket.addEventListener('close', manejadorClose);
      this.webSocket.addEventListener('error', manejadorError);

      temporizador = setTimeout(() => {
        limpiar();
        reject(new Error('Timeout esperando mensaje del WebSocket'));
      }, 30000); // 30 segundos de timeout
    });
  }

  getClasePayload(): { name: string } {
    return String;
  }

  getTipoComunicacion(): string {
    return 'WebSocket';
  }

  getSalaId(): string {
    return this.salaId;
  }

  getCanalSala(): string {
    return this.canalSala;
  }

  getTipoVerificacion(): string {
    return this.TIPO_COMPROBAR_HOMOLOGO;
  }

  comprobarHomologo(tipoComunicacionFront: string): boolean {
    if (!tipoComunicacionFront || tipoComunicacionFront.trim() === '') {
      return false;
    }
    return (
      this.getTipoComunicacion().toLowerCase() ===
      tipoComunicacionFront.toLowerCase().trim()
    );
  }

  /**
   * Comprueba si el WebSocket está conectado.
   */
  estaConectado(): boolean {
    return this.conectado;
  }
}
