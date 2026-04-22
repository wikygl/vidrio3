package C3;

/* renamed from: C3.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0163m {

    /* renamed from: a  reason: collision with root package name */
    public final Object f491a;

    /* renamed from: b  reason: collision with root package name */
    public final u3.l<Throwable, l3.g> f492b;

    /* JADX WARN: Multi-variable type inference failed */
    public C0163m(Object obj, u3.l<? super Throwable, l3.g> lVar) {
        this.f491a = obj;
        this.f492b = lVar;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0163m)) {
            return false;
        }
        C0163m c0163m = (C0163m) obj;
        if (v3.h.a(this.f491a, c0163m.f491a) && v3.h.a(this.f492b, c0163m.f492b)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int hashCode;
        Object obj = this.f491a;
        if (obj == null) {
            hashCode = 0;
        } else {
            hashCode = obj.hashCode();
        }
        return this.f492b.hashCode() + (hashCode * 31);
    }

    public final String toString() {
        return "CompletedWithCancellation(result=" + this.f491a + ", onCancellation=" + this.f492b + ')';
    }
}
