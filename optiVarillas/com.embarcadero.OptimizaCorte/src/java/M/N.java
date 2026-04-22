package M;

import M.O;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class N extends O.b<Boolean> {
    @Override // M.O.b
    public final Boolean a(View view) {
        return Boolean.valueOf(O.h.c(view));
    }

    @Override // M.O.b
    public final void b(View view, Boolean bool) {
        O.h.g(view, bool.booleanValue());
    }

    @Override // M.O.b
    public final boolean e(Boolean bool, Boolean bool2) {
        boolean z4;
        boolean z5;
        Boolean bool3 = bool;
        Boolean bool4 = bool2;
        boolean z6 = false;
        if (bool3 != null && bool3.booleanValue()) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (bool4 != null && bool4.booleanValue()) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z4 == z5) {
            z6 = true;
        }
        return !z6;
    }
}
