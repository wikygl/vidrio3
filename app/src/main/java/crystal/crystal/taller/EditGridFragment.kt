package crystal.crystal.taller
// EditGridFragment.kt
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import crystal.crystal.R

class EditGridFragment : DialogFragment() {

    private var anchoTotal: Float = 150f
    private var altoTotal: Float = 180f

    private lateinit var anchosColumnas: MutableList<Float>
    private lateinit var alturasFilasPorColumna: MutableList<MutableList<Float>>

    private lateinit var gridDrawingView: GridDrawingView
    private lateinit var columnInputs: MutableList<EditText>
    private lateinit var rowInputsPorColumna: MutableList<MutableList<EditText>>

    // Listas para mantener el seguimiento de qué columnas y filas han sido editadas
    private lateinit var columnasEditadas: MutableList<Boolean>
    private lateinit var filasEditadasPorColumna: MutableList<MutableList<Boolean>>

    // Interfaz para comunicar cambios con la actividad
    interface OnGridUpdatedListener {
        fun onGridUpdated(anchosColumnas: List<Float>, alturasFilasPorColumna: List<List<Float>>)
    }

    private var listener: OnGridUpdatedListener? = null

    // Bandera para evitar bucles infinitos al actualizar los EditText programáticamente
    private var isUpdating = false

