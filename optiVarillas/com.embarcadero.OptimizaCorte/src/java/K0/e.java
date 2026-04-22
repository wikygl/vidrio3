package K0;

import androidx.work.impl.foreground.SystemForegroundService;
import com.google.android.gms.internal.ads.Rk;
import com.google.android.gms.internal.ads.jl;
import com.google.android.material.datepicker.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1278j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ int f1279k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f1280l;

    public /* synthetic */ e(int i4, int i5, Object obj) {
        this.f1278j = i5;
        this.f1280l = obj;
        this.f1279k = i4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f1278j) {
            case 0:
                ((SystemForegroundService) this.f1280l).n.cancel(this.f1279k);
                return;
            case 1:
                Rk rk = ((jl) this.f1280l).o;
                if (rk != null) {
                    rk.onWindowVisibilityChanged(this.f1279k);
                    return;
                }
                return;
            default:
                ((h) this.f1280l).l0.d0(this.f1279k);
                return;
        }
    }
}
