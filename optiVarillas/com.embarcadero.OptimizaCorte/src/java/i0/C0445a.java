package i0;

/* renamed from: i0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0445a {

    /* renamed from: a  reason: collision with root package name */
    public final String f3593a;

    /* renamed from: b  reason: collision with root package name */
    public final boolean f3594b;

    /* renamed from: i0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class C0053a {
    }

    public C0445a() {
        this("", false);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0445a)) {
            return false;
        }
        C0445a c0445a = (C0445a) obj;
        if (v3.h.a(this.f3593a, c0445a.f3593a) && this.f3594b == c0445a.f3594b) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int i4;
        int hashCode = this.f3593a.hashCode() * 31;
        if (this.f3594b) {
            i4 = 1231;
        } else {
            i4 = 1237;
        }
        return hashCode + i4;
    }

    public final String toString() {
        return "GetTopicsRequest: adsSdkName=" + this.f3593a + ", shouldRecordObservation=" + this.f3594b;
    }

    public C0445a(String str, boolean z4) {
        v3.h.e(str, "adsSdkName");
        this.f3593a = str;
        this.f3594b = z4;
    }
}
