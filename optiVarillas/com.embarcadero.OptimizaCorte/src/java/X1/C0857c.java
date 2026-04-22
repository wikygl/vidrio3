package x1;

import com.google.android.gms.common.util.VisibleForTesting;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@VisibleForTesting
/* renamed from: x1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0857c extends Thread {

    /* renamed from: j  reason: collision with root package name */
    public final WeakReference<C0855a> f6470j;

    /* renamed from: k  reason: collision with root package name */
    public final long f6471k;

    /* renamed from: l  reason: collision with root package name */
    public final CountDownLatch f6472l = new CountDownLatch(1);

    /* renamed from: m  reason: collision with root package name */
    public boolean f6473m = false;

    public C0857c(C0855a c0855a, long j4) {
        this.f6470j = new WeakReference<>(c0855a);
        this.f6471k = j4;
        start();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        C0855a c0855a;
        WeakReference<C0855a> weakReference = this.f6470j;
        try {
            if (!this.f6472l.await(this.f6471k, TimeUnit.MILLISECONDS) && (c0855a = weakReference.get()) != null) {
                c0855a.c();
                this.f6473m = true;
            }
        } catch (InterruptedException unused) {
            C0855a c0855a2 = weakReference.get();
            if (c0855a2 != null) {
                c0855a2.c();
                this.f6473m = true;
            }
        }
    }
}
