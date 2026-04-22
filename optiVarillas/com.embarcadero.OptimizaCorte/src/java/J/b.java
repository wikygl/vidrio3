package J;

import A1.L0;
import E.e;
import J.j;
import android.os.Handler;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public final B2.a f1152a;

    /* renamed from: b  reason: collision with root package name */
    public final Handler f1153b;

    public b(e.a aVar, Handler handler) {
        this.f1152a = aVar;
        this.f1153b = handler;
    }

    public final void a(j.a aVar) {
        int i4 = aVar.f1178b;
        Handler handler = this.f1153b;
        B2.a aVar2 = this.f1152a;
        if (i4 == 0) {
            handler.post(new L0(aVar2, 2, aVar.f1177a));
        } else {
            handler.post(new a(i4, 0, aVar2));
        }
    }
}
