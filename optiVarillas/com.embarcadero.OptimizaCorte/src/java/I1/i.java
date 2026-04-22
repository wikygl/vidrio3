package i1;

import C3.C;
import f1.AbstractC0413c;
import f1.C0411a;
import f1.C0412b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class i extends r {

    /* renamed from: a  reason: collision with root package name */
    public final s f3635a;

    /* renamed from: b  reason: collision with root package name */
    public final String f3636b;

    /* renamed from: c  reason: collision with root package name */
    public final AbstractC0413c<?> f3637c;

    /* renamed from: d  reason: collision with root package name */
    public final C f3638d;

    /* renamed from: e  reason: collision with root package name */
    public final C0412b f3639e;

    public i(s sVar, String str, C0411a c0411a, C c4, C0412b c0412b) {
        this.f3635a = sVar;
        this.f3636b = str;
        this.f3637c = c0411a;
        this.f3638d = c4;
        this.f3639e = c0412b;
    }

    @Override // i1.r
    public final C0412b a() {
        return this.f3639e;
    }

    @Override // i1.r
    public final AbstractC0413c<?> b() {
        return this.f3637c;
    }

    @Override // i1.r
    public final C c() {
        return this.f3638d;
    }

    @Override // i1.r
    public final s d() {
        return this.f3635a;
    }

    @Override // i1.r
    public final String e() {
        return this.f3636b;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof r)) {
            return false;
        }
        r rVar = (r) obj;
        if (this.f3635a.equals(rVar.d()) && this.f3636b.equals(rVar.e()) && this.f3637c.equals(rVar.b()) && this.f3638d.equals(rVar.c()) && this.f3639e.equals(rVar.a())) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return ((((((((this.f3635a.hashCode() ^ 1000003) * 1000003) ^ this.f3636b.hashCode()) * 1000003) ^ this.f3637c.hashCode()) * 1000003) ^ this.f3638d.hashCode()) * 1000003) ^ this.f3639e.hashCode();
    }

    public final String toString() {
        return "SendRequest{transportContext=" + this.f3635a + ", transportName=" + this.f3636b + ", event=" + this.f3637c + ", transformer=" + this.f3638d + ", encoding=" + this.f3639e + "}";
    }
}
