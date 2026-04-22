package r;

import java.util.LinkedHashMap;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class h<K, V> {

    /* renamed from: a  reason: collision with root package name */
    public final LinkedHashMap<K, V> f5659a;

    /* renamed from: b  reason: collision with root package name */
    public int f5660b;

    /* renamed from: c  reason: collision with root package name */
    public final int f5661c;

    /* renamed from: d  reason: collision with root package name */
    public int f5662d;

    /* renamed from: e  reason: collision with root package name */
    public int f5663e;

    public h(int i4) {
        if (i4 > 0) {
            this.f5661c = i4;
            this.f5659a = new LinkedHashMap<>(0, 0.75f, true);
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }

    public final V a(K k4) {
        if (k4 != null) {
            synchronized (this) {
                try {
                    V v4 = this.f5659a.get(k4);
                    if (v4 != null) {
                        this.f5662d++;
                        return v4;
                    }
                    this.f5663e++;
                    return null;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        throw new NullPointerException("key == null");
    }

    public final V b(K k4, V v4) {
        V put;
        if (k4 != null) {
            synchronized (this) {
                try {
                    this.f5660b++;
                    put = this.f5659a.put(k4, v4);
                    if (put != null) {
                        this.f5660b--;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            c(this.f5661c);
            return put;
        }
        throw new NullPointerException("key == null || value == null");
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0065, code lost:
        throw new java.lang.IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void c(int r3) {
        /*
            r2 = this;
        L0:
            monitor-enter(r2)
            int r0 = r2.f5660b     // Catch: java.lang.Throwable -> L12
            if (r0 < 0) goto L47
            java.util.LinkedHashMap<K, V> r0 = r2.f5659a     // Catch: java.lang.Throwable -> L12
            boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> L12
            if (r0 == 0) goto L14
            int r0 = r2.f5660b     // Catch: java.lang.Throwable -> L12
            if (r0 != 0) goto L47
            goto L14
        L12:
            r3 = move-exception
            goto L66
        L14:
            int r0 = r2.f5660b     // Catch: java.lang.Throwable -> L12
            if (r0 <= r3) goto L45
            java.util.LinkedHashMap<K, V> r0 = r2.f5659a     // Catch: java.lang.Throwable -> L12
            boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> L12
            if (r0 == 0) goto L21
            goto L45
        L21:
            java.util.LinkedHashMap<K, V> r0 = r2.f5659a     // Catch: java.lang.Throwable -> L12
            java.util.Set r0 = r0.entrySet()     // Catch: java.lang.Throwable -> L12
            java.util.Iterator r0 = r0.iterator()     // Catch: java.lang.Throwable -> L12
            java.lang.Object r0 = r0.next()     // Catch: java.lang.Throwable -> L12
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch: java.lang.Throwable -> L12
            java.lang.Object r1 = r0.getKey()     // Catch: java.lang.Throwable -> L12
            r0.getValue()     // Catch: java.lang.Throwable -> L12
            java.util.LinkedHashMap<K, V> r0 = r2.f5659a     // Catch: java.lang.Throwable -> L12
            r0.remove(r1)     // Catch: java.lang.Throwable -> L12
            int r0 = r2.f5660b     // Catch: java.lang.Throwable -> L12
            int r0 = r0 + (-1)
            r2.f5660b = r0     // Catch: java.lang.Throwable -> L12
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L12
            goto L0
        L45:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L12
            return
        L47:
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L12
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L12
            r0.<init>()     // Catch: java.lang.Throwable -> L12
            java.lang.Class r1 = r2.getClass()     // Catch: java.lang.Throwable -> L12
            java.lang.String r1 = r1.getName()     // Catch: java.lang.Throwable -> L12
            r0.append(r1)     // Catch: java.lang.Throwable -> L12
            java.lang.String r1 = ".sizeOf() is reporting inconsistent results!"
            r0.append(r1)     // Catch: java.lang.Throwable -> L12
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> L12
            r3.<init>(r0)     // Catch: java.lang.Throwable -> L12
            throw r3     // Catch: java.lang.Throwable -> L12
        L66:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L12
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: r.h.c(int):void");
    }

    public final synchronized String toString() {
        int i4;
        int i5;
        int i6;
        int i7;
        try {
            int i8 = this.f5662d;
            int i9 = this.f5663e + i8;
            if (i9 != 0) {
                i4 = (i8 * 100) / i9;
            } else {
                i4 = 0;
            }
            Locale locale = Locale.US;
            i5 = this.f5661c;
            i6 = this.f5662d;
            i7 = this.f5663e;
        } catch (Throwable th) {
            throw th;
        }
        return "LruCache[maxSize=" + i5 + ",hits=" + i6 + ",misses=" + i7 + ",hitRate=" + i4 + "%]";
    }
}
