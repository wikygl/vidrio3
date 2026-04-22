package m0;

import java.io.Closeable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import q0.InterfaceC0768c;

/* renamed from: m0.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0733i implements InterfaceC0768c, Closeable {

    /* renamed from: r  reason: collision with root package name */
    public static final TreeMap<Integer, C0733i> f5330r = new TreeMap<>();

    /* renamed from: j  reason: collision with root package name */
    public volatile String f5331j;

    /* renamed from: k  reason: collision with root package name */
    public final long[] f5332k;

    /* renamed from: l  reason: collision with root package name */
    public final double[] f5333l;

    /* renamed from: m  reason: collision with root package name */
    public final String[] f5334m;

    /* renamed from: n  reason: collision with root package name */
    public final byte[][] f5335n;

    /* renamed from: o  reason: collision with root package name */
    public final int[] f5336o;

    /* renamed from: p  reason: collision with root package name */
    public final int f5337p;

    /* renamed from: q  reason: collision with root package name */
    public int f5338q;

    public C0733i(int i4) {
        this.f5337p = i4;
        int i5 = i4 + 1;
        this.f5336o = new int[i5];
        this.f5332k = new long[i5];
        this.f5333l = new double[i5];
        this.f5334m = new String[i5];
        this.f5335n = new byte[i5];
    }

    public static C0733i b(String str, int i4) {
        TreeMap<Integer, C0733i> treeMap = f5330r;
        synchronized (treeMap) {
            try {
                Map.Entry<Integer, C0733i> ceilingEntry = treeMap.ceilingEntry(Integer.valueOf(i4));
                if (ceilingEntry != null) {
                    treeMap.remove(ceilingEntry.getKey());
                    C0733i value = ceilingEntry.getValue();
                    value.f5331j = str;
                    value.f5338q = i4;
                    return value;
                }
                C0733i c0733i = new C0733i(i4);
                c0733i.f5331j = str;
                c0733i.f5338q = i4;
                return c0733i;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // q0.InterfaceC0768c
    public final void a(r0.d dVar) {
        for (int i4 = 1; i4 <= this.f5338q; i4++) {
            int i5 = this.f5336o[i4];
            if (i5 != 1) {
                if (i5 != 2) {
                    if (i5 != 3) {
                        if (i5 != 4) {
                            if (i5 == 5) {
                                dVar.a(i4, this.f5335n[i4]);
                            }
                        } else {
                            dVar.g(this.f5334m[i4], i4);
                        }
                    } else {
                        dVar.b(i4, this.f5333l[i4]);
                    }
                } else {
                    dVar.d(i4, this.f5332k[i4]);
                }
            } else {
                dVar.f(i4);
            }
        }
    }

    @Override // q0.InterfaceC0768c
    public final String d() {
        return this.f5331j;
    }

    public final void f(int i4, long j4) {
        this.f5336o[i4] = 2;
        this.f5332k[i4] = j4;
    }

    public final void g(int i4) {
        this.f5336o[i4] = 1;
    }

    public final void i(String str, int i4) {
        this.f5336o[i4] = 4;
        this.f5334m[i4] = str;
    }

    public final void k() {
        TreeMap<Integer, C0733i> treeMap = f5330r;
        synchronized (treeMap) {
            treeMap.put(Integer.valueOf(this.f5337p), this);
            if (treeMap.size() > 15) {
                int size = treeMap.size() - 10;
                Iterator<Integer> it = treeMap.descendingKeySet().iterator();
                while (true) {
                    int i4 = size - 1;
                    if (size <= 0) {
                        break;
                    }
                    it.next();
                    it.remove();
                    size = i4;
                }
            }
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
    }
}
