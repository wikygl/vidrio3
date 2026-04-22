package m3;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class a<E> extends AbstractList<Object> implements List<Object> {

    /* renamed from: m  reason: collision with root package name */
    public static final Object[] f5342m = new Object[0];

    /* renamed from: j  reason: collision with root package name */
    public int f5343j;

    /* renamed from: k  reason: collision with root package name */
    public Object[] f5344k = f5342m;

    /* renamed from: l  reason: collision with root package name */
    public int f5345l;

    @Override // java.util.AbstractList, java.util.List
    public final void add(int i4, E e4) {
        int i5;
        int i6 = this.f5345l;
        if (i4 < 0 || i4 > i6) {
            throw new IndexOutOfBoundsException(X1.b.d(i4, i6, "index: ", ", size: "));
        }
        if (i4 == i6) {
            j(e4);
        } else if (i4 == 0) {
            l(i6 + 1);
            int i7 = this.f5343j;
            if (i7 == 0) {
                Object[] objArr = this.f5344k;
                v3.h.e(objArr, "<this>");
                i7 = objArr.length;
            }
            int i8 = i7 - 1;
            this.f5343j = i8;
            this.f5344k[i8] = e4;
            this.f5345l++;
        } else {
            l(i6 + 1);
            int n4 = n(this.f5343j + i4);
            int i9 = this.f5345l;
            if (i4 < ((i9 + 1) >> 1)) {
                if (n4 == 0) {
                    Object[] objArr2 = this.f5344k;
                    v3.h.e(objArr2, "<this>");
                    i5 = objArr2.length - 1;
                } else {
                    i5 = n4 - 1;
                }
                int i10 = this.f5343j;
                if (i10 == 0) {
                    Object[] objArr3 = this.f5344k;
                    v3.h.e(objArr3, "<this>");
                    i10 = objArr3.length;
                }
                int i11 = i10 - 1;
                int i12 = this.f5343j;
                if (i5 >= i12) {
                    Object[] objArr4 = this.f5344k;
                    objArr4[i11] = objArr4[i12];
                    b.m(i12, i12 + 1, i5 + 1, objArr4, objArr4);
                } else {
                    Object[] objArr5 = this.f5344k;
                    b.m(i12 - 1, i12, objArr5.length, objArr5, objArr5);
                    Object[] objArr6 = this.f5344k;
                    objArr6[objArr6.length - 1] = objArr6[0];
                    b.m(0, 1, i5 + 1, objArr6, objArr6);
                }
                this.f5344k[i5] = e4;
                this.f5343j = i11;
            } else {
                int n5 = n(this.f5343j + i9);
                if (n4 < n5) {
                    Object[] objArr7 = this.f5344k;
                    b.m(n4 + 1, n4, n5, objArr7, objArr7);
                } else {
                    Object[] objArr8 = this.f5344k;
                    b.m(1, 0, n5, objArr8, objArr8);
                    Object[] objArr9 = this.f5344k;
                    objArr9[0] = objArr9[objArr9.length - 1];
                    b.m(n4 + 1, n4, objArr9.length - 1, objArr9, objArr9);
                }
                this.f5344k[n4] = e4;
            }
            this.f5345l++;
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public final boolean addAll(int i4, Collection<? extends E> collection) {
        v3.h.e(collection, "elements");
        int i5 = this.f5345l;
        if (i4 >= 0 && i4 <= i5) {
            if (collection.isEmpty()) {
                return false;
            }
            int i6 = this.f5345l;
            if (i4 == i6) {
                return addAll(collection);
            }
            l(collection.size() + i6);
            int n4 = n(this.f5343j + this.f5345l);
            int n5 = n(this.f5343j + i4);
            int size = collection.size();
            if (i4 < ((this.f5345l + 1) >> 1)) {
                int i7 = this.f5343j;
                int i8 = i7 - size;
                if (n5 < i7) {
                    Object[] objArr = this.f5344k;
                    b.m(i8, i7, objArr.length, objArr, objArr);
                    if (size >= n5) {
                        Object[] objArr2 = this.f5344k;
                        b.m(objArr2.length - size, 0, n5, objArr2, objArr2);
                    } else {
                        Object[] objArr3 = this.f5344k;
                        b.m(objArr3.length - size, 0, size, objArr3, objArr3);
                        Object[] objArr4 = this.f5344k;
                        b.m(0, size, n5, objArr4, objArr4);
                    }
                } else if (i8 >= 0) {
                    Object[] objArr5 = this.f5344k;
                    b.m(i8, i7, n5, objArr5, objArr5);
                } else {
                    Object[] objArr6 = this.f5344k;
                    i8 += objArr6.length;
                    int i9 = n5 - i7;
                    int length = objArr6.length - i8;
                    if (length >= i9) {
                        b.m(i8, i7, n5, objArr6, objArr6);
                    } else {
                        b.m(i8, i7, i7 + length, objArr6, objArr6);
                        Object[] objArr7 = this.f5344k;
                        b.m(0, this.f5343j + length, n5, objArr7, objArr7);
                    }
                }
                this.f5343j = i8;
                int i10 = n5 - size;
                if (i10 < 0) {
                    i10 += this.f5344k.length;
                }
                k(i10, collection);
            } else {
                int i11 = n5 + size;
                if (n5 < n4) {
                    int i12 = size + n4;
                    Object[] objArr8 = this.f5344k;
                    if (i12 <= objArr8.length) {
                        b.m(i11, n5, n4, objArr8, objArr8);
                    } else if (i11 >= objArr8.length) {
                        b.m(i11 - objArr8.length, n5, n4, objArr8, objArr8);
                    } else {
                        int length2 = n4 - (i12 - objArr8.length);
                        b.m(0, length2, n4, objArr8, objArr8);
                        Object[] objArr9 = this.f5344k;
                        b.m(i11, n5, length2, objArr9, objArr9);
                    }
                } else {
                    Object[] objArr10 = this.f5344k;
                    b.m(size, 0, n4, objArr10, objArr10);
                    Object[] objArr11 = this.f5344k;
                    if (i11 >= objArr11.length) {
                        b.m(i11 - objArr11.length, n5, objArr11.length, objArr11, objArr11);
                    } else {
                        b.m(0, objArr11.length - size, objArr11.length, objArr11, objArr11);
                        Object[] objArr12 = this.f5344k;
                        b.m(i11, n5, objArr12.length - size, objArr12, objArr12);
                    }
                }
                k(n5, collection);
            }
            return true;
        }
        throw new IndexOutOfBoundsException(X1.b.d(i4, i5, "index: ", ", size: "));
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final void clear() {
        int n4 = n(this.f5343j + this.f5345l);
        int i4 = this.f5343j;
        if (i4 < n4) {
            Object[] objArr = this.f5344k;
            v3.h.e(objArr, "<this>");
            Arrays.fill(objArr, i4, n4, (Object) null);
        } else if (!isEmpty()) {
            Object[] objArr2 = this.f5344k;
            Arrays.fill(objArr2, this.f5343j, objArr2.length, (Object) null);
            Object[] objArr3 = this.f5344k;
            v3.h.e(objArr3, "<this>");
            Arrays.fill(objArr3, 0, n4, (Object) null);
        }
        this.f5343j = 0;
        this.f5345l = 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean contains(Object obj) {
        if (indexOf(obj) != -1) {
            return true;
        }
        return false;
    }

    @Override // java.util.AbstractList, java.util.List
    public final E get(int i4) {
        int i5 = this.f5345l;
        if (i4 >= 0 && i4 < i5) {
            return (E) this.f5344k[n(this.f5343j + i4)];
        }
        throw new IndexOutOfBoundsException(X1.b.d(i4, i5, "index: ", ", size: "));
    }

    @Override // java.util.AbstractList, java.util.List
    public final int indexOf(Object obj) {
        int i4;
        int n4 = n(this.f5343j + this.f5345l);
        int i5 = this.f5343j;
        if (i5 < n4) {
            while (i5 < n4) {
                if (v3.h.a(obj, this.f5344k[i5])) {
                    i4 = this.f5343j;
                } else {
                    i5++;
                }
            }
            return -1;
        } else if (i5 >= n4) {
            int length = this.f5344k.length;
            while (true) {
                if (i5 < length) {
                    if (v3.h.a(obj, this.f5344k[i5])) {
                        i4 = this.f5343j;
                        break;
                    }
                    i5++;
                } else {
                    for (int i6 = 0; i6 < n4; i6++) {
                        if (v3.h.a(obj, this.f5344k[i6])) {
                            i5 = i6 + this.f5344k.length;
                            i4 = this.f5343j;
                        }
                    }
                    return -1;
                }
            }
        } else {
            return -1;
        }
        return i5 - i4;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean isEmpty() {
        if (this.f5345l == 0) {
            return true;
        }
        return false;
    }

    public final void j(E e4) {
        l(this.f5345l + 1);
        this.f5344k[n(this.f5343j + this.f5345l)] = e4;
        this.f5345l++;
    }

    public final void k(int i4, Collection<? extends E> collection) {
        Iterator<? extends E> it = collection.iterator();
        int length = this.f5344k.length;
        while (i4 < length && it.hasNext()) {
            this.f5344k[i4] = it.next();
            i4++;
        }
        int i5 = this.f5343j;
        for (int i6 = 0; i6 < i5 && it.hasNext(); i6++) {
            this.f5344k[i6] = it.next();
        }
        this.f5345l = collection.size() + this.f5345l;
    }

    public final void l(int i4) {
        if (i4 >= 0) {
            Object[] objArr = this.f5344k;
            if (i4 <= objArr.length) {
                return;
            }
            if (objArr == f5342m) {
                if (i4 < 10) {
                    i4 = 10;
                }
                this.f5344k = new Object[i4];
                return;
            }
            int length = objArr.length;
            int i5 = length + (length >> 1);
            if (i5 - i4 < 0) {
                i5 = i4;
            }
            if (i5 - 2147483639 > 0) {
                if (i4 > 2147483639) {
                    i5 = Integer.MAX_VALUE;
                } else {
                    i5 = 2147483639;
                }
            }
            Object[] objArr2 = new Object[i5];
            b.m(0, this.f5343j, objArr.length, objArr, objArr2);
            Object[] objArr3 = this.f5344k;
            int length2 = objArr3.length;
            int i6 = this.f5343j;
            b.m(length2 - i6, 0, i6, objArr3, objArr2);
            this.f5343j = 0;
            this.f5344k = objArr2;
            return;
        }
        throw new IllegalStateException("Deque is too big.");
    }

    @Override // java.util.AbstractList, java.util.List
    public final int lastIndexOf(Object obj) {
        int length;
        int i4;
        int n4 = n(this.f5343j + this.f5345l);
        int i5 = this.f5343j;
        if (i5 < n4) {
            length = n4 - 1;
            if (i5 <= length) {
                while (!v3.h.a(obj, this.f5344k[length])) {
                    if (length != i5) {
                        length--;
                    }
                }
                i4 = this.f5343j;
                return length - i4;
            }
            return -1;
        }
        if (i5 > n4) {
            int i6 = n4 - 1;
            while (true) {
                if (-1 < i6) {
                    if (v3.h.a(obj, this.f5344k[i6])) {
                        length = i6 + this.f5344k.length;
                        i4 = this.f5343j;
                        break;
                    }
                    i6--;
                } else {
                    Object[] objArr = this.f5344k;
                    v3.h.e(objArr, "<this>");
                    length = objArr.length - 1;
                    int i7 = this.f5343j;
                    if (i7 <= length) {
                        while (!v3.h.a(obj, this.f5344k[length])) {
                            if (length != i7) {
                                length--;
                            }
                        }
                        i4 = this.f5343j;
                    }
                }
            }
        }
        return -1;
    }

    public final int m(int i4) {
        Object[] objArr = this.f5344k;
        v3.h.e(objArr, "<this>");
        if (i4 == objArr.length - 1) {
            return 0;
        }
        return i4 + 1;
    }

    public final int n(int i4) {
        Object[] objArr = this.f5344k;
        if (i4 >= objArr.length) {
            return i4 - objArr.length;
        }
        return i4;
    }

    public final E o() {
        if (!isEmpty()) {
            Object[] objArr = this.f5344k;
            int i4 = this.f5343j;
            E e4 = (E) objArr[i4];
            objArr[i4] = null;
            this.f5343j = m(i4);
            this.f5345l--;
            return e4;
        }
        throw new NoSuchElementException("ArrayDeque is empty.");
    }

    @Override // java.util.AbstractList, java.util.List
    public final Object remove(int i4) {
        int i5 = this.f5345l;
        if (i4 >= 0 && i4 < i5) {
            if (i4 == size() - 1) {
                if (!isEmpty()) {
                    int n4 = n((size() - 1) + this.f5343j);
                    Object[] objArr = this.f5344k;
                    Object obj = objArr[n4];
                    objArr[n4] = null;
                    this.f5345l--;
                    return obj;
                }
                throw new NoSuchElementException("ArrayDeque is empty.");
            } else if (i4 == 0) {
                return o();
            } else {
                int n5 = n(this.f5343j + i4);
                Object[] objArr2 = this.f5344k;
                Object obj2 = objArr2[n5];
                if (i4 < (this.f5345l >> 1)) {
                    int i6 = this.f5343j;
                    if (n5 >= i6) {
                        b.m(i6 + 1, i6, n5, objArr2, objArr2);
                    } else {
                        b.m(1, 0, n5, objArr2, objArr2);
                        Object[] objArr3 = this.f5344k;
                        objArr3[0] = objArr3[objArr3.length - 1];
                        int i7 = this.f5343j;
                        b.m(i7 + 1, i7, objArr3.length - 1, objArr3, objArr3);
                    }
                    Object[] objArr4 = this.f5344k;
                    int i8 = this.f5343j;
                    objArr4[i8] = null;
                    this.f5343j = m(i8);
                } else {
                    int n6 = n((size() - 1) + this.f5343j);
                    if (n5 <= n6) {
                        Object[] objArr5 = this.f5344k;
                        b.m(n5, n5 + 1, n6 + 1, objArr5, objArr5);
                    } else {
                        Object[] objArr6 = this.f5344k;
                        b.m(n5, n5 + 1, objArr6.length, objArr6, objArr6);
                        Object[] objArr7 = this.f5344k;
                        objArr7[objArr7.length - 1] = objArr7[0];
                        b.m(0, 1, n6 + 1, objArr7, objArr7);
                    }
                    this.f5344k[n6] = null;
                }
                this.f5345l--;
                return obj2;
            }
        }
        throw new IndexOutOfBoundsException(X1.b.d(i4, i5, "index: ", ", size: "));
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean removeAll(Collection<? extends Object> collection) {
        int n4;
        v3.h.e(collection, "elements");
        boolean z4 = false;
        z4 = false;
        z4 = false;
        if (!isEmpty() && this.f5344k.length != 0) {
            int n5 = n(this.f5343j + this.f5345l);
            int i4 = this.f5343j;
            if (i4 < n5) {
                n4 = i4;
                while (i4 < n5) {
                    Object obj = this.f5344k[i4];
                    if (!collection.contains(obj)) {
                        this.f5344k[n4] = obj;
                        n4++;
                    } else {
                        z4 = true;
                    }
                    i4++;
                }
                Object[] objArr = this.f5344k;
                v3.h.e(objArr, "<this>");
                Arrays.fill(objArr, n4, n5, (Object) null);
            } else {
                int length = this.f5344k.length;
                int i5 = i4;
                boolean z5 = false;
                while (i4 < length) {
                    Object[] objArr2 = this.f5344k;
                    Object obj2 = objArr2[i4];
                    objArr2[i4] = null;
                    if (!collection.contains(obj2)) {
                        this.f5344k[i5] = obj2;
                        i5++;
                    } else {
                        z5 = true;
                    }
                    i4++;
                }
                n4 = n(i5);
                for (int i6 = 0; i6 < n5; i6++) {
                    Object[] objArr3 = this.f5344k;
                    Object obj3 = objArr3[i6];
                    objArr3[i6] = null;
                    if (!collection.contains(obj3)) {
                        this.f5344k[n4] = obj3;
                        n4 = m(n4);
                    } else {
                        z5 = true;
                    }
                }
                z4 = z5;
            }
            if (z4) {
                int i7 = n4 - this.f5343j;
                if (i7 < 0) {
                    i7 += this.f5344k.length;
                }
                this.f5345l = i7;
            }
        }
        return z4;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean retainAll(Collection<? extends Object> collection) {
        int n4;
        v3.h.e(collection, "elements");
        boolean z4 = false;
        z4 = false;
        z4 = false;
        if (!isEmpty() && this.f5344k.length != 0) {
            int n5 = n(this.f5343j + this.f5345l);
            int i4 = this.f5343j;
            if (i4 < n5) {
                n4 = i4;
                while (i4 < n5) {
                    Object obj = this.f5344k[i4];
                    if (collection.contains(obj)) {
                        this.f5344k[n4] = obj;
                        n4++;
                    } else {
                        z4 = true;
                    }
                    i4++;
                }
                Object[] objArr = this.f5344k;
                v3.h.e(objArr, "<this>");
                Arrays.fill(objArr, n4, n5, (Object) null);
            } else {
                int length = this.f5344k.length;
                int i5 = i4;
                boolean z5 = false;
                while (i4 < length) {
                    Object[] objArr2 = this.f5344k;
                    Object obj2 = objArr2[i4];
                    objArr2[i4] = null;
                    if (collection.contains(obj2)) {
                        this.f5344k[i5] = obj2;
                        i5++;
                    } else {
                        z5 = true;
                    }
                    i4++;
                }
                n4 = n(i5);
                for (int i6 = 0; i6 < n5; i6++) {
                    Object[] objArr3 = this.f5344k;
                    Object obj3 = objArr3[i6];
                    objArr3[i6] = null;
                    if (collection.contains(obj3)) {
                        this.f5344k[n4] = obj3;
                        n4 = m(n4);
                    } else {
                        z5 = true;
                    }
                }
                z4 = z5;
            }
            if (z4) {
                int i7 = n4 - this.f5343j;
                if (i7 < 0) {
                    i7 += this.f5344k.length;
                }
                this.f5345l = i7;
            }
        }
        return z4;
    }

    @Override // java.util.AbstractList, java.util.List
    public final E set(int i4, E e4) {
        int i5 = this.f5345l;
        if (i4 >= 0 && i4 < i5) {
            int n4 = n(this.f5343j + i4);
            Object[] objArr = this.f5344k;
            E e5 = (E) objArr[n4];
            objArr[n4] = e4;
            return e5;
        }
        throw new IndexOutOfBoundsException(X1.b.d(i4, i5, "index: ", ", size: "));
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final int size() {
        return this.f5345l;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final Object[] toArray() {
        return toArray(new Object[this.f5345l]);
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final <T> T[] toArray(T[] tArr) {
        v3.h.e(tArr, "array");
        int length = tArr.length;
        int i4 = this.f5345l;
        if (length < i4) {
            Object newInstance = Array.newInstance(tArr.getClass().getComponentType(), i4);
            v3.h.c(newInstance, "null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.arrayOfNulls>");
            tArr = (T[]) ((Object[]) newInstance);
        }
        int n4 = n(this.f5343j + this.f5345l);
        int i5 = this.f5343j;
        if (i5 < n4) {
            b.m(0, i5, n4, this.f5344k, tArr);
        } else if (!isEmpty()) {
            Object[] objArr = this.f5344k;
            b.m(0, this.f5343j, objArr.length, objArr, tArr);
            Object[] objArr2 = this.f5344k;
            b.m(objArr2.length - this.f5343j, 0, n4, objArr2, tArr);
        }
        int length2 = tArr.length;
        int i6 = this.f5345l;
        if (length2 > i6) {
            tArr[i6] = null;
        }
        return tArr;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean remove(Object obj) {
        int indexOf = indexOf(obj);
        if (indexOf == -1) {
            return false;
        }
        remove(indexOf);
        return true;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean add(E e4) {
        j(e4);
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public final boolean addAll(Collection<? extends E> collection) {
        v3.h.e(collection, "elements");
        if (collection.isEmpty()) {
            return false;
        }
        l(collection.size() + this.f5345l);
        k(n(this.f5343j + this.f5345l), collection);
        return true;
    }
}
