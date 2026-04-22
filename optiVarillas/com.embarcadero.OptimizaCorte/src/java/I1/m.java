package i1;

import f1.C0412b;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class m {

    /* renamed from: a  reason: collision with root package name */
    public final C0412b f3650a;

    /* renamed from: b  reason: collision with root package name */
    public final byte[] f3651b;

    public m(C0412b c0412b, byte[] bArr) {
        if (c0412b != null) {
            if (bArr != null) {
                this.f3650a = c0412b;
                this.f3651b = bArr;
                return;
            }
            throw new NullPointerException("bytes is null");
        }
        throw new NullPointerException("encoding is null");
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof m)) {
            return false;
        }
        m mVar = (m) obj;
        if (!this.f3650a.equals(mVar.f3650a)) {
            return false;
        }
        return Arrays.equals(this.f3651b, mVar.f3651b);
    }

    public final int hashCode() {
        return ((this.f3650a.hashCode() ^ 1000003) * 1000003) ^ Arrays.hashCode(this.f3651b);
    }

    public final String toString() {
        return "EncodedPayload{encoding=" + this.f3650a + ", bytes=[...]}";
    }
}
