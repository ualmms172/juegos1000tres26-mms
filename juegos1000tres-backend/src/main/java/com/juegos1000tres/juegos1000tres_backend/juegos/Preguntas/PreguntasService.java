package com.juegos1000tres.juegos1000tres_backend.juegos.Preguntas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.juegos1000tres.juegos1000tres_backend.comunicacion.Conexion;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Envio;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Recibo;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Traductor;
import com.juegos1000tres.juegos1000tres_backend.sala.SalaRoom;
import com.juegos1000tres.juegos1000tres_backend.sala.SalaService;

/**
 * Servicio que gestiona las partidas de Preguntas.
 * Mantiene un mapa de partidas por uuid de sala.
 */
@Service
public class PreguntasService {

  private final Map<String, PreguntasJuego> partidas = new ConcurrentHashMap<>();
  private final SalaService salaService;

  public PreguntasService(SalaService salaService) {
    this.salaService = salaService;
  }

  /**
   * Obtiene o crea la partida de Preguntas para una sala.
   */
  public PreguntasJuego obtenerPartida(String uuid) {
    return partidas.computeIfAbsent(uuid, this::crearPartidaNueva);
  }

  /**
   * Obtiene el estado actual de la partida.
   */
  public PreguntasEstadoRespuesta obtenerEstado(String uuid) {
    PreguntasJuego partida = obtenerPartida(uuid);
    return construirRespuestaEstado(partida);
  }

  /**
   * Registra un jugador en la partida.
   */
  public PreguntasEstadoRespuesta registrarJugador(String uuid, String jugadorId, String nombreJugador) {
    PreguntasJuego partida = obtenerPartida(uuid);
    partida.registrarJugador(jugadorId, nombreJugador);
    return construirRespuestaEstado(partida);
  }

  /**
   * Inicia una nueva ronda.
   */
  public PreguntasEstadoRespuesta iniciarRonda(String uuid, String actorId) {
    SalaRoom sala = salaService.obtenerSalaRoom(uuid);
    if (sala == null || !sala.getHostId().equals(actorId)) {
      throw new IllegalArgumentException("Solo el host puede iniciar una ronda");
    }

    PreguntasJuego partida = obtenerPartida(uuid);
    partida.iniciarRonda();
    return construirRespuestaEstado(partida);
  }

  /**
   * Actualiza el borrador de respuesta de un jugador.
   */
  public PreguntasEstadoRespuesta actualizarBorrador(String uuid, String jugadorId, String texto) {
    PreguntasJuego partida = obtenerPartida(uuid);
    partida.actualizarBorrador(jugadorId, texto);
    return construirRespuestaEstado(partida);
  }

  /**
   * Envía la respuesta finalizada de un jugador.
   */
  public PreguntasEstadoRespuesta enviarRespuesta(String uuid, String jugadorId, String respuesta) {
    PreguntasJuego partida = obtenerPartida(uuid);
    partida.enviarRespuesta(jugadorId, respuesta);
    return construirRespuestaEstado(partida);
  }

  /**
   * El jugador elegido elige la mejor respuesta.
   */
  public PreguntasEstadoRespuesta elegirRespuesta(String uuid, String jugadorId, String opcionId) {
    PreguntasJuego partida = obtenerPartida(uuid);
    partida.elegirRespuesta(jugadorId, opcionId);
    return construirRespuestaEstado(partida);
  }

  /**
   * Finaliza la partida y actualiza victorias en la sala.
   */
  public void finalizarPartida(String uuid, String actorId) {
    SalaRoom sala = salaService.obtenerSalaRoom(uuid);
    if (sala == null || !sala.getHostId().equals(actorId)) {
      throw new IllegalArgumentException("Solo el host puede finalizar la partida");
    }

    PreguntasJuego partida = obtenerPartida(uuid);
    
    // Obtener ganador de la última ronda y sumar victoria
    String ganadorId = partida.obtenerGanadorRonda();
    if (ganadorId != null) {
      salaService.incrementarVictoria(uuid, ganadorId);
    }

    // Limpiar la partida
    partidas.remove(uuid);
  }

