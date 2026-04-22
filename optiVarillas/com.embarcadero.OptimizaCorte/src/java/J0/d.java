package J0;

import C0.i;
import android.content.Context;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class d<T> {
    public static final String f = i.e("ConstraintTracker");

    /* renamed from: a  reason: collision with root package name */
    public final O0.a f1199a;

    /* renamed from: b  reason: collision with root package name */
    public final Context f1200b;

    /* renamed from: c  reason: collision with root package name */
    public final Object f1201c = new Object();

    /* renamed from: d  reason: collision with root package name */
    public final LinkedHashSet f1202d = new LinkedHashSet();

    /* renamed from: e  reason: collision with root package name */
    public T f1203e;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public class a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final /* synthetic */ List f1204j;

        public a(ArrayList arrayList) {
            this.f1204j = arrayList;
        }

        @Override // java.lang.Runnable
        public final void run() {
            for (H0.a aVar : this.f1204j) {
                aVar.a(d.this.f1203e);
            }
        }
    }

    public d(Context context, O0.a aVar) {
        this.f1200b = context.getApplicationContext();
        this.f1199a = aVar;
    }

    public abstract T a();

    public final void b(I0.c cVar) {
        synchronized (this.f1201c) {
            try {
                if (this.f1202d.remove(cVar) && this.f1202d.isEmpty()) {
                    e();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void c(T t3) {
        synchronized (this.f1201c) {
            try {
                T t4 = this.f1203e;
                if (t4 != t3 && (t4 == null || !t4.equals(t3))) {
                    this.f1203e = t3;
                    ((O0.b) this.f1199a).f1796c.execute(new a(new ArrayList(this.f1202d)));
                }
            } finally {
            }
        }
    }

    public abstract void d();

    public abstract void e();
}
