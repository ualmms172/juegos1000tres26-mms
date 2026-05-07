/**
 * Interfaz que representa un evento a ejecutar cuando se recibe un cierto comando/payload.
 * PAYLOAD es el tipo de dato que recibe el evento (String, JSON, etc).
 * 
 * Nota: En el frontend, los eventos son unidireccionales (no tienen "respuesta").
 * Si necesitas enviar una respuesta, usa Traductor.enviar() como una acción independiente.
 */
export interface Evento<PAYLOAD> {
  /**
   * Ejecuta la lógica del evento.
   * @param payload Los datos recibidos
   */
  hacer(payload: PAYLOAD): void;
}
