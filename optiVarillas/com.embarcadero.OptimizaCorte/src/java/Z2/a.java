package Z2;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class a<E> implements ListIterator<Object>, Iterator<Object> {

    /* renamed from: j  reason: collision with root package name */
    public final int f2843j;

    /* renamed from: k  reason: collision with root package name */
    public int f2844k;

    public a(int i4, int i5) {
        if (i5 >= 0 && i5 <= i4) {
            this.f2843j = i4;
            this.f2844k = i5;
            return;
        }
        throw new IndexOutOfBoundsException(A0.c.b(i5, i4, "index"));
    }

    public abstract E a(int i4);

    @Override // java.util.ListIterator
    @Deprecated
    public final void add(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public final boolean hasNext() {
        if (this.f2844k < this.f2843j) {
            return true;
        }
        return false;
    }

    @Override // java.util.ListIterator
    public final boolean hasPrevious() {
        if (this.f2844k > 0) {
            return true;
        }
        return false;
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public final E next() {
        if (hasNext()) {
            int i4 = this.f2844k;
            this.f2844k = i4 + 1;
            return a(i4);
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.ListIterator
    public final int nextIndex() {
        return this.f2844k;
    }

    @Override // java.util.ListIterator
    public final E previous() {
        if (hasPrevious()) {
            int i4 = this.f2844k - 1;
            this.f2844k = i4;
            return a(i4);
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.ListIterator
    public final int previousIndex() {
        return this.f2844k - 1;
    }

    @Override // java.util.ListIterator, java.util.Iterator
    @Deprecated
    public final void remove() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ListIterator
    @Deprecated
    public final void set(Object obj) {
        throw new UnsupportedOperationException();
    }
}
