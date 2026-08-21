package crystal.crystal.taller

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import crystal.crystal.FeaturesV1

/**
 * Banner "🚧 En desarrollo" para las calculadoras aún NO terminadas (v1). Se engancha UNA sola vez
 * desde CrystalApp vía ActivityLifecycleCallbacks, así no hay que tocar cada actividad. Las
 * terminadas (Nova Corrediza, Puertas, Mampara Paflón, VentanaAl) no están en la lista. El banner
 * cubre la pantalla al entrar y se descarta al tocarlo (deja curiosear la calculadora).
 */
object AvisoDesarrollo {

    // Nombres simples de clase de las calculadoras en desarrollo.
    private val enDesarrollo = setOf(
        "Muro", "Vitroven", "RejasActivity", "MamparaVidrioActivity",
        "PDuchaActivity", "DivisionBanoActivity", "BarandaActivity",
        "MamparaFC", "PivotAl"
    )

    private const val TAG_BANNER = "aviso_en_desarrollo"

    fun registrar(app: Application) {
        if (!FeaturesV1.AVISO_CALCULADORAS_EN_DESARROLLO) return
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity::class.java.simpleName in enDesarrollo) mostrarBanner(activity)
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    private fun mostrarBanner(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content) ?: return
        content.post {
            if (content.findViewWithTag<android.view.View>(TAG_BANNER) != null) return@post
            val d = activity.resources.displayMetrics.density
            val overlay = FrameLayout(activity).apply {
                tag = TAG_BANNER
                setBackgroundColor(0xCC000000.toInt())   // negro ~80%
                isClickable = true
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
            val texto = TextView(activity).apply {
                text = "🚧\n\nEn desarrollo\nMuy pronto disponible\n\n(toca para ver el avance)"
                setTextColor(Color.WHITE)
                textSize = 22f
                gravity = Gravity.CENTER
                setLineSpacing(0f, 1.2f)
                val p = (24 * d).toInt()
                setPadding(p, p, p, p)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )
            }
            overlay.addView(texto)
            overlay.setOnClickListener { (overlay.parent as? ViewGroup)?.removeView(overlay) }
            content.addView(overlay)
        }
    }
}
