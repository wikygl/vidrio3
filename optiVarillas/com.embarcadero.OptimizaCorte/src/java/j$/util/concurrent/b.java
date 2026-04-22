package j$.util.concurrent;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class b implements Collection, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;

    /* renamed from: a  reason: collision with root package name */
    final ConcurrentHashMap f4154a;

    /* JADX INFO: Access modifiers changed from: package-private */
    public b(ConcurrentHashMap concurrentHashMap) {
        this.f4154a = concurrentHashMap;
    }

    @Override // java.util.Collection
    public final void clear() {
        this.f4154a.clear();
    }

    @Override // java.util.Collection
    public abstract boolean contains(Object obj);

    /* JADX WARN: Removed duplicated region for block: B:6:0x000c  */
    @Override // java.util.Collection
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean containsAll(java.util.Collection r2) {
        /*
            r1 = this;
            if (r2 == r1) goto L1a
            java.util.Iterator r2 = r2.iterator()
        L6:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L1a
            java.lang.Object r0 = r2.next()
            if (r0 == 0) goto L18
            boolean r0 = r1.contains(r0)
            if (r0 != 0) goto L6
        L18:
            r2 = 0
            return r2
        L1a:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.b.containsAll(java.util.Collection):boolean");
    }

    @Override // java.util.Collection
    public final boolean isEmpty() {
        return this.f4154a.isEmpty();
    }

    @Override // java.util.Collection, java.lang.Iterable
    public abstract Iterator iterator();

    @Override // java.util.Collection
    public abstract boolean remove(Object obj);

    @Override // java.util.Collection
    public boolean removeAll(Collection collection) {
        collection.getClass();
        l[] lVarArr = this.f4154a.f4141a;
        boolean z4 = false;
        if (lVarArr == null) {
            return false;
        }
        if (!(collection instanceof Set) || collection.size() <= lVarArr.length) {
            for (Object obj : collection) {
                z4 |= remove(obj);
            }
        } else {
            Iterator it = iterator();
            while (it.hasNext()) {
                if (collection.contains(it.next())) {
                    it.remove();
                    z4 = true;
                }
            }
        }
        return z4;
    }

    @Override // java.util.Collection
    public final boolean retainAll(Collection collection) {
        collection.getClass();
        Iterator it = iterator();
        boolean z4 = false;
        while (it.hasNext()) {
            if (!collection.contains(it.next())) {
                it.remove();
                z4 = true;
            }
        }
        return z4;
    }

    @Override // java.util.Collection
    public final int size() {
        return this.f4154a.size();
    }

    @Override // java.util.Collection
    public final Object[] toArray() {
        long j4 = this.f4154a.j();
        if (j4 < 0) {
            j4 = 0;
        }
        if (j4 <= 2147483639) {
            int i4 = (int) j4;
            Object[] objArr = new Object[i4];
            Iterator it = iterator();
            int i5 = 0;
            while (it.hasNext()) {
                Object next = it.next();
                if (i5 == i4) {
                    if (i4 >= 2147483639) {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                    int i6 = i4 < 1073741819 ? (i4 >>> 1) + 1 + i4 : 2147483639;
                    objArr = Arrays.copyOf(objArr, i6);
                    i4 = i6;
                }
                objArr[i5] = next;
                i5++;
            }
            return i5 == i4 ? objArr : Arrays.copyOf(objArr, i5);
        }
        throw new OutOfMemoryError("Required array size too large");
    }

    @Override // java.util.Collection
    public final Object[] toArray(Object[] objArr) {
        long j4 = this.f4154a.j();
        if (j4 < 0) {
            j4 = 0;
        }
        if (j4 <= 2147483639) {
            int i4 = (int) j4;
            Object[] objArr2 = objArr.length >= i4 ? objArr : (Object[]) Array.newInstance(objArr.getClass().getComponentType(), i4);
            int length = objArr2.length;
            Iterator it = iterator();
            int i5 = 0;
            while (it.hasNext()) {
                Object next = it.next();
                if (i5 == length) {
                    if (length >= 2147483639) {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                    int i6 = length < 1073741819 ? (length >>> 1) + 1 + length : 2147483639;
                    objArr2 = Arrays.copyOf(objArr2, i6);
                    length = i6;
                }
                objArr2[i5] = next;
                i5++;
            }
            if (objArr != objArr2 || i5 >= length) {
                return i5 == length ? objArr2 : Arrays.copyOf(objArr2, i5);
            }
            objArr2[i5] = null;
            return objArr2;
        }
        throw new OutOfMemoryError("Required array size too large");
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("[");
        Iterator it = iterator();
        if (it.hasNext()) {
            while (true) {
                Object next = it.next();
                if (next == this) {
                    next = "(this Collection)";
                }
                sb.append(next);
                if (!it.hasNext()) {
                    break;
                }
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
