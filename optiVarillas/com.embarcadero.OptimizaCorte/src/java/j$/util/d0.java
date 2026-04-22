package j$.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class d0 implements Spliterator {

    /* renamed from: a  reason: collision with root package name */
    private final java.util.Collection f4198a;

    /* renamed from: b  reason: collision with root package name */
    private Iterator f4199b = null;

    /* renamed from: c  reason: collision with root package name */
    private final int f4200c;

    /* renamed from: d  reason: collision with root package name */
    private long f4201d;

    /* renamed from: e  reason: collision with root package name */
    private int f4202e;

    public d0(java.util.Collection collection, int i4) {
        this.f4198a = collection;
        this.f4200c = (i4 & 4096) == 0 ? i4 | 16448 : i4;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return this.f4200c;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        if (this.f4199b == null) {
            java.util.Collection collection = this.f4198a;
            this.f4199b = collection.iterator();
            long size = collection.size();
            this.f4201d = size;
            return size;
        }
        return this.f4201d;
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        Iterator it = this.f4199b;
        if (it == null) {
            java.util.Collection collection = this.f4198a;
            Iterator it2 = collection.iterator();
            this.f4199b = it2;
            this.f4201d = collection.size();
            it = it2;
        }
        if (it instanceof InterfaceC0507j) {
            ((InterfaceC0507j) it).forEachRemaining(consumer);
            return;
        }
        Objects.requireNonNull(consumer);
        while (it.hasNext()) {
            consumer.accept(it.next());
        }
    }

    @Override // j$.util.Spliterator
    public Comparator getComparator() {
        if (D.e(this, 4)) {
            return null;
        }
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
        if (this.f4199b == null) {
            java.util.Collection collection = this.f4198a;
            this.f4199b = collection.iterator();
            this.f4201d = collection.size();
        }
        if (this.f4199b.hasNext()) {
            consumer.accept(this.f4199b.next());
            return true;
        }
        return false;
    }

    @Override // j$.util.Spliterator
    public final Spliterator trySplit() {
        long j4;
        Iterator it = this.f4199b;
        if (it == null) {
            java.util.Collection collection = this.f4198a;
            Iterator it2 = collection.iterator();
            this.f4199b = it2;
            j4 = collection.size();
            this.f4201d = j4;
            it = it2;
        } else {
            j4 = this.f4201d;
        }
        if (j4 <= 1 || !it.hasNext()) {
            return null;
        }
        int i4 = this.f4202e + 1024;
        if (i4 > j4) {
            i4 = (int) j4;
        }
        if (i4 > 33554432) {
            i4 = 33554432;
        }
        Object[] objArr = new Object[i4];
        int i5 = 0;
        do {
            objArr[i5] = it.next();
            i5++;
            if (i5 >= i4) {
                break;
            }
        } while (it.hasNext());
        this.f4202e = i5;
        long j5 = this.f4201d;
        if (j5 != Long.MAX_VALUE) {
            this.f4201d = j5 - i5;
        }
        return new W(objArr, 0, i5, this.f4200c);
    }
}
