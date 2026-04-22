package j$.util.stream;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class F2 extends B2 {

    /* renamed from: c  reason: collision with root package name */
    private T2 f4278c;

    @Override // j$.util.stream.InterfaceC0584n2, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        this.f4278c.accept(d4);
    }

    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public final void k() {
        double[] dArr = (double[]) this.f4278c.e();
        Arrays.sort(dArr);
        InterfaceC0599q2 interfaceC0599q2 = this.f4530a;
        interfaceC0599q2.l(dArr.length);
        int i4 = 0;
        if (this.f4257b) {
            int length = dArr.length;
            while (i4 < length) {
                double d4 = dArr[i4];
                if (interfaceC0599q2.n()) {
                    break;
                }
                interfaceC0599q2.accept(d4);
                i4++;
            }
        } else {
            int length2 = dArr.length;
            while (i4 < length2) {
                interfaceC0599q2.accept(dArr[i4]);
                i4++;
            }
        }
        interfaceC0599q2.k();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [j$.util.stream.T2] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    @Override // j$.util.stream.AbstractC0564j2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4278c = j4 > 0 ? new Z2((int) j4) : new Z2();
    }
}
