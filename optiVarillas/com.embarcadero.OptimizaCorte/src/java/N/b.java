package N;

import A1.P0;
import M.O;
import M.V;
import S0.M0;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AutoCompleteTextView;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class b implements AccessibilityManager.TouchExplorationStateChangeListener {

    /* renamed from: a  reason: collision with root package name */
    public final M0 f1741a;

    public b(M0 m02) {
        this.f1741a = m02;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        return this.f1741a.equals(((b) obj).f1741a);
    }

    public final int hashCode() {
        return this.f1741a.hashCode();
    }

    @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
    public final void onTouchExplorationStateChanged(boolean z4) {
        int i4;
        U2.m mVar = (U2.m) this.f1741a.f2161j;
        AutoCompleteTextView autoCompleteTextView = mVar.f2412h;
        if (autoCompleteTextView != null && !P0.b(autoCompleteTextView)) {
            if (z4) {
                i4 = 2;
            } else {
                i4 = 1;
            }
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            mVar.f2426d.setImportantForAccessibility(i4);
        }
    }
}
