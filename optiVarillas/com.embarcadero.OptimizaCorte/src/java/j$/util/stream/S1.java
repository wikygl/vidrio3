package j$.util.stream;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class S1 extends W1 implements V1, InterfaceC0589o2 {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Supplier f4389b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ObjIntConsumer f4390c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ BinaryOperator f4391d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public S1(Supplier supplier, ObjIntConsumer objIntConsumer, BinaryOperator binaryOperator) {
        this.f4389b = supplier;
        this.f4390c = objIntConsumer;
        this.f4391d = binaryOperator;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        this.f4390c.accept(this.f4429a, i4);
    }

    @Override // j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final /* synthetic */ void accept(long j4) {
        AbstractC0637z0.l();
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        m((Integer) obj);
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        this.f4429a = this.f4391d.apply(this.f4429a, ((S1) v12).f4429a);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4429a = this.f4389b.get();
    }

    @Override // j$.util.stream.InterfaceC0589o2
    public final /* synthetic */ void m(Integer num) {
        AbstractC0637z0.g(this, num);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
