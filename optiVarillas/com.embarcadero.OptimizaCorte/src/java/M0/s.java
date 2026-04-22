package M0;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class s {

    /* renamed from: e  reason: collision with root package name */
    public static final String f1717e = C0.i.e("WorkTimer");

    /* renamed from: a  reason: collision with root package name */
    public final ScheduledExecutorService f1718a;

    /* renamed from: b  reason: collision with root package name */
    public final HashMap f1719b;

    /* renamed from: c  reason: collision with root package name */
    public final HashMap f1720c;

    /* renamed from: d  reason: collision with root package name */
    public final Object f1721d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class a implements ThreadFactory {

        /* renamed from: a  reason: collision with root package name */
        public int f1722a;

        @Override // java.util.concurrent.ThreadFactory
        public final Thread newThread(Runnable runnable) {
            Thread newThread = Executors.defaultThreadFactory().newThread(runnable);
            newThread.setName("WorkManager-WorkTimer-thread-" + this.f1722a);
            this.f1722a = this.f1722a + 1;
            return newThread;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface b {
        void b(String str);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class c implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final s f1723j;

        /* renamed from: k  reason: collision with root package name */
        public final String f1724k;

        public c(s sVar, String str) {
            this.f1723j = sVar;
            this.f1724k = str;
        }

        @Override // java.lang.Runnable
        public final void run() {
            synchronized (this.f1723j.f1721d) {
                try {
                    if (((c) this.f1723j.f1719b.remove(this.f1724k)) != null) {
                        b bVar = (b) this.f1723j.f1720c.remove(this.f1724k);
                        if (bVar != null) {
                            bVar.b(this.f1724k);
                        }
                    } else {
                        C0.i c4 = C0.i.c();
                        String str = this.f1724k;
                        c4.a("WrkTimerRunnable", "Timer with " + str + " is already marked as complete.", new Throwable[0]);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [M0.s$a, java.lang.Object, java.util.concurrent.ThreadFactory] */
    public s() {
        ?? obj = new Object();
        obj.f1722a = 0;
        this.f1719b = new HashMap();
        this.f1720c = new HashMap();
        this.f1721d = new Object();
        this.f1718a = Executors.newSingleThreadScheduledExecutor(obj);
    }

    public final void a(String str, b bVar) {
        synchronized (this.f1721d) {
            C0.i c4 = C0.i.c();
            String str2 = f1717e;
            c4.a(str2, "Starting timer for " + str, new Throwable[0]);
            b(str);
            c cVar = new c(this, str);
            this.f1719b.put(str, cVar);
            this.f1720c.put(str, bVar);
            this.f1718a.schedule(cVar, 600000L, TimeUnit.MILLISECONDS);
        }
    }

    public final void b(String str) {
        synchronized (this.f1721d) {
            try {
                if (((c) this.f1719b.remove(str)) != null) {
                    C0.i c4 = C0.i.c();
                    String str2 = f1717e;
                    c4.a(str2, "Stopping timer for " + str, new Throwable[0]);
                    this.f1720c.remove(str);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
