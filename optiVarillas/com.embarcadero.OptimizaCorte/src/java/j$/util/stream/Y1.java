package j$.util.stream;

import java.util.function.IntConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class Y1 extends AbstractC0524b2 implements InterfaceC0589o2 {
    @Override // j$.util.stream.AbstractC0524b2, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        this.f4459b++;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        m((Integer) obj);
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        this.f4459b += ((AbstractC0524b2) v12).f4459b;
    }

    @Override // j$.util.stream.W1, java.util.function.Supplier
    public final Object get() {
        return Long.valueOf(this.f4459b);
    }

    @Override // j$.util.stream.InterfaceC0589o2
    public final /* synthetic */ void m(Integer num) {
        AbstractC0637z0.g(this, num);
    }
}
