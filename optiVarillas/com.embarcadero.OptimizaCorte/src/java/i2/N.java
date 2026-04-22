package i2;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class N extends W implements ListIterator {

    /* renamed from: j  reason: collision with root package name */
    public final int f3683j;

    /* renamed from: k  reason: collision with root package name */
    public int f3684k;

    /* renamed from: l  reason: collision with root package name */
    public final P f3685l;

    public N(P p4, int i4) {
        int size = p4.size();
        if (i4 >= 0 && i4 <= size) {
            this.f3683j = size;
            this.f3684k = i4;
            this.f3685l = p4;
            return;
        }
        throw new IndexOutOfBoundsException(J.c(i4, size, "index"));
    }

    public final Object a(int i4) {
        return this.f3685l.get(i4);
    }

    @Override // java.util.ListIterator
    @Deprecated
    public final void add(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Iterator, java.util.ListIterator
    public final boolean hasNext() {
        if (this.f3684k < this.f3683j) {
            return true;
        }
        return false;
    }

    @Override // java.util.ListIterator
    public final boolean hasPrevious() {
        if (this.f3684k > 0) {
            return true;
        }
        return false;
    }

    @Override // java.util.Iterator, java.util.ListIterator
    public final Object next() {
        if (hasNext()) {
            int i4 = this.f3684k;
            this.f3684k = i4 + 1;
            return a(i4);
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.ListIterator
    public final int nextIndex() {
        return this.f3684k;
    }

    @Override // java.util.ListIterator
    public final Object previous() {
        if (hasPrevious()) {
            int i4 = this.f3684k - 1;
            this.f3684k = i4;
            return a(i4);
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.ListIterator
    public final int previousIndex() {
        return this.f3684k - 1;
    }

    @Override // java.util.ListIterator
    @Deprecated
    public final void set(Object obj) {
        throw new UnsupportedOperationException();
    }
}
