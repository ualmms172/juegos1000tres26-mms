package com.juegos1000tres.juegos1000tres_backend.juegos.Preguntas;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el juego Preguntas.
 * Maneja endpoints específicos para la sala: /sala/{uuid}/juego/preguntas
 */
@RestController
@RequestMapping("/sala/{uuid}/juego/preguntas")
@CrossOrigin(origins = "*")
public class PreguntasController {

  private final PreguntasService preguntasService;

  public PreguntasController(PreguntasService preguntasService) {
    this.preguntasService = preguntasService;
  }

  /**
   * Obtiene el estado actual de la partida de Preguntas.
   */
  @GetMapping("/estado")
  public ResponseEntity<PreguntasEstadoRespuesta> obtenerEstado(@PathVariable String uuid) {
    try {
      PreguntasEstadoRespuesta estado = preguntasService.obtenerEstado(uuid);
      return ResponseEntity.ok(estado);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Registra un jugador en la partida.
   */
  @PostMapping("/registrar")
  public ResponseEntity<PreguntasEstadoRespuesta> registrarJugador(
      @PathVariable String uuid,
      @RequestParam String jugadorId,
      @RequestParam(defaultValue = "Jugador") String nombre) {
    try {
      PreguntasEstadoRespuesta respuesta = preguntasService.registrarJugador(uuid, jugadorId, nombre);
      return ResponseEntity.ok(respuesta);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Inicia una nueva ronda (solo host).
   */
  @PostMapping("/iniciar-ronda")
  public ResponseEntity<PreguntasEstadoRespuesta> iniciarRonda(
      @PathVariable String uuid,
      @RequestParam String actorId) {
    try {
      PreguntasEstadoRespuesta respuesta = preguntasService.iniciarRonda(uuid, actorId);
      return ResponseEntity.ok(respuesta);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Actualiza el borrador de respuesta de un jugador.
   */
  @PostMapping("/actualizar-borrador")
  public ResponseEntity<PreguntasEstadoRespuesta> actualizarBorrador(
      @PathVariable String uuid,
      @RequestParam String jugadorId,
      @RequestBody Map<String, String> payload) {
    try {
      String texto = payload.getOrDefault("texto", "");
      PreguntasEstadoRespuesta respuesta = preguntasService.actualizarBorrador(uuid, jugadorId, texto);
      return ResponseEntity.ok(respuesta);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Envía la respuesta finalizada de un jugador.
   */
  @PostMapping("/enviar-respuesta")
  public ResponseEntity<PreguntasEstadoRespuesta> enviarRespuesta(
      @PathVariable String uuid,
      @RequestParam String jugadorId,
      @RequestBody Map<String, String> payload) {
    try {
      String respuesta = payload.getOrDefault("respuesta", "");
      PreguntasEstadoRespuesta estadoRespuesta = preguntasService.enviarRespuesta(uuid, jugadorId, respuesta);
      return ResponseEntity.ok(estadoRespuesta);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * El jugador elegido elige la mejor respuesta.
   */
  @PostMapping("/elegir-respuesta")
  public ResponseEntity<PreguntasEstadoRespuesta> elegirRespuesta(
      @PathVariable String uuid,
      @RequestParam String jugadorId,
      @RequestParam String opcionId) {
    try {
      PreguntasEstadoRespuesta respuesta = preguntasService.elegirRespuesta(uuid, jugadorId, opcionId);
      return ResponseEntity.ok(respuesta);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Finaliza la partida y suma victoria al ganador.
   */
  @PostMapping("/finalizar")
  public ResponseEntity<Void> finalizarPartida(
      @PathVariable String uuid,
      @RequestParam String actorId) {
    try {
      preguntasService.finalizarPartida(uuid, actorId);
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
