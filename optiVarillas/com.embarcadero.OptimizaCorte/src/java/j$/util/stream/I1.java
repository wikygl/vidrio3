package j$.util.stream;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class I1 extends W1 implements V1, InterfaceC0584n2 {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Supplier f4303b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ObjDoubleConsumer f4304c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ BinaryOperator f4305d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public I1(Supplier supplier, ObjDoubleConsumer objDoubleConsumer, BinaryOperator binaryOperator) {
        this.f4303b = supplier;
        this.f4304c = objDoubleConsumer;
        this.f4305d = binaryOperator;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        this.f4304c.accept(this.f4429a, d4);
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
        this.f4429a = this.f4305d.apply(this.f4429a, ((I1) v12).f4429a);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4429a = this.f4303b.get();
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
