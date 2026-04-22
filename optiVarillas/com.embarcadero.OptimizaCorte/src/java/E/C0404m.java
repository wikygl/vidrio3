package e;

import M.O;
import M.V;
import android.view.View;
import java.util.WeakHashMap;

/* renamed from: e.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0404m extends H.a {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ LayoutInflater$Factory2C0401j f3286j;

    public C0404m(LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j) {
        this.f3286j = layoutInflater$Factory2C0401j;
    }

    @Override // M.W
    public final void b() {
        LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = this.f3286j;
        layoutInflater$Factory2C0401j.f3204E.setAlpha(1.0f);
        layoutInflater$Factory2C0401j.f3207H.d(null);
        layoutInflater$Factory2C0401j.f3207H = null;
    }

    @Override // H.a, M.W
    public final void e() {
        LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = this.f3286j;
        layoutInflater$Factory2C0401j.f3204E.setVisibility(0);
        if (layoutInflater$Factory2C0401j.f3204E.getParent() instanceof View) {
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            O.c.c((View) layoutInflater$Factory2C0401j.f3204E.getParent());
        }
    }
}
