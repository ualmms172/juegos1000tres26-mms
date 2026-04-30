package com.juegos1000tres.juegos1000tres_backend.juegos.PruebaWebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;

import com.juegos1000tres.juegos1000tres_backend.comunicacion.Envio;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Recibo;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.Traductor;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.implementaciones.ComunicacionRuntimeConfig;
import com.juegos1000tres.juegos1000tres_backend.comunicacion.implementaciones.WebSocketConexion;

/**
 * Gestiona instancias de PruebaWebSocket por sala.
 * Crea canales WebSocket dinámicos usando el UUID de sala:
 *   /ws/salas/{uuid}/jugadores  →  para los jugadores
 *   /ws/salas/{uuid}/pantalla   →  para la pantalla
 *
 * Esto permite que el frontend conecte usando el UUID real de sala
 * en lugar de canales fijos, siguiendo el Patrón Traductor por sala.
 */
@Service
public class PruebaWebSocketManager {

    private static final long SLEEP_MS = 40L;
    private static final String PAYLOAD_VACIO = "{}";

    private final Map<String, PruebaWebSocketInstancia> instancias = new ConcurrentHashMap<>();

    public synchronized void crearInstanciaParaSala(String salaUuid) {
        if (instancias.containsKey(salaUuid)) {
            return; // ya existe
        }

        int puerto = ComunicacionRuntimeConfig.websocketPuerto();

        // Canales: /ws/salas/{uuid}/jugadores  y  /ws/salas/{uuid}/pantalla
        WebSocketConexion conexionJugadores = new WebSocketConexion(salaUuid, "jugadores", puerto);
        WebSocketConexion conexionPantalla  = new WebSocketConexion(salaUuid, "pantalla",  puerto);

        // Tradcutores de canal (para que el juego envíe mensajes a los clientes)
        Traductor<String> traductorCanalJugadores = new Traductor<>(
                conexionJugadores, Envio.paraStringDesdeOut(), Recibo.paraJsonString());
        Traductor<String> traductorCanalPantalla = new Traductor<>(
                conexionPantalla,  Envio.paraStringDesdeOut(), Recibo.paraJsonString());

        // Instancia del juego — toda la comunicación interna pasa por Traductor
        PruebaWebSocket juego = new PruebaWebSocket(traductorCanalJugadores, traductorCanalPantalla);

        // Recibo con los eventos registrados (patrón Traductor)
        Recibo<String> reciboEventos = Recibo.paraJsonString()
                .conEvento(PruebaWebSocket.COMANDO_ENVIAR_TEXTO, new EnviarTextoEvento(juego));

        // Traductor de eventos: recibe mensajes entrantes de jugadores y los procesa
        Traductor<String> traductorEventos = new Traductor<>(
                conexionJugadores, Envio.paraStringDesdeOut(), reciboEventos);

        juego.iniciar();

        AtomicBoolean activo = new AtomicBoolean(true);
        ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread hilo = new Thread(runnable, "prueba-ws-loop-" + salaUuid);
            hilo.setDaemon(true);
            return hilo;
        });
        executor.submit(() -> bucleProcesamiento(traductorEventos, activo));

        instancias.put(salaUuid, new PruebaWebSocketInstancia(
                juego, traductorEventos, conexionJugadores, conexionPantalla, activo, executor));
    }

    public synchronized void detenerInstanciaParaSala(String salaUuid) {
        PruebaWebSocketInstancia instancia = instancias.remove(salaUuid);
        if (instancia == null) {
            return;
        }

        instancia.activo().set(false);
        instancia.juego().terminar();
        instancia.executor().shutdownNow();
        instancia.conexionJugadores().desconectar();
        instancia.conexionPantalla().desconectar();
    }

    private void bucleProcesamiento(Traductor<String> traductorEventos, AtomicBoolean activo) {
        while (activo.get()) {
            try {
                String payload = traductorEventos.recibirPayload();
                if (payload == null || payload.isBlank() || PAYLOAD_VACIO.equals(payload.trim())) {
                    dormirBreve();
                    continue;
                }
                traductorEventos.procesar(payload);
            } catch (RuntimeException ex) {
                dormirBreve();
            }
        }
    }

    private void dormirBreve() {
        try {
            Thread.sleep(SLEEP_MS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private record PruebaWebSocketInstancia(
            PruebaWebSocket juego,
            Traductor<String> traductorEventos,
            WebSocketConexion conexionJugadores,
            WebSocketConexion conexionPantalla,
            AtomicBoolean activo,
            ExecutorService executor) {
    }
}
