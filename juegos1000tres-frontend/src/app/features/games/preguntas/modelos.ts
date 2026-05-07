export type FaseRonda = 'ESPERANDO_JUGADORES' | 'RESPONDIENDO' | 'ELEGIENDO' | 'MOSTRANDO_RESULTADO';

export interface JugadorElegido {
  jugadorId: string;
  nombreJugador: string;
}

export interface OpcionPregunta {
  opcionId: string;
  autorJugadorId: string;
  texto: string;
  seleccionable: boolean;
}

export interface EstadoPreguntas {
  comando: string;
  fase: FaseRonda;
  rondaActual: number;
  mensaje: string;
  tiempoLimiteRespuestaSegundos: number;
  tiempoRestanteMs: number;
  jugadorElegido?: JugadorElegido;
  preguntaActual: string;
  respuestasEsperadas: number;
  respuestasRecibidas: number;
  respondedoresPendientes: string[];
  opciones: OpcionPregunta[];
  marcador: Array<{ nombre: string; puntos: number }>;
  puedeIniciarRonda: boolean;
}
