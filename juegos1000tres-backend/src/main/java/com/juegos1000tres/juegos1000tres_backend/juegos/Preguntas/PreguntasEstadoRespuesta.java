package com.juegos1000tres.juegos1000tres_backend.juegos.Preguntas;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO que representa el estado actual de una partida de Preguntas.
 * Se usa como respuesta en los endpoints REST.
 */
public class PreguntasEstadoRespuesta {

  @JsonProperty("comando")
  private String comando;

  @JsonProperty("fase")
  private String fase;

  @JsonProperty("rondaActual")
  private int rondaActual;

  @JsonProperty("mensaje")
  private String mensaje;

  @JsonProperty("tiempoLimiteRespuestaSegundos")
  private int tiempoLimiteRespuestaSegundos;

  @JsonProperty("tiempoRestanteMs")
  private long tiempoRestanteMs;

  @JsonProperty("jugadorElegido")
  private Map<String, Object> jugadorElegido;

  @JsonProperty("preguntaActual")
  private String preguntaActual;

  @JsonProperty("respuestasEsperadas")
  private int respuestasEsperadas;

  @JsonProperty("respuestasRecibidas")
  private int respuestasRecibidas;

  @JsonProperty("respondedoresPendientes")
  private List<String> respondedoresPendientes;

  @JsonProperty("opciones")
  private List<Map<String, Object>> opciones;

  @JsonProperty("marcador")
  private List<Map<String, Object>> marcador;

  @JsonProperty("puedeIniciarRonda")
  private boolean puedeIniciarRonda;

  // Constructores
  public PreguntasEstadoRespuesta() {
  }

  // Getters y Setters
  public String getComando() {
    return comando;
  }

  public void setComando(String comando) {
    this.comando = comando;
  }

  public String getFase() {
    return fase;
  }

  public void setFase(String fase) {
    this.fase = fase;
  }

  public int getRondaActual() {
    return rondaActual;
  }

  public void setRondaActual(int rondaActual) {
    this.rondaActual = rondaActual;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }

  public int getTiempoLimiteRespuestaSegundos() {
    return tiempoLimiteRespuestaSegundos;
  }

  public void setTiempoLimiteRespuestaSegundos(int tiempoLimiteRespuestaSegundos) {
    this.tiempoLimiteRespuestaSegundos = tiempoLimiteRespuestaSegundos;
  }

  public long getTiempoRestanteMs() {
    return tiempoRestanteMs;
  }

  public void setTiempoRestanteMs(long tiempoRestanteMs) {
    this.tiempoRestanteMs = tiempoRestanteMs;
  }

  public Map<String, Object> getJugadorElegido() {
    return jugadorElegido;
  }

  public void setJugadorElegido(Map<String, Object> jugadorElegido) {
    this.jugadorElegido = jugadorElegido;
  }

  public String getPreguntaActual() {
    return preguntaActual;
  }

  public void setPreguntaActual(String preguntaActual) {
    this.preguntaActual = preguntaActual;
  }

  public int getRespuestasEsperadas() {
    return respuestasEsperadas;
  }

  public void setRespuestasEsperadas(int respuestasEsperadas) {
    this.respuestasEsperadas = respuestasEsperadas;
  }

  public int getRespuestasRecibidas() {
    return respuestasRecibidas;
  }

  public void setRespuestasRecibidas(int respuestasRecibidas) {
    this.respuestasRecibidas = respuestasRecibidas;
  }

  public List<String> getRespondedoresPendientes() {
    return respondedoresPendientes;
  }

  public void setRespondedoresPendientes(List<String> respondedoresPendientes) {
    this.respondedoresPendientes = respondedoresPendientes;
  }

  public List<Map<String, Object>> getOpciones() {
    return opciones;
  }

  public void setOpciones(List<Map<String, Object>> opciones) {
    this.opciones = opciones;
  }

  public List<Map<String, Object>> getMarcador() {
    return marcador;
  }

  public void setMarcador(List<Map<String, Object>> marcador) {
    this.marcador = marcador;
  }

  public boolean isPuedeIniciarRonda() {
    return puedeIniciarRonda;
  }

  public void setPuedeIniciarRonda(boolean puedeIniciarRonda) {
    this.puedeIniciarRonda = puedeIniciarRonda;
  }

}
