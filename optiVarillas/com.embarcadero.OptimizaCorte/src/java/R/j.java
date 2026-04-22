package r;

import java.util.ConcurrentModificationException;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class j<K, V> {

    /* renamed from: m  reason: collision with root package name */
    public static Object[] f5679m;

    /* renamed from: n  reason: collision with root package name */
    public static int f5680n;

    /* renamed from: o  reason: collision with root package name */
    public static Object[] f5681o;

    /* renamed from: p  reason: collision with root package name */
    public static int f5682p;

    /* renamed from: j  reason: collision with root package name */
    public int[] f5683j;

    /* renamed from: k  reason: collision with root package name */
    public Object[] f5684k;

    /* renamed from: l  reason: collision with root package name */
    public int f5685l;

    public j() {
        this.f5683j = C0777f.f5652a;
        this.f5684k = C0777f.f5653b;
        this.f5685l = 0;
    }

    public static void c(int[] iArr, Object[] objArr, int i4) {
        if (iArr.length == 8) {
            synchronized (j.class) {
                try {
                    if (f5682p < 10) {
                        objArr[0] = f5681o;
                        objArr[1] = iArr;
                        for (int i5 = (i4 << 1) - 1; i5 >= 2; i5--) {
                            objArr[i5] = null;
                        }
                        f5681o = objArr;
                        f5682p++;
                    }
                } finally {
                }
            }
        } else if (iArr.length == 4) {
            synchronized (j.class) {
                try {
                    if (f5680n < 10) {
                        objArr[0] = f5679m;
                        objArr[1] = iArr;
                        for (int i6 = (i4 << 1) - 1; i6 >= 2; i6--) {
                            objArr[i6] = null;
                        }
                        f5679m = objArr;
                        f5680n++;
                    }
                } finally {
                }
            }
        }
    }

    public final void a(int i4) {
        if (i4 == 8) {
            synchronized (j.class) {
                try {
                    Object[] objArr = f5681o;
                    if (objArr != null) {
                        this.f5684k = objArr;
                        f5681o = (Object[]) objArr[0];
                        this.f5683j = (int[]) objArr[1];
                        objArr[1] = null;
                        objArr[0] = null;
                        f5682p--;
                        return;
                    }
                } finally {
                }
            }
        } else if (i4 == 4) {
            synchronized (j.class) {
                try {
                    Object[] objArr2 = f5679m;
                    if (objArr2 != null) {
                        this.f5684k = objArr2;
                        f5679m = (Object[]) objArr2[0];
                        this.f5683j = (int[]) objArr2[1];
                        objArr2[1] = null;
                        objArr2[0] = null;
                        f5680n--;
                        return;
                    }
                } finally {
                }
            }
        }
        this.f5683j = new int[i4];
        this.f5684k = new Object[i4 << 1];
    }

    public final void b(int i4) {
        int i5 = this.f5685l;
        int[] iArr = this.f5683j;
        if (iArr.length < i4) {
            Object[] objArr = this.f5684k;
            a(i4);
            if (this.f5685l > 0) {
                System.arraycopy(iArr, 0, this.f5683j, 0, i5);
                System.arraycopy(objArr, 0, this.f5684k, 0, i5 << 1);
            }
            c(iArr, objArr, i5);
        }
        if (this.f5685l == i5) {
            return;
        }
        throw new ConcurrentModificationException();
    }

    public final void clear() {
        int i4 = this.f5685l;
        if (i4 > 0) {
            int[] iArr = this.f5683j;
            Object[] objArr = this.f5684k;
            this.f5683j = C0777f.f5652a;
            this.f5684k = C0777f.f5653b;
            this.f5685l = 0;
            c(iArr, objArr, i4);
        }
        if (this.f5685l <= 0) {
            return;
        }
        throw new ConcurrentModificationException();
    }

    public final boolean containsKey(Object obj) {
        if (e(obj) >= 0) {
            return true;
        }
        return false;
    }

    public final boolean containsValue(Object obj) {
        if (g(obj) >= 0) {
            return true;
        }
        return false;
    }

    public final int d(int i4, Object obj) {
        int i5 = this.f5685l;
        if (i5 == 0) {
            return -1;
        }
        try {
            int a4 = C0777f.a(i5, i4, this.f5683j);
            if (a4 < 0) {
                return a4;
            }
            if (obj.equals(this.f5684k[a4 << 1])) {
                return a4;
            }
            int i6 = a4 + 1;
            while (i6 < i5 && this.f5683j[i6] == i4) {
                if (obj.equals(this.f5684k[i6 << 1])) {
                    return i6;
                }
                i6++;
            }
            for (int i7 = a4 - 1; i7 >= 0 && this.f5683j[i7] == i4; i7--) {
                if (obj.equals(this.f5684k[i7 << 1])) {
                    return i7;
                }
            }
            return ~i6;
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new ConcurrentModificationException();
        }
    }

    public final int e(Object obj) {
        if (obj == null) {
            return f();
        }
        return d(obj.hashCode(), obj);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof j) {
            j jVar = (j) obj;
            if (this.f5685l != jVar.f5685l) {
                return false;
            }
            for (int i4 = 0; i4 < this.f5685l; i4++) {
                try {
                    K h4 = h(i4);
                    V j4 = j(i4);
                    Object orDefault = jVar.getOrDefault(h4, null);
                    if (j4 == null) {
                        if (orDefault != null || !jVar.containsKey(h4)) {
                            return false;
                        }
                    } else if (!j4.equals(orDefault)) {
                        return false;
                    }
                } catch (ClassCastException | NullPointerException unused) {
                    return false;
                }
            }
            return true;
        }
        if (obj instanceof Map) {
            Map map = (Map) obj;
            if (this.f5685l != map.size()) {
                return false;
            }
            for (int i5 = 0; i5 < this.f5685l; i5++) {
                try {
                    K h5 = h(i5);
                    V j5 = j(i5);
                    Object obj2 = map.get(h5);
                    if (j5 == null) {
                        if (obj2 != null || !map.containsKey(h5)) {
                            return false;
                        }
                    } else if (!j5.equals(obj2)) {
                        return false;
                    }
                } catch (ClassCastException | NullPointerException unused2) {
                }
            }
            return true;
        }
        return false;
    }

    public final int f() {
        int i4 = this.f5685l;
        if (i4 == 0) {
            return -1;
        }
        try {
            int a4 = C0777f.a(i4, 0, this.f5683j);
            if (a4 < 0) {
                return a4;
            }
            if (this.f5684k[a4 << 1] == null) {
                return a4;
            }
            int i5 = a4 + 1;
            while (i5 < i4 && this.f5683j[i5] == 0) {
                if (this.f5684k[i5 << 1] == null) {
                    return i5;
                }
                i5++;
            }
            for (int i6 = a4 - 1; i6 >= 0 && this.f5683j[i6] == 0; i6--) {
                if (this.f5684k[i6 << 1] == null) {
                    return i6;
                }
            }
            return ~i5;
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new ConcurrentModificationException();
        }
    }

    public final int g(Object obj) {
        int i4 = this.f5685l * 2;
        Object[] objArr = this.f5684k;
        if (obj == null) {
            for (int i5 = 1; i5 < i4; i5 += 2) {
                if (objArr[i5] == null) {
                    return i5 >> 1;
                }
            }
            return -1;
        }
        for (int i6 = 1; i6 < i4; i6 += 2) {
            if (obj.equals(objArr[i6])) {
                return i6 >> 1;
            }
        }
        return -1;
    }

    public final V get(Object obj) {
        return getOrDefault(obj, null);
    }

    public final V getOrDefault(Object obj, V v4) {
        int e4 = e(obj);
        if (e4 >= 0) {
            return (V) this.f5684k[(e4 << 1) + 1];
        }
        return v4;
    }

    public final K h(int i4) {
        return (K) this.f5684k[i4 << 1];
    }

    public final int hashCode() {
        int hashCode;
        int[] iArr = this.f5683j;
        Object[] objArr = this.f5684k;
        int i4 = this.f5685l;
        int i5 = 1;
        int i6 = 0;
        int i7 = 0;
        while (i6 < i4) {
            Object obj = objArr[i5];
            int i8 = iArr[i6];
            if (obj == null) {
                hashCode = 0;
            } else {
                hashCode = obj.hashCode();
            }
            i7 += hashCode ^ i8;
            i6++;
            i5 += 2;
        }
        return i7;
    }

    public final V i(int i4) {
        Object[] objArr = this.f5684k;
        int i5 = i4 << 1;
        V v4 = (V) objArr[i5 + 1];
        int i6 = this.f5685l;
        int i7 = 0;
        if (i6 <= 1) {
            c(this.f5683j, objArr, i6);
            this.f5683j = C0777f.f5652a;
            this.f5684k = C0777f.f5653b;
        } else {
            int i8 = i6 - 1;
            int[] iArr = this.f5683j;
            int i9 = 8;
            if (iArr.length > 8 && i6 < iArr.length / 3) {
                if (i6 > 8) {
                    i9 = i6 + (i6 >> 1);
                }
                a(i9);
                if (i6 == this.f5685l) {
                    if (i4 > 0) {
                        System.arraycopy(iArr, 0, this.f5683j, 0, i4);
                        System.arraycopy(objArr, 0, this.f5684k, 0, i5);
                    }
                    if (i4 < i8) {
                        int i10 = i4 + 1;
                        int i11 = i8 - i4;
                        System.arraycopy(iArr, i10, this.f5683j, i4, i11);
                        System.arraycopy(objArr, i10 << 1, this.f5684k, i5, i11 << 1);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                if (i4 < i8) {
                    int i12 = i4 + 1;
                    int i13 = i8 - i4;
                    System.arraycopy(iArr, i12, iArr, i4, i13);
                    Object[] objArr2 = this.f5684k;
                    System.arraycopy(objArr2, i12 << 1, objArr2, i5, i13 << 1);
                }
                Object[] objArr3 = this.f5684k;
                int i14 = i8 << 1;
                objArr3[i14] = null;
                objArr3[i14 + 1] = null;
            }
            i7 = i8;
        }
        if (i6 == this.f5685l) {
            this.f5685l = i7;
            return v4;
        }
        throw new ConcurrentModificationException();
    }

    public final boolean isEmpty() {
        if (this.f5685l <= 0) {
            return true;
        }
        return false;
    }

    public final V j(int i4) {
        return (V) this.f5684k[(i4 << 1) + 1];
    }

    public final V put(K k4, V v4) {
        int i4;
        int d4;
        int i5 = this.f5685l;
        if (k4 == null) {
            d4 = f();
            i4 = 0;
        } else {
            int hashCode = k4.hashCode();
            i4 = hashCode;
            d4 = d(hashCode, k4);
        }
        if (d4 >= 0) {
            int i6 = (d4 << 1) + 1;
            Object[] objArr = this.f5684k;
            V v5 = (V) objArr[i6];
            objArr[i6] = v4;
            return v5;
        }
        int i7 = ~d4;
        int[] iArr = this.f5683j;
        if (i5 >= iArr.length) {
            int i8 = 8;
            if (i5 >= 8) {
                i8 = (i5 >> 1) + i5;
            } else if (i5 < 4) {
                i8 = 4;
            }
            Object[] objArr2 = this.f5684k;
            a(i8);
            if (i5 == this.f5685l) {
                int[] iArr2 = this.f5683j;
                if (iArr2.length > 0) {
                    System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
                    System.arraycopy(objArr2, 0, this.f5684k, 0, objArr2.length);
                }
                c(iArr, objArr2, i5);
            } else {
                throw new ConcurrentModificationException();
            }
        }
        if (i7 < i5) {
            int[] iArr3 = this.f5683j;
            int i9 = i7 + 1;
            System.arraycopy(iArr3, i7, iArr3, i9, i5 - i7);
            Object[] objArr3 = this.f5684k;
            System.arraycopy(objArr3, i7 << 1, objArr3, i9 << 1, (this.f5685l - i7) << 1);
        }
        int i10 = this.f5685l;
        if (i5 == i10) {
            int[] iArr4 = this.f5683j;
            if (i7 < iArr4.length) {
                iArr4[i7] = i4;
                Object[] objArr4 = this.f5684k;
                int i11 = i7 << 1;
                objArr4[i11] = k4;
                objArr4[i11 + 1] = v4;
                this.f5685l = i10 + 1;
                return null;
            }
        }
        throw new ConcurrentModificationException();
    }

    public final V putIfAbsent(K k4, V v4) {
        V orDefault = getOrDefault(k4, null);
        if (orDefault == null) {
            return put(k4, v4);
        }
        return orDefault;
    }

    public final V remove(Object obj) {
        int e4 = e(obj);
        if (e4 >= 0) {
            return i(e4);
        }
        return null;
    }

    public final V replace(K k4, V v4) {
        int e4 = e(k4);
        if (e4 >= 0) {
            int i4 = (e4 << 1) + 1;
            Object[] objArr = this.f5684k;
            V v5 = (V) objArr[i4];
            objArr[i4] = v4;
            return v5;
        }
        return null;
    }

    public final int size() {
        return this.f5685l;
    }

    public final String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.f5685l * 28);
        sb.append('{');
        for (int i4 = 0; i4 < this.f5685l; i4++) {
            if (i4 > 0) {
                sb.append(", ");
            }
            K h4 = h(i4);
            if (h4 != this) {
                sb.append(h4);
            } else {
                sb.append("(this Map)");
            }
            sb.append('=');
            V j4 = j(i4);
            if (j4 != this) {
                sb.append(j4);
            } else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public final boolean remove(Object obj, Object obj2) {
        int e4 = e(obj);
        if (e4 >= 0) {
            V j4 = j(e4);
            if (obj2 == j4 || (obj2 != null && obj2.equals(j4))) {
                i(e4);
                return true;
            }
            return false;
        }
        return false;
    }

    public final boolean replace(K k4, V v4, V v5) {
        int e4 = e(k4);
        if (e4 >= 0) {
            V j4 = j(e4);
            if (j4 == v4 || (v4 != null && v4.equals(j4))) {
                int i4 = (e4 << 1) + 1;
                Object[] objArr = this.f5684k;
                Object obj = objArr[i4];
                objArr[i4] = v5;
                return true;
            }
            return false;
        }
        return false;
    }

    public j(int i4) {
        if (i4 == 0) {
            this.f5683j = C0777f.f5652a;
            this.f5684k = C0777f.f5653b;
        } else {
            a(i4);
        }
        this.f5685l = 0;
    }

    public j(j<K, V> jVar) {
        this();
        if (jVar != null) {
            int i4 = jVar.f5685l;
            b(this.f5685l + i4);
            if (this.f5685l != 0) {
                for (int i5 = 0; i5 < i4; i5++) {
                    put(jVar.h(i5), jVar.j(i5));
                }
            } else if (i4 > 0) {
                System.arraycopy(jVar.f5683j, 0, this.f5683j, 0, i4);
                System.arraycopy(jVar.f5684k, 0, this.f5684k, 0, i4 << 1);
                this.f5685l = i4;
            }
        }
    }
}
