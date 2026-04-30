import { Enviable } from '../../../../core/comunicacion';

export class PreguntasEstadoEnviable extends Enviable {
  private readonly estado: Record<string, unknown>;

  constructor(estado: Record<string, unknown>) {
    super();
    this.estado = estado;
  }

  out(): string {
    return JSON.stringify(this.estado);
  }

  in(entrada: unknown): void {
    void entrada;
  }
}
