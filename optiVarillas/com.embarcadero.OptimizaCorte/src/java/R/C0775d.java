package r;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import r.i;

/* renamed from: r.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0775d<E> implements Collection<E>, Set<E> {

    /* renamed from: n  reason: collision with root package name */
    public static final int[] f5638n = new int[0];

    /* renamed from: o  reason: collision with root package name */
    public static final Object[] f5639o = new Object[0];

    /* renamed from: p  reason: collision with root package name */
    public static Object[] f5640p;

    /* renamed from: q  reason: collision with root package name */
    public static int f5641q;

    /* renamed from: r  reason: collision with root package name */
    public static Object[] f5642r;

    /* renamed from: s  reason: collision with root package name */
    public static int f5643s;

    /* renamed from: j  reason: collision with root package name */
    public int[] f5644j;

    /* renamed from: k  reason: collision with root package name */
    public Object[] f5645k;

    /* renamed from: l  reason: collision with root package name */
    public int f5646l;

    /* renamed from: m  reason: collision with root package name */
    public C0774c f5647m;

    public C0775d() {
        this(0);
    }

    public static void k(int[] iArr, Object[] objArr, int i4) {
        if (iArr.length == 8) {
            synchronized (C0775d.class) {
                try {
                    if (f5643s < 10) {
                        objArr[0] = f5642r;
                        objArr[1] = iArr;
                        for (int i5 = i4 - 1; i5 >= 2; i5--) {
                            objArr[i5] = null;
                        }
                        f5642r = objArr;
                        f5643s++;
                    }
                } finally {
                }
            }
        } else if (iArr.length == 4) {
            synchronized (C0775d.class) {
                try {
                    if (f5641q < 10) {
                        objArr[0] = f5640p;
                        objArr[1] = iArr;
                        for (int i6 = i4 - 1; i6 >= 2; i6--) {
                            objArr[i6] = null;
                        }
                        f5640p = objArr;
                        f5641q++;
                    }
                } finally {
                }
            }
        }
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean add(E e4) {
        int i4;
        int l2;
        if (e4 == null) {
            l2 = m();
            i4 = 0;
        } else {
            int hashCode = e4.hashCode();
            i4 = hashCode;
            l2 = l(hashCode, e4);
        }
        if (l2 >= 0) {
            return false;
        }
        int i5 = ~l2;
        int i6 = this.f5646l;
        int[] iArr = this.f5644j;
        if (i6 >= iArr.length) {
            int i7 = 8;
            if (i6 >= 8) {
                i7 = (i6 >> 1) + i6;
            } else if (i6 < 4) {
                i7 = 4;
            }
            Object[] objArr = this.f5645k;
            j(i7);
            int[] iArr2 = this.f5644j;
            if (iArr2.length > 0) {
                System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
                System.arraycopy(objArr, 0, this.f5645k, 0, objArr.length);
            }
            k(iArr, objArr, this.f5646l);
        }
        int i8 = this.f5646l;
        if (i5 < i8) {
            int[] iArr3 = this.f5644j;
            int i9 = i5 + 1;
            System.arraycopy(iArr3, i5, iArr3, i9, i8 - i5);
            Object[] objArr2 = this.f5645k;
            System.arraycopy(objArr2, i5, objArr2, i9, this.f5646l - i5);
        }
        this.f5644j[i5] = i4;
        this.f5645k[i5] = e4;
        this.f5646l++;
        return true;
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean addAll(Collection<? extends E> collection) {
        int size = collection.size() + this.f5646l;
        int[] iArr = this.f5644j;
        boolean z4 = false;
        if (iArr.length < size) {
            Object[] objArr = this.f5645k;
            j(size);
            int i4 = this.f5646l;
            if (i4 > 0) {
                System.arraycopy(iArr, 0, this.f5644j, 0, i4);
                System.arraycopy(objArr, 0, this.f5645k, 0, this.f5646l);
            }
            k(iArr, objArr, this.f5646l);
        }
        for (E e4 : collection) {
            z4 |= add(e4);
        }
        return z4;
    }

    @Override // java.util.Collection, java.util.Set
    public final void clear() {
        int i4 = this.f5646l;
        if (i4 != 0) {
            k(this.f5644j, this.f5645k, i4);
            this.f5644j = f5638n;
            this.f5645k = f5639o;
            this.f5646l = 0;
        }
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean contains(Object obj) {
        if (indexOf(obj) >= 0) {
            return true;
        }
        return false;
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean containsAll(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            if (!contains(it.next())) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Set) {
            Set set = (Set) obj;
            if (this.f5646l != set.size()) {
                return false;
            }
            for (int i4 = 0; i4 < this.f5646l; i4++) {
                try {
                    if (!set.contains(this.f5645k[i4])) {
                        return false;
                    }
                } catch (ClassCastException | NullPointerException unused) {
                }
            }
            return true;
        }
        return false;
    }

    @Override // java.util.Collection, java.util.Set
    public final int hashCode() {
        int[] iArr = this.f5644j;
        int i4 = this.f5646l;
        int i5 = 0;
        for (int i6 = 0; i6 < i4; i6++) {
            i5 += iArr[i6];
        }
        return i5;
    }

    public final int indexOf(Object obj) {
        if (obj == null) {
            return m();
        }
        return l(obj.hashCode(), obj);
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean isEmpty() {
        if (this.f5646l <= 0) {
            return true;
        }
        return false;
    }

    @Override // java.util.Collection, java.lang.Iterable, java.util.Set
    public final Iterator<E> iterator() {
        if (this.f5647m == null) {
            this.f5647m = new C0774c(this);
        }
        C0774c c0774c = this.f5647m;
        if (c0774c.f5665b == null) {
            c0774c.f5665b = new i.c();
        }
        return (Iterator<E>) c0774c.f5665b.iterator();
    }

    public final void j(int i4) {
        if (i4 == 8) {
            synchronized (C0775d.class) {
                try {
                    Object[] objArr = f5642r;
                    if (objArr != null) {
                        this.f5645k = objArr;
                        f5642r = (Object[]) objArr[0];
                        this.f5644j = (int[]) objArr[1];
                        objArr[1] = null;
                        objArr[0] = null;
                        f5643s--;
                        return;
                    }
                } finally {
                }
            }
        } else if (i4 == 4) {
            synchronized (C0775d.class) {
                try {
                    Object[] objArr2 = f5640p;
                    if (objArr2 != null) {
                        this.f5645k = objArr2;
                        f5640p = (Object[]) objArr2[0];
                        this.f5644j = (int[]) objArr2[1];
                        objArr2[1] = null;
                        objArr2[0] = null;
                        f5641q--;
                        return;
                    }
                } finally {
                }
            }
        }
        this.f5644j = new int[i4];
        this.f5645k = new Object[i4];
    }

    public final int l(int i4, Object obj) {
        int i5 = this.f5646l;
        if (i5 == 0) {
            return -1;
        }
        int a4 = C0777f.a(i5, i4, this.f5644j);
        if (a4 < 0) {
            return a4;
        }
        if (obj.equals(this.f5645k[a4])) {
            return a4;
        }
        int i6 = a4 + 1;
        while (i6 < i5 && this.f5644j[i6] == i4) {
            if (obj.equals(this.f5645k[i6])) {
                return i6;
            }
            i6++;
        }
        for (int i7 = a4 - 1; i7 >= 0 && this.f5644j[i7] == i4; i7--) {
            if (obj.equals(this.f5645k[i7])) {
                return i7;
            }
        }
        return ~i6;
    }

    public final int m() {
        int i4 = this.f5646l;
        if (i4 == 0) {
            return -1;
        }
        int a4 = C0777f.a(i4, 0, this.f5644j);
        if (a4 < 0) {
            return a4;
        }
        if (this.f5645k[a4] == null) {
            return a4;
        }
        int i5 = a4 + 1;
        while (i5 < i4 && this.f5644j[i5] == 0) {
            if (this.f5645k[i5] == null) {
                return i5;
            }
            i5++;
        }
        for (int i6 = a4 - 1; i6 >= 0 && this.f5644j[i6] == 0; i6--) {
            if (this.f5645k[i6] == null) {
                return i6;
            }
        }
        return ~i5;
    }

    public final void n(int i4) {
        Object[] objArr = this.f5645k;
        Object obj = objArr[i4];
        int i5 = this.f5646l;
        if (i5 <= 1) {
            k(this.f5644j, objArr, i5);
            this.f5644j = f5638n;
            this.f5645k = f5639o;
            this.f5646l = 0;
            return;
        }
        int[] iArr = this.f5644j;
        int i6 = 8;
        if (iArr.length > 8 && i5 < iArr.length / 3) {
            if (i5 > 8) {
                i6 = i5 + (i5 >> 1);
            }
            j(i6);
            this.f5646l--;
            if (i4 > 0) {
                System.arraycopy(iArr, 0, this.f5644j, 0, i4);
                System.arraycopy(objArr, 0, this.f5645k, 0, i4);
            }
            int i7 = this.f5646l;
            if (i4 < i7) {
                int i8 = i4 + 1;
                System.arraycopy(iArr, i8, this.f5644j, i4, i7 - i4);
                System.arraycopy(objArr, i8, this.f5645k, i4, this.f5646l - i4);
                return;
            }
            return;
        }
        int i9 = i5 - 1;
        this.f5646l = i9;
        if (i4 < i9) {
            int i10 = i4 + 1;
            System.arraycopy(iArr, i10, iArr, i4, i9 - i4);
            Object[] objArr2 = this.f5645k;
            System.arraycopy(objArr2, i10, objArr2, i4, this.f5646l - i4);
        }
        this.f5645k[this.f5646l] = null;
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean remove(Object obj) {
        int indexOf = indexOf(obj);
        if (indexOf >= 0) {
            n(indexOf);
            return true;
        }
        return false;
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean removeAll(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        boolean z4 = false;
        while (it.hasNext()) {
            z4 |= remove(it.next());
        }
        return z4;
    }

    @Override // java.util.Collection, java.util.Set
    public final boolean retainAll(Collection<?> collection) {
        boolean z4 = false;
        for (int i4 = this.f5646l - 1; i4 >= 0; i4--) {
            if (!collection.contains(this.f5645k[i4])) {
                n(i4);
                z4 = true;
            }
        }
        return z4;
    }

    @Override // java.util.Collection, java.util.Set
    public final int size() {
        return this.f5646l;
    }

    @Override // java.util.Collection, java.util.Set
    public final Object[] toArray() {
        int i4 = this.f5646l;
        Object[] objArr = new Object[i4];
        System.arraycopy(this.f5645k, 0, objArr, 0, i4);
        return objArr;
    }

    public final String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.f5646l * 14);
        sb.append('{');
        for (int i4 = 0; i4 < this.f5646l; i4++) {
            if (i4 > 0) {
                sb.append(", ");
            }
            Object obj = this.f5645k[i4];
            if (obj != this) {
                sb.append(obj);
            } else {
                sb.append("(this Set)");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public C0775d(int i4) {
        if (i4 == 0) {
            this.f5644j = f5638n;
            this.f5645k = f5639o;
        } else {
            j(i4);
        }
        this.f5646l = 0;
    }

    @Override // java.util.Collection, java.util.Set
    public final <T> T[] toArray(T[] tArr) {
        if (tArr.length < this.f5646l) {
            tArr = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), this.f5646l));
        }
        System.arraycopy(this.f5645k, 0, tArr, 0, this.f5646l);
        int length = tArr.length;
        int i4 = this.f5646l;
        if (length > i4) {
            tArr[i4] = null;
        }
        return tArr;
    }
}
