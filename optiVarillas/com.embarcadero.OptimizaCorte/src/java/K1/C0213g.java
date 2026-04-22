package K1;

import com.google.android.gms.internal.ads.N9;
import com.google.android.gms.internal.ads.qX;

/* renamed from: K1.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0213g implements qX {

    /* renamed from: a  reason: collision with root package name */
    public final C0212f f1370a;

    public C0213g(C0212f c0212f) {
        this.f1370a = c0212f;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final Object c() {
        char c4;
        String str = this.f1370a.f1369a;
        switch (str.hashCode()) {
            case -1999289321:
                if (str.equals("NATIVE")) {
                    c4 = 2;
                    break;
                }
                c4 = 65535;
                break;
            case -1372958932:
                if (str.equals("INTERSTITIAL")) {
                    c4 = 1;
                    break;
                }
                c4 = 65535;
                break;
            case 543046670:
                if (str.equals("REWARDED")) {
                    c4 = 3;
                    break;
                }
                c4 = 65535;
                break;
            case 1951953708:
                if (str.equals("BANNER")) {
                    c4 = 0;
                    break;
                }
                c4 = 65535;
                break;
            default:
                c4 = 65535;
                break;
        }
        if (c4 != 0) {
            if (c4 != 1) {
                if (c4 != 2) {
                    if (c4 != 3) {
                        return N9.k;
                    }
                    return N9.r;
                }
                return N9.q;
            }
            return N9.n;
        }
        return N9.l;
    }
}
