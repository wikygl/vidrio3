package j$.util.stream;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class N2 extends B2 {

    /* renamed from: c  reason: collision with root package name */
    private double[] f4341c;

    /* renamed from: d  reason: collision with root package name */
    private int f4342d;

    @Override // j$.util.stream.InterfaceC0584n2, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        double[] dArr = this.f4341c;
        int i4 = this.f4342d;
        this.f4342d = i4 + 1;
        dArr[i4] = d4;
    }

    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public final void k() {
        int i4 = 0;
        Arrays.sort(this.f4341c, 0, this.f4342d);
        InterfaceC0599q2 interfaceC0599q2 = this.f4530a;
        interfaceC0599q2.l(this.f4342d);
        if (this.f4257b) {
            while (i4 < this.f4342d && !interfaceC0599q2.n()) {
                interfaceC0599q2.accept(this.f4341c[i4]);
                i4++;
            }
        } else {
            while (i4 < this.f4342d) {
                interfaceC0599q2.accept(this.f4341c[i4]);
                i4++;
            }
        }
        interfaceC0599q2.k();
        this.f4341c = null;
    }

    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4341c = new double[(int) j4];
    }
}
