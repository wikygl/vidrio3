package j$.util.stream;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C1 extends W1 implements V1, InterfaceC0594p2 {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Supplier f4258b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ObjLongConsumer f4259c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ BinaryOperator f4260d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C1(Supplier supplier, ObjLongConsumer objLongConsumer, BinaryOperator binaryOperator) {
        this.f4258b = supplier;
        this.f4259c = objLongConsumer;
        this.f4260d = binaryOperator;
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
    public final void accept(long j4) {
        this.f4259c.accept(this.f4429a, j4);
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        j((Long) obj);
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        this.f4429a = this.f4260d.apply(this.f4429a, ((C1) v12).f4429a);
    }

    @Override // j$.util.stream.InterfaceC0594p2
    public final /* synthetic */ void j(Long l2) {
        AbstractC0637z0.i(this, l2);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4429a = this.f4258b.get();
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
