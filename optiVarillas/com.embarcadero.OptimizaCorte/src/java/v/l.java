package v;

import com.google.android.gms.internal.ads.yn;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class l extends e {

    /* renamed from: e0  reason: collision with root package name */
    public ArrayList<e> f6198e0 = new ArrayList<>();

    public void B() {
        ArrayList<e> arrayList = this.f6198e0;
        if (arrayList == null) {
            return;
        }
        int size = arrayList.size();
        for (int i4 = 0; i4 < size; i4++) {
            e eVar = this.f6198e0.get(i4);
            if (eVar instanceof l) {
                ((l) eVar).B();
            }
        }
    }

    @Override // v.e
    public void s() {
        this.f6198e0.clear();
        super.s();
    }

    @Override // v.e
    public final void u(yn ynVar) {
        super.u(ynVar);
        int size = this.f6198e0.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.f6198e0.get(i4).u(ynVar);
        }
    }
}
