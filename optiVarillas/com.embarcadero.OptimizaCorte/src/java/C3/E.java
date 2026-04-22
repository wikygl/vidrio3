package C3;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class E {

    /* renamed from: a  reason: collision with root package name */
    public static final H f428a;

    static {
        String str;
        boolean z4;
        H h4;
        int i4 = F3.w.f946a;
        try {
            str = System.getProperty("kotlinx.coroutines.main.delay");
        } catch (SecurityException unused) {
            str = null;
        }
        if (str != null) {
            z4 = Boolean.parseBoolean(str);
        } else {
            z4 = false;
        }
        if (!z4) {
            h4 = D.f426r;
        } else {
            G3.c cVar = K.f431a;
            g0 g0Var = F3.q.f944a;
            g0Var.getClass();
            if (!(g0Var instanceof H)) {
                h4 = D.f426r;
            } else {
                h4 = (H) g0Var;
            }
        }
        f428a = h4;
    }
}
