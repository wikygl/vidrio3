package j$.util.stream;

import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class Q2 extends E2 {

    /* renamed from: d  reason: collision with root package name */
    private Object[] f4368d;

    /* renamed from: e  reason: collision with root package name */
    private int f4369e;

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        Object[] objArr = this.f4368d;
        int i4 = this.f4369e;
        this.f4369e = i4 + 1;
        objArr[i4] = obj;
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final void k() {
        int i4 = 0;
        Arrays.sort(this.f4368d, 0, this.f4369e, this.f4272b);
        InterfaceC0599q2 interfaceC0599q2 = this.f4545a;
        interfaceC0599q2.l(this.f4369e);
        if (this.f4273c) {
            while (i4 < this.f4369e && !interfaceC0599q2.n()) {
                interfaceC0599q2.accept((InterfaceC0599q2) this.f4368d[i4]);
                i4++;
            }
        } else {
            while (i4 < this.f4369e) {
                interfaceC0599q2.accept((InterfaceC0599q2) this.f4368d[i4]);
                i4++;
            }
        }
        interfaceC0599q2.k();
        this.f4368d = null;
    }

    @Override // j$.util.stream.AbstractC0579m2, j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        if (j4 >= 2147483639) {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
        this.f4368d = new Object[(int) j4];
    }
}
