package h0;

import C3.C;
import C3.C0155e;
import android.adservices.measurement.MeasurementManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.InputEvent;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class j {

    @SuppressLint({"NewApi", "ClassVerificationFailure"})
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class a extends j {

        /* renamed from: a  reason: collision with root package name */
        public final MeasurementManager f3506a;

        public a(Context context) {
            Object systemService;
            systemService = context.getSystemService(N2.a.b());
            v3.h.d(systemService, "context.getSystemService…:class.java\n            )");
            this.f3506a = C0432c.a(systemService);
        }

        /* JADX WARN: Type inference failed for: r1v0, types: [h0.i, java.lang.Object] */
        @Override // h0.j
        public Object a(n3.d<? super Integer> dVar) {
            C0155e c0155e = new C0155e(C.d(dVar));
            c0155e.m();
            this.f3506a.getMeasurementApiStatus(new Object(), new I.e(c0155e));
            return c0155e.l();
        }

        /* JADX WARN: Type inference failed for: r1v0, types: [h0.i, java.lang.Object] */
        @Override // h0.j
        public Object b(Uri uri, InputEvent inputEvent, n3.d<? super l3.g> dVar) {
            C0155e c0155e = new C0155e(C.d(dVar));
            c0155e.m();
            this.f3506a.registerSource(uri, inputEvent, new Object(), new I.e(c0155e));
            Object l2 = c0155e.l();
            if (l2 == o3.a.f5500j) {
                return l2;
            }
            return l3.g.f5271a;
        }

        /* JADX WARN: Type inference failed for: r1v0, types: [h0.i, java.lang.Object] */
        @Override // h0.j
        public Object c(Uri uri, n3.d<? super l3.g> dVar) {
            C0155e c0155e = new C0155e(C.d(dVar));
            c0155e.m();
            this.f3506a.registerTrigger(uri, new Object(), new I.e(c0155e));
            Object l2 = c0155e.l();
            if (l2 == o3.a.f5500j) {
                return l2;
            }
            return l3.g.f5271a;
        }

        public Object d(C0430a c0430a, n3.d<? super l3.g> dVar) {
            new C0155e(C.d(dVar)).m();
            f.c();
            throw null;
        }

        public Object e(k kVar, n3.d<? super l3.g> dVar) {
            new C0155e(C.d(dVar)).m();
            g.c();
            throw null;
        }

        public Object f(l lVar, n3.d<? super l3.g> dVar) {
            new C0155e(C.d(dVar)).m();
            h.b();
            throw null;
        }
    }

    public abstract Object a(n3.d<? super Integer> dVar);

    public abstract Object b(Uri uri, InputEvent inputEvent, n3.d<? super l3.g> dVar);

    public abstract Object c(Uri uri, n3.d<? super l3.g> dVar);
}
