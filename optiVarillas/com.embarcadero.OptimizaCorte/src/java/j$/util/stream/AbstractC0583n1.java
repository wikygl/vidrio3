package j$.util.stream;

import j$.util.Spliterator;
import java.util.ArrayDeque;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/* renamed from: j$.util.stream.n1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0583n1 extends AbstractC0593p1 implements j$.util.P {
    @Override // j$.util.P
    public final void forEachRemaining(Object obj) {
        if (this.f4561a == null) {
            return;
        }
        if (this.f4564d != null) {
            do {
            } while (tryAdvance(obj));
            return;
        }
        Spliterator spliterator = this.f4563c;
        if (spliterator != null) {
            ((j$.util.P) spliterator).forEachRemaining(obj);
            return;
        }
        ArrayDeque b4 = b();
        while (true) {
            K0 k02 = (K0) AbstractC0593p1.a(b4);
            if (k02 == null) {
                this.f4561a = null;
                return;
            }
            k02.f(obj);
        }
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        forEachRemaining((Object) doubleConsumer);
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        forEachRemaining((Object) intConsumer);
    }

    public /* bridge */ /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        forEachRemaining((Object) longConsumer);
    }

    @Override // j$.util.P
    public final boolean tryAdvance(Object obj) {
        K0 k02;
        if (c()) {
            boolean tryAdvance = ((j$.util.P) this.f4564d).tryAdvance(obj);
            if (!tryAdvance) {
                if (this.f4563c == null && (k02 = (K0) AbstractC0593p1.a(this.f4565e)) != null) {
                    j$.util.P spliterator = k02.spliterator();
                    this.f4564d = spliterator;
                    return spliterator.tryAdvance(obj);
                }
                this.f4561a = null;
            }
            return tryAdvance;
        }
        return false;
    }

    public /* bridge */ /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return tryAdvance((Object) doubleConsumer);
    }

    public /* bridge */ /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return tryAdvance((Object) intConsumer);
    }

    public /* bridge */ /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return tryAdvance((Object) longConsumer);
    }
}
