package j$.util.stream;

import j$.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class U2 extends Y2 implements j$.util.J {

    /* renamed from: g  reason: collision with root package name */
    final /* synthetic */ V2 f4423g;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public U2(V2 v22, int i4, int i5, int i6, int i7) {
        super(v22, i4, i5, i6, i7);
        this.f4423g = v22;
    }

    @Override // j$.util.stream.Y2
    final void a(int i4, Object obj, Object obj2) {
        ((IntConsumer) obj2).accept(((int[]) obj)[i4]);
    }

    @Override // j$.util.stream.Y2
    final j$.util.P b(Object obj, int i4, int i5) {
        return Spliterators.k((int[]) obj, i4, i5 + i4);
    }

    @Override // j$.util.stream.Y2
    final j$.util.P c(int i4, int i5, int i6, int i7) {
        return new U2(this.f4423g, i4, i5, i6, i7);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        j$.util.D.b(this, consumer);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return j$.util.D.g(this, consumer);
    }
}
