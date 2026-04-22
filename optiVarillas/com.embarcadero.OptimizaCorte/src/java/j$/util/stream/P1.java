package j$.util.stream;

import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class P1 implements V1, InterfaceC0589o2 {

    /* renamed from: a  reason: collision with root package name */
    private int f4355a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f4356b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ IntBinaryOperator f4357c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public P1(int i4, IntBinaryOperator intBinaryOperator) {
        this.f4356b = i4;
        this.f4357c = intBinaryOperator;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        this.f4355a = this.f4357c.applyAsInt(this.f4355a, i4);
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
        accept(((P1) v12).f4355a);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        return Integer.valueOf(this.f4355a);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final void l(long j4) {
        this.f4355a = this.f4356b;
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
