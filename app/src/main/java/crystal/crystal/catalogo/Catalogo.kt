package crystal.crystal.catalogo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityCatalogoBinding
import kotlinx.android.synthetic.main.activity_taller.*


class Catalogo : AppCompatActivity() {

    private var productoSeleccionado: String? = null


    private lateinit var bindin : ActivityCatalogoBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindin=ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(bindin.root)

        configureAdapter()

        micro.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))}
        bindin.btnCatalogo.setOnClickListener {
            startActivity(Intent(this, SubirActivity::class.java))  }
    }

    private fun configureAdapter() {
        bindin.recyclerView.adapter = Adapter(ListadoCat.productos) { data ->
            productoSeleccionado = data.nombre
            cambiarImagenes()
        }
}

    private fun cambiarImagenes() {
        // Verificar si se ha seleccionado un producto
        val producto = productoSeleccionado ?: return

        // Obtener el ID de la imagen correspondiente en Firebase Storage
        val imagenID = obtenerImagenID(producto)

        // Cambiar las imágenes del RecyclerView
        for (i in 0 until bindin.recyclerView.childCount) {
            val viewHolder = bindin.recyclerView.getChildViewHolder(bindin.recyclerView.getChildAt(i))
            if (viewHolder is Holder) {
                val item = viewHolder.itemView.tag as Data
                // Cargar la nueva imagen utilizando Glide
                Glide.with(this)
                    .load(imagenID)
                    .into(viewHolder.binding.foto)
            }
        }
    }

    private fun obtenerImagenID(producto: String): String {
        // Implementa la lógica para obtener el ID de la imagen correspondiente en Firebase Storage
        // según el nombre del producto seleccionado
        // Aquí deberás buscar en tu carpeta de Firebase Storage y devolver el ID de la imagen correspondiente
        // Puedes almacenar los ID de las imágenes en una estructura de datos adecuada en tu clase ListadoCat

        // Ejemplo de implementación utilizando un mapa en ListadoCat:
        val imagenMap = mapOf(
            "Puertas" to "ID_IMAGEN_PUERTAS",
            "Ventanas" to "ID_IMAGEN_VENTANAS",
            // ... mapea el nombre de cada producto con su respectivo ID de imagen
        )

        return imagenMap[producto] ?: ""
    }



    /*private fun adapterFotos(){
        val recyclerView= findViewById<RecyclerView>(R.id.recyclerFotos)
        recyclerView.adapter=AdapterFotos(ListadoCat.ventanas)

        private fun obtenerImagenID(producto: String): String {
    // Crea una instancia de FirebaseStorage
    val storage = FirebaseStorage.getInstance()

    // Obtén una referencia a la carpeta en Firebase Storage donde se encuentran las imágenes
    val storageRef = storage.reference.child("ruta_a_tu_carpeta_de_imagenes")

    // Realiza una consulta o búsqueda en la carpeta de imágenes para obtener el ID de la imagen
    // según el nombre del producto seleccionado
    // Aquí puedes utilizar métodos como listAll() para obtener la lista de elementos en la carpeta
    // y luego recorrerla para encontrar la imagen correspondiente

    // Ejemplo de implementación utilizando listAll():
    storageRef.listAll()
        .addOnSuccessListener { listResult ->
            for (item in listResult.items) {
                val nombreImagen = item.name
                val nombreProducto = obtenerNombreProductoDesdeImagen(nombreImagen)
                if (nombreProducto == producto) {
                    // Se encontró la imagen correspondiente al producto
                    val imagenID = obtenerIDDesdeNombreImagen(nombreImagen)
                    // Realiza alguna acción con el ID de la imagen
                    return imagenID
                }
            }
            // No se encontró ninguna imagen correspondiente al producto
            return ""
        }
        .addOnFailureListener { exception ->
            // Ocurrió un error al realizar la consulta en Firebase Storage
            // Maneja el error de acuerdo a tus necesidades
            return ""
        }
}

private fun obtenerNombreProductoDesdeImagen(nombreImagen: String): String {
    // Implementa la lógica para obtener el nombre del producto desde el nombre de la imagen
    // Puedes utilizar alguna convención o estructura de nombres para hacer esto

    // Ejemplo de implementación:
    // Si las imágenes se nombran como "producto1_imagen.png", puedes extraer "producto1" como el nombre del producto
    val nombreProducto = nombreImagen.substringBefore("_imagen")
    return nombreProducto
}

private fun obtenerIDDesdeNombreImagen(nombreImagen: String): String {
    // Implementa la lógica para obtener el ID desde el nombre de la imagen
    // Puedes utilizar alguna convención o estructura de nombres para hacer esto

    // Ejemplo de implementación:
    // Si las imágenes se nombran como "producto1_imagen.png", puedes extraer "producto1" como el ID de la imagen
    val imagenID = nombreImagen.substringBefore("_imagen")
    return imagenID
}


    }*/

}