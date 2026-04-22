package j$.util.stream;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class H2 extends D2 {

    /* renamed from: c  reason: collision with root package name */
    private X2 f4297c;

    @Override // j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        this.f4297c.accept(j4);
    }

    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public final void k() {
        long[] jArr = (long[]) this.f4297c.e();
        Arrays.sort(jArr);
        InterfaceC0599q2 interfaceC0599q2 = this.f4537a;
        interfaceC0599q2.l(jArr.length);
        int i4 = 0;
        if (this.f4266b) {
            int length = jArr.length;
            while (i4 < length) {
                long j4 = jArr[i4];
                if (interfaceC0599q2.n()) {
                    break;
                }
                interfaceC0599q2.accept(j4);
                i4++;
            }
        } else {
            int length2 = jArr.length;
            while (i4 < length2) {
                interfaceC0599q2.accept(jArr[i4]);
                i4++;
            }
        }
        interfaceC0599q2.k();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [j$.util.stream.X2] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4297c = j4 > 0 ? new Z2((int) j4) : new Z2();
    }
}
