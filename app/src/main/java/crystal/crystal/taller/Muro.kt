package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.FichaActivity
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.databinding.ActivityMuroBinding

class Muro : AppCompatActivity(), EditGridFragment.OnGridUpdatedListener {

    private lateinit var binding : ActivityMuroBinding

    private var anchoTotal: Float = 100f
    private var altoTotal: Float = 100f
    private var anchosColumnas: MutableList<Float> = mutableListOf()
    private var alturasFilasPorColumna: MutableList<MutableList<Float>> = mutableListOf()
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMuroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.med1.requestFocus()
        disenoInicial()

        binding.btnCalcular.setOnClickListener {

            vidrios()
            marcos()
            tubos()
            calcularAluminioNaves()

        }
        binding.btDisenar.setOnClickListener {

            diseno()
            /*val ancho = binding.med1.text.toString().toFloat()
            val alto = binding.med2.text.toString().toFloat()
            val filas = binding.nFilas.text.toString().toInt()
            val colum = binding.nCol.text.toString().toInt()

            binding.med1.setText("")
            binding.med2.setText("")
            binding.nCol.setText("")
            binding.nFilas.setText("")

            binding.med1.hint="$ancho"
            binding.med2.hint="$alto"
            binding.nCol.hint="$colum"
            binding.nFilas.hint="$filas"*/
            binding.etTubo.requestFocus()

        }
        binding.btArchivar.setOnClickListener {
            archivarMapas()
        }
        binding.btArchivar.setOnLongClickListener {
            // Llamar a la función para guardar el Map
            MapStorage.guardarMap(this, mapListas)

            // Mostrar un mensaje de confirmación
            Toast.makeText(this, "Map guardado correctamente", Toast.LENGTH_SHORT).show()

            true // Retorna true para indicar que el evento fue manejado
        }

