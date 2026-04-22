package M0;

import C0.l;
import androidx.work.impl.WorkDatabase;
import java.util.LinkedList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class d implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final D0.b f1666j = new D0.b();

    public static void a(D0.k kVar, String str) {
        WorkDatabase workDatabase = kVar.f587c;
        L0.q n4 = workDatabase.n();
        L0.b i4 = workDatabase.i();
        LinkedList linkedList = new LinkedList();
        linkedList.add(str);
        while (!linkedList.isEmpty()) {
            String str2 = (String) linkedList.remove();
            L0.r rVar = (L0.r) n4;
            C0.o f = rVar.f(str2);
            if (f != C0.o.f339l && f != C0.o.f340m) {
                rVar.p(C0.o.f342o, str2);
            }
            linkedList.addAll(((L0.c) i4).a(str2));
        }
        D0.c cVar = kVar.f;
        synchronized (cVar.f566t) {
            try {
                boolean z4 = false;
                C0.i.c().a(D0.c.f555u, "Processor cancelling " + str, new Throwable[0]);
                cVar.f564r.add(str);
                D0.n nVar = (D0.n) cVar.f561o.remove(str);
                if (nVar != null) {
                    z4 = true;
                }
                if (nVar == null) {
                    nVar = (D0.n) cVar.f562p.remove(str);
                }
                D0.c.c(str, nVar);
                if (z4) {
                    cVar.i();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        for (D0.d dVar : kVar.f589e) {
            dVar.b(str);
        }
    }

    public abstract void b();

    @Override // java.lang.Runnable
    public final void run() {
        D0.b bVar = this.f1666j;
        try {
            b();
            bVar.a(C0.l.f331a);
        } catch (Throwable th) {
            bVar.a(new l.a.C0003a(th));
        }
    }
}
