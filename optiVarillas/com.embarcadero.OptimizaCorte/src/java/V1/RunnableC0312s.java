package V1;

import android.content.Context;
import android.content.pm.PackageInfo;
import com.google.android.gms.internal.ads.I7;
import com.google.android.gms.internal.ads.O6;
import java.util.concurrent.Future;

/* renamed from: V1.s  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class RunnableC0312s implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2600j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ int f2601k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f2602l;

    public /* synthetic */ RunnableC0312s(int i4, int i5, Object obj) {
        this.f2600j = i5;
        this.f2602l = obj;
        this.f2601k = i4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        O6 o6;
        switch (this.f2600j) {
            case 0:
                ((u) this.f2602l).f(this.f2601k);
                return;
            default:
                int i4 = this.f2601k;
                I7 i7 = (I7) this.f2602l;
                if (i4 > 0) {
                    try {
                        Thread.sleep(i4 * 1000);
                    } catch (InterruptedException unused) {
                    }
                }
                try {
                    PackageInfo packageInfo = i7.a.getPackageManager().getPackageInfo(i7.a.getPackageName(), 0);
                    Context context = i7.a;
                    o6 = com.google.android.gms.internal.ads.i.f(context, context.getPackageName(), Integer.toString(packageInfo.versionCode));
                } catch (Throwable unused2) {
                    o6 = null;
                }
                ((I7) this.f2602l).j = o6;
                if (this.f2601k < 4) {
                    if (o6 == null || !o6.v0() || o6.D0().equals("0000000000000000000000000000000000000000000000000000000000000000") || !o6.w0() || !o6.B0().F() || o6.B0().D() == -2) {
                        I7 i72 = (I7) this.f2602l;
                        int i5 = this.f2601k + 1;
                        if (i72.n) {
                            Future<?> submit = i72.b.submit(new RunnableC0312s(i5, 1, i72));
                            if (i5 == 0) {
                                i72.k = submit;
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
        }
    }
}
