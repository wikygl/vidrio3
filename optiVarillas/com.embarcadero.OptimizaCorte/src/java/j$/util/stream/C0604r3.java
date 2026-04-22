package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.util.stream.r3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0604r3 extends AbstractC0550g3 implements j$.util.J {
    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.stream.Z2, java.util.function.IntConsumer, java.lang.Object, j$.util.stream.d] */
    @Override // j$.util.stream.AbstractC0550g3
    final void d() {
        ?? z22 = new Z2();
        this.f4516h = z22;
        Objects.requireNonNull(z22);
        this.f4514e = this.f4511b.S(new C0600q3(z22, 0));
        this.f = new C0516a(this, 3);
    }

    @Override // j$.util.stream.AbstractC0550g3
    final AbstractC0550g3 e(Spliterator spliterator) {
        return new AbstractC0550g3(this.f4511b, spliterator, this.f4510a);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        j$.util.D.b(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(IntConsumer intConsumer) {
        if (this.f4516h != null || this.f4517i) {
            do {
            } while (tryAdvance(intConsumer));
            return;
        }
        Objects.requireNonNull(intConsumer);
        c();
        Objects.requireNonNull(intConsumer);
        C0600q3 c0600q3 = new C0600q3(intConsumer, 1);
        this.f4511b.R(this.f4513d, c0600q3);
        this.f4517i = true;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return j$.util.D.g(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(IntConsumer intConsumer) {
        Objects.requireNonNull(intConsumer);
        boolean a4 = a();
        if (a4) {
            V2 v22 = (V2) this.f4516h;
            long j4 = this.f4515g;
            int t3 = v22.t(j4);
            intConsumer.accept((v22.f4467c == 0 && t3 == 0) ? ((int[]) v22.f4444e)[(int) j4] : ((int[][]) v22.f)[t3][(int) (j4 - v22.f4468d[t3])]);
        }
        return a4;
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final j$.util.J trySplit() {
        return (j$.util.J) super.trySplit();
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final j$.util.P trySplit() {
        return (j$.util.J) super.trySplit();
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final Spliterator trySplit() {
        return (j$.util.J) super.trySplit();
    }
}
