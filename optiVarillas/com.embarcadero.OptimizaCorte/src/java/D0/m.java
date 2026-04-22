package D0;

import android.annotation.SuppressLint;
import androidx.work.ListenableWorker;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class m implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ N0.c f597j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ String f598k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ n f599l;

    public m(n nVar, N0.c cVar, String str) {
        this.f599l = nVar;
        this.f597j = cVar;
        this.f598k = str;
    }

    @Override // java.lang.Runnable
    @SuppressLint({"SyntheticAccessor"})
    public final void run() {
        String str = this.f598k;
        n nVar = this.f599l;
        try {
            try {
                ListenableWorker.a aVar = (ListenableWorker.a) this.f597j.get();
                if (aVar == null) {
                    C0.i c4 = C0.i.c();
                    String str2 = n.f600C;
                    String str3 = nVar.f607n.f1452c;
                    c4.b(str2, str3 + " returned a null result. Treating it as a failure.", new Throwable[0]);
                } else {
                    C0.i.c().a(n.f600C, String.format("%s returned a %s result.", nVar.f607n.f1452c, aVar), new Throwable[0]);
                    nVar.f610q = aVar;
                }
            } catch (InterruptedException e4) {
                e = e4;
                C0.i c5 = C0.i.c();
                String str4 = n.f600C;
                c5.b(str4, str + " failed because it threw an exception/error", e);
            } catch (CancellationException e5) {
                C0.i c6 = C0.i.c();
                String str5 = n.f600C;
                c6.d(str5, str + " was cancelled", e5);
            } catch (ExecutionException e6) {
                e = e6;
                C0.i c52 = C0.i.c();
                String str42 = n.f600C;
                c52.b(str42, str + " failed because it threw an exception/error", e);
            }
            nVar.c();
        } catch (Throwable th) {
            nVar.c();
            throw th;
        }
    }
}
