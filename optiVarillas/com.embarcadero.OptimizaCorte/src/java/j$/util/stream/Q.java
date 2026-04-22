package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.Consumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class Q extends S {

    /* renamed from: b  reason: collision with root package name */
    final Consumer f4364b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Q(Consumer consumer, boolean z4) {
        super(z4);
        this.f4364b = consumer;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f4364b.accept(obj);
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
}
