package r3;

import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class a extends q3.a {

    /* renamed from: r3.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class C0066a {

        /* renamed from: a  reason: collision with root package name */
        public static final Integer f5725a;

        static {
            Integer num;
            Object obj;
            Integer num2 = null;
            try {
                obj = Class.forName("android.os.Build$VERSION").getField("SDK_INT").get(null);
            } catch (Throwable unused) {
            }
            if (obj instanceof Integer) {
                num = (Integer) obj;
                if (num != null && num.intValue() > 0) {
                    num2 = num;
                }
                f5725a = num2;
            }
            num = null;
            if (num != null) {
                num2 = num;
            }
            f5725a = num2;
        }
    }

    @Override // q3.a
    public final void a(Throwable th, Throwable th2) {
        h.e(th, "cause");
        h.e(th2, "exception");
        Integer num = C0066a.f5725a;
        if (num != null && num.intValue() < 19) {
            super.a(th, th2);
        } else {
            th.addSuppressed(th2);
        }
    }
}
