package i1;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class j extends s {

    /* renamed from: a  reason: collision with root package name */
    public final String f3640a;

    /* renamed from: b  reason: collision with root package name */
    public final byte[] f3641b;

    /* renamed from: c  reason: collision with root package name */
    public final f1.d f3642c;

    public j(String str, byte[] bArr, f1.d dVar) {
        this.f3640a = str;
        this.f3641b = bArr;
        this.f3642c = dVar;
    }

    @Override // i1.s
    public final String a() {
        return this.f3640a;
    }

    @Override // i1.s
    public final byte[] b() {
        return this.f3641b;
    }

    @Override // i1.s
    public final f1.d c() {
        return this.f3642c;
    }

    public final boolean equals(Object obj) {
        byte[] b4;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof s)) {
            return false;
        }
        s sVar = (s) obj;
        if (this.f3640a.equals(sVar.a())) {
            if (sVar instanceof j) {
                b4 = ((j) sVar).f3641b;
            } else {
                b4 = sVar.b();
            }
            if (Arrays.equals(this.f3641b, b4) && this.f3642c.equals(sVar.c())) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return ((((this.f3640a.hashCode() ^ 1000003) * 1000003) ^ Arrays.hashCode(this.f3641b)) * 1000003) ^ this.f3642c.hashCode();
    }
}
