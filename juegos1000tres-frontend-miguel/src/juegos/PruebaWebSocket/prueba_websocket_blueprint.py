import os

from flask import Blueprint, render_template


def create_prueba_websocket_blueprint(base_dir: str) -> Blueprint:
    if not isinstance(base_dir, str) or not base_dir.strip():
        raise ValueError("base_dir es obligatorio para crear el blueprint de PruebaWebSocket")

    blueprint = Blueprint(
        "prueba_websocket",
        __name__,
        template_folder=os.path.join(base_dir, "src", "juegos", "PruebaWebSocket", "templates"),
        static_folder=os.path.join(base_dir, "src", "juegos", "PruebaWebSocket", "static"),
        static_url_path="/juegos/PruebaWebSocket/static",
    )

    @blueprint.route("/")
    def prueba_websocket_home():
        sala_id = _obtener_sala_id()
        return render_template(
            "prueba_websocket.html",
            sala_id=sala_id,
            ws_jugadores_url=_construir_url_ws(sala_id, "jugadores"),
        )

    @blueprint.route("/pantalla")
    def prueba_websocket_pantalla():
        sala_id = _obtener_sala_id()
        return render_template(
            "prueba_websocket_pantalla.html",
            sala_id=sala_id,
            ws_pantalla_url=_construir_url_ws(sala_id, "pantalla"),
        )

    return blueprint


def _obtener_sala_id() -> str:
    sala_id = os.getenv("PRUEBA_WEBSOCKET_SALA_ID", "prueba-websocket").strip()
    return sala_id or "prueba-websocket"


def _construir_url_ws(sala_id: str, rol: str) -> str:
    base_url = os.getenv("PRUEBA_WEBSOCKET_WS_BASE_URL", "ws://127.0.0.1:8091/ws/salas").strip().rstrip("/")
    return f"{base_url}/{sala_id}/{rol}"
