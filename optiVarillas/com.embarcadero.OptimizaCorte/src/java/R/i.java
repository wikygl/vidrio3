package r;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class i<K, V> {

    /* renamed from: a  reason: collision with root package name */
    public i<K, V>.b f5664a;

    /* renamed from: b  reason: collision with root package name */
    public i<K, V>.c f5665b;

    /* renamed from: c  reason: collision with root package name */
    public i<K, V>.e f5666c;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public final class a<T> implements Iterator<T> {

        /* renamed from: j  reason: collision with root package name */
        public final int f5667j;

        /* renamed from: k  reason: collision with root package name */
        public int f5668k;

        /* renamed from: l  reason: collision with root package name */
        public int f5669l;

        /* renamed from: m  reason: collision with root package name */
        public boolean f5670m = false;

        public a(int i4) {
            this.f5667j = i4;
            this.f5668k = i.this.d();
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            if (this.f5669l < this.f5668k) {
                return true;
            }
            return false;
        }

        @Override // java.util.Iterator
        public final T next() {
            if (hasNext()) {
                T t3 = (T) i.this.b(this.f5669l, this.f5667j);
                this.f5669l++;
                this.f5670m = true;
                return t3;
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public final void remove() {
            if (this.f5670m) {
                int i4 = this.f5669l - 1;
                this.f5669l = i4;
                this.f5668k--;
                this.f5670m = false;
                i.this.h(i4);
                return;
            }
            throw new IllegalStateException();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public final class b implements Set<Map.Entry<K, V>> {
        public b() {
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean add(Object obj) {
            Map.Entry entry = (Map.Entry) obj;
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean addAll(Collection<? extends Map.Entry<K, V>> collection) {
            i iVar = i.this;
            int d4 = iVar.d();
            for (Map.Entry<K, V> entry : collection) {
                iVar.g(entry.getKey(), entry.getValue());
            }
            if (d4 != iVar.d()) {
                return true;
            }
            return false;
        }

        @Override // java.util.Set, java.util.Collection
        public final void clear() {
            i.this.a();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean contains(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            i iVar = i.this;
            int e4 = iVar.e(key);
            if (e4 < 0) {
                return false;
            }
            Object b4 = iVar.b(e4, 1);
            Object value = entry.getValue();
            if (b4 != value && (b4 == null || !b4.equals(value))) {
                return false;
            }
            return true;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean containsAll(Collection<?> collection) {
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                if (!contains(it.next())) {
                    return false;
                }
            }
            return true;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean equals(Object obj) {
            return i.j(this, obj);
        }

        @Override // java.util.Set, java.util.Collection
        public final int hashCode() {
            int hashCode;
            int hashCode2;
            i iVar = i.this;
            int i4 = 0;
            for (int d4 = iVar.d() - 1; d4 >= 0; d4--) {
                Object b4 = iVar.b(d4, 0);
                Object b5 = iVar.b(d4, 1);
                if (b4 == null) {
                    hashCode = 0;
                } else {
                    hashCode = b4.hashCode();
                }
                if (b5 == null) {
                    hashCode2 = 0;
                } else {
                    hashCode2 = b5.hashCode();
                }
                i4 += hashCode ^ hashCode2;
            }
            return i4;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean isEmpty() {
            if (i.this.d() == 0) {
                return true;
            }
            return false;
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public final Iterator<Map.Entry<K, V>> iterator() {
            return new d();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean remove(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final int size() {
            return i.this.d();
        }

        @Override // java.util.Set, java.util.Collection
        public final Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final <T> T[] toArray(T[] tArr) {
            throw new UnsupportedOperationException();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public final class c implements Set<K> {
        public c() {
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean add(K k4) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean addAll(Collection<? extends K> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Set, java.util.Collection
        public final void clear() {
            i.this.a();
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean contains(Object obj) {
            if (i.this.e(obj) >= 0) {
                return true;
            }
            return false;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean containsAll(Collection<?> collection) {
            Map<K, V> c4 = i.this.c();
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                if (!c4.containsKey(it.next())) {
                    return false;
                }
            }
            return true;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean equals(Object obj) {
            return i.j(this, obj);
        }

        @Override // java.util.Set, java.util.Collection
        public final int hashCode() {
            int hashCode;
            i iVar = i.this;
            int i4 = 0;
            for (int d4 = iVar.d() - 1; d4 >= 0; d4--) {
                Object b4 = iVar.b(d4, 0);
                if (b4 == null) {
                    hashCode = 0;
                } else {
                    hashCode = b4.hashCode();
                }
                i4 += hashCode;
            }
            return i4;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean isEmpty() {
            if (i.this.d() == 0) {
                return true;
            }
            return false;
        }

        @Override // java.util.Set, java.util.Collection, java.lang.Iterable
        public final Iterator<K> iterator() {
            return new a(0);
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean remove(Object obj) {
            i iVar = i.this;
            int e4 = iVar.e(obj);
            if (e4 >= 0) {
                iVar.h(e4);
                return true;
            }
            return false;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean removeAll(Collection<?> collection) {
            Map<K, V> c4 = i.this.c();
            int size = c4.size();
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                c4.remove(it.next());
            }
            if (size != c4.size()) {
                return true;
            }
            return false;
        }

        @Override // java.util.Set, java.util.Collection
        public final boolean retainAll(Collection<?> collection) {
            Map<K, V> c4 = i.this.c();
            int size = c4.size();
            Iterator<K> it = c4.keySet().iterator();
            while (it.hasNext()) {
                if (!collection.contains(it.next())) {
                    it.remove();
                }
            }
            if (size != c4.size()) {
                return true;
            }
            return false;
        }

        @Override // java.util.Set, java.util.Collection
        public final int size() {
            return i.this.d();
        }

        @Override // java.util.Set, java.util.Collection
        public final <T> T[] toArray(T[] tArr) {
            return (T[]) i.this.k(0, tArr);
        }

        @Override // java.util.Set, java.util.Collection
        public final Object[] toArray() {
            i iVar = i.this;
            int d4 = iVar.d();
            Object[] objArr = new Object[d4];
            for (int i4 = 0; i4 < d4; i4++) {
                objArr[i4] = iVar.b(i4, 0);
            }
            return objArr;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public final class d implements Iterator<Map.Entry<K, V>>, Map.Entry<K, V> {

        /* renamed from: j  reason: collision with root package name */
        public int f5674j;

        /* renamed from: l  reason: collision with root package name */
        public boolean f5676l = false;

        /* renamed from: k  reason: collision with root package name */
        public int f5675k = -1;

        public d() {
            this.f5674j = i.this.d() - 1;
        }

        @Override // java.util.Map.Entry
        public final boolean equals(Object obj) {
            if (this.f5676l) {
                if (!(obj instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry entry = (Map.Entry) obj;
                Object key = entry.getKey();
                int i4 = this.f5675k;
                i iVar = i.this;
                Object b4 = iVar.b(i4, 0);
                if (key != b4 && (key == null || !key.equals(b4))) {
                    return false;
                }
                Object value = entry.getValue();
                Object b5 = iVar.b(this.f5675k, 1);
                if (value != b5 && (value == null || !value.equals(b5))) {
                    return false;
                }
                return true;
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        @Override // java.util.Map.Entry
        public final K getKey() {
            if (this.f5676l) {
                return (K) i.this.b(this.f5675k, 0);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        @Override // java.util.Map.Entry
        public final V getValue() {
            if (this.f5676l) {
                return (V) i.this.b(this.f5675k, 1);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            if (this.f5675k < this.f5674j) {
                return true;
            }
            return false;
        }

        @Override // java.util.Map.Entry
        public final int hashCode() {
            int hashCode;
            if (this.f5676l) {
                int i4 = this.f5675k;
                i iVar = i.this;
                int i5 = 0;
                Object b4 = iVar.b(i4, 0);
                Object b5 = iVar.b(this.f5675k, 1);
                if (b4 == null) {
                    hashCode = 0;
                } else {
                    hashCode = b4.hashCode();
                }
                if (b5 != null) {
                    i5 = b5.hashCode();
                }
                return hashCode ^ i5;
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        @Override // java.util.Iterator
        public final Object next() {
            if (hasNext()) {
                this.f5675k++;
                this.f5676l = true;
                return this;
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public final void remove() {
            if (this.f5676l) {
                i.this.h(this.f5675k);
                this.f5675k--;
                this.f5674j--;
                this.f5676l = false;
                return;
            }
            throw new IllegalStateException();
        }

        @Override // java.util.Map.Entry
        public final V setValue(V v4) {
            if (this.f5676l) {
                return (V) i.this.i(this.f5675k, v4);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public final String toString() {
            return getKey() + "=" + getValue();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public final class e implements Collection<V> {
        public e() {
        }

        @Override // java.util.Collection
        public final boolean add(V v4) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public final boolean addAll(Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }

        @Override // java.util.Collection
        public final void clear() {
            i.this.a();
        }

        @Override // java.util.Collection
        public final boolean contains(Object obj) {
            if (i.this.f(obj) >= 0) {
                return true;
            }
            return false;
        }

        @Override // java.util.Collection
        public final boolean containsAll(Collection<?> collection) {
            Iterator<?> it = collection.iterator();
            while (it.hasNext()) {
                if (!contains(it.next())) {
                    return false;
                }
            }
            return true;
        }

        @Override // java.util.Collection
        public final boolean isEmpty() {
            if (i.this.d() == 0) {
                return true;
            }
            return false;
        }

        @Override // java.util.Collection, java.lang.Iterable
        public final Iterator<V> iterator() {
            return new a(1);
        }

        @Override // java.util.Collection
        public final boolean remove(Object obj) {
            i iVar = i.this;
            int f = iVar.f(obj);
            if (f >= 0) {
                iVar.h(f);
                return true;
            }
            return false;
        }

        @Override // java.util.Collection
        public final boolean removeAll(Collection<?> collection) {
            i iVar = i.this;
            int d4 = iVar.d();
            int i4 = 0;
            boolean z4 = false;
            while (i4 < d4) {
                if (collection.contains(iVar.b(i4, 1))) {
                    iVar.h(i4);
                    i4--;
                    d4--;
                    z4 = true;
                }
                i4++;
            }
            return z4;
        }

        @Override // java.util.Collection
        public final boolean retainAll(Collection<?> collection) {
            i iVar = i.this;
            int d4 = iVar.d();
            int i4 = 0;
            boolean z4 = false;
            while (i4 < d4) {
                if (!collection.contains(iVar.b(i4, 1))) {
                    iVar.h(i4);
                    i4--;
                    d4--;
                    z4 = true;
                }
                i4++;
            }
            return z4;
        }

        @Override // java.util.Collection
        public final int size() {
            return i.this.d();
        }

        @Override // java.util.Collection
        public final <T> T[] toArray(T[] tArr) {
            return (T[]) i.this.k(1, tArr);
        }

        @Override // java.util.Collection
        public final Object[] toArray() {
            i iVar = i.this;
            int d4 = iVar.d();
            Object[] objArr = new Object[d4];
            for (int i4 = 0; i4 < d4; i4++) {
                objArr[i4] = iVar.b(i4, 1);
            }
            return objArr;
        }
    }

    public static <T> boolean j(Set<T> set, Object obj) {
        if (set == obj) {
            return true;
        }
        if (obj instanceof Set) {
            Set set2 = (Set) obj;
            try {
                if (set.size() == set2.size()) {
                    if (set.containsAll(set2)) {
                        return true;
                    }
                }
                return false;
            } catch (ClassCastException | NullPointerException unused) {
            }
        }
        return false;
    }

    public abstract void a();

    public abstract Object b(int i4, int i5);

    public abstract Map<K, V> c();

    public abstract int d();

    public abstract int e(Object obj);

    public abstract int f(Object obj);

    public abstract void g(K k4, V v4);

    public abstract void h(int i4);

    public abstract V i(int i4, V v4);

    public final Object[] k(int i4, Object[] objArr) {
        int d4 = d();
        if (objArr.length < d4) {
            objArr = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), d4);
        }
        for (int i5 = 0; i5 < d4; i5++) {
            objArr[i5] = b(i5, i4);
        }
        if (objArr.length > d4) {
            objArr[d4] = null;
        }
        return objArr;
    }
}
