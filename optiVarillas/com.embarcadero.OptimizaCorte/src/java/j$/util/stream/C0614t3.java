package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.util.stream.t3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0614t3 extends AbstractC0550g3 implements j$.util.M {
    /* JADX WARN: Type inference failed for: r0v0, types: [j$.util.stream.Z2, java.lang.Object, j$.util.stream.d, java.util.function.LongConsumer] */
    @Override // j$.util.stream.AbstractC0550g3
    final void d() {
        ?? z22 = new Z2();
        this.f4516h = z22;
        Objects.requireNonNull(z22);
        this.f4514e = this.f4511b.S(new C0609s3(z22, 0));
        this.f = new C0516a(this, 4);
    }

    @Override // j$.util.stream.AbstractC0550g3
    final AbstractC0550g3 e(Spliterator spliterator) {
        return new AbstractC0550g3(this.f4511b, spliterator, this.f4510a);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        j$.util.D.c(this, consumer);
    }

    @Override // j$.util.P
    public final void forEachRemaining(LongConsumer longConsumer) {
        if (this.f4516h != null || this.f4517i) {
            do {
            } while (tryAdvance(longConsumer));
            return;
        }
        Objects.requireNonNull(longConsumer);
        c();
        Objects.requireNonNull(longConsumer);
        C0609s3 c0609s3 = new C0609s3(longConsumer, 1);
        this.f4511b.R(this.f4513d, c0609s3);
        this.f4517i = true;
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return j$.util.D.h(this, consumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(LongConsumer longConsumer) {
        Objects.requireNonNull(longConsumer);
        boolean a4 = a();
        if (a4) {
            X2 x22 = (X2) this.f4516h;
            long j4 = this.f4515g;
            int t3 = x22.t(j4);
            longConsumer.accept((x22.f4467c == 0 && t3 == 0) ? ((long[]) x22.f4444e)[(int) j4] : ((long[][]) x22.f)[t3][(int) (j4 - x22.f4468d[t3])]);
        }
        return a4;
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final j$.util.M trySplit() {
        return (j$.util.M) super.trySplit();
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final j$.util.P trySplit() {
        return (j$.util.M) super.trySplit();
    }

    @Override // j$.util.stream.AbstractC0550g3, j$.util.Spliterator
    public final Spliterator trySplit() {
        return (j$.util.M) super.trySplit();
    }
}
