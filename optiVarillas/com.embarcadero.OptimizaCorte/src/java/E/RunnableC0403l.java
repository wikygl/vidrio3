package e;

import M.O;
import M.V;
import android.view.ViewGroup;

/* renamed from: e.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class RunnableC0403l implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ LayoutInflater$Factory2C0401j f3284j;

    /* renamed from: e.l$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class a extends H.a {
        public a() {
        }

        @Override // M.W
        public final void b() {
            RunnableC0403l runnableC0403l = RunnableC0403l.this;
            runnableC0403l.f3284j.f3204E.setAlpha(1.0f);
            LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = runnableC0403l.f3284j;
            layoutInflater$Factory2C0401j.f3207H.d(null);
            layoutInflater$Factory2C0401j.f3207H = null;
        }

        @Override // H.a, M.W
        public final void e() {
            RunnableC0403l.this.f3284j.f3204E.setVisibility(0);
        }
    }

    public RunnableC0403l(LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j) {
        this.f3284j = layoutInflater$Factory2C0401j;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z4;
        ViewGroup viewGroup;
        LayoutInflater$Factory2C0401j layoutInflater$Factory2C0401j = this.f3284j;
        layoutInflater$Factory2C0401j.f3205F.showAtLocation(layoutInflater$Factory2C0401j.f3204E, 55, 0, 0);
        V v4 = layoutInflater$Factory2C0401j.f3207H;
        if (v4 != null) {
            v4.b();
        }
        if (layoutInflater$Factory2C0401j.f3209J && (viewGroup = layoutInflater$Factory2C0401j.f3210K) != null && viewGroup.isLaidOut()) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (z4) {
            layoutInflater$Factory2C0401j.f3204E.setAlpha(0.0f);
            V a4 = O.a(layoutInflater$Factory2C0401j.f3204E);
            a4.a(1.0f);
            layoutInflater$Factory2C0401j.f3207H = a4;
            a4.d(new a());
            return;
        }
        layoutInflater$Factory2C0401j.f3204E.setAlpha(1.0f);
        layoutInflater$Factory2C0401j.f3204E.setVisibility(0);
    }
}
