package s3;

import w3.b;
import w3.c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class a extends r3.a {

    /* renamed from: s3.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class C0070a {

        /* renamed from: a  reason: collision with root package name */
        public static final Integer f5777a;

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
                f5777a = num2;
            }
            num = null;
            if (num != null) {
                num2 = num;
            }
            f5777a = num2;
        }
    }

    @Override // q3.a
    public final c b() {
        Integer num = C0070a.f5777a;
        if (num != null && num.intValue() < 34) {
            return new b();
        }
        return new c();
    }
}
