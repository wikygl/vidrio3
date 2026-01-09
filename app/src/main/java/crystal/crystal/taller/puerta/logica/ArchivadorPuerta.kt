package crystal.crystal.taller.puerta.logica

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityPuertaPanoBinding

object ArchivadorPuerta {
    fun archivar(contexto: Context, binding: ActivityPuertaPanoBinding, mapListas: MutableMap<String, MutableList<MutableList<String>>>) {
        ListaCasilla.incrementarContadorVentanas(contexto)
        if (esValido(binding.lyMarco)) ListaCasilla.procesarArchivar(contexto, binding.txMarco, binding.tvMarco, mapListas)
        if (esValido(binding.lyPaflon)) ListaCasilla.procesarArchivar(contexto, binding.txPaflon, binding.tvPaflon, mapListas)
        if (esValido(binding.lyTubo)) ListaCasilla.procesarArchivar(contexto, binding.txTubo, binding.tvTubo, mapListas)
        if (esValido(binding.lyJunki)) ListaCasilla.procesarArchivar(contexto, binding.txJunki, binding.tvJunki, mapListas)
        if (esValido(binding.lyTope)) ListaCasilla.procesarArchivar(contexto, binding.txTope, binding.tvTope, mapListas)
        if (esValido(binding.lyVidrios)) ListaCasilla.procesarArchivar(contexto, binding.tvVidrios, binding.txVidrios, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(contexto, binding.tvProyectoActivo)
        println("Datos agregados al proyecto: ${ProyectoManager.getProyectoActivo()}")
        println(mapListas)
    }
    private fun esValido(ly: LinearLayout): Boolean = ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
}