package j$.util.stream;

import java.util.function.LongConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class Z1 extends AbstractC0524b2 implements InterfaceC0594p2 {
    @Override // j$.util.stream.AbstractC0524b2, j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        this.f4459b++;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        j((Long) obj);
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        this.f4459b += ((AbstractC0524b2) v12).f4459b;
    }

    @Override // j$.util.stream.W1, java.util.function.Supplier
    public final Object get() {
        return Long.valueOf(this.f4459b);
    }

    @Override // j$.util.stream.InterfaceC0594p2
    public final /* synthetic */ void j(Long l2) {
        AbstractC0637z0.i(this, l2);
    }
}
