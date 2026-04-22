package j$.util.stream;

import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class E1 implements V1, InterfaceC0584n2 {

    /* renamed from: a  reason: collision with root package name */
    private double f4269a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ double f4270b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ DoubleBinaryOperator f4271c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public E1(double d4, DoubleBinaryOperator doubleBinaryOperator) {
        this.f4270b = d4;
        this.f4271c = doubleBinaryOperator;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        this.f4269a = this.f4271c.applyAsDouble(this.f4269a, d4);
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

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        accept(((E1) v12).f4269a);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        return Double.valueOf(this.f4269a);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4269a = this.f4270b;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }

    @Override // j$.util.stream.InterfaceC0584n2
    public final /* synthetic */ void p(Double d4) {
        AbstractC0637z0.e(this, d4);
    }
}
