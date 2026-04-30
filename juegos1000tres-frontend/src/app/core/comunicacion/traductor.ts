import { Conexion } from './conexion';
import { Enviable } from './enviable';
import { Envio } from './envio';
import { Recibo } from './recibo';

/**
 * Orquestador central de la comunicación.
 * Coordina la conexión, el envío y la recepción de datos.
 * PAYLOAD es el tipo de dato que se transmite (String, JSON, etc).
 */
export class Traductor<PAYLOAD> {
  private conexion: Conexion<PAYLOAD>;
  private envio: Envio<PAYLOAD>;
  private recibo: Recibo<PAYLOAD>;
  private clasePayload: { name: string };

  constructor(
    conexion: Conexion<PAYLOAD>,
    envio: Envio<PAYLOAD>,
    recibo: Recibo<PAYLOAD>
  ) {
    if (!conexion) {
      throw new Error('La conexión es obligatoria');
    }
    if (!envio) {
      throw new Error('La estrategia de envío es obligatoria');
    }
    if (!recibo) {
      throw new Error('La estrategia de recibimiento es obligatoria');
    }

    this.conexion = conexion;
    this.envio = envio;
    this.recibo = recibo;
    this.clasePayload = conexion.getClasePayload();

    this.validarCompatibilidadDePayload(
      this.clasePayload,
      this.envio.getClasePayload(),
      'Envio'
    );
    this.validarCompatibilidadDePayload(
      this.clasePayload,
      this.recibo.getClasePayload(),
      'Recibo'
    );
  }

  /**
   * Traduce un Enviable al formato PAYLOAD usando la estrategia de envío.
   */
  traducirEnviableAFormato(enviable: Enviable): PAYLOAD {
    return this.envio.traducirEnviableAFormato(enviable);
  }

  /**
   * Traduce y envía un Enviable a través de la conexión.
   */
  enviar(enviable: Enviable): void {
    const payload = this.traducirEnviableAFormato(enviable);
    this.conexion.enviar(payload);
  }

  /**
   * Envía un payload directamente sin traducción.
   */
  enviarPayload(payload: PAYLOAD): void {
    if (!payload) {
      throw new Error('El payload es obligatorio');
    }
    this.conexion.enviar(payload);
  }

  /**
   * Procesa un payload recibido (ejecuta el evento correspondiente).
   * En el frontend, los eventos son unidireccionales: el evento procesa los datos pero no retorna respuesta.
   * Si necesitas enviar algo de vuelta, usa enviar() como una acción independiente.
   */
  procesar(payload: PAYLOAD): void {
    if (!payload) {
      throw new Error('El payload es obligatorio');
    }

    this.recibo.procesar(payload);
  }

  /**
   * Recibe un payload a través de la conexión.
   */
  recibirPayload(): PAYLOAD | Promise<PAYLOAD> {
    return this.conexion.recibir();
  }

  /**
   * Recibe y procesa un payload.
   */
  async recibirYProcesar(): Promise<void> {
    const payload = await Promise.resolve(this.recibirPayload());
    this.procesar(payload);
  }

  /**
   * Conecta la comunicación.
   */
  conectar(): void {
    this.conexion.conectar();
  }

  /**
   * Desconecta la comunicación.
   */
  desconectar(): void {
    this.conexion.desconectar();
  }

  /**
   * Obtiene la conexión subyacente.
   */
  getConexion(): Conexion<PAYLOAD> {
    return this.conexion;
  }

  /**
   * Obtiene la estrategia de envío.
   */
  getEnvio(): Envio<PAYLOAD> {
    return this.envio;
  }

  /**
   * Obtiene la estrategia de recibimiento.
   */
  getRecibo(): Recibo<PAYLOAD> {
    return this.recibo;
  }

  /**
   * Valida que todas las partes usen el mismo tipo de PAYLOAD.
   */
  private validarCompatibilidadDePayload(
    clasePayloadConexion: { name: string },
    clasePayloadComponent: { name: string },
    nombreComponent: string
  ): void {
    if (
      clasePayloadConexion.name !== clasePayloadComponent.name
    ) {
      throw new Error(
        `Incompatibilidad de PAYLOAD: Conexión usa ${clasePayloadConexion.name} pero ${nombreComponent} usa ${clasePayloadComponent.name}`
      );
    }
  }
}
