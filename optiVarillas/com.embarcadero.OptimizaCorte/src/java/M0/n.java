package M0;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.work.ListenableWorker;
import java.util.UUID;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class n implements Runnable {

    /* renamed from: p  reason: collision with root package name */
    public static final String f1689p = C0.i.e("WorkForegroundRunnable");

    /* renamed from: j  reason: collision with root package name */
    public final N0.c<Void> f1690j = new N0.a();

    /* renamed from: k  reason: collision with root package name */
    public final Context f1691k;

    /* renamed from: l  reason: collision with root package name */
    public final L0.p f1692l;

    /* renamed from: m  reason: collision with root package name */
    public final ListenableWorker f1693m;

    /* renamed from: n  reason: collision with root package name */
    public final C0.g f1694n;

    /* renamed from: o  reason: collision with root package name */
    public final O0.a f1695o;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final /* synthetic */ N0.c f1696j;

        public a(N0.c cVar) {
            this.f1696j = cVar;
        }

        @Override // java.lang.Runnable
        public final void run() {
            this.f1696j.l(n.this.f1693m.getForegroundInfoAsync());
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class b implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final /* synthetic */ N0.c f1698j;

        public b(N0.c cVar) {
            this.f1698j = cVar;
        }

        @Override // java.lang.Runnable
        public final void run() {
            n nVar = n.this;
            try {
                C0.f fVar = (C0.f) this.f1698j.get();
                if (fVar != null) {
                    C0.i c4 = C0.i.c();
                    String str = n.f1689p;
                    L0.p pVar = nVar.f1692l;
                    ListenableWorker listenableWorker = nVar.f1693m;
                    String str2 = pVar.f1452c;
                    c4.a(str, "Updating notification for " + str2, new Throwable[0]);
                    listenableWorker.setRunInForeground(true);
                    N0.c<Void> cVar = nVar.f1690j;
                    C0.g gVar = nVar.f1694n;
                    Context context = nVar.f1691k;
                    UUID id = listenableWorker.getId();
                    p pVar2 = (p) gVar;
                    pVar2.getClass();
                    N0.a aVar = new N0.a();
                    ((O0.b) pVar2.f1706a).a(new o(pVar2, aVar, id, fVar, context, 0));
                    cVar.l(aVar);
                    return;
                }
                String str3 = nVar.f1692l.f1452c;
                throw new IllegalStateException("Worker was marked important (" + str3 + ") but did not provide ForegroundInfo");
            } catch (Throwable th) {
                nVar.f1690j.k(th);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [N0.c<java.lang.Void>, N0.a] */
    @SuppressLint({"LambdaLast"})
    public n(Context context, L0.p pVar, ListenableWorker listenableWorker, p pVar2, O0.a aVar) {
        this.f1691k = context;
        this.f1692l = pVar;
        this.f1693m = listenableWorker;
        this.f1694n = pVar2;
        this.f1695o = aVar;
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [N0.a, N0.c] */
    @Override // java.lang.Runnable
    @SuppressLint({"UnsafeExperimentalUsageError"})
    public final void run() {
        if (this.f1692l.f1465q && !I.a.a()) {
            ?? aVar = new N0.a();
            O0.b bVar = (O0.b) this.f1695o;
            bVar.f1796c.execute(new a(aVar));
            aVar.a(new b(aVar), bVar.f1796c);
            return;
        }
        this.f1690j.j(null);
    }
}
