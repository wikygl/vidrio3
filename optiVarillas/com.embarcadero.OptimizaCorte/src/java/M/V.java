package M;

import android.view.View;
import java.lang.ref.WeakReference;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class V {

    /* renamed from: a  reason: collision with root package name */
    public final WeakReference<View> f1551a;

    public V(View view) {
        this.f1551a = new WeakReference<>(view);
    }

    public final void a(float f) {
        View view = this.f1551a.get();
        if (view != null) {
            view.animate().alpha(f);
        }
    }

    public final void b() {
        View view = this.f1551a.get();
        if (view != null) {
            view.animate().cancel();
        }
    }

    public final void c(long j4) {
        View view = this.f1551a.get();
        if (view != null) {
            view.animate().setDuration(j4);
        }
    }

    public final void d(W w4) {
        View view = this.f1551a.get();
        if (view != null) {
            if (w4 != null) {
                view.animate().setListener(new U(w4, view));
            } else {
                view.animate().setListener(null);
            }
        }
    }

    public final void e(float f) {
        View view = this.f1551a.get();
        if (view != null) {
            view.animate().translationY(f);
        }
    }
}
