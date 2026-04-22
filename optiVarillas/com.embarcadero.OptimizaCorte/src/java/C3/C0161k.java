package C3;

import java.util.concurrent.CancellationException;

/* renamed from: C3.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0161k {

    /* renamed from: a  reason: collision with root package name */
    public final Object f484a;

    /* renamed from: b  reason: collision with root package name */
    public final AbstractC0154d f485b;

    /* renamed from: c  reason: collision with root package name */
    public final u3.l<Throwable, l3.g> f486c;

    /* renamed from: d  reason: collision with root package name */
    public final Object f487d;

    /* renamed from: e  reason: collision with root package name */
    public final Throwable f488e;

    /* JADX WARN: Multi-variable type inference failed */
    public C0161k(Object obj, AbstractC0154d abstractC0154d, u3.l<? super Throwable, l3.g> lVar, Object obj2, Throwable th) {
        this.f484a = obj;
        this.f485b = abstractC0154d;
        this.f486c = lVar;
        this.f487d = obj2;
        this.f488e = th;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0161k)) {
            return false;
        }
        C0161k c0161k = (C0161k) obj;
        if (v3.h.a(this.f484a, c0161k.f484a) && v3.h.a(this.f485b, c0161k.f485b) && v3.h.a(this.f486c, c0161k.f486c) && v3.h.a(this.f487d, c0161k.f487d) && v3.h.a(this.f488e, c0161k.f488e)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int hashCode;
        int hashCode2;
        int hashCode3;
        int hashCode4;
        int i4 = 0;
        Object obj = this.f484a;
        if (obj == null) {
            hashCode = 0;
        } else {
            hashCode = obj.hashCode();
        }
        int i5 = hashCode * 31;
        AbstractC0154d abstractC0154d = this.f485b;
        if (abstractC0154d == null) {
            hashCode2 = 0;
        } else {
            hashCode2 = abstractC0154d.hashCode();
        }
        int i6 = (i5 + hashCode2) * 31;
        u3.l<Throwable, l3.g> lVar = this.f486c;
        if (lVar == null) {
            hashCode3 = 0;
        } else {
            hashCode3 = lVar.hashCode();
        }
        int i7 = (i6 + hashCode3) * 31;
        Object obj2 = this.f487d;
        if (obj2 == null) {
            hashCode4 = 0;
        } else {
            hashCode4 = obj2.hashCode();
        }
        int i8 = (i7 + hashCode4) * 31;
        Throwable th = this.f488e;
        if (th != null) {
            i4 = th.hashCode();
        }
        return i8 + i4;
    }

    public final String toString() {
        return "CompletedContinuation(result=" + this.f484a + ", cancelHandler=" + this.f485b + ", onCancellation=" + this.f486c + ", idempotentResume=" + this.f487d + ", cancelCause=" + this.f488e + ')';
    }

    public /* synthetic */ C0161k(Object obj, AbstractC0154d abstractC0154d, u3.l lVar, CancellationException cancellationException, int i4) {
        this(obj, (i4 & 2) != 0 ? null : abstractC0154d, (i4 & 4) != 0 ? null : lVar, (Object) null, (i4 & 16) != 0 ? null : cancellationException);
    }
}
