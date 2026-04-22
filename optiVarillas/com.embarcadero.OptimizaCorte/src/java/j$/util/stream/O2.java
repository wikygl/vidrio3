package j$.util.stream;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class O2 extends C2 {

    /* renamed from: c  reason: collision with root package name */
    private int[] f4350c;

    /* renamed from: d  reason: collision with root package name */
    private int f4351d;

    @Override // j$.util.stream.InterfaceC0589o2, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        int[] iArr = this.f4350c;
        int i5 = this.f4351d;
        this.f4351d = i5 + 1;
        iArr[i5] = i4;
    }

    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public final void k() {
        int i4 = 0;
        Arrays.sort(this.f4350c, 0, this.f4351d);
        InterfaceC0599q2 interfaceC0599q2 = this.f4534a;
        interfaceC0599q2.l(this.f4351d);
        if (this.f4261b) {
            while (i4 < this.f4351d && !interfaceC0599q2.n()) {
                interfaceC0599q2.accept(this.f4350c[i4]);
                i4++;
            }
        } else {
            while (i4 < this.f4351d) {
                interfaceC0599q2.accept(this.f4350c[i4]);
                i4++;
            }
        }
        interfaceC0599q2.k();
        this.f4350c = null;
    }

    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4350c = new int[(int) j4];
    }
}
