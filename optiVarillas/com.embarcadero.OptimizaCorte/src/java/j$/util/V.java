package j$.util;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class V implements InterfaceC0515s, DoubleConsumer, InterfaceC0507j {

    /* renamed from: a  reason: collision with root package name */
    boolean f4105a = false;

    /* renamed from: b  reason: collision with root package name */
    double f4106b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ G f4107c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public V(G g4) {
        this.f4107c = g4;
    }

    @Override // java.util.function.DoubleConsumer
    public final void accept(double d4) {
        this.f4105a = true;
        this.f4106b = d4;
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }

    @Override // j$.util.InterfaceC0515s, java.util.Iterator, j$.util.InterfaceC0507j
    public final void forEachRemaining(Consumer consumer) {
        if (consumer instanceof DoubleConsumer) {
            forEachRemaining((DoubleConsumer) consumer);
            return;
        }
        Objects.requireNonNull(consumer);
        if (g0.f4227a) {
            g0.a(V.class, "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
            throw null;
        }
        Objects.requireNonNull(consumer);
        forEachRemaining((DoubleConsumer) new C0513p(consumer));
    }

    @Override // j$.util.B
    public final void forEachRemaining(DoubleConsumer doubleConsumer) {
        Objects.requireNonNull(doubleConsumer);
        while (hasNext()) {
            doubleConsumer.accept(nextDouble());
        }
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        if (!this.f4105a) {
            this.f4107c.tryAdvance((DoubleConsumer) this);
        }
        return this.f4105a;
    }

    @Override // java.util.Iterator
    public final Double next() {
        if (g0.f4227a) {
            g0.a(V.class, "{0} calling PrimitiveIterator.OfDouble.nextLong()");
            throw null;
        }
        return Double.valueOf(nextDouble());
    }

    @Override // j$.util.InterfaceC0515s
    public final double nextDouble() {
        if (this.f4105a || hasNext()) {
            this.f4105a = false;
            return this.f4106b;
        }
        throw new NoSuchElementException();
    }
}
