package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.IntConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class O extends S implements InterfaceC0589o2 {

    /* renamed from: b  reason: collision with root package name */
    final IntConsumer f4344b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public O(IntConsumer intConsumer, boolean z4) {
        super(z4);
        this.f4344b = intConsumer;
    }

    @Override // j$.util.stream.S, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        this.f4344b.accept(i4);
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        m((Integer) obj);
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // j$.util.stream.K3
    public final Object b(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        abstractC0521b.R(spliterator, this);
        return null;
    }

    @Override // j$.util.stream.K3
    public final /* bridge */ /* synthetic */ Object c(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        e(abstractC0521b, spliterator);
        return null;
    }

    @Override // java.util.function.Supplier
    public final /* bridge */ /* synthetic */ Object get() {
        return null;
    }

    @Override // j$.util.stream.InterfaceC0589o2
    public final /* synthetic */ void m(Integer num) {
        AbstractC0637z0.g(this, num);
    }
}
