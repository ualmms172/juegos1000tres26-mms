package com.juegos1000tres.juegos1000tres_backend.juegos.SpaceInvaders;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.juegos1000tres.juegos1000tres_backend.comunicacion.Enviable;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Traductor;
import com.juegos1000tres.juegos1000tres_backend.modelos.Juego;

public class SpaceInvader extends Juego {

	private final Map<UUID, EstadoJugadorInterno> estadoJugadores;
	private volatile boolean enCurso;

	public SpaceInvader(int numeroJugadores, Traductor<?> conexionJugadores, Traductor<?> conexionPantalla) {
		super(numeroJugadores, true, conexionJugadores, conexionPantalla);
		this.estadoJugadores = new ConcurrentHashMap<>();
		this.enCurso = false;
	}

	public void registrarJugador(UUID jugadorId, String nombreJugador) {
		UUID id = Objects.requireNonNull(jugadorId, "El id del jugador es obligatorio");

		if (nombreJugador == null || nombreJugador.isBlank()) {
			throw new IllegalArgumentException("El nombre del jugador no puede estar vacio");
		}

		this.estadoJugadores.putIfAbsent(id, new EstadoJugadorInterno(id, nombreJugador.trim()));
	}

	public void actualizarPuntuacion(UUID jugadorId, int puntuacionTotal) {
		if (puntuacionTotal < 0) {
			throw new IllegalArgumentException("La puntuacion no puede ser negativa");
		}

		EstadoJugadorInterno estadoJugador = obtenerJugadorRegistrado(jugadorId);
		estadoJugador.setPuntuacion(puntuacionTotal);
	}

	public void marcarJugadorComoMuerto(UUID jugadorId) {
		EstadoJugadorInterno estadoJugador = obtenerJugadorRegistrado(jugadorId);
		estadoJugador.setMuerto(true);
	}

	public int getPuntuacion(UUID jugadorId) {
		return obtenerJugadorRegistrado(jugadorId).getPuntuacion();
	}

	public boolean haPerdido(UUID jugadorId) {
		return obtenerJugadorRegistrado(jugadorId).isMuerto();
	}

	public Set<UUID> getJugadoresQueHanPerdido() {
		Set<UUID> jugadoresMuertos = new LinkedHashSet<>();
		for (EstadoJugadorInterno estadoJugador : this.estadoJugadores.values()) {
			if (estadoJugador.isMuerto()) {
				jugadoresMuertos.add(estadoJugador.getJugadorId());
			}
		}
		return Set.copyOf(jugadoresMuertos);
	}

	public EstadoJugadoresSpaceInvaders crearEstadoEnviable() {
		List<EstadoJugadoresSpaceInvaders.EstadoJugadorDTO> jugadores = new ArrayList<>();
		for (EstadoJugadorInterno estado : this.estadoJugadores.values()) {
			jugadores.add(new EstadoJugadoresSpaceInvaders.EstadoJugadorDTO(
					estado.getJugadorId(),
					estado.getNombreJugador(),
					estado.getPuntuacion(),
					estado.isMuerto()));
		}

		return new EstadoJugadoresSpaceInvaders(jugadores);
	}

	@Override
	public void procesarMensajeEntrante(Enviable mensaje) {
		// El procesamiento real se conectara desde los eventos/API.
	}

	@Override
	public void iniciar() {
		this.enCurso = true;
	}

	@Override
	public void terminar() {
		this.enCurso = false;
	}

	public boolean isEnCurso() {
		return enCurso;
	}

	private EstadoJugadorInterno obtenerJugadorRegistrado(UUID jugadorId) {
		UUID id = Objects.requireNonNull(jugadorId, "El id del jugador es obligatorio");
		EstadoJugadorInterno estadoJugador = this.estadoJugadores.get(id);

		if (estadoJugador == null) {
			throw new IllegalArgumentException("El jugador no esta registrado en la partida");
		}

		return estadoJugador;
	}

	static final class EstadoJugadorInterno {

		private final UUID jugadorId;
		private final String nombreJugador;
		private int puntuacion;
		private boolean muerto;

		EstadoJugadorInterno(UUID jugadorId, String nombreJugador) {
			this.jugadorId = Objects.requireNonNull(jugadorId, "El id del jugador es obligatorio");
			this.nombreJugador = Objects.requireNonNull(nombreJugador, "El nombre del jugador es obligatorio");
			this.puntuacion = 0;
			this.muerto = false;
		}

		UUID getJugadorId() {
			return jugadorId;
		}

		String getNombreJugador() {
			return nombreJugador;
		}

		int getPuntuacion() {
			return puntuacion;
		}

		boolean isMuerto() {
			return muerto;
		}

		void setPuntuacion(int puntuacion) {
			this.puntuacion = puntuacion;
		}

		void setMuerto(boolean muerto) {
			this.muerto = muerto;
		}
	}
}
