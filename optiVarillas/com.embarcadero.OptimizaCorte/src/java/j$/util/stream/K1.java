package j$.util.stream;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class K1 extends W1 implements V1 {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Object f4316b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ BiFunction f4317c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ BinaryOperator f4318d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public K1(Object obj, BiFunction biFunction, BinaryOperator binaryOperator) {
        this.f4316b = obj;
        this.f4317c = biFunction;
        this.f4318d = binaryOperator;
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
        this.f4429a = this.f4317c.apply(this.f4429a, obj);
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        this.f4429a = this.f4318d.apply(this.f4429a, ((K1) v12).f4429a);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4429a = this.f4316b;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
