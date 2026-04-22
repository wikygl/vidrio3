package j$.util;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class U implements A, LongConsumer, InterfaceC0507j {

    /* renamed from: a  reason: collision with root package name */
    boolean f4102a = false;

    /* renamed from: b  reason: collision with root package name */
    long f4103b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ M f4104c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public U(M m4) {
        this.f4104c = m4;
    }

    @Override // java.util.function.LongConsumer
    public final void accept(long j4) {
        this.f4102a = true;
        this.f4103b = j4;
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    @Override // j$.util.A, java.util.Iterator
    public final void forEachRemaining(Consumer consumer) {
        if (consumer instanceof LongConsumer) {
            forEachRemaining((LongConsumer) consumer);
            return;
        }
        Objects.requireNonNull(consumer);
        if (g0.f4227a) {
            g0.a(U.class, "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
            throw null;
        }
        Objects.requireNonNull(consumer);
        forEachRemaining((LongConsumer) new C0644x(consumer));
    }

    @Override // j$.util.B
    public final void forEachRemaining(LongConsumer longConsumer) {
        Objects.requireNonNull(longConsumer);
        while (hasNext()) {
            longConsumer.accept(nextLong());
        }
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        if (!this.f4102a) {
            this.f4104c.tryAdvance((LongConsumer) this);
        }
        return this.f4102a;
    }

    @Override // java.util.Iterator
    public final Long next() {
        if (g0.f4227a) {
            g0.a(U.class, "{0} calling PrimitiveIterator.OfLong.nextLong()");
            throw null;
        }
        return Long.valueOf(nextLong());
    }

    @Override // j$.util.A
    public final long nextLong() {
        if (this.f4102a || hasNext()) {
            this.f4102a = false;
            return this.f4103b;
        }
        throw new NoSuchElementException();
    }
}
