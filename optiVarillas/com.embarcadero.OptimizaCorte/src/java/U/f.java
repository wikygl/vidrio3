package u;

import I.h;
import com.google.android.gms.internal.ads.yn;
import java.util.Arrays;
import u.b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class f implements b.a {

    /* renamed from: a  reason: collision with root package name */
    public int f5920a = 16;

    /* renamed from: b  reason: collision with root package name */
    public final int[] f5921b = new int[16];

    /* renamed from: c  reason: collision with root package name */
    public int[] f5922c = new int[16];

    /* renamed from: d  reason: collision with root package name */
    public int[] f5923d = new int[16];

    /* renamed from: e  reason: collision with root package name */
    public float[] f5924e = new float[16];
    public int[] f = new int[16];

    /* renamed from: g  reason: collision with root package name */
    public int[] f5925g = new int[16];

    /* renamed from: h  reason: collision with root package name */
    public int f5926h = 0;

    /* renamed from: i  reason: collision with root package name */
    public int f5927i = -1;

    /* renamed from: j  reason: collision with root package name */
    public final b f5928j;

    /* renamed from: k  reason: collision with root package name */
    public final yn f5929k;

    public f(b bVar, yn ynVar) {
        this.f5928j = bVar;
        this.f5929k = ynVar;
        clear();
    }

    @Override // u.b.a
    public final float a(int i4) {
        int i5 = this.f5926h;
        int i6 = this.f5927i;
        for (int i7 = 0; i7 < i5; i7++) {
            if (i7 == i4) {
                return this.f5924e[i6];
            }
            i6 = this.f5925g[i6];
            if (i6 == -1) {
                return 0.0f;
            }
        }
        return 0.0f;
    }

    @Override // u.b.a
    public final float b(e eVar, boolean z4) {
        int[] iArr;
        int i4;
        int n4 = n(eVar);
        if (n4 == -1) {
            return 0.0f;
        }
        int i5 = eVar.f5905b;
        int i6 = i5 % 16;
        int[] iArr2 = this.f5921b;
        int i7 = iArr2[i6];
        if (i7 != -1) {
            if (this.f5923d[i7] == i5) {
                int[] iArr3 = this.f5922c;
                iArr2[i6] = iArr3[i7];
                iArr3[i7] = -1;
            } else {
                while (true) {
                    iArr = this.f5922c;
                    i4 = iArr[i7];
                    if (i4 == -1 || this.f5923d[i4] == i5) {
                        break;
                    }
                    i7 = i4;
                }
                if (i4 != -1 && this.f5923d[i4] == i5) {
                    iArr[i7] = iArr[i4];
                    iArr[i4] = -1;
                }
            }
        }
        float f = this.f5924e[n4];
        if (this.f5927i == n4) {
            this.f5927i = this.f5925g[n4];
        }
        this.f5923d[n4] = -1;
        int[] iArr4 = this.f;
        int i8 = iArr4[n4];
        if (i8 != -1) {
            int[] iArr5 = this.f5925g;
            iArr5[i8] = iArr5[n4];
        }
        int i9 = this.f5925g[n4];
        if (i9 != -1) {
            iArr4[i9] = iArr4[n4];
        }
        this.f5926h--;
        eVar.f5914l--;
        if (z4) {
            eVar.b(this.f5928j);
        }
        return f;
    }

    @Override // u.b.a
    public final float c(b bVar, boolean z4) {
        float g4 = g(bVar.f5879a);
        b(bVar.f5879a, z4);
        f fVar = (f) bVar.f5882d;
        int i4 = fVar.f5926h;
        int i5 = 0;
        int i6 = 0;
        while (i5 < i4) {
            int i7 = fVar.f5923d[i6];
            if (i7 != -1) {
                f(((e[]) this.f5929k.m)[i7], fVar.f5924e[i6] * g4, z4);
                i5++;
            }
            i6++;
        }
        return g4;
    }

    @Override // u.b.a
    public final void clear() {
        int i4 = this.f5926h;
        for (int i5 = 0; i5 < i4; i5++) {
            e i6 = i(i5);
            if (i6 != null) {
                i6.b(this.f5928j);
            }
        }
        for (int i7 = 0; i7 < this.f5920a; i7++) {
            this.f5923d[i7] = -1;
            this.f5922c[i7] = -1;
        }
        for (int i8 = 0; i8 < 16; i8++) {
            this.f5921b[i8] = -1;
        }
        this.f5926h = 0;
        this.f5927i = -1;
    }

    @Override // u.b.a
    public final int d() {
        return this.f5926h;
    }

    @Override // u.b.a
    public final boolean e(e eVar) {
        if (n(eVar) != -1) {
            return true;
        }
        return false;
    }

    @Override // u.b.a
    public final void f(e eVar, float f, boolean z4) {
        if (f > -0.001f && f < 0.001f) {
            return;
        }
        int n4 = n(eVar);
        if (n4 == -1) {
            h(eVar, f);
            return;
        }
        float[] fArr = this.f5924e;
        float f4 = fArr[n4] + f;
        fArr[n4] = f4;
        if (f4 > -0.001f && f4 < 0.001f) {
            fArr[n4] = 0.0f;
            b(eVar, z4);
        }
    }

    @Override // u.b.a
    public final float g(e eVar) {
        int n4 = n(eVar);
        if (n4 != -1) {
            return this.f5924e[n4];
        }
        return 0.0f;
    }

    @Override // u.b.a
    public final void h(e eVar, float f) {
        if (f > -0.001f && f < 0.001f) {
            b(eVar, true);
            return;
        }
        int i4 = 0;
        if (this.f5926h == 0) {
            m(0, eVar, f);
            l(eVar, 0);
            this.f5927i = 0;
            return;
        }
        int n4 = n(eVar);
        if (n4 != -1) {
            this.f5924e[n4] = f;
            return;
        }
        int i5 = this.f5926h + 1;
        int i6 = this.f5920a;
        if (i5 >= i6) {
            int i7 = i6 * 2;
            this.f5923d = Arrays.copyOf(this.f5923d, i7);
            this.f5924e = Arrays.copyOf(this.f5924e, i7);
            this.f = Arrays.copyOf(this.f, i7);
            this.f5925g = Arrays.copyOf(this.f5925g, i7);
            this.f5922c = Arrays.copyOf(this.f5922c, i7);
            for (int i8 = this.f5920a; i8 < i7; i8++) {
                this.f5923d[i8] = -1;
                this.f5922c[i8] = -1;
            }
            this.f5920a = i7;
        }
        int i9 = this.f5926h;
        int i10 = this.f5927i;
        int i11 = -1;
        for (int i12 = 0; i12 < i9; i12++) {
            int i13 = this.f5923d[i10];
            int i14 = eVar.f5905b;
            if (i13 == i14) {
                this.f5924e[i10] = f;
                return;
            }
            if (i13 < i14) {
                i11 = i10;
            }
            i10 = this.f5925g[i10];
            if (i10 == -1) {
                break;
            }
        }
        while (true) {
            if (i4 < this.f5920a) {
                if (this.f5923d[i4] == -1) {
                    break;
                }
                i4++;
            } else {
                i4 = -1;
                break;
            }
        }
        m(i4, eVar, f);
        if (i11 != -1) {
            this.f[i4] = i11;
            int[] iArr = this.f5925g;
            iArr[i4] = iArr[i11];
            iArr[i11] = i4;
        } else {
            this.f[i4] = -1;
            if (this.f5926h > 0) {
                this.f5925g[i4] = this.f5927i;
                this.f5927i = i4;
            } else {
                this.f5925g[i4] = -1;
            }
        }
        int i15 = this.f5925g[i4];
        if (i15 != -1) {
            this.f[i15] = i4;
        }
        l(eVar, i4);
    }

    @Override // u.b.a
    public final e i(int i4) {
        int i5 = this.f5926h;
        if (i5 == 0) {
            return null;
        }
        int i6 = this.f5927i;
        for (int i7 = 0; i7 < i5; i7++) {
            if (i7 == i4 && i6 != -1) {
                return ((e[]) this.f5929k.m)[this.f5923d[i6]];
            }
            i6 = this.f5925g[i6];
            if (i6 == -1) {
                break;
            }
        }
        return null;
    }

    @Override // u.b.a
    public final void j(float f) {
        int i4 = this.f5926h;
        int i5 = this.f5927i;
        for (int i6 = 0; i6 < i4; i6++) {
            float[] fArr = this.f5924e;
            fArr[i5] = fArr[i5] / f;
            i5 = this.f5925g[i5];
            if (i5 == -1) {
                return;
            }
        }
    }

    @Override // u.b.a
    public final void k() {
        int i4 = this.f5926h;
        int i5 = this.f5927i;
        for (int i6 = 0; i6 < i4; i6++) {
            float[] fArr = this.f5924e;
            fArr[i5] = fArr[i5] * (-1.0f);
            i5 = this.f5925g[i5];
            if (i5 == -1) {
                return;
            }
        }
    }

    public final void l(e eVar, int i4) {
        int[] iArr;
        int i5 = eVar.f5905b % 16;
        int[] iArr2 = this.f5921b;
        int i6 = iArr2[i5];
        if (i6 == -1) {
            iArr2[i5] = i4;
        } else {
            while (true) {
                iArr = this.f5922c;
                int i7 = iArr[i6];
                if (i7 == -1) {
                    break;
                }
                i6 = i7;
            }
            iArr[i6] = i4;
        }
        this.f5922c[i4] = -1;
    }

    public final void m(int i4, e eVar, float f) {
        this.f5923d[i4] = eVar.f5905b;
        this.f5924e[i4] = f;
        this.f[i4] = -1;
        this.f5925g[i4] = -1;
        eVar.a(this.f5928j);
        eVar.f5914l++;
        this.f5926h++;
    }

    public final int n(e eVar) {
        if (this.f5926h == 0) {
            return -1;
        }
        int i4 = eVar.f5905b;
        int i5 = this.f5921b[i4 % 16];
        if (i5 == -1) {
            return -1;
        }
        if (this.f5923d[i5] == i4) {
            return i5;
        }
        do {
            i5 = this.f5922c[i5];
            if (i5 == -1) {
                break;
            }
        } while (this.f5923d[i5] != i4);
        if (i5 == -1 || this.f5923d[i5] != i4) {
            return -1;
        }
        return i5;
    }

    public final String toString() {
        String c4;
        String c5;
        String str = hashCode() + " { ";
        int i4 = this.f5926h;
        for (int i5 = 0; i5 < i4; i5++) {
            e i6 = i(i5);
            if (i6 != null) {
                String str2 = str + i6 + " = " + a(i5) + " ";
                int n4 = n(i6);
                String c6 = h.c(str2, "[p: ");
                int i7 = this.f[n4];
                yn ynVar = this.f5929k;
                if (i7 != -1) {
                    c4 = c6 + ((e[]) ynVar.m)[this.f5923d[this.f[n4]]];
                } else {
                    c4 = h.c(c6, "none");
                }
                String c7 = h.c(c4, ", n: ");
                if (this.f5925g[n4] != -1) {
                    c5 = c7 + ((e[]) ynVar.m)[this.f5923d[this.f5925g[n4]]];
                } else {
                    c5 = h.c(c7, "none");
                }
                str = h.c(c5, "]");
            }
        }
        return h.c(str, " }");
    }
}
