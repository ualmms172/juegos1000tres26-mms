/**
 * Interfaz que define el contrato para la comunicación de red.
 * PAYLOAD es el tipo de dato que se envía/recibe (String, JSON, etc).
 */
export interface Conexion<PAYLOAD> {
  /**
   * Establece la conexión.
   */
  conectar(): void;

  /**
   * Cierra la conexión.
   */
  desconectar(): void;

  /**
   * Envía un payload.
   */
  enviar(payload: PAYLOAD): void;

  /**
   * Recibe un payload (bloqueante o asincrónico según implementación).
   */
  recibir(): PAYLOAD | Promise<PAYLOAD>;

  /**
   * Retorna la clase del payload soportada.
   */
  getClasePayload(): { name: string };

  /**
   * Retorna el tipo de comunicación (ej: "WebSocket", "HTTP").
   */
  getTipoComunicacion(): string;

  /**
   * Retorna el ID de la sala.
   */
  getSalaId(): string;

  /**
   * Retorna el canal o URL de la sala.
   */
  getCanalSala(): string;

  /**
   * Retorna el tipo de verificación (por defecto "ComprobarHomologo").
   */
  getTipoVerificacion?(): string;

  /**
   * Comprueba si el tipo de comunicación del frontend es compatible.
   */
  comprobarHomologo?(tipoComunicacionFront: string): boolean;
}
