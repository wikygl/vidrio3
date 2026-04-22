package K1;

import com.google.android.gms.internal.ads.Zv;
import com.google.android.gms.internal.ads.mQ;
import com.google.android.gms.internal.ads.qX;
import com.google.android.gms.internal.ads.zX;
import java.util.Locale;

/* renamed from: K1.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0214h implements qX {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f1371a;

    /* renamed from: b  reason: collision with root package name */
    public final Object f1372b;

    public /* synthetic */ C0214h(int i4, Object obj) {
        this.f1371a = i4;
        this.f1372b = obj;
    }

    public final Object c() {
        switch (this.f1371a) {
            case 0:
                String lowerCase = ((C0212f) this.f1372b).f1369a.toLowerCase(Locale.ROOT);
                mQ.e(lowerCase);
                return lowerCase;
            default:
                return new t((Zv) ((zX) this.f1372b).c());
        }
    }
}
