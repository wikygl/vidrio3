package I0;

import C0.i;
import L0.p;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class c<T> implements H0.a<T> {

    /* renamed from: a  reason: collision with root package name */
    public final ArrayList f1142a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    public T f1143b;

    /* renamed from: c  reason: collision with root package name */
    public final J0.d<T> f1144c;

    /* renamed from: d  reason: collision with root package name */
    public a f1145d;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public interface a {
    }

    public c(J0.d<T> dVar) {
        this.f1144c = dVar;
    }

    @Override // H0.a
    public final void a(T t3) {
        this.f1143b = t3;
        e(this.f1145d, t3);
    }

    public abstract boolean b(p pVar);

    public abstract boolean c(T t3);

    public final void d(Iterable<p> iterable) {
        this.f1142a.clear();
        for (p pVar : iterable) {
            if (b(pVar)) {
                this.f1142a.add(pVar.f1450a);
            }
        }
        if (this.f1142a.isEmpty()) {
            this.f1144c.b(this);
        } else {
            J0.d<T> dVar = this.f1144c;
            synchronized (dVar.f1201c) {
                try {
                    if (dVar.f1202d.add(this)) {
                        if (dVar.f1202d.size() == 1) {
                            dVar.f1203e = dVar.a();
                            i.c().a(J0.d.f, String.format("%s: initial state = %s", dVar.getClass().getSimpleName(), dVar.f1203e), new Throwable[0]);
                            dVar.d();
                        }
                        a(dVar.f1203e);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        e(this.f1145d, this.f1143b);
    }

    public final void e(a aVar, T t3) {
        if (!this.f1142a.isEmpty() && aVar != null) {
            if (t3 != null && !c(t3)) {
                ArrayList arrayList = this.f1142a;
                H0.d dVar = (H0.d) aVar;
                synchronized (dVar.f1019c) {
                    try {
                        ArrayList arrayList2 = new ArrayList();
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            String str = (String) it.next();
                            if (dVar.a(str)) {
                                i c4 = i.c();
                                String str2 = H0.d.f1016d;
                                c4.a(str2, "Constraints met for " + str, new Throwable[0]);
                                arrayList2.add(str);
                            }
                        }
                        H0.c cVar = dVar.f1017a;
                        if (cVar != null) {
                            cVar.d(arrayList2);
                        }
                    } finally {
                    }
                }
                return;
            }
            ((H0.d) aVar).b(this.f1142a);
        }
    }
}
