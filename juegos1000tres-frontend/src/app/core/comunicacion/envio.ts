import { Enviable } from './enviable';

/**
 * Estrategia de envío: convierte un Enviable a un formato específico (String, JSON, etc).
 * Es genérica sobre el tipo de PAYLOAD (el formato final de envío).
 */
export class Envio<PAYLOAD> {
  private clasePayload: { name: string };
  private traductor: (enviable: Enviable) => PAYLOAD;

  constructor(
    clasePayload: { name: string },
    traductor: (enviable: Enviable) => PAYLOAD
  ) {
    if (!clasePayload) {
      throw new Error('La clase de payload es obligatoria');
    }
    if (!traductor) {
      throw new Error('La función de traducción es obligatoria');
    }

    this.clasePayload = clasePayload;
    this.traductor = traductor;
  }

  /**
   * Traduce un Enviable al formato PAYLOAD.
   */
  traducirEnviableAFormato(enviable: Enviable): PAYLOAD {
    return this.traductor(enviable);
  }

  /**
   * Retorna la clase del payload.
   */
  getClasePayload(): { name: string } {
    return this.clasePayload;
  }

  /**
   * Estrategia predeterminada: envío como String (JSON).
   */
  static paraStringDesdeOut(): Envio<string> {
    return new Envio<string>(String, (enviable: Enviable) => {
      if (!enviable) {
        throw new Error('El enviable es obligatorio');
      }

      const salida = enviable.out();
      if (typeof salida !== 'string') {
        throw new Error(
          'Envio<String> requiere que Enviable.out() devuelva String'
        );
      }

      return salida;
    });
  }
}
