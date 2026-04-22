package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.a1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0518a1 extends T2 implements F0, A0 {
    @Override // j$.util.stream.A0, j$.util.stream.D0
    public final F0 a() {
        return this;
    }

    @Override // j$.util.stream.D0
    public final L0 a() {
        return this;
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
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        p((Double) obj);
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.K0, j$.util.stream.L0
    public final K0 b(int i4) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.L0
    public final /* bridge */ /* synthetic */ L0 b(int i4) {
        b(i4);
        throw null;
    }

    @Override // j$.util.stream.Z2, j$.util.stream.K0
    public final void d(Object obj, int i4) {
        super.d((double[]) obj, i4);
    }

    @Override // j$.util.stream.Z2, j$.util.stream.K0
    public final Object e() {
        return (double[]) super.e();
    }

    @Override // j$.util.stream.Z2, j$.util.stream.K0
    public final void f(Object obj) {
        super.f((DoubleConsumer) obj);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        clear();
        u(j4);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ Object[] o(IntFunction intFunction) {
        return AbstractC0637z0.m(this, intFunction);
    }

    @Override // j$.util.stream.InterfaceC0584n2
    public final /* synthetic */ void p(Double d4) {
        AbstractC0637z0.e(this, d4);
    }

    @Override // j$.util.stream.L0
    public final /* synthetic */ int q() {
        return 0;
    }

    @Override // j$.util.stream.T2, j$.util.stream.Z2, java.lang.Iterable
    public final j$.util.P spliterator() {
        return super.spliterator();
    }

    @Override // j$.util.stream.T2, j$.util.stream.Z2, java.lang.Iterable
    public final Spliterator spliterator() {
        return super.spliterator();
    }

    @Override // j$.util.stream.L0
    /* renamed from: y */
    public final /* synthetic */ void i(Double[] dArr, int i4) {
        AbstractC0637z0.n(this, dArr, i4);
    }

    @Override // j$.util.stream.L0
    /* renamed from: z */
    public final /* synthetic */ F0 h(long j4, long j5, IntFunction intFunction) {
        return AbstractC0637z0.t(this, j4, j5);
    }
}
