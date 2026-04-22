package j$.util.stream;

import java.util.Arrays;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.g1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0548g1 extends O0 implements D0 {
    @Override // j$.util.stream.D0
    public final L0 a() {
        int i4 = this.f4346b;
        Object[] objArr = this.f4345a;
        if (i4 >= objArr.length) {
            return this;
        }
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", Integer.valueOf(this.f4346b), Integer.valueOf(objArr.length)));
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

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        int i4 = this.f4346b;
        Object[] objArr = this.f4345a;
        if (i4 >= objArr.length) {
            throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", Integer.valueOf(objArr.length)));
        }
        this.f4346b = 1 + i4;
        objArr[i4] = obj;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void k() {
        int i4 = this.f4346b;
        Object[] objArr = this.f4345a;
        if (i4 < objArr.length) {
            throw new IllegalStateException(String.format("End size %d is less than fixed size %d", Integer.valueOf(this.f4346b), Integer.valueOf(objArr.length)));
        }
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        Object[] objArr = this.f4345a;
        if (j4 != objArr.length) {
            throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", Long.valueOf(j4), Integer.valueOf(objArr.length)));
        }
        this.f4346b = 0;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }

    @Override // j$.util.stream.O0
    public final String toString() {
        Object[] objArr = this.f4345a;
        return String.format("FixedNodeBuilder[%d][%s]", Integer.valueOf(objArr.length - this.f4346b), Arrays.toString(objArr));
    }
}
