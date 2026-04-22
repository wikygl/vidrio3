package L;

import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class c<T> {

    /* renamed from: a  reason: collision with root package name */
    public final Object[] f1424a;

    /* renamed from: b  reason: collision with root package name */
    public int f1425b;

    public c(int i4) {
        if (i4 > 0) {
            this.f1424a = new Object[i4];
            return;
        }
        throw new IllegalArgumentException("The max pool size must be > 0".toString());
    }

    public T a() {
        int i4 = this.f1425b;
        if (i4 <= 0) {
            return null;
        }
        int i5 = i4 - 1;
        Object[] objArr = this.f1424a;
        T t3 = (T) objArr[i5];
        h.c(t3, "null cannot be cast to non-null type T of androidx.core.util.Pools.SimplePool");
        objArr[i5] = null;
        this.f1425b--;
        return t3;
    }

    public boolean b(T t3) {
        Object[] objArr;
        boolean z4;
        h.e(t3, "instance");
        int i4 = this.f1425b;
        int i5 = 0;
        while (true) {
            objArr = this.f1424a;
            if (i5 < i4) {
                if (objArr[i5] == t3) {
                    z4 = true;
                    break;
                }
                i5++;
            } else {
                z4 = false;
                break;
            }
        }
        if (!z4) {
            int i6 = this.f1425b;
            if (i6 >= objArr.length) {
                return false;
            }
            objArr[i6] = t3;
            this.f1425b = i6 + 1;
            return true;
        }
        throw new IllegalStateException("Already in the pool!".toString());
    }
}
