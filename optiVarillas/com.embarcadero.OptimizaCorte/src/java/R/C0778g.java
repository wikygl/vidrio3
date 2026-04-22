package r;

/* renamed from: r.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0778g<E> implements Cloneable {

    /* renamed from: n  reason: collision with root package name */
    public static final Object f5654n = new Object();

    /* renamed from: j  reason: collision with root package name */
    public boolean f5655j = false;

    /* renamed from: k  reason: collision with root package name */
    public long[] f5656k;

    /* renamed from: l  reason: collision with root package name */
    public Object[] f5657l;

    /* renamed from: m  reason: collision with root package name */
    public int f5658m;

    public C0778g() {
        int i4;
        int i5 = 4;
        while (true) {
            i4 = 80;
            if (i5 >= 32) {
                break;
            }
            int i6 = (1 << i5) - 12;
            if (80 <= i6) {
                i4 = i6;
                break;
            }
            i5++;
        }
        int i7 = i4 / 8;
        this.f5656k = new long[i7];
        this.f5657l = new Object[i7];
    }

    public final void a(long j4, Long l2) {
        int i4 = this.f5658m;
        if (i4 != 0 && j4 <= this.f5656k[i4 - 1]) {
            g(j4, l2);
            return;
        }
        if (this.f5655j && i4 >= this.f5656k.length) {
            d();
        }
        int i5 = this.f5658m;
        if (i5 >= this.f5656k.length) {
            int i6 = (i5 + 1) * 8;
            int i7 = 4;
            while (true) {
                if (i7 >= 32) {
                    break;
                }
                int i8 = (1 << i7) - 12;
                if (i6 <= i8) {
                    i6 = i8;
                    break;
                }
                i7++;
            }
            int i9 = i6 / 8;
            long[] jArr = new long[i9];
            Object[] objArr = new Object[i9];
            long[] jArr2 = this.f5656k;
            System.arraycopy(jArr2, 0, jArr, 0, jArr2.length);
            Object[] objArr2 = this.f5657l;
            System.arraycopy(objArr2, 0, objArr, 0, objArr2.length);
            this.f5656k = jArr;
            this.f5657l = objArr;
        }
        this.f5656k[i5] = j4;
        this.f5657l[i5] = l2;
        this.f5658m = i5 + 1;
    }

    public final void b() {
        int i4 = this.f5658m;
        Object[] objArr = this.f5657l;
        for (int i5 = 0; i5 < i4; i5++) {
            objArr[i5] = null;
        }
        this.f5658m = 0;
        this.f5655j = false;
    }

    /* renamed from: c */
    public final C0778g<E> clone() {
        try {
            C0778g<E> c0778g = (C0778g) super.clone();
            c0778g.f5656k = (long[]) this.f5656k.clone();
            c0778g.f5657l = (Object[]) this.f5657l.clone();
            return c0778g;
        } catch (CloneNotSupportedException e4) {
            throw new AssertionError(e4);
        }
    }

    public final void d() {
        int i4 = this.f5658m;
        long[] jArr = this.f5656k;
        Object[] objArr = this.f5657l;
        int i5 = 0;
        for (int i6 = 0; i6 < i4; i6++) {
            Object obj = objArr[i6];
            if (obj != f5654n) {
                if (i6 != i5) {
                    jArr[i5] = jArr[i6];
                    objArr[i5] = obj;
                    objArr[i6] = null;
                }
                i5++;
            }
        }
        this.f5655j = false;
        this.f5658m = i5;
    }

    public final Object e(long j4, Long l2) {
        Object obj;
        int b4 = C0777f.b(this.f5656k, this.f5658m, j4);
        if (b4 >= 0 && (obj = this.f5657l[b4]) != f5654n) {
            return obj;
        }
        return l2;
    }

    public final long f(int i4) {
        if (this.f5655j) {
            d();
        }
        return this.f5656k[i4];
    }

    public final void g(long j4, E e4) {
        int b4 = C0777f.b(this.f5656k, this.f5658m, j4);
        if (b4 >= 0) {
            this.f5657l[b4] = e4;
            return;
        }
        int i4 = ~b4;
        int i5 = this.f5658m;
        if (i4 < i5) {
            Object[] objArr = this.f5657l;
            if (objArr[i4] == f5654n) {
                this.f5656k[i4] = j4;
                objArr[i4] = e4;
                return;
            }
        }
        if (this.f5655j && i5 >= this.f5656k.length) {
            d();
            i4 = ~C0777f.b(this.f5656k, this.f5658m, j4);
        }
        int i6 = this.f5658m;
        if (i6 >= this.f5656k.length) {
            int i7 = (i6 + 1) * 8;
            int i8 = 4;
            while (true) {
                if (i8 >= 32) {
                    break;
                }
                int i9 = (1 << i8) - 12;
                if (i7 <= i9) {
                    i7 = i9;
                    break;
                }
                i8++;
            }
            int i10 = i7 / 8;
            long[] jArr = new long[i10];
            Object[] objArr2 = new Object[i10];
            long[] jArr2 = this.f5656k;
            System.arraycopy(jArr2, 0, jArr, 0, jArr2.length);
            Object[] objArr3 = this.f5657l;
            System.arraycopy(objArr3, 0, objArr2, 0, objArr3.length);
            this.f5656k = jArr;
            this.f5657l = objArr2;
        }
        int i11 = this.f5658m - i4;
        if (i11 != 0) {
            long[] jArr3 = this.f5656k;
            int i12 = i4 + 1;
            System.arraycopy(jArr3, i4, jArr3, i12, i11);
            Object[] objArr4 = this.f5657l;
            System.arraycopy(objArr4, i4, objArr4, i12, this.f5658m - i4);
        }
        this.f5656k[i4] = j4;
        this.f5657l[i4] = e4;
        this.f5658m++;
    }

    public final void h(long j4) {
        int b4 = C0777f.b(this.f5656k, this.f5658m, j4);
        if (b4 >= 0) {
            Object[] objArr = this.f5657l;
            Object obj = objArr[b4];
            Object obj2 = f5654n;
            if (obj != obj2) {
                objArr[b4] = obj2;
                this.f5655j = true;
            }
        }
    }

    public final int i() {
        if (this.f5655j) {
            d();
        }
        return this.f5658m;
    }

    public final E j(int i4) {
        if (this.f5655j) {
            d();
        }
        return (E) this.f5657l[i4];
    }

    public final String toString() {
        if (i() <= 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.f5658m * 28);
        sb.append('{');
        for (int i4 = 0; i4 < this.f5658m; i4++) {
            if (i4 > 0) {
                sb.append(", ");
            }
            sb.append(f(i4));
            sb.append('=');
            E j4 = j(i4);
            if (j4 != this) {
                sb.append(j4);
            } else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
