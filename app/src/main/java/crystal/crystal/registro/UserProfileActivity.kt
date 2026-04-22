package crystal.crystal.registro

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import crystal.crystal.R
import crystal.crystal.databinding.ActivityUserProfileBinding
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var repository: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = UserProfileRepository(this)

        supportActionBar?.title = "Mi perfil"

        binding.btnGuardarPerfil.setOnClickListener { guardarPerfil() }
        binding.etFotoPerfil.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) actualizarPreviewFoto()
        }

        cargarPerfil()
    }

    private fun cargarPerfil() {
        lifecycleScope.launch {
            try {
                val profile = repository.loadProfile()
                if (profile == null) {
                    Toast.makeText(this@UserProfileActivity, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                binding.etNombrePerfil.setText(profile.nombre)
                binding.etEmailPerfil.setText(profile.email)
                binding.etFotoPerfil.setText(profile.imagenPerfil)
                binding.cbPlatformCrystal.isChecked = "crystal" in profile.platforms
                binding.cbPlatformPuntos.isChecked = "puntos" in profile.platforms
                actualizarPreviewFoto()
            } catch (e: Exception) {
                Toast.makeText(this@UserProfileActivity, "Error cargando perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun guardarPerfil() {
        val nombre = binding.etNombrePerfil.text.toString().trim()
        val email = binding.etEmailPerfil.text.toString().trim()
        val imagen = binding.etFotoPerfil.text.toString().trim()
        val platforms = buildList {
            if (binding.cbPlatformCrystal.isChecked) add("crystal")
            if (binding.cbPlatformPuntos.isChecked) add("puntos")
        }

        if (nombre.isBlank()) {
            binding.etNombrePerfil.error = "Ingresa un nombre"
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            binding.etEmailPerfil.error = "Ingresa un email valido"
            return
        }

        lifecycleScope.launch {
            try {
                binding.btnGuardarPerfil.isEnabled = false
                repository.saveProfile(
                    nombre = nombre,
                    email = email,
                    imagenPerfil = imagen,
                    platforms = platforms
                )
                Toast.makeText(this@UserProfileActivity, "Perfil guardado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                binding.btnGuardarPerfil.isEnabled = true
                Toast.makeText(this@UserProfileActivity, "Error guardando perfil: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarPreviewFoto() {
        val url = binding.etFotoPerfil.text.toString().trim()
        Glide.with(this)
            .load(url.ifBlank { R.drawable.ic_usuario4 })
            .circleCrop()
            .placeholder(R.drawable.ic_usuario4)
            .error(R.drawable.ic_usuario4)
            .into(binding.ivProfilePreview)
    }
}
