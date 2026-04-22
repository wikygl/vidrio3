package H0;

import C0.i;
import I0.c;
import J0.f;
import J0.g;
import J0.h;
import L0.p;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class d implements c.a {

    /* renamed from: d  reason: collision with root package name */
    public static final String f1016d = i.e("WorkConstraintsTracker");

    /* renamed from: a  reason: collision with root package name */
    public final c f1017a;

    /* renamed from: b  reason: collision with root package name */
    public final I0.c<?>[] f1018b;

    /* renamed from: c  reason: collision with root package name */
    public final Object f1019c;

    public d(Context context, O0.a aVar, c cVar) {
        Context applicationContext = context.getApplicationContext();
        this.f1017a = cVar;
        this.f1018b = new I0.c[]{new I0.c<>((J0.a) h.a(applicationContext, aVar).f1214a), new I0.c<>((J0.b) h.a(applicationContext, aVar).f1215b), new I0.c<>((g) h.a(applicationContext, aVar).f1217d), new I0.c<>((f) h.a(applicationContext, aVar).f1216c), new I0.c<>((f) h.a(applicationContext, aVar).f1216c), new I0.c<>((f) h.a(applicationContext, aVar).f1216c), new I0.c<>((f) h.a(applicationContext, aVar).f1216c)};
        this.f1019c = new Object();
    }

    public final boolean a(String str) {
        I0.c<?>[] cVarArr;
        synchronized (this.f1019c) {
            try {
                for (I0.c<?> cVar : this.f1018b) {
                    Object obj = cVar.f1143b;
                    if (obj != null && cVar.c(obj) && cVar.f1142a.contains(str)) {
                        i.c().a(f1016d, "Work " + str + " constrained by " + cVar.getClass().getSimpleName(), new Throwable[0]);
                        return false;
                    }
                }
                return true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void b(List<String> list) {
        synchronized (this.f1019c) {
            try {
                c cVar = this.f1017a;
                if (cVar != null) {
                    cVar.c(list);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void c(Iterable<p> iterable) {
        I0.c<?>[] cVarArr;
        I0.c<?>[] cVarArr2;
        synchronized (this.f1019c) {
            try {
                for (I0.c<?> cVar : this.f1018b) {
                    if (cVar.f1145d != null) {
                        cVar.f1145d = null;
                        cVar.e(null, cVar.f1143b);
                    }
                }
                for (I0.c<?> cVar2 : this.f1018b) {
                    cVar2.d(iterable);
                }
                for (I0.c<?> cVar3 : this.f1018b) {
                    if (cVar3.f1145d != this) {
                        cVar3.f1145d = this;
                        cVar3.e(this, cVar3.f1143b);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void d() {
        I0.c<?>[] cVarArr;
        synchronized (this.f1019c) {
            try {
                for (I0.c<?> cVar : this.f1018b) {
                    ArrayList arrayList = cVar.f1142a;
                    if (!arrayList.isEmpty()) {
                        arrayList.clear();
                        cVar.f1144c.b(cVar);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
