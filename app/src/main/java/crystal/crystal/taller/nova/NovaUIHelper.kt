package crystal.crystal.taller.nova

import android.view.View
import android.widget.LinearLayout

/**
 * Lo que queda de NovaUIHelper tras la fase 4 del motor único (2026-09-30): el diseño, las
 * referencias y los materiales salen de taller/nova/motor; lo demás está en viejo/NovaUIHelperViejo.kt.
 */
object NovaUIHelper {
    /** Una fila de resultados que se archiva: la que se ve (o la que ocupa su lugar). */
    fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }
}
