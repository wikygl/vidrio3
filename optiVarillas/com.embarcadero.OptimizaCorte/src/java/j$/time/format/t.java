package j$.time.format;

import j$.util.concurrent.ConcurrentHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class t {

    /* renamed from: a  reason: collision with root package name */
    public static final t f3962a = new Object();

    /* JADX WARN: Type inference failed for: r0v0, types: [j$.time.format.t, java.lang.Object] */
    static {
        new ConcurrentHashMap(16, 0.75f, 2);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof t) {
            ((t) obj).getClass();
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return 182;
    }

    public final String toString() {
        return "DecimalStyle[0+-.]";
    }
}
