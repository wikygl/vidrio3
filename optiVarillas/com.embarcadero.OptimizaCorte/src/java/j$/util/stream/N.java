package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.DoubleConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class N extends S implements InterfaceC0584n2 {

    /* renamed from: b  reason: collision with root package name */
    final DoubleConsumer f4334b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public N(DoubleConsumer doubleConsumer, boolean z4) {
        super(z4);
        this.f4334b = doubleConsumer;
    }

    @Override // j$.util.stream.S, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        this.f4334b.accept(d4);
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        p((Double) obj);
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
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

    @Override // j$.util.stream.InterfaceC0584n2
    public final /* synthetic */ void p(Double d4) {
        AbstractC0637z0.e(this, d4);
    }
}
