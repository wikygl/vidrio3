package A1;

import com.google.android.gms.internal.ads.Ze;
import java.util.ArrayList;
import java.util.List;
import y1.InterfaceC0862b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class S0 extends Ze {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ T0 f87j;

    public final void t2(List list) {
        int i4;
        ArrayList arrayList;
        synchronized (this.f87j.f89a) {
            T0 t02 = this.f87j;
            t02.f91c = false;
            t02.f92d = true;
            arrayList = new ArrayList(this.f87j.f90b);
            this.f87j.f90b.clear();
        }
        T0.d(list);
        int size = arrayList.size();
        for (i4 = 0; i4 < size; i4++) {
            ((InterfaceC0862b) arrayList.get(i4)).a();
        }
    }
}
