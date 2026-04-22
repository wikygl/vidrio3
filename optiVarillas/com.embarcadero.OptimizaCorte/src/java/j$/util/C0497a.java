package j$.util;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.function.Consumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0497a implements Spliterator {

    /* renamed from: a  reason: collision with root package name */
    private final java.util.List f4116a;

    /* renamed from: b  reason: collision with root package name */
    private int f4117b;

    /* renamed from: c  reason: collision with root package name */
    private int f4118c;

    private C0497a(C0497a c0497a, int i4, int i5) {
        this.f4116a = c0497a.f4116a;
        this.f4117b = i4;
        this.f4118c = i5;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0497a(java.util.List list) {
        this.f4116a = list;
        this.f4117b = 0;
        this.f4118c = -1;
    }

    private int a() {
        int i4 = this.f4118c;
        if (i4 < 0) {
            int size = this.f4116a.size();
            this.f4118c = size;
            return size;
        }
        return i4;
    }

    @Override // j$.util.Spliterator
    public final int characteristics() {
        return 16464;
    }

    @Override // j$.util.Spliterator
    public final long estimateSize() {
        return a() - this.f4117b;
    }

    @Override // j$.util.Spliterator
    public final void forEachRemaining(Consumer consumer) {
        Objects.requireNonNull(consumer);
        int a4 = a();
        this.f4117b = a4;
        for (int i4 = this.f4117b; i4 < a4; i4++) {
            try {
                consumer.accept(this.f4116a.get(i4));
            } catch (IndexOutOfBoundsException unused) {
                throw new ConcurrentModificationException();
            }
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
        int a4 = a();
        int i4 = this.f4117b;
        if (i4 < a4) {
            this.f4117b = i4 + 1;
            try {
                consumer.accept(this.f4116a.get(i4));
                return true;
            } catch (IndexOutOfBoundsException unused) {
                throw new ConcurrentModificationException();
            }
        }
        return false;
    }

    @Override // j$.util.Spliterator
    public final Spliterator trySplit() {
        int a4 = a();
        int i4 = this.f4117b;
        int i5 = (a4 + i4) >>> 1;
        if (i4 >= i5) {
            return null;
        }
        this.f4117b = i5;
        return new C0497a(this, i4, i5);
    }
}
