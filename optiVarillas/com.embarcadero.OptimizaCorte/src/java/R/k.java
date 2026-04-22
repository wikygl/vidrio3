package r;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class k<E> implements Cloneable {

    /* renamed from: m  reason: collision with root package name */
    public static final Object f5686m = new Object();

    /* renamed from: j  reason: collision with root package name */
    public int[] f5687j;

    /* renamed from: k  reason: collision with root package name */
    public Object[] f5688k;

    /* renamed from: l  reason: collision with root package name */
    public int f5689l;

    public k() {
        int i4;
        int i5 = 4;
        while (true) {
            i4 = 40;
            if (i5 >= 32) {
                break;
            }
            int i6 = (1 << i5) - 12;
            if (40 <= i6) {
                i4 = i6;
                break;
            }
            i5++;
        }
        int i7 = i4 / 4;
        this.f5687j = new int[i7];
        this.f5688k = new Object[i7];
    }

    public final void a(int i4, E e4) {
        int i5 = this.f5689l;
        if (i5 != 0 && i4 <= this.f5687j[i5 - 1]) {
            d(i4, e4);
            return;
        }
        if (i5 >= this.f5687j.length) {
            int i6 = (i5 + 1) * 4;
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
            int i9 = i6 / 4;
            int[] iArr = new int[i9];
            Object[] objArr = new Object[i9];
            int[] iArr2 = this.f5687j;
            System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
            Object[] objArr2 = this.f5688k;
            System.arraycopy(objArr2, 0, objArr, 0, objArr2.length);
            this.f5687j = iArr;
            this.f5688k = objArr;
        }
        this.f5687j[i5] = i4;
        this.f5688k[i5] = e4;
        this.f5689l = i5 + 1;
    }

    /* renamed from: b */
    public final k<E> clone() {
        try {
            k<E> kVar = (k) super.clone();
            kVar.f5687j = (int[]) this.f5687j.clone();
            kVar.f5688k = (Object[]) this.f5688k.clone();
            return kVar;
        } catch (CloneNotSupportedException e4) {
            throw new AssertionError(e4);
        }
    }

    public final Object c(int i4, Integer num) {
        Object obj;
        int a4 = C0777f.a(this.f5689l, i4, this.f5687j);
        if (a4 >= 0 && (obj = this.f5688k[a4]) != f5686m) {
            return obj;
        }
        return num;
    }

    public final void d(int i4, E e4) {
        int a4 = C0777f.a(this.f5689l, i4, this.f5687j);
        if (a4 >= 0) {
            this.f5688k[a4] = e4;
            return;
        }
        int i5 = ~a4;
        int i6 = this.f5689l;
        if (i5 < i6) {
            Object[] objArr = this.f5688k;
            if (objArr[i5] == f5686m) {
                this.f5687j[i5] = i4;
                objArr[i5] = e4;
                return;
            }
        }
        if (i6 >= this.f5687j.length) {
            int i7 = (i6 + 1) * 4;
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
            int i10 = i7 / 4;
            int[] iArr = new int[i10];
            Object[] objArr2 = new Object[i10];
            int[] iArr2 = this.f5687j;
            System.arraycopy(iArr2, 0, iArr, 0, iArr2.length);
            Object[] objArr3 = this.f5688k;
            System.arraycopy(objArr3, 0, objArr2, 0, objArr3.length);
            this.f5687j = iArr;
            this.f5688k = objArr2;
        }
        int i11 = this.f5689l - i5;
        if (i11 != 0) {
            int[] iArr3 = this.f5687j;
            int i12 = i5 + 1;
            System.arraycopy(iArr3, i5, iArr3, i12, i11);
            Object[] objArr4 = this.f5688k;
            System.arraycopy(objArr4, i5, objArr4, i12, this.f5689l - i5);
        }
        this.f5687j[i5] = i4;
        this.f5688k[i5] = e4;
        this.f5689l++;
    }

    public final String toString() {
        int i4 = this.f5689l;
        if (i4 <= 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(i4 * 28);
        sb.append('{');
        for (int i5 = 0; i5 < this.f5689l; i5++) {
            if (i5 > 0) {
                sb.append(", ");
            }
            sb.append(this.f5687j[i5]);
            sb.append('=');
            Object obj = this.f5688k[i5];
            if (obj != this) {
                sb.append(obj);
            } else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
