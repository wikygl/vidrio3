package e3;

import java.io.OutputStream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class b extends OutputStream {

    /* renamed from: j  reason: collision with root package name */
    public long f3360j;

    @Override // java.io.OutputStream
    public final void write(int i4) {
        this.f3360j++;
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr) {
        this.f3360j += bArr.length;
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr, int i4, int i5) {
        int i6;
        if (i4 >= 0 && i4 <= bArr.length && i5 >= 0 && (i6 = i4 + i5) <= bArr.length && i6 >= 0) {
            this.f3360j += i5;
            return;
        }
        throw new IndexOutOfBoundsException();
    }
}
