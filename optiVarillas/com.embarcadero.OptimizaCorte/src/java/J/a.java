package J;

import D.f;
import E.e;
import com.google.android.gms.internal.ads.WX;
import com.google.android.gms.internal.ads.YX;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1149j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ int f1150k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f1151l;

    public /* synthetic */ a(int i4, int i5, Object obj) {
        this.f1149j = i5;
        this.f1151l = obj;
        this.f1150k = i4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f1149j) {
            case 0:
                f.e eVar = ((e.a) ((B2.a) this.f1151l)).f812j;
                if (eVar != null) {
                    eVar.b(this.f1150k);
                    return;
                }
                return;
            default:
                YX yx = ((WX) this.f1151l).b;
                int i4 = this.f1150k;
                if (i4 != -3 && i4 != -2) {
                    if (i4 != -1) {
                        if (i4 != 1) {
                            X1.b.g(i4, "Unknown focus change type: ", "AudioFocusManager");
                            return;
                        }
                        yx.c(1);
                        yx.b(1);
                        return;
                    }
                    yx.b(-1);
                    yx.a();
                    return;
                } else if (i4 != -2) {
                    yx.c(3);
                    return;
                } else {
                    yx.b(0);
                    yx.c(2);
                    return;
                }
        }
    }
}
