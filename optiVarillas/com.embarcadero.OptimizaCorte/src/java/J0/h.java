package J0;

import android.content.Context;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class h {

    /* renamed from: e  reason: collision with root package name */
    public static h f1213e;

    /* renamed from: a  reason: collision with root package name */
    public Object f1214a;

    /* renamed from: b  reason: collision with root package name */
    public Object f1215b;

    /* renamed from: c  reason: collision with root package name */
    public Object f1216c;

    /* renamed from: d  reason: collision with root package name */
    public Object f1217d;

    /* JADX WARN: Type inference failed for: r1v1, types: [J0.h, java.lang.Object] */
    public static synchronized h a(Context context, O0.a aVar) {
        h hVar;
        synchronized (h.class) {
            try {
                if (f1213e == null) {
                    ?? obj = new Object();
                    Context applicationContext = context.getApplicationContext();
                    obj.f1214a = new c(applicationContext, aVar);
                    obj.f1215b = new c(applicationContext, aVar);
                    obj.f1216c = new f(applicationContext, aVar);
                    obj.f1217d = new c(applicationContext, aVar);
                    f1213e = obj;
                }
                hVar = f1213e;
            } catch (Throwable th) {
                throw th;
            }
        }
        return hVar;
    }
}
