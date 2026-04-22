package F3;

import C3.O;
import F3.z;
import java.lang.Comparable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class y<T extends z & Comparable<? super T>> {

    /* renamed from: b  reason: collision with root package name */
    public static final AtomicIntegerFieldUpdater f951b = AtomicIntegerFieldUpdater.newUpdater(y.class, "_size");
    private volatile int _size;

    /* renamed from: a  reason: collision with root package name */
    public T[] f952a;

    public final void a(O.a aVar) {
        aVar.f((O.b) this);
        T[] tArr = this.f952a;
        AtomicIntegerFieldUpdater atomicIntegerFieldUpdater = f951b;
        if (tArr == null) {
            tArr = (T[]) new z[4];
            this.f952a = tArr;
        } else if (atomicIntegerFieldUpdater.get(this) >= tArr.length) {
            Object[] copyOf = Arrays.copyOf(tArr, atomicIntegerFieldUpdater.get(this) * 2);
            v3.h.d(copyOf, "copyOf(this, newSize)");
            tArr = (T[]) ((z[]) copyOf);
            this.f952a = tArr;
        }
        int i4 = atomicIntegerFieldUpdater.get(this);
        atomicIntegerFieldUpdater.set(this, i4 + 1);
        tArr[i4] = aVar;
        aVar.f441k = i4;
        e(i4);
    }

    public final T b() {
        T t3;
        synchronized (this) {
            T[] tArr = this.f952a;
            if (tArr != null) {
                t3 = tArr[0];
            } else {
                t3 = null;
            }
        }
        return t3;
    }

    public final void c(z zVar) {
        synchronized (this) {
            if (zVar.e() != null) {
                d(zVar.c());
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0064, code lost:
        if (((java.lang.Comparable) r6).compareTo(r7) < 0) goto L15;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final T d(int r9) {
        /*
            r8 = this;
            T extends F3.z & java.lang.Comparable<? super T>[] r0 = r8.f952a
            v3.h.b(r0)
            java.util.concurrent.atomic.AtomicIntegerFieldUpdater r1 = F3.y.f951b
            int r2 = r1.get(r8)
            r3 = -1
            int r2 = r2 + r3
            r1.set(r8, r2)
            int r2 = r1.get(r8)
            if (r9 >= r2) goto L80
            int r2 = r1.get(r8)
            r8.f(r9, r2)
            int r2 = r9 + (-1)
            int r2 = r2 / 2
            if (r9 <= 0) goto L3c
            r4 = r0[r9]
            v3.h.b(r4)
            java.lang.Comparable r4 = (java.lang.Comparable) r4
            r5 = r0[r2]
            v3.h.b(r5)
            int r4 = r4.compareTo(r5)
            if (r4 >= 0) goto L3c
            r8.f(r9, r2)
            r8.e(r2)
            goto L80
        L3c:
            int r2 = r9 * 2
            int r4 = r2 + 1
            int r5 = r1.get(r8)
            if (r4 < r5) goto L47
            goto L80
        L47:
            T extends F3.z & java.lang.Comparable<? super T>[] r5 = r8.f952a
            v3.h.b(r5)
            int r2 = r2 + 2
            int r6 = r1.get(r8)
            if (r2 >= r6) goto L67
            r6 = r5[r2]
            v3.h.b(r6)
            java.lang.Comparable r6 = (java.lang.Comparable) r6
            r7 = r5[r4]
            v3.h.b(r7)
            int r6 = r6.compareTo(r7)
            if (r6 >= 0) goto L67
            goto L68
        L67:
            r2 = r4
        L68:
            r4 = r5[r9]
            v3.h.b(r4)
            java.lang.Comparable r4 = (java.lang.Comparable) r4
            r5 = r5[r2]
            v3.h.b(r5)
            int r4 = r4.compareTo(r5)
            if (r4 > 0) goto L7b
            goto L80
        L7b:
            r8.f(r9, r2)
            r9 = r2
            goto L3c
        L80:
            int r9 = r1.get(r8)
            r9 = r0[r9]
            v3.h.b(r9)
            r2 = 0
            r9.f(r2)
            r9.a(r3)
            int r1 = r1.get(r8)
            r0[r1] = r2
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: F3.y.d(int):F3.z");
    }

    public final void e(int i4) {
        while (i4 > 0) {
            T[] tArr = this.f952a;
            v3.h.b(tArr);
            int i5 = (i4 - 1) / 2;
            T t3 = tArr[i5];
            v3.h.b(t3);
            T t4 = tArr[i4];
            v3.h.b(t4);
            if (((Comparable) t3).compareTo(t4) <= 0) {
                return;
            }
            f(i4, i5);
            i4 = i5;
        }
    }

    public final void f(int i4, int i5) {
        T[] tArr = this.f952a;
        v3.h.b(tArr);
        T t3 = tArr[i5];
        v3.h.b(t3);
        T t4 = tArr[i4];
        v3.h.b(t4);
        tArr[i4] = t3;
        tArr[i5] = t4;
        t3.a(i4);
        t4.a(i5);
    }
}
