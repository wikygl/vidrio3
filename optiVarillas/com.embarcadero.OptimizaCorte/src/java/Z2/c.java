package Z2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class c<E> extends Z2.b<E> implements List<E>, RandomAccess {

    /* renamed from: k  reason: collision with root package name */
    public static final a f2846k = new a(d.f2851n, 0);

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a<E> extends Z2.a<E> {

        /* renamed from: l  reason: collision with root package name */
        public final c<E> f2847l;

        public a(c<E> cVar, int i4) {
            super(cVar.size(), i4);
            this.f2847l = cVar;
        }

        @Override // Z2.a
        public final E a(int i4) {
            return this.f2847l.get(i4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class b extends c<E> {

        /* renamed from: l  reason: collision with root package name */
        public final transient int f2848l;

        /* renamed from: m  reason: collision with root package name */
        public final transient int f2849m;

        public b(int i4, int i5) {
            this.f2848l = i4;
            this.f2849m = i5;
        }

        @Override // java.util.List
        public final E get(int i4) {
            A0.c.c(i4, this.f2849m);
            return c.this.get(i4 + this.f2848l);
        }

        @Override // Z2.c, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
        public final Iterator iterator() {
            return listIterator(0);
        }

        @Override // Z2.b
        public final Object[] k() {
            return c.this.k();
        }

        @Override // Z2.b
        public final int l() {
            return c.this.m() + this.f2848l + this.f2849m;
        }

        @Override // Z2.c, java.util.List
        public final ListIterator listIterator() {
            return listIterator(0);
        }

        @Override // Z2.b
        public final int m() {
            return c.this.m() + this.f2848l;
        }

        @Override // Z2.c, java.util.List
        /* renamed from: o */
        public final c<E> subList(int i4, int i5) {
            A0.c.d(i4, i5, this.f2849m);
            int i6 = this.f2848l;
            return c.this.subList(i4 + i6, i5 + i6);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public final int size() {
            return this.f2849m;
        }

        @Override // Z2.c, java.util.List
        public final /* bridge */ /* synthetic */ ListIterator listIterator(int i4) {
            return listIterator(i4);
        }
    }

    @Override // java.util.List
    @Deprecated
    public final void add(int i4, E e4) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    @Deprecated
    public final boolean addAll(int i4, Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean contains(Object obj) {
        if (indexOf(obj) >= 0) {
            return true;
        }
        return false;
    }

    @Override // java.util.Collection, java.util.List
    public final boolean equals(Object obj) {
        E next;
        if (obj == this) {
            return true;
        }
        if (obj instanceof List) {
            List list = (List) obj;
            int size = size();
            if (size == list.size()) {
                if (list instanceof RandomAccess) {
                    for (int i4 = 0; i4 < size; i4++) {
                        E e4 = get(i4);
                        Object obj2 = list.get(i4);
                        if (e4 == obj2 || (e4 != null && e4.equals(obj2))) {
                        }
                    }
                    return true;
                }
                Iterator<E> it = list.iterator();
                for (E e5 : this) {
                    if (it.hasNext() && (e5 == (next = it.next()) || (e5 != null && e5.equals(next)))) {
                    }
                }
                return !it.hasNext();
            }
        }
        return false;
    }

    @Override // java.util.Collection, java.util.List
    public final int hashCode() {
        int size = size();
        int i4 = 1;
        for (int i5 = 0; i5 < size; i5++) {
            i4 = ~(~(get(i5).hashCode() + (i4 * 31)));
        }
        return i4;
    }

    @Override // java.util.List
    public final int indexOf(Object obj) {
        if (obj == null) {
            return -1;
        }
        int size = size();
        for (int i4 = 0; i4 < size; i4++) {
            if (obj.equals(get(i4))) {
                return i4;
            }
        }
        return -1;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.List
    public Iterator iterator() {
        return listIterator(0);
    }

    @Override // Z2.b
    public int j(Object[] objArr) {
        int size = size();
        for (int i4 = 0; i4 < size; i4++) {
            objArr[i4] = get(i4);
        }
        return size;
    }

    @Override // java.util.List
    public final int lastIndexOf(Object obj) {
        if (obj == null) {
            return -1;
        }
        for (int size = size() - 1; size >= 0; size--) {
            if (obj.equals(get(size))) {
                return size;
            }
        }
        return -1;
    }

    @Override // java.util.List
    /* renamed from: n */
    public final a listIterator(int i4) {
        int size = size();
        if (i4 >= 0 && i4 <= size) {
            if (isEmpty()) {
                return f2846k;
            }
            return new a(this, i4);
        }
        throw new IndexOutOfBoundsException(A0.c.b(i4, size, "index"));
    }

    @Override // java.util.List
    /* renamed from: o */
    public c<E> subList(int i4, int i5) {
        A0.c.d(i4, i5, size());
        int i6 = i5 - i4;
        if (i6 == size()) {
            return this;
        }
        if (i6 == 0) {
            return d.f2851n;
        }
        return new b(i4, i6);
    }

    @Override // java.util.List
    @Deprecated
    public final E remove(int i4) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    @Deprecated
    public final E set(int i4, E e4) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.List
    public ListIterator listIterator() {
        return listIterator(0);
    }
}
