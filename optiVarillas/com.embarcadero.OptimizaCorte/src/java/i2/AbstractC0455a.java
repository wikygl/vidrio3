package i2;

import android.app.Application;
import android.content.Context;

/* renamed from: i2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0455a {

    /* renamed from: a  reason: collision with root package name */
    public static C0459e f3708a;

    public static AbstractC0455a a(Context context) {
        C0459e c0459e;
        synchronized (AbstractC0455a.class) {
            try {
                if (f3708a == null) {
                    Application application = (Application) context.getApplicationContext();
                    application.getClass();
                    f3708a = new C0459e(application);
                }
                c0459e = f3708a;
            } catch (Throwable th) {
                throw th;
            }
        }
        return c0459e;
    }

    public abstract c0 b();

    public abstract C0469o c();
}
