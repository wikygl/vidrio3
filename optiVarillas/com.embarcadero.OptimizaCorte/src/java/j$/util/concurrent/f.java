package j$.util.concurrent;

import j$.util.D;
import j$.util.Spliterator;
import java.util.Comparator;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class f extends p implements Spliterator {

    /* renamed from: i  reason: collision with root package name */
    final ConcurrentHashMap f4155i;

    /* renamed from: j  reason: collision with root package name */
    long f4156j;

    /* JADX INFO: Access modifiers changed from: package-private */
    public f(l[] lVarArr, int i4, int i5, int i6, long j4, ConcurrentHashMap concurrentHashMap) {
        super(lVarArr, i4, i5, i6);
        this.f4155i = concurrentHashMap;
        this.f4156j = j4;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return 4353;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return this.f4156j;
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        while (true) {
            l a4 = a();
            if (a4 == null) {
                return;
            }
            consumer.accept(new k(a4.f4165b, a4.f4166c, this.f4155i));
        }
    }

    @Override // j$.util.Spliterator
    public final Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ long getExactSizeIfKnown() {
        return D.d(this);
    }

    @Override // j$.util.Spliterator
    public final /* synthetic */ boolean hasCharacteristics(int i4) {
        return D.e(this, i4);
    }

    @Override // j$.util.Spliterator
    public final boolean tryAdvance(Consumer consumer) {
        consumer.getClass();
        l a4 = a();
        if (a4 == null) {
            return false;
        }
        consumer.accept(new k(a4.f4165b, a4.f4166c, this.f4155i));
        return true;
    }

    @Override // j$.util.Spliterator
    public final Spliterator trySplit() {
        int i4 = this.f;
        int i5 = this.f4177g;
        int i6 = (i4 + i5) >>> 1;
        if (i6 <= i4) {
            return null;
        }
        l[] lVarArr = this.f4172a;
        this.f4177g = i6;
        long j4 = this.f4156j >>> 1;
        this.f4156j = j4;
        return new f(lVarArr, this.f4178h, i6, i5, j4, this.f4155i);
    }
}
