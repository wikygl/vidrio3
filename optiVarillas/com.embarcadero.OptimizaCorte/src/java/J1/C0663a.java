package j1;

import i1.n;
import java.util.ArrayList;
import java.util.Arrays;

/* renamed from: j1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0663a extends AbstractC0668f {

    /* renamed from: a  reason: collision with root package name */
    public final Iterable<n> f4740a;

    /* renamed from: b  reason: collision with root package name */
    public final byte[] f4741b;

    public C0663a() {
        throw null;
    }

    public C0663a(ArrayList arrayList, byte[] bArr) {
        this.f4740a = arrayList;
        this.f4741b = bArr;
    }

    @Override // j1.AbstractC0668f
    public final Iterable<n> a() {
        return this.f4740a;
    }

    @Override // j1.AbstractC0668f
    public final byte[] b() {
        return this.f4741b;
    }

    public final boolean equals(Object obj) {
        byte[] b4;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractC0668f)) {
            return false;
        }
        AbstractC0668f abstractC0668f = (AbstractC0668f) obj;
        if (this.f4740a.equals(abstractC0668f.a())) {
            if (abstractC0668f instanceof C0663a) {
                b4 = ((C0663a) abstractC0668f).f4741b;
            } else {
                b4 = abstractC0668f.b();
            }
            if (Arrays.equals(this.f4741b, b4)) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return ((this.f4740a.hashCode() ^ 1000003) * 1000003) ^ Arrays.hashCode(this.f4741b);
    }

    public final String toString() {
        return "BackendRequest{events=" + this.f4740a + ", extras=" + Arrays.toString(this.f4741b) + "}";
    }
}