        // Establecer OnClickListener para mostrar el Fragment
        binding.rectanguloView.setOnClickListener {
            mostrarEditGridFragment()
        }
        binding.rectanguloView.setOnLongClickListener {
            startActivity(Intent(this, FichaActivity::class.java))
            true
        }
    }

    private fun disenoInicial(){
        // Inicializar anchosColumnas y alturasFilasPorColumna con valores predeterminados
        anchosColumnas = mutableListOf(anchoTotal / 2, anchoTotal / 2)
        alturasFilasPorColumna = mutableListOf(
            mutableListOf(altoTotal / 2, altoTotal / 2), // Column 1
            mutableListOf(altoTotal / 2, altoTotal / 2), // Column 2
            mutableListOf(altoTotal / 2, altoTotal / 2) // Column 3
        )

        // Configurar la cuadrícula inicialmente
        binding.rectanguloView.configurarParametros(
            anchoTotal,
            altoTotal,
            anchosColumnas,
            alturasFilasPorColumna
        )
    }

    override fun onGridUpdated(
        anchosColumnas: List<Float>,
        alturasFilasPorColumna: List<List<Float>>
    ) {
        // Actualizar las listas locales
        this.anchosColumnas = anchosColumnas.toMutableList()
        this.alturasFilasPorColumna = alturasFilasPorColumna.map { it.toMutableList() }.toMutableList()

        // Obtener los parámetros actuales desde GridDrawingView
        val anchoTotal = binding.rectanguloView.getAnchoTotal()
        val altoTotal = binding.rectanguloView.getAltoTotal()

        // Actualizar la cuadrícula principal con los nuevos valores
        binding.rectanguloView.configurarParametros(
            anchoTotal,
            altoTotal,
            this.anchosColumnas,
            this.alturasFilasPorColumna
        )
    }

    private fun df(defo: Float): String {
        val resultado = if (defo % 1.0 == 0.0) {
            "%.0f".format(defo)
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    private fun mostrarEditGridFragment() {
        // Obtener los parámetros actuales desde GridDrawingView
        val gridDrawingView = binding.rectanguloView
        val anchoTotal = gridDrawingView.getAnchoTotal()
        val altoTotal = gridDrawingView.getAltoTotal()
        val anchosColumnas = gridDrawingView.getAnchosColumnas()
        val alturasFilasPorColumna = gridDrawingView.getAlturasFilasPorColumna()

        // Crear una nueva instancia del fragmento con los parámetros actuales
        val fragment = EditGridFragment.newInstance(
            anchoTotal,
            altoTotal,
            anchosColumnas,
            alturasFilasPorColumna
        )
        fragment.show(supportFragmentManager, "EditGridFragment")
    }

    private fun diseno() {
        val ancho = binding.med1.text.toString().toFloatOrNull() ?: anchoTotal
        val alto = binding.med2.text.toString().toFloatOrNull() ?: altoTotal
        val colum = binding.nCol.text.toString().toIntOrNull() ?: 3
        val filas = binding.nFilas.text.toString().toIntOrNull() ?: 2 // Leer el número de filas

        // Actualizar las variables de ancho y alto totales
        anchoTotal = ancho
        altoTotal = alto

        // Inicializar anchosColumnas y alturasFilasPorColumna con los valores ingresados
        anchosColumnas = MutableList(colum) { ancho / colum }
        alturasFilasPorColumna = MutableList(colum) { MutableList(filas) { alto / filas } }

        // Configura la cuadrícula
        binding.rectanguloView.configurarParametros(anchoTotal, altoTotal, anchosColumnas, alturasFilasPorColumna)
    }

    //FUNCIONES DE ARCHIVO
    private fun archivarMapas() {
        ListaCasilla.incrementarContadorVentanas()

        // Caso especial para txReferencias
        if (esValido(binding.lyReferencias)) {
            ListaCasilla.procesarReferencias(binding.tvReferencias, binding.txReferencias, mapListas) // referencias
        }
        // Usar la clase ListaCasilla para procesar y archivar solo los TextView válidos
        if (esValido(binding.ulayout)) {
            ListaCasilla.procesarArchivar(binding.txMarco, binding.tvMarco, mapListas) // marco
        }
        if (esValido(binding.lyAlnMarco)) {
            ListaCasilla.procesarArchivar(binding.txAlnMarco, binding.tvAlnMarco, mapListas) // aln marco
        }
        if (esValido(binding.lyAlnTubo)) {
            ListaCasilla.procesarArchivar(binding.txAlnTubo, binding.tvAlnTubo, mapListas) // aln tubo
        }
        if (esValido(binding.tuboLayout)) {
            ListaCasilla.procesarArchivar(binding.txTubo, binding.tvTubo, mapListas) // tubo
        }

        if (esValido(binding.lyVidrios)) {
            ListaCasilla.procesarArchivar(binding.txVidrios, binding.tvVidrios, mapListas) // vidrios
        }
        if (esValido(binding.lyClient)) {
            ListaCasilla.procesarArchivar(binding.tvC, binding.txC, mapListas) // cliente
        }
        if (esValido(binding.lyAncho)) {
            ListaCasilla.procesarArchivar(binding.tvAncho, binding.txAncho, mapListas) // ancho
        }
        if (esValido(binding.lyAlto)) {
            ListaCasilla.procesarArchivar(binding.tvAlto, binding.txAlto, mapListas) // alto
        }
        if (esValido(binding.lyPuente)) {
            ListaCasilla.procesarArchivar(binding.tvPuente, binding.txPuente, mapListas) // altura Puente
        }
        if (esValido(binding.lyDivisiones)) {
            ListaCasilla.procesarArchivar(binding.tvDivisiones, binding.txDivisiones, mapListas) // divisiones
        }
        if (esValido(binding.lyFijos)) {
            ListaCasilla.procesarArchivar(binding.tvFijos, binding.txFijos, mapListas) // nFijos
        }
        if (esValido(binding.lyCorredizas)) {
            ListaCasilla.procesarArchivar(binding.tvCorredizas, binding.txCorredizas, mapListas) // nCorredizas
        }
        if (esValido(binding.lyDiseno)) {
            ListaCasilla.procesarArchivar(binding.tvDiseno, binding.txDiseno, mapListas) // ancho
        }
        if (esValido(binding.lyGrados)) {
            ListaCasilla.procesarArchivar(binding.tvGrados, binding.txGrados, mapListas) // grados
        }
        if(esValido(binding.lyTipo)){
            ListaCasilla.procesarArchivar(binding.tvTipo,binding.txTipo,mapListas) // tipo de ventana
        }
        // Aquí puedes hacer algo con `mapListas`, como mostrarlo o guardarlo
        binding.tvPuntos.setText( mapListas.toString())
        println(mapListas)

    }
    // Función para verificar si un Layout es visible o tiene estado GONE
    private fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun vidrios() {
        // Leer el valor de la gruña ingresado por el usuario
        val gruna = binding.etGruna.text.toString().toFloatOrNull() ?: 0f

        // Mapa para almacenar las dimensiones y sus cantidades
        val medidasCantidadMap = mutableMapOf<String, Int>()

        // Iterar sobre las columnas
        for (colIndex in anchosColumnas.indices) {
            val anchoColumna = anchosColumnas[colIndex]

            // Obtener la lista de alturas de filas para esta columna
            val alturasFilas = alturasFilasPorColumna[colIndex]

            // Iterar sobre las filas
            for (filaAltura in alturasFilas) {
                // Restar la gruña al ancho y alto de cada vidrio
                val anchoVidrio = anchoColumna - gruna
                val altoVidrio = filaAltura - gruna

                // Evitar dimensiones negativas
                val anchoVidrioAjustado = if (anchoVidrio > 0f) anchoVidrio else 0f
                val altoVidrioAjustado = if (altoVidrio > 0f) altoVidrio else 0f

                // Crear una clave con el formato "ancho x alto"
                val medida = "${df(anchoVidrioAjustado)} x ${df(altoVidrioAjustado)}"

                // Incrementar la cantidad para esta medida
                medidasCantidadMap[medida] = medidasCantidadMap.getOrDefault(medida, 0) + 1
            }
        }

        // Construir el texto de salida
        val builder = StringBuilder()
        for ((medida, cantidad) in medidasCantidadMap) {
            builder.append("$medida = $cantidad\n")
        }

        // Mostrar el resultado en el TextView
        binding.tvVidrios.text = builder.toString()
    }

    private fun marcos() {
        // Leer el valor del marco ingresado por el usuario
        val marco = binding.etMarco.text.toString().toFloatOrNull() ?: 0f

        // Evitar valores negativos o cero
        if (marco <= 0f) {
            Toast.makeText(this, "Por favor, ingrese un valor válido para el marco", Toast.LENGTH_SHORT).show()
            return
        }

        val medidasCantidadMap = mutableMapOf<String, Int>()

        // Marcos verticales (izquierda y derecha)
        val alturaMarco = altoTotal // Altura total de la ventana
        val cantidadVerticales = 2
        val medidaVertical = df(alturaMarco)
        medidasCantidadMap[medidaVertical] = cantidadVerticales

        // Marcos horizontales (arriba y abajo)
        val anchoMarco = anchoTotal - (2 * marco) // Ancho total menos los marcos verticales
        val cantidadHorizontales = 2
        val medidaHorizontal = df(anchoMarco)
        medidasCantidadMap[medidaHorizontal] = cantidadHorizontales

        // Construir el texto de salida
        val builder = StringBuilder()
        builder.append("")
        for ((medida, cantidad) in medidasCantidadMap) {
            builder.append("$medida = $cantidad\n")
        }

        // Mostrar el resultado en un TextView (por ejemplo, binding.marcosTxt)
        binding.tvMarco.text = builder.toString()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun tubos() {
        // Leer el valor del marco y del tubo ingresado por el usuario
        val marco = binding.etMarco.text.toString().toFloatOrNull() ?: 0f
        val tubo = binding.etTubo.text.toString().toFloatOrNull() ?: 0f

        // Evitar valores negativos o cero
        if (marco <= 0f || tubo <= 0f) {
            Toast.makeText(this, "Por favor, ingrese valores válidos para el marco y el tubo", Toast.LENGTH_SHORT).show()
            return
        }

        val medidasCantidadMap = mutableMapOf<String, Int>()

        // Tubos verticales (entre columnas)
        val alturaTuboVertical = altoTotal - (2 * marco)
        val cantidadTubosVerticales = anchosColumnas.size - 1 // columnas - 1

        if (cantidadTubosVerticales > 0 && alturaTuboVertical > 0) {
            val medidaVertical = df(alturaTuboVertical)
            medidasCantidadMap[medidaVertical] = cantidadTubosVerticales
        }

        // Tubos horizontales (entre filas)
        // Recorremos las columnas para calcular los tubos horizontales
        for (colIndex in anchosColumnas.indices) {
            val anchuraColumna = anchosColumnas[colIndex]
            val filasEnColumna = alturasFilasPorColumna[colIndex]

            val cantidadTubosHorizontales = filasEnColumna.size - 1
            if (cantidadTubosHorizontales > 0) {
                // Dependiendo de la posición de la columna, ajustamos el ancho
                val ajusteInicio = if (colIndex == 0) marco else tubo / 2
                val ajusteFin = if (colIndex == anchosColumnas.size - 1) marco else tubo / 2
                val anchoTuboHorizontal = anchuraColumna - ajusteInicio - ajusteFin

                if (anchoTuboHorizontal > 0) {
                    val medidaHorizontal = df(anchoTuboHorizontal)
                    val cantidadExistente = medidasCantidadMap.getOrDefault(medidaHorizontal, 0)
                    medidasCantidadMap[medidaHorizontal] = cantidadExistente + cantidadTubosHorizontales
                }
            }
        }

        // Construir el texto de salida
        val builder = StringBuilder()
        builder.append("")
        for ((medida, cantidad) in medidasCantidadMap) {
            builder.append("$medida = $cantidad\n")
        }

        // Mostrar el resultado en un TextView (por ejemplo, binding.tubosTxt)
        binding.tvTubo.text = builder.toString()
    }

    private fun parseNavesInput(input: String): List<Pair<Int, Int>> {
        val navesList = mutableListOf<Pair<Int, Int>>()
        val entries = input.split(";")
        for (entry in entries) {
            val parts = entry.split(",")
            if (parts.size == 2) {
                val colPart = parts[0].trim()
                val rowPart = parts[1].trim()
                if (colPart.startsWith("c") && rowPart.startsWith("f")) {
                    val colStr = colPart.substring(1)
                    val rowStr = rowPart.substring(1)
                    val colIndex = colStr.toIntOrNull()?.minus(1)
                    val rowIndex = rowStr.toIntOrNull()?.minus(1)
                    if (colIndex != null && rowIndex != null) {
                        navesList.add(Pair(colIndex, rowIndex))
                    }
                }
            }
        }
        return navesList
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun calcularAluminioNaves() {
        // Leer los valores del marco y del tubo ingresados por el usuario
        val marco = binding.etMarco.text.toString().toFloatOrNull() ?: 0f
        val tubo = binding.etTubo.text.toString().toFloatOrNull() ?: 0f

        if (marco <= 0f || tubo <= 0f) {
            Toast.makeText(this, "Por favor, ingrese valores válidos para el marco y el tubo", Toast.LENGTH_SHORT).show()
            return
        }

        // Leer y parsear el input de etNaves
        val navesInput = binding.etNaves.text.toString()
        val navesList = parseNavesInput(navesInput)

        if (navesList.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese posiciones válidas para las naves", Toast.LENGTH_SHORT).show()
            return
        }

        val alnMarcoMedidasCantidadMap = mutableMapOf<String, Int>()
        val alnTuboMedidasCantidadMap = mutableMapOf<String, Int>()

        for (nave in navesList) {
            val colIndex = nave.first
            val rowIndex = nave.second

            // Verificar si la celda existe
            if (colIndex in anchosColumnas.indices && rowIndex in alturasFilasPorColumna[colIndex].indices) {
                val anchuraColumna = anchosColumnas[colIndex]
                val alturaFila = alturasFilasPorColumna[colIndex][rowIndex]

                // Cálculo de AlnMarco

                // Cálculo para los elementos verticales (izquierda y derecha)
                val ajusteInicioVertical = if (rowIndex == 0) marco else tubo / 2
                val ajusteFinVertical = if (rowIndex == alturasFilasPorColumna[colIndex].size - 1) marco else tubo / 2
                val altoAlnMarcoVertical = alturaFila - ajusteInicioVertical - ajusteFinVertical

                // Cantidad de elementos verticales por nave: 2
                val medidaAltoVertical = df(altoAlnMarcoVertical)
                alnMarcoMedidasCantidadMap[medidaAltoVertical] = alnMarcoMedidasCantidadMap.getOrDefault(medidaAltoVertical, 0) + 2

                // Cálculo para los elementos horizontales (arriba y abajo)
                val ajusteInicioHorizontal = if (colIndex == 0) marco else tubo / 2
                val ajusteFinHorizontal = if (colIndex == anchosColumnas.size - 1) marco else tubo / 2
                val anchoAlnMarcoHorizontal = anchuraColumna - ajusteInicioHorizontal - ajusteFinHorizontal

                // Cantidad de elementos horizontales por nave: 2
                val medidaAnchoHorizontal = df(anchoAlnMarcoHorizontal)
                alnMarcoMedidasCantidadMap[medidaAnchoHorizontal] = alnMarcoMedidasCantidadMap.getOrDefault(medidaAnchoHorizontal, 0) + 2

                // Cálculo de AlnTubo (restando 2 a cada medida de AlnMarco)
                val altoAlnTuboVertical = altoAlnMarcoVertical - 2f
                val anchoAlnTuboHorizontal = anchoAlnMarcoHorizontal - 2f

                // Evitar dimensiones negativas
                val altoAlnTuboVerticalAjustado = if (altoAlnTuboVertical > 0f) altoAlnTuboVertical else 0f
                val anchoAlnTuboHorizontalAjustado = if (anchoAlnTuboHorizontal > 0f) anchoAlnTuboHorizontal else 0f

                // Cantidad de elementos de AlnTubo por nave: 2 verticales y 2 horizontales
                val medidaAltoTuboVertical = df(altoAlnTuboVerticalAjustado)
                alnTuboMedidasCantidadMap[medidaAltoTuboVertical] = alnTuboMedidasCantidadMap.getOrDefault(medidaAltoTuboVertical, 0) + 2

                val medidaAnchoTuboHorizontal = df(anchoAlnTuboHorizontalAjustado)
                alnTuboMedidasCantidadMap[medidaAnchoTuboHorizontal] = alnTuboMedidasCantidadMap.getOrDefault(medidaAnchoTuboHorizontal, 0) + 2

            } else {
                // La celda no existe
                Toast.makeText(this, "La posición c${colIndex + 1},f${rowIndex + 1} no es válida", Toast.LENGTH_SHORT).show()
            }
        }

        // Construir el texto de salida para AlnMarco
        val builderAlnMarco = StringBuilder()
        builderAlnMarco.append("")
        for ((medida, cantidad) in alnMarcoMedidasCantidadMap) {
            builderAlnMarco.append("$medida = $cantidad\n")
        }

        // Mostrar el resultado en el TextView correspondiente
        binding.tvAlnMarco.text = builderAlnMarco.toString()

        // Construir el texto de salida para AlnTubo
        val builderAlnTubo = StringBuilder()
        builderAlnTubo.append("")
        for ((medida, cantidad) in alnTuboMedidasCantidadMap) {
            builderAlnTubo.append("$medida = $cantidad\n")
        }

        // Mostrar el resultado en el TextView correspondiente
        binding.tvAlnTubo.text = builderAlnTubo.toString()
    }

}