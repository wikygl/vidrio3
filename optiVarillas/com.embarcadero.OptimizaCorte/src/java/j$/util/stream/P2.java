package j$.util.stream;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class P2 extends D2 {

    /* renamed from: c  reason: collision with root package name */
    private long[] f4358c;

    /* renamed from: d  reason: collision with root package name */
    private int f4359d;

    @Override // j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        long[] jArr = this.f4358c;
        int i4 = this.f4359d;
        this.f4359d = i4 + 1;
        jArr[i4] = j4;
    }

    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public final void k() {
        int i4 = 0;
        Arrays.sort(this.f4358c, 0, this.f4359d);
        InterfaceC0599q2 interfaceC0599q2 = this.f4537a;
        interfaceC0599q2.l(this.f4359d);
        if (this.f4266b) {
            while (i4 < this.f4359d && !interfaceC0599q2.n()) {
                interfaceC0599q2.accept(this.f4358c[i4]);
                i4++;
            }
        } else {
            while (i4 < this.f4359d) {
                interfaceC0599q2.accept(this.f4358c[i4]);
                i4++;
            }
        }
        interfaceC0599q2.k();
        this.f4358c = null;
    }

    @Override // j$.util.stream.AbstractC0574l2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4358c = new long[(int) j4];
    }
}
