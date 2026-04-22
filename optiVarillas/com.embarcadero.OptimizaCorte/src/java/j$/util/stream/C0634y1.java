package j$.util.stream;

import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.y1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0634y1 extends C0520a3 implements L0, D0 {
    @Override // j$.util.stream.D0
    public final L0 a() {
        return this;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(int i4) {
        AbstractC0637z0.k();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final /* synthetic */ void accept(long j4) {
        AbstractC0637z0.l();
        throw null;
    }

    @Override // j$.util.stream.L0
    public final L0 b(int i4) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ L0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.w(this, j4, j5, intFunction);
    }

    @Override // j$.util.stream.L0
    public final void i(Object[] objArr, int i4) {
        long j4 = i4;
        long count = count() + j4;
        if (count > objArr.length || count < j4) {
            throw new IndexOutOfBoundsException("does not fit");
        }
        if (this.f4467c == 0) {
            System.arraycopy(this.f4447e, 0, objArr, i4, this.f4466b);
            return;
        }
        for (int i5 = 0; i5 < this.f4467c; i5++) {
            Object[] objArr2 = this.f[i5];
            System.arraycopy(objArr2, 0, objArr, i4, objArr2.length);
            i4 += this.f[i5].length;
        }
        int i6 = this.f4466b;
        if (i6 > 0) {
            System.arraycopy(this.f4447e, 0, objArr, i4, i6);
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        clear();
        r(j4);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }

    @Override // j$.util.stream.L0
    public final Object[] o(IntFunction intFunction) {
        long count = count();
        if (count < 2147483639) {
            Object[] objArr = (Object[]) intFunction.apply((int) count);
            i(objArr, 0);
            return objArr;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ int q() {
        return 0;
    }
}
