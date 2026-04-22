package j$.util.stream;

import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class T1 implements V1, InterfaceC0594p2 {

    /* renamed from: a  reason: collision with root package name */
    private long f4406a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long f4407b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ LongBinaryOperator f4408c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public T1(long j4, LongBinaryOperator longBinaryOperator) {
        this.f4407b = j4;
        this.f4408c = longBinaryOperator;
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
        this.f4406a = this.f4408c.applyAsLong(this.f4406a, j4);
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
        accept(((T1) v12).f4406a);
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        return Long.valueOf(this.f4406a);
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
        this.f4406a = this.f4407b;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ boolean n() {
        return false;
    }
}
