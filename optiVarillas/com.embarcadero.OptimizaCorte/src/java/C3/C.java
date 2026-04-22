package C3;

import a3.InterfaceFutureC0346a;
import android.content.Context;
import android.provider.Settings;
import com.google.android.gms.internal.ads.nc;
import n3.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class C {
    public static void a(boolean z4) {
        if (z4) {
            return;
        }
        throw new IllegalArgumentException();
    }

    public static n3.d b(u3.p pVar, Object obj, n3.d dVar) {
        v3.h.e(dVar, "completion");
        return ((p3.a) pVar).a(dVar);
    }

    public static final String c(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    public static n3.d d(n3.d dVar) {
        p3.c cVar;
        v3.h.e(dVar, "<this>");
        if (dVar instanceof p3.c) {
            cVar = (p3.c) dVar;
        } else {
            cVar = null;
        }
        if (cVar != null && (dVar = cVar.f5580l) == null) {
            n3.f fVar = cVar.f5579k;
            v3.h.b(fVar);
            n3.e eVar = (n3.e) fVar.E(e.a.f5386j);
            if (eVar != null) {
                dVar = eVar.u(cVar);
            } else {
                dVar = cVar;
            }
            cVar.f5580l = dVar;
        }
        return dVar;
    }

    public static final String e(n3.d dVar) {
        String str;
        if (dVar instanceof F3.h) {
            return dVar.toString();
        }
        try {
            str = dVar + '@' + c(dVar);
        } catch (Throwable th) {
            str = B2.a.a(th);
        }
        Throwable a4 = l3.c.a(str);
        String str2 = str;
        if (a4 != null) {
            str2 = dVar.getClass().getName() + '@' + c(dVar);
        }
        return (String) str2;
    }

    public static void f(Context context) {
        boolean z4;
        Object obj = E1.l.f870b;
        if (((Boolean) nc.a.e()).booleanValue()) {
            try {
                if (Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) != 0) {
                    synchronized (E1.l.f870b) {
                        z4 = E1.l.f871c;
                    }
                    if (!z4) {
                        InterfaceFutureC0346a b4 = new D1.N(context).b();
                        E1.m.f("Updating ad debug logging enablement.");
                        com.google.android.gms.internal.ads.i.o(b4, "AdDebugLogUpdater.updateEnablement");
                    }
                }
            } catch (Exception e4) {
                E1.m.h("Fail to determine debug setting.", e4);
            }
        }
    }
}
