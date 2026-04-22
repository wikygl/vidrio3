package j$.util.stream;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class G2 extends C2 {

    /* renamed from: c  reason: collision with root package name */
    private V2 f4288c;

    @Override // j$.util.stream.InterfaceC0589o2, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        this.f4288c.accept(i4);
    }

    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public final void k() {
        int[] iArr = (int[]) this.f4288c.e();
        Arrays.sort(iArr);
        InterfaceC0599q2 interfaceC0599q2 = this.f4534a;
        interfaceC0599q2.l(iArr.length);
        int i4 = 0;
        if (this.f4261b) {
            int length = iArr.length;
            while (i4 < length) {
                int i5 = iArr[i4];
                if (interfaceC0599q2.n()) {
                    break;
                }
                interfaceC0599q2.accept(i5);
                i4++;
            }
        } else {
            int length2 = iArr.length;
            while (i4 < length2) {
                interfaceC0599q2.accept(iArr[i4]);
                i4++;
            }
        }
        interfaceC0599q2.k();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [j$.util.stream.V2] */
    /* JADX WARN: Type inference failed for: r0v5 */
    /* JADX WARN: Type inference failed for: r0v6 */
    @Override // j$.util.stream.AbstractC0569k2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4288c = j4 > 0 ? new Z2((int) j4) : new Z2();
    }
}