    companion object {
        fun newInstance(
            anchoTotal: Float,
            altoTotal: Float,
            anchosColumnas: List<Float>? = null,
            alturasFilasPorColumna: List<List<Float>>? = null
        ): EditGridFragment {
            val fragment = EditGridFragment()
            val args = Bundle()
            args.putFloat("anchoTotal", anchoTotal)
            args.putFloat("altoTotal", altoTotal)
            if (anchosColumnas != null) {
                args.putFloatArray("anchosColumnas", anchosColumnas.toFloatArray())
            }
            if (alturasFilasPorColumna != null) {
                val alturasArrayList = ArrayList<FloatArray>()
                for (list in alturasFilasPorColumna) {
                    alturasArrayList.add(list.toFloatArray())
                }
                args.putSerializable("alturasFilasPorColumna", alturasArrayList)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private fun df(defo: Float): String {
        val resultado = if (defo % 1.0 == 0.0) {
            "%.0f".format(defo)
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGridUpdatedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnGridUpdatedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            anchoTotal = it.getFloat("anchoTotal")
            altoTotal = it.getFloat("altoTotal")
            anchosColumnas = if (it.containsKey("anchosColumnas")) {
                it.getFloatArray("anchosColumnas")?.toMutableList()
                    ?: mutableListOf(anchoTotal)
            } else {
                mutableListOf(anchoTotal)
            }
            alturasFilasPorColumna = if (it.containsKey("alturasFilasPorColumna")) {
                val alturasArrayList = it.getSerializable("alturasFilasPorColumna") as ArrayList<FloatArray>
                alturasArrayList.map { array -> array.toMutableList() }.toMutableList()
            } else {
                mutableListOf(mutableListOf(altoTotal))
            }
        } ?: run {
            // Valores por defecto si no se proporcionan argumentos
            anchosColumnas = mutableListOf(anchoTotal)
            alturasFilasPorColumna = mutableListOf(mutableListOf(altoTotal))
        }
        // Inicializar las listas de seguimiento de edición
        columnasEditadas = MutableList(anchosColumnas.size) { false }
        filasEditadasPorColumna = alturasFilasPorColumna.map { MutableList(it.size) { false } }.toMutableList()

        // Restaurar estado si está disponible
        savedInstanceState?.let {
            anchosColumnas = it.getFloatArray("anchosColumnas")?.toMutableList() ?: anchosColumnas
            // Restaurar alturasFilasPorColumna si es necesario
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloatArray("anchosColumnas", anchosColumnas.toFloatArray())
        // Guardar alturasFilasPorColumna si es necesario
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_grid, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        gridDrawingView = view.findViewById(R.id.gridDrawingView)
        gridDrawingView.configurarParametros(
            anchoTotal,
            altoTotal,
            anchosColumnas,
            alturasFilasPorColumna
        )

        val columnLayout = view.findViewById<LinearLayout>(R.id.columnInputsLayout)
        val filasPorColumnaLayout = view.findViewById<LinearLayout>(R.id.filasPorColumnaLayout)

        columnInputs = mutableListOf()
        rowInputsPorColumna = mutableListOf()

        // Limpiamos los layouts
        columnLayout.removeAllViews()
        filasPorColumnaLayout.removeAllViews()

        for (i in anchosColumnas.indices) {
            val container = LinearLayout(requireContext())
            container.orientation = LinearLayout.VERTICAL
            container.gravity = Gravity.CENTER

            val label = TextView(requireContext())
            label.text = "Col${i + 1}"
            label.gravity = Gravity.CENTER

            val editText = EditText(requireContext())
            editText.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.hint = df(anchosColumnas[i])
            editText.setText(df(anchosColumnas[i]))
            editText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!isUpdating) {
                        columnasEditadas[i] = true
                        adjustColumnWidths(i)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            })
            container.addView(label)
            container.addView(editText)
            columnLayout.addView(container)
            columnInputs.add(editText)
        }

        // Generar inputs para las filas por columna
        generarInputsDeFilas()

        // Botón opcional para cerrar el fragmento
        val closeButton = view.findViewById<Button>(R.id.buttonClose)
        closeButton?.setOnClickListener {
            dismiss()
        }
    }

    private fun generarInputsDeFilas() {
        val filasPorColumnaLayout = view?.findViewById<LinearLayout>(R.id.filasPorColumnaLayout)
        filasPorColumnaLayout?.removeAllViews()
        rowInputsPorColumna.clear()
        filasEditadasPorColumna = mutableListOf()
        for (i in anchosColumnas.indices) {
            val columnaContainer = LinearLayout(requireContext())
            columnaContainer.orientation = LinearLayout.VERTICAL
            columnaContainer.gravity = Gravity.CENTER
            columnaContainer.setPadding(10, 10, 10, 10)
            val columnaLabel = TextView(requireContext())
            columnaLabel.text = "Filas Col ${i + 1}"
            columnaLabel.gravity = Gravity.CENTER

            // Botones para agregar y eliminar filas
            val buttonsLayout = LinearLayout(requireContext())
            buttonsLayout.orientation = LinearLayout.HORIZONTAL
            val addButton = Button(requireContext())
            addButton.text = "+"
            addButton.setOnClickListener {
                agregarFilaEnColumna(i)
            }
            val removeButton = Button(requireContext())
            removeButton.text = "-"
            removeButton.setOnClickListener {
                eliminarFilaEnColumna(i)
            }
            buttonsLayout.addView(addButton)
            buttonsLayout.addView(removeButton)

            // Contenedor para los inputs de filas de esta columna
            val filasContainer = LinearLayout(requireContext())
            filasContainer.orientation = LinearLayout.VERTICAL

            val rowInputs = mutableListOf<EditText>()
            val filasEditadas = mutableListOf<Boolean>()

            for (j in alturasFilasPorColumna[i].indices) {
                val filaContainer = LinearLayout(requireContext())
                filaContainer.orientation = LinearLayout.VERTICAL
                filaContainer.gravity = Gravity.CENTER

                val filaLabel = TextView(requireContext())
                filaLabel.text = "Fila ${j + 1}"
                filaLabel.gravity = Gravity.CENTER

                val editText = EditText(requireContext())
                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                editText.hint = df(alturasFilasPorColumna[i][j])
                editText.setText(df(alturasFilasPorColumna[i][j]))
                editText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val columnaIndex = i
                val filaIndex = j
                editText.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if (!isUpdating) {
                            filasEditadas[filaIndex] = true
                            adjustRowHeights(columnaIndex, filaIndex)
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
                filaContainer.addView(filaLabel)
                filaContainer.addView(editText)
                filasContainer.addView(filaContainer)
                rowInputs.add(editText)
                filasEditadas.add(false)
            }

            columnaContainer.addView(columnaLabel)
            columnaContainer.addView(buttonsLayout)
            columnaContainer.addView(filasContainer)
            filasPorColumnaLayout?.addView(columnaContainer)
            rowInputsPorColumna.add(rowInputs)
            filasEditadasPorColumna.add(filasEditadas)
        }
    }

    private fun agregarFilaEnColumna(columnaIndex: Int) {
        isUpdating = true
        // Agregar una altura inicial para la nueva fila
        val numFilas = alturasFilasPorColumna[columnaIndex].size + 1
        val nuevaAltura = altoTotal / numFilas
        alturasFilasPorColumna[columnaIndex].add(nuevaAltura)

        // Ajustar las alturas de las filas existentes
        ajustarAlturasEnColumna(columnaIndex)

        // Actualizar la interfaz
        generarInputsDeFilas()
        updateGrid()
        isUpdating = false
    }

    private fun eliminarFilaEnColumna(columnaIndex: Int) {
        isUpdating = true
        if (alturasFilasPorColumna[columnaIndex].size > 1) {
            alturasFilasPorColumna[columnaIndex].removeAt(alturasFilasPorColumna[columnaIndex].size - 1)

            // Ajustar las alturas de las filas restantes
            ajustarAlturasEnColumna(columnaIndex)

            // Actualizar la interfaz
            generarInputsDeFilas()
            updateGrid()
        } else {
            Toast.makeText(requireContext(), "La columna debe tener al menos una fila", Toast.LENGTH_SHORT).show()
        }
        isUpdating = false
    }

    private fun ajustarAlturasEnColumna(columnaIndex: Int) {
        val numFilas = alturasFilasPorColumna[columnaIndex].size
        val nuevaAltura = altoTotal / numFilas
        for (j in 0 until numFilas) {
            alturasFilasPorColumna[columnaIndex][j] = nuevaAltura
        }
    }

    private fun adjustColumnWidths(changedIndex: Int) {
        isUpdating = true

        val totalWidth = anchoTotal
        val newValueText = columnInputs[changedIndex].text.toString()
        val newValue = newValueText.toFloatOrNull() ?: 0f

        if (newValue <= 0f) {
            Toast.makeText(
                requireContext(),
                "El valor debe ser mayor que cero",
                Toast.LENGTH_SHORT
            ).show()
            isUpdating = false
            return
        }

        anchosColumnas[changedIndex] = newValue

        // Sumar los anchos de las columnas editadas
        var editedColumnsTotal = 0f
        for (i in anchosColumnas.indices) {
            if (columnasEditadas[i]) {
                editedColumnsTotal += anchosColumnas[i]
            }
        }

        val remainingWidth = totalWidth - editedColumnsTotal
        if (remainingWidth < 0f) {
            Toast.makeText(
                requireContext(),
                "La suma de los anchos excede el total",
                Toast.LENGTH_SHORT
            ).show()
            isUpdating = false
            return
        }

        // Contar cuántas columnas no editadas quedan
        val remainingColumns = columnasEditadas.count { !it }

        if (remainingColumns > 0) {
            val equalWidth = remainingWidth / remainingColumns

            // Actualizar las columnas no editadas
            for (i in anchosColumnas.indices) {
                if (!columnasEditadas[i]) {
                    anchosColumnas[i] = equalWidth
                    val formattedValue = df(equalWidth)
                    columnInputs[i].setText(formattedValue)
                }
            }
        } else {
            // Si todas las columnas han sido editadas y el total no coincide
            if (remainingWidth != 0f) {
                Toast.makeText(
                    requireContext(),
                    "No es posible ajustar los anchos para mantener el total",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Actualizar la cuadrícula
        updateGrid()

        isUpdating = false
    }

    private fun adjustRowHeights(columnaIndex: Int, changedFilaIndex: Int) {
        isUpdating = true

        val totalHeight = altoTotal
        val editText = rowInputsPorColumna[columnaIndex][changedFilaIndex]
        val newValueText = editText.text.toString()
        val newValue = newValueText.toFloatOrNull() ?: 0f

        if (newValue <= 0f) {
            Toast.makeText(
                requireContext(),
                "El valor debe ser mayor que cero",
                Toast.LENGTH_SHORT
            ).show()
            isUpdating = false
            return
        }

        alturasFilasPorColumna[columnaIndex][changedFilaIndex] = newValue

        // Sumar los altos de las filas editadas
        var editedRowsTotal = 0f
        for (j in alturasFilasPorColumna[columnaIndex].indices) {
            if (filasEditadasPorColumna[columnaIndex][j]) {
                editedRowsTotal += alturasFilasPorColumna[columnaIndex][j]
            }
        }

        val remainingHeight = totalHeight - editedRowsTotal
        if (remainingHeight < 0f) {
            Toast.makeText(
                requireContext(),
                "La suma de los altos excede el total",
                Toast.LENGTH_SHORT
            ).show()
            isUpdating = false
            return
        }

        // Contar cuántas filas no editadas quedan
        val remainingRows = filasEditadasPorColumna[columnaIndex].count { !it }

        if (remainingRows > 0) {
            val equalHeight = remainingHeight / remainingRows

            // Actualizar las filas no editadas
            for (j in alturasFilasPorColumna[columnaIndex].indices) {
                if (!filasEditadasPorColumna[columnaIndex][j]) {
                    alturasFilasPorColumna[columnaIndex][j] = equalHeight
                    val formattedValue = df(equalHeight)
                    rowInputsPorColumna[columnaIndex][j].setText(formattedValue)
                }
            }
        } else {
            // Si todas las filas han sido editadas y el total no coincide
            if (remainingHeight != 0f) {
                Toast.makeText(
                    requireContext(),
                    "No es posible ajustar los altos para mantener el total",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Actualizar la cuadrícula
        updateGrid()

        isUpdating = false
    }

    private fun updateGrid() {
        // Actualizar la cuadrícula
        gridDrawingView.configurarParametros(
            anchoTotal,
            altoTotal,
            anchosColumnas,
            alturasFilasPorColumna
        )

        // Notificar a la actividad que la cuadrícula ha sido actualizada
        listener?.onGridUpdated(anchosColumnas, alturasFilasPorColumna)
    }
}









