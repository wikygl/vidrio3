package S0;

import android.view.View;
import android.widget.ImageView;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5;
import e.C0397f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class N0 implements View.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2173j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0397f f2174k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f2175l;

    public /* synthetic */ N0(C0397f c0397f, Object obj, int i4) {
        this.f2173j = i4;
        this.f2174k = c0397f;
        this.f2175l = obj;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [android.content.Context, java.lang.Object, com.embarcadero.OptimizaCorte.Activities.ActivityTestBillingV5, e.f] */
    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        Object obj = this.f2175l;
        androidx.fragment.app.p pVar = this.f2174k;
        switch (this.f2173j) {
            case 0:
                int i4 = ActivityOptimizacion.f3061g0;
                androidx.fragment.app.p pVar2 = (ActivityOptimizacion) pVar;
                pVar2.getClass();
                ((ImageView) obj).setImageDrawable(B2.a.f(pVar2, 2131165362));
                return;
            default:
                int i5 = ActivityTestBillingV5.f3117Q;
                ?? r02 = (ActivityTestBillingV5) pVar;
                r02.getClass();
                Y0.a b4 = Y0.a.b(r02, (Z0.a) obj);
                r02.f3126P = b4;
                b4.h(Y0.a.f2822c, new C0294z0(r02));
                return;
        }
    }
}
