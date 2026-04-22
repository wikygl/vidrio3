package K1;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.internal.ads.dk;
import java.util.concurrent.Callable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class E implements Callable {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f1292a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f1293b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ Object f1294c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ Object f1295d;

    public /* synthetic */ E(Object obj, Object obj2, Object obj3, int i4) {
        this.f1292a = i4;
        this.f1293b = obj;
        this.f1294c = obj2;
        this.f1295d = obj3;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f1292a) {
            case 0:
                C0208b c0208b = (C0208b) this.f1293b;
                Context context = c0208b.f1342l;
                dk dkVar = (dk) this.f1294c;
                return c0208b.C4(context, dkVar.j, dkVar.k, dkVar.l, dkVar.m, (Bundle) this.f1295d);
            default:
                Q0.a aVar = (Q0.a) this.f1293b;
                return aVar.f1930q.y3(aVar.f1928o.getPackageName(), (String) this.f1294c, (String) this.f1295d);
        }
    }
}
