package j$.util;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class T implements InterfaceC0643w, IntConsumer, InterfaceC0507j {

    /* renamed from: a  reason: collision with root package name */
    boolean f4099a = false;

    /* renamed from: b  reason: collision with root package name */
    int f4100b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ J f4101c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public T(J j4) {
        this.f4101c = j4;
    }

    @Override // java.util.function.IntConsumer
    public final void accept(int i4) {
        this.f4099a = true;
        this.f4100b = i4;
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // j$.util.InterfaceC0643w, java.util.Iterator, j$.util.InterfaceC0507j
    public final void forEachRemaining(Consumer consumer) {
        if (consumer instanceof IntConsumer) {
            forEachRemaining((IntConsumer) consumer);
            return;
        }
        Objects.requireNonNull(consumer);
        if (g0.f4227a) {
            g0.a(T.class, "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
            throw null;
        }
        Objects.requireNonNull(consumer);
        forEachRemaining((IntConsumer) new C0640t(consumer));
    }

    @Override // j$.util.B
    public final void forEachRemaining(IntConsumer intConsumer) {
        Objects.requireNonNull(intConsumer);
        while (hasNext()) {
            intConsumer.accept(nextInt());
        }
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        if (!this.f4099a) {
            this.f4101c.tryAdvance((IntConsumer) this);
        }
        return this.f4099a;
    }

    @Override // java.util.Iterator
    public final Integer next() {
        if (g0.f4227a) {
            g0.a(T.class, "{0} calling PrimitiveIterator.OfInt.nextInt()");
            throw null;
        }
        return Integer.valueOf(nextInt());
    }

    @Override // j$.util.InterfaceC0643w
    public final int nextInt() {
        if (this.f4099a || hasNext()) {
            this.f4099a = false;
            return this.f4100b;
        }
        throw new NoSuchElementException();
    }
}
