import { Evento } from '../../../../core/comunicacion';
import { EstadoPreguntas } from '../modelos';

export class PreguntasEstadoEvento implements Evento<string> {
  constructor(private readonly onEstado: (estado: Partial<EstadoPreguntas>) => void) {}

  hacer(payload: string): void {
    try {
      const estado = JSON.parse(payload) as Partial<EstadoPreguntas>;
      this.onEstado(estado);
    } catch {
      // Ignorar payloads no válidos.
    }
  }
}
