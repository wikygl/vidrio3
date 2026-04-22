package b2;

import android.content.Context;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class c {

    /* renamed from: b  reason: collision with root package name */
    public static final c f2925b;

    /* renamed from: a  reason: collision with root package name */
    public b f2926a;

    /* JADX WARN: Type inference failed for: r0v0, types: [b2.c, java.lang.Object] */
    static {
        ?? obj = new Object();
        obj.f2926a = null;
        f2925b = obj;
    }

    public static b a(Context context) {
        b bVar;
        c cVar = f2925b;
        synchronized (cVar) {
            try {
                if (cVar.f2926a == null) {
                    if (context.getApplicationContext() != null) {
                        context = context.getApplicationContext();
                    }
                    cVar.f2926a = new b(context);
                }
                bVar = cVar.f2926a;
            } catch (Throwable th) {
                throw th;
            }
        }
        return bVar;
    }
}
