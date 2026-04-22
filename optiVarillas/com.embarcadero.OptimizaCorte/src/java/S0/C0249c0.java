package S0;

import android.app.ProgressDialog;
import c1.C0368a;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import java.util.Comparator;

/* renamed from: S0.c0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class C0249c0 implements Comparator {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2225j;

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        switch (this.f2225j) {
            case 0:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                return Double.compare(((b1.e) obj2).f2915d, ((b1.e) obj).f2915d);
            default:
                return Double.compare(((C0368a) obj).f2934a, ((C0368a) obj2).f2934a);
        }
    }
}
