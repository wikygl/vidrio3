package calculaora.e.vidrio3

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import java.util.zip.Inflater


@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER")
class MainActivity : AppCompatActivity() {

    private var lista: MutableList<Listado> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.N)
    val decimalFormat = android.icu.text.DecimalFormat(0.00.toString())
    /*resultadoText.text = if("$result".endsWith(".0")) { "$result".replace(".0","") } else { "%.2f".format(result) }*/
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLimpiar.setOnClickListener {
            try {
                precio_unitario.text = "0.0"
                precio_cantidad.text = "0.0"
                pc_txt.text = "0.0"
                mc_txt.text = "0.0"
                med1_editxt.setText("")
                med2_editxt.setText("")
                cant_editxt.setText("")
                precio_editxt.setText("")
                pro_editxt.setText("")
                med1_editxt.hint = ""
                med2_editxt.hint = ""
                cant_editxt.hint = ""
                precio_total.text = "0.0"
                lista.clear()
                list.onRemoteAdapterConnected()
                actualizar()
            } catch (e: Exception) {
            }
        }

        btnCalcular.setOnClickListener {
            try {

                val medida1 = med1_editxt.text.toString().toFloat()
                val medida2 = med2_editxt.text.toString().toFloat()
                val cantidad = cant_editxt.text.toString().toInt()
                val precio = precio_editxt.text.toString().toFloat()
                val producto = pro_editxt.text.toString()
                //val retaso=1.8F
                val piescua = (medida1) * (medida2) / 900
                val piescant = piescua * cantidad.toFloat()
                val metroscua = medida1 * medida2 / 10000 * cantidad.toFloat()

                val costounitario = piescant * precio / cantidad
                val costocantidad = piescant * precio

                pc_txt.text = decimalFormat.format(piescant)
                mc_txt.text = decimalFormat.format(metroscua)
                precio_unitario.text = decimalFormat.format(costounitario)
                precio_cantidad.text = decimalFormat.format(costocantidad)

                med1_editxt.setText("")
                med1_editxt.hint = "$medida1"
                med2_editxt.setText("")
                med2_editxt.hint = "$medida2"
                cant_editxt.setText("")
                cant_editxt.hint = "$cantidad"

                val medidas =
                    Listado(medida1, medida2, cantidad, piescant, precio, costocantidad, producto)

                lista.add(medidas)
                actualizar()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "ingrese un número válido", Toast.LENGTH_LONG).show()
            }
        }

       /* list.setOnItemClickListener { parent, view, position, id ->
            try {
                var indice = lista[position]
                var dialogo = AlertDialog.Builder(this)
                var modelo= layoutInflater.inflate(R.layout.dialogo,null)
                val eliminar = modelo.findViewById<Button>(R.id.btn_dialogo_eliminar)
                val editar = modelo.findViewById<Button>(R.id.btn_dialogo_editar)
                dialogo.setView(modelo)
                var dialogoper=dialogo.create()
                dialogoper.show()
                eliminar.setOnClickListener(){
                    fileList()[position]
                }
                editar.setOnClickListener(){}

                actualizar()


            } catch (e: Exception){}
        }*/

    }
    private fun actualizar() {
        try {
            val listaString = mutableListOf<String>()
            for (medidas in lista) {
                listaString.add(
                    "${medidas.medi1} x ${medidas.medi2} x ${medidas.canti} = ${
                        decimalFormat.format(medidas.piescua)} x S/.${medidas.precio}" +
                            " == S/.${decimalFormat.format(medidas.costo)} -> ${medidas.producto}")

                for (@Suppress("NAME_SHADOWING") listaString in lista) {
                    var recupe = 0F
                    for (listaString in lista) {
                        recupe += listaString.costo.toString().toFloat()  }
                    precio_total.text = decimalFormat.format(recupe)  }

                list.setOnItemClickListener { parent, view, position, id ->
                    try {
                        var indice = listaString
                        var dialogo = AlertDialog.Builder(this)
                        var modelo= layoutInflater.inflate(R.layout.dialogo,null)
                        val eliminar = modelo.findViewById<Button>(R.id.btn_dialogo_eliminar)
                        val editar = modelo.findViewById<Button>(R.id.btn_dialogo_editar)
                        dialogo.setView(modelo)
                        var dialogoper=dialogo.create()
                        dialogoper.show()
                        eliminar.setOnClickListener(){
                            for (medidas in lista){
                                list.onRemoteAdapterConnected()
                            }
                            }
                        editar.setOnClickListener(){

                        }

                    } catch (e: Exception){}

                   /* list.setOnItemClickListener { parent, view, position, id ->
                        try {
                            val indice = lista[position]
                            AlertDialog.Builder(this).apply {
                                setTitle("Ojito,ojito!")
                                setMessage("¿Qué desea hacer?")
                                setPositiveButton("Eliminar") { dialogInterface: DialogInterface, i: Int ->
                                    list.onRemoteAdapterConnected()
                                    actualizar()
                                }

                            }.show()
                        } catch (e: Exception){}
                    }*/
                }
                val adaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaString)
                list.adapter = adaptador }


        } catch (e:Exception) {
        }
    }

}


/*resultadoText.text = if("$result".endsWith(".0")) { "$result".replace(".0","") } else { "%.2f".format(result) }*/