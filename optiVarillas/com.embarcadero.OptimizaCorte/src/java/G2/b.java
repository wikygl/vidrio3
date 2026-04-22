package G2;

import android.view.ViewTreeObserver;
import com.google.android.material.floatingactionbutton.d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b implements ViewTreeObserver.OnPreDrawListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ d f961j;

    public b(d dVar) {
        this.f961j = dVar;
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public final boolean onPreDraw() {
        d dVar = this.f961j;
        float rotation = dVar.q.getRotation();
        if (dVar.j != rotation) {
            dVar.j = rotation;
            return true;
        }
        return true;
    }
}
