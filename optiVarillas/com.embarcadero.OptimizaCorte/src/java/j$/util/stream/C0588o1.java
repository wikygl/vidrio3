package j$.util.stream;

import j$.util.Spliterator;
import java.util.ArrayDeque;
import java.util.function.Consumer;

/* renamed from: j$.util.stream.o1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0588o1 extends AbstractC0593p1 {
    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        if (this.f4561a == null) {
            return;
        }
        if (this.f4564d != null) {
            do {
            } while (tryAdvance(consumer));
            return;
        }
        Spliterator spliterator = this.f4563c;
        if (spliterator != null) {
            spliterator.forEachRemaining(consumer);
            return;
        }
        ArrayDeque b4 = b();
        while (true) {
            L0 a4 = AbstractC0593p1.a(b4);
            if (a4 == null) {
                this.f4561a = null;
                return;
            }
            a4.forEach(consumer);
        }
    }

    @Override // j$.util.Spliterator
    public final boolean tryAdvance(Consumer consumer) {
        L0 a4;
        if (c()) {
            boolean tryAdvance = this.f4564d.tryAdvance(consumer);
            if (!tryAdvance) {
                if (this.f4563c == null && (a4 = AbstractC0593p1.a(this.f4565e)) != null) {
                    Spliterator spliterator = a4.spliterator();
                    this.f4564d = spliterator;
                    return spliterator.tryAdvance(consumer);
                }
                this.f4561a = null;
            }
            return tryAdvance;
        }
        return false;
    }
}
