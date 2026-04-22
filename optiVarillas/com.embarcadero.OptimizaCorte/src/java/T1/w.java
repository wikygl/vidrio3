package T1;

import android.content.Context;
import android.util.Log;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class w {

    /* renamed from: a  reason: collision with root package name */
    public static final q f2369a;

    /* renamed from: b  reason: collision with root package name */
    public static final r f2370b;

    /* renamed from: c  reason: collision with root package name */
    public static Context f2371c;

    /* JADX WARN: Type inference failed for: r0v2, types: [T1.u, T1.q] */
    /* JADX WARN: Type inference failed for: r0v3, types: [T1.u, T1.r] */
    static {
        new u(s.Z("0\u0082\u0005È0\u0082\u0003° \u0003\u0002\u0001\u0002\u0002\u0014\u0010\u008ae\bsù/\u008eQí"));
        new u(s.Z("0\u0082\u0006\u00040\u0082\u0003ì \u0003\u0002\u0001\u0002\u0002\u0014\u0003£²\u00ad×árÊkì"));
        f2369a = new u(s.Z("0\u0082\u0004C0\u0082\u0003+ \u0003\u0002\u0001\u0002\u0002\t\u0000Âà\u0087FdJ0\u008d0"));
        f2370b = new u(s.Z("0\u0082\u0004¨0\u0082\u0003\u0090 \u0003\u0002\u0001\u0002\u0002\t\u0000Õ\u0085¸l}ÓNõ0"));
    }

    public static synchronized void a(Context context) {
        synchronized (w.class) {
            if (f2371c == null) {
                if (context != null) {
                    f2371c = context.getApplicationContext();
                    return;
                }
                return;
            }
            Log.w("GoogleCertificates", "GoogleCertificates has been initialized already");
        }
    }
}
