/**
 * Clase abstracta que define un objeto que puede ser enviado/recibido a través de la red.
 * Toda información que viaje por la red debe implementar este contrato.
 */
export abstract class Enviable {
  /**
   * Convierte el objeto a su formato de transmisión (generalmente JSON string).
   * @returns El objeto serializado para enviar
   */
  abstract out(): unknown;

  /**
   * Desconvierte un formato de transmisión y carga el objeto.
   * @param entrada Los datos recibidos (generalmente JSON string)
   */
  abstract in(entrada: unknown): void;
}
