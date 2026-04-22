package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class P extends S implements InterfaceC0594p2 {

    /* renamed from: b  reason: collision with root package name */
    final LongConsumer f4353b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public P(LongConsumer longConsumer, boolean z4) {
        super(z4);
        this.f4353b = longConsumer;
    }

    @Override // j$.util.stream.S, j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        this.f4353b.accept(j4);
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        j((Long) obj);
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
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

    @Override // j$.util.stream.InterfaceC0594p2
    public final /* synthetic */ void j(Long l2) {
        AbstractC0637z0.i(this, l2);
    }
}
