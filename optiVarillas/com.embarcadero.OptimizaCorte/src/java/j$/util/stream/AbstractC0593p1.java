package j$.util.stream;

import j$.util.Spliterator;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;

/* renamed from: j$.util.stream.p1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0593p1 implements Spliterator {

    /* renamed from: a  reason: collision with root package name */
    L0 f4561a;

    /* renamed from: b  reason: collision with root package name */
    int f4562b;

    /* renamed from: c  reason: collision with root package name */
    Spliterator f4563c;

    /* renamed from: d  reason: collision with root package name */
    Spliterator f4564d;

    /* renamed from: e  reason: collision with root package name */
    ArrayDeque f4565e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0593p1(L0 l0) {
        this.f4561a = l0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static L0 a(Deque deque) {
        while (true) {
            L0 l0 = (L0) deque.pollFirst();
            if (l0 == null) {
                return null;
            }
            if (l0.q() != 0) {
                for (int q4 = l0.q() - 1; q4 >= 0; q4--) {
                    deque.addFirst(l0.b(q4));
                }
            } else if (l0.count() > 0) {
                return l0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ArrayDeque b() {
        ArrayDeque arrayDeque = new ArrayDeque(8);
        int q4 = this.f4561a.q();
        while (true) {
            q4--;
            if (q4 < this.f4562b) {
                return arrayDeque;
            }
            arrayDeque.addFirst(this.f4561a.b(q4));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean c() {
        if (this.f4561a == null) {
            return false;
        }
        if (this.f4564d == null) {
            Spliterator spliterator = this.f4563c;
            if (spliterator == null) {
                ArrayDeque b4 = b();
                this.f4565e = b4;
                L0 a4 = a(b4);
                if (a4 == null) {
                    this.f4561a = null;
                    return false;
                }
                spliterator = a4.spliterator();
            }
            this.f4564d = spliterator;
            return true;
        }
        return true;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return 64;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        long j4 = 0;
        if (this.f4561a == null) {
            return 0L;
        }
        Spliterator spliterator = this.f4563c;
        if (spliterator != null) {
            return spliterator.estimateSize();
        }
        for (int i4 = this.f4562b; i4 < this.f4561a.q(); i4++) {
            j4 += this.f4561a.b(i4).count();
        }
        return j4;
    }

    @Override // j$.util.Spliterator
    public final Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return j$.util.D.d(this);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return j$.util.D.e(this, i4);
    }

    @Override // j$.util.Spliterator
    public /* bridge */ /* synthetic */ j$.util.G trySplit() {
        return (j$.util.G) trySplit();
    }

    @Override // j$.util.Spliterator
    public /* bridge */ /* synthetic */ j$.util.J trySplit() {
        return (j$.util.J) trySplit();
    }

    @Override // j$.util.Spliterator
    public /* bridge */ /* synthetic */ j$.util.M trySplit() {
        return (j$.util.M) trySplit();
    }

    @Override // j$.util.Spliterator
    public /* bridge */ /* synthetic */ j$.util.P trySplit() {
        return (j$.util.P) trySplit();
    }

    @Override // j$.util.Spliterator
    public final Spliterator trySplit() {
        L0 l0 = this.f4561a;
        if (l0 == null || this.f4564d != null) {
            return null;
        }
        Spliterator spliterator = this.f4563c;
        if (spliterator != null) {
            return spliterator.trySplit();
        }
        if (this.f4562b < l0.q() - 1) {
            L0 l02 = this.f4561a;
            int i4 = this.f4562b;
            this.f4562b = i4 + 1;
            return l02.b(i4).spliterator();
        }
        L0 b4 = this.f4561a.b(this.f4562b);
        this.f4561a = b4;
        if (b4.q() == 0) {
            Spliterator spliterator2 = this.f4561a.spliterator();
            this.f4563c = spliterator2;
            return spliterator2.trySplit();
        }
        L0 l03 = this.f4561a;
        this.f4562b = 1;
        return l03.b(0).spliterator();
    }
}
