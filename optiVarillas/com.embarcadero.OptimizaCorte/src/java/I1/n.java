package i1;

import com.google.auto.value.AutoValue;
import i1.h;
import java.util.HashMap;
import java.util.Map;

@AutoValue
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class n {

    @AutoValue.Builder
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class a {
        public final void a(String str, String str2) {
            Map<String, String> map = ((h.a) this).f;
            if (map != null) {
                map.put(str, str2);
                return;
            }
            throw new IllegalStateException("Property \"autoMetadata\" has not been set");
        }
    }

    public final String a(String str) {
        String str2 = b().get(str);
        if (str2 == null) {
            return "";
        }
        return str2;
    }

    public abstract Map<String, String> b();

    public abstract Integer c();

    public abstract m d();

    public abstract long e();

    public final int f(String str) {
        String str2 = b().get(str);
        if (str2 == null) {
            return 0;
        }
        return Integer.valueOf(str2).intValue();
    }

    public abstract String g();

    public abstract long h();

    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object, i1.h$a] */
    public final h.a i() {
        ?? obj = new Object();
        String g4 = g();
        if (g4 != null) {
            obj.f3630a = g4;
            obj.f3631b = c();
            m d4 = d();
            if (d4 != null) {
                obj.f3632c = d4;
                obj.f3633d = Long.valueOf(e());
                obj.f3634e = Long.valueOf(h());
                obj.f = new HashMap(b());
                return obj;
            }
            throw new NullPointerException("Null encodedPayload");
        }
        throw new NullPointerException("Null transportName");
    }
}
