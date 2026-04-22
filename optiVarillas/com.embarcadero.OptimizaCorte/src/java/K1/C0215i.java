package K1;

import com.google.android.gms.internal.ads.Xr;
import com.google.android.gms.internal.ads.qX;
import java.util.HashSet;
import java.util.Locale;

/* renamed from: K1.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0215i implements qX {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f1373a;

    /* renamed from: b  reason: collision with root package name */
    public final Object f1374b;

    public /* synthetic */ C0215i(int i4, Object obj) {
        this.f1373a = i4;
        this.f1374b = obj;
    }

    public final Object c() {
        switch (this.f1373a) {
            case 0:
                C0212f c0212f = (C0212f) this.f1374b;
                c0212f.getClass();
                HashSet hashSet = new HashSet();
                hashSet.add(c0212f.f1369a.toLowerCase(Locale.ROOT));
                return hashSet;
            default:
                return ((Xr) this.f1374b).d;
        }
    }
}
