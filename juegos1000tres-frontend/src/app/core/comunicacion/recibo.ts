import { Evento } from './evento';

/**
 * Estrategia de recepción: procesa payloads recibidos y enruta a eventos.
 * Puede trabajar de dos formas:
 * 1. Con enrutamiento por comando: Extrae el comando del payload y lo enruta al evento correspondiente.
 * 2. Con procesador personalizado: Aplica un procesador personalizado a todo payload recibido.
 */
export class Recibo<PAYLOAD> {
  private static readonly PATRON_COMANDO_JSON = /"comando"\s*:\s*"([^"]+)"/;

  private clasePayload: { name: string };
  private extractorComando: ((payload: PAYLOAD) => string) | null;
  private procesadorPersonalizado: ((payload: PAYLOAD) => void) | null;
  private eventosPorComando: Map<string, Evento<PAYLOAD>>;

  constructor(
    clasePayload: { name: string },
    extractorComando?: (payload: PAYLOAD) => string
  );
  constructor(
    clasePayload: { name: string },
    procesadorPersonalizado?: (payload: PAYLOAD) => void
  );
  constructor(
    clasePayload: { name: string },
    extractorOProcesador?: ((payload: PAYLOAD) => string) | ((payload: PAYLOAD) => void)
  ) {
    if (!clasePayload) {
      throw new Error('La clase de payload es obligatoria');
    }

    this.clasePayload = clasePayload;
    this.eventosPorComando = new Map();

    // Detectar si es extractor o procesador por aridad
    if (extractorOProcesador) {
      if (extractorOProcesador.length === 1) {
        this.extractorComando = extractorOProcesador as (payload: PAYLOAD) => string;
        this.procesadorPersonalizado = null;
      } else {
        this.extractorComando = null;
        this.procesadorPersonalizado = extractorOProcesador as (
          payload: PAYLOAD
        ) => void;
      }
    } else {
      this.extractorComando = null;
      this.procesadorPersonalizado = null;
    }
  }

  /**
   * Registra un evento para un comando específico.
   */
  conEvento(comando: string, evento: Evento<PAYLOAD>): this {
    if (this.extractorComando === null) {
      throw new Error('Este Recibo no soporta enrutado por comando');
    }

    if (!comando) {
      throw new Error('El comando es obligatorio');
    }

    if (!evento) {
      throw new Error('El evento es obligatorio');
    }

    const comandoNormalizado = this.normalizarComando(comando);
    this.eventosPorComando.set(comandoNormalizado, evento);

    return this;
  }

  /**
   * Procesa un payload recibido y ejecuta el evento correspondiente.
   */
  procesar(payload: PAYLOAD): void {
    if (!payload) {
      throw new Error('El payload es obligatorio');
    }

    if (this.procesadorPersonalizado !== null) {
      this.procesadorPersonalizado(payload);
    } else if (this.extractorComando !== null) {
      const comando = this.extractorComando(payload);
      if (!comando) {
        throw new Error('No se pudo extraer el comando del payload');
      }

      const comandoNormalizado = this.normalizarComando(comando);
      const evento = this.eventosPorComando.get(comandoNormalizado);

      if (!evento) {
        throw new Error(
          `No hay evento registrado para el comando: ${comandoNormalizado}`
        );
      }

      evento.hacer(payload);
    } else {
      throw new Error(
        'El Recibo no tiene ni extractor de comando ni procesador personalizado'
      );
    }
  }

  /**
   * Obtiene la clase del payload.
   */
  getClasePayload(): { name: string } {
    return this.clasePayload;
  }

  /**
   * Normaliza un comando a minúsculas para comparación insensible a mayúsculas.
   */
  private normalizarComando(comando: string): string {
    return comando ? comando.toLowerCase().trim() : '';
  }

  /**
   * Extrae el comando de un JSON string usando regex.
   */
  static extractorComandoDesdeJson(): (payload: string) => string {
    return (payload: string) => {
      const match = Recibo.PATRON_COMANDO_JSON.exec(payload);
      return match ? match[1] : '';
    };
  }
}
