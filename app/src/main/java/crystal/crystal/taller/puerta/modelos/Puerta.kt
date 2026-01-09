package crystal.crystal.taller.puerta.modelos

data class Puerta(val nombre: String, val medida: String, val zocalo: String)

data class Variante(val nombre: String, val imagen: Int)

// Plano / geometría
data class Punto(val x: Float, val y: Float)
data class Linea(val inicio: Punto, val fin: Punto)
data class ParLineas(val linea1: Linea, val linea2: Linea)
data class DatosPlano(val eje: Punto, val paresLineas: List<ParLineas>)