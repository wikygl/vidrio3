package j$.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class S implements Iterator, Consumer {

    /* renamed from: a  reason: collision with root package name */
    boolean f4091a = false;

    /* renamed from: b  reason: collision with root package name */
    Object f4092b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Spliterator f4093c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public S(Spliterator spliterator) {
        this.f4093c = spliterator;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f4091a = true;
        this.f4092b = obj;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        if (!this.f4091a) {
            this.f4093c.tryAdvance(this);
        }
        return this.f4091a;
    }

    @Override // java.util.Iterator
    public final Object next() {
        if (this.f4091a || hasNext()) {
            this.f4091a = false;
            return this.f4092b;
        }
        throw new NoSuchElementException();
    }
}
