package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.util.stream.p3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0595p3 extends AbstractC0550g3 implements j$.util.G {
    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.stream.Z2, java.lang.Object, java.util.function.DoubleConsumer, j$.util.stream.d] */
    @Override // j$.util.stream.AbstractC0550g3
    final void d() {
        ?? z22 = new Z2();
        this.f4516h = z22;
        Objects.requireNonNull(z22);
        this.f4514e = this.f4511b.S(new C0590o3(z22, 0));
        this.f = new C0516a(this, 2);
    }

    @Override // j$.util.stream.AbstractC0550g3
    final AbstractC0550g3 e(Spliterator spliterator) {
        return new AbstractC0550g3(this.f4511b, spliterator, this.f4510a);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        j$.util.D.a(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(DoubleConsumer doubleConsumer) {
        if (this.f4516h != null || this.f4517i) {
            do {
            } while (tryAdvance(doubleConsumer));
            return;
        }
        Objects.requireNonNull(doubleConsumer);
        c();
        Objects.requireNonNull(doubleConsumer);
        C0590o3 c0590o3 = new C0590o3(doubleConsumer, 1);
        this.f4511b.R(this.f4513d, c0590o3);
        this.f4517i = true;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return j$.util.D.f(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(DoubleConsumer doubleConsumer) {
        Objects.requireNonNull(doubleConsumer);
        boolean a4 = a();
        if (a4) {
            T2 t22 = (T2) this.f4516h;
            long j4 = this.f4515g;
            int t3 = t22.t(j4);
            doubleConsumer.accept((t22.f4467c == 0 && t3 == 0) ? ((double[]) t22.f4444e)[(int) j4] : ((double[][]) t22.f)[t3][(int) (j4 - t22.f4468d[t3])]);
        }
        return a4;
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final j$.util.G trySplit() {
        return (j$.util.G) super.trySplit();
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final j$.util.P trySplit() {
        return (j$.util.G) super.trySplit();
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final Spliterator trySplit() {
        return (j$.util.G) super.trySplit();
    }
}