  /**
   * Construye la respuesta con el estado actualizado de la partida.
   */
  private PreguntasEstadoRespuesta construirRespuestaEstado(PreguntasJuego partida) {
    Map<String, Object> estado = partida.crearEstadoEnviable().getEstado();
    
    PreguntasEstadoRespuesta respuesta = new PreguntasEstadoRespuesta();
    respuesta.setComando(asString(estado.get("comando"), "ESTADO_PREGUNTAS"));
    respuesta.setFase(asString(estado.get("fase"), "ESPERANDO_JUGADORES"));
    respuesta.setRondaActual(asInt(estado.get("rondaActual"), 0));
    respuesta.setMensaje(asString(estado.get("mensaje"), ""));
    respuesta.setTiempoLimiteRespuestaSegundos(asInt(estado.get("tiempoLimiteRespuestaSegundos"), 0));
    respuesta.setTiempoRestanteMs(asLong(estado.get("tiempoRestanteMs"), 0L));

    respuesta.setJugadorElegido(asMap(estado.get("jugadorElegido")));
    respuesta.setPreguntaActual(asString(estado.get("preguntaActual"), ""));
    respuesta.setRespuestasEsperadas(asInt(estado.get("respuestasEsperadas"), 0));
    respuesta.setRespuestasRecibidas(asInt(estado.get("respuestasRecibidas"), 0));
    respuesta.setRespondedoresPendientes(asListStrings(estado.get("respondedoresPendientes")));
    respuesta.setOpciones(asListMaps(estado.get("opciones")));
    respuesta.setMarcador(asListMaps(estado.get("marcador")));
    respuesta.setPuedeIniciarRonda(Boolean.TRUE.equals(estado.get("puedeIniciarRonda")));
    
    return respuesta;
  }

  private PreguntasJuego crearPartidaNueva(String uuid) {
    return new PreguntasJuego(
        2,
        crearTraductorNoOp(uuid),
        crearTraductorNoOp(uuid),
        cargarPreguntasPorDefecto());
  }

  private Traductor<String> crearTraductorNoOp(String uuid) {
    Conexion<String> conexion = new Conexion<>() {
      @Override
      public void conectar() {
      }

      @Override
      public void desconectar() {
      }

      @Override
      public void enviar(String payload) {
      }

      @Override
      public String recibir() {
        return "{}";
      }

      @Override
      public Class<String> getClasePayload() {
        return String.class;
      }

      @Override
      public String getTipoComunicacion() {
        return "HTTP";
      }

      @Override
      public String getSalaId() {
        return uuid;
      }

      @Override
      public String getCanalSala() {
        return "";
      }
    };

    Envio<String> envio = Envio.paraStringDesdeOut();
    Recibo<String> recibo = Recibo.paraJsonString();
    return new Traductor<>(conexion, envio, recibo);
  }

  private List<String> cargarPreguntasPorDefecto() {
    List<String> preguntas = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
        new ClassPathResource("preguntas/preguntas.txt").getInputStream(), StandardCharsets.UTF_8))) {
      String linea;
      while ((linea = reader.readLine()) != null) {
        String pregunta = linea.trim();
        if (!pregunta.isEmpty()) {
          preguntas.add(pregunta);
        }
      }
    } catch (java.io.IOException error) {
      preguntas.add("¿Cuál es la respuesta correcta?");
      preguntas.add("¿Qué opción elegirías en esta situación?");
    }

    return preguntas;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> asMap(Object value) {
    if (value instanceof Map<?, ?> map) {
      return (Map<String, Object>) map;
    }
    return Map.of();
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> asListMaps(Object value) {
    if (value instanceof List<?> list) {
      List<Map<String, Object>> resultado = new ArrayList<>();
      for (Object item : list) {
        if (item instanceof Map<?, ?> map) {
          resultado.add((Map<String, Object>) map);
        }
      }
      return resultado;
    }
    return List.of();
  }

  private List<String> asListStrings(Object value) {
    if (value instanceof List<?> list) {
      List<String> resultado = new ArrayList<>();
      for (Object item : list) {
        if (item != null) {
          resultado.add(String.valueOf(item));
        }
      }
      return resultado;
    }
    return List.of();
  }

  private String asString(Object value, String defecto) {
    return value == null ? defecto : String.valueOf(value);
  }

  private int asInt(Object value, int defecto) {
    if (value instanceof Number numero) {
      return numero.intValue();
    }
    try {
      return value == null ? defecto : Integer.parseInt(String.valueOf(value));
    } catch (NumberFormatException error) {
      return defecto;
    }
  }

  private long asLong(Object value, long defecto) {
    if (value instanceof Number numero) {
      return numero.longValue();
    }
    try {
      return value == null ? defecto : Long.parseLong(String.valueOf(value));
    } catch (NumberFormatException error) {
      return defecto;
    }
  }
}
