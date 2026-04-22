package j$.util.stream;

import j$.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class S2 extends Y2 implements j$.util.G {

    /* renamed from: g  reason: collision with root package name */
    final /* synthetic */ T2 f4392g;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public S2(T2 t22, int i4, int i5, int i6, int i7) {
        super(t22, i4, i5, i6, i7);
        this.f4392g = t22;
    }

    @Override // j$.util.stream.Y2
    final void a(int i4, Object obj, Object obj2) {
        ((DoubleConsumer) obj2).accept(((double[]) obj)[i4]);
    }

    @Override // j$.util.stream.Y2
    final j$.util.P b(Object obj, int i4, int i5) {
        return Spliterators.j((double[]) obj, i4, i5 + i4);
    }

    @Override // j$.util.stream.Y2
    final j$.util.P c(int i4, int i5, int i6, int i7) {
        return new S2(this.f4392g, i4, i5, i6, i7);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ void forEachRemaining(Consumer consumer) {
        j$.util.D.a(this, consumer);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return j$.util.D.f(this, consumer);
    }
}
