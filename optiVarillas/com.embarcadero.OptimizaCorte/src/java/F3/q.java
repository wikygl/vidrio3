package F3;

import C3.g0;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class q {

    /* renamed from: a  reason: collision with root package name */
    public static final g0 f944a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v12, types: [A3.a] */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.Object[], F3.p[]] */
    static {
        String str;
        int i4 = w.f946a;
        Object obj = null;
        try {
            str = System.getProperty("kotlinx.coroutines.fast.service.loader");
        } catch (SecurityException unused) {
            str = null;
        }
        if (str != null) {
            Boolean.parseBoolean(str);
        }
        try {
            Iterator it = Arrays.asList(new p[]{new Object()}).iterator();
            v3.h.e(it, "<this>");
            A3.f fVar = new A3.f(it);
            if (!(fVar instanceof A3.a)) {
                fVar = new A3.a(fVar);
            }
            List<? extends p> m4 = A3.c.m(fVar);
            Iterator it2 = m4.iterator();
            if (it2.hasNext()) {
                obj = it2.next();
                if (it2.hasNext()) {
                    int c4 = ((p) obj).c();
                    do {
                        Object next = it2.next();
                        int c5 = ((p) next).c();
                        if (c4 < c5) {
                            obj = next;
                            c4 = c5;
                        }
                    } while (it2.hasNext());
                }
            }
            p pVar = (p) obj;
            if (pVar != null) {
                try {
                    g0 b4 = pVar.b(m4);
                    if (b4 != null) {
                        f944a = b4;
                        return;
                    }
                } catch (Throwable th) {
                    pVar.a();
                    throw th;
                }
            }
            throw new IllegalStateException("Module with the Main dispatcher is missing. Add dependency providing the Main dispatcher, e.g. 'kotlinx-coroutines-android' and ensure it has the same version as 'kotlinx-coroutines-core'");
        } catch (Throwable th2) {
            throw new ServiceConfigurationError(th2.getMessage(), th2);
        }
    }
}
