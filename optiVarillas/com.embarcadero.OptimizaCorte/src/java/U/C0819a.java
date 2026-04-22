package u;

import I.h;
import com.google.android.gms.internal.ads.yn;
import java.util.Arrays;
import u.b;

/* renamed from: u.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0819a implements b.a {

    /* renamed from: b  reason: collision with root package name */
    public final b f5871b;

    /* renamed from: c  reason: collision with root package name */
    public final yn f5872c;

    /* renamed from: a  reason: collision with root package name */
    public int f5870a = 0;

    /* renamed from: d  reason: collision with root package name */
    public int f5873d = 8;

    /* renamed from: e  reason: collision with root package name */
    public int[] f5874e = new int[8];
    public int[] f = new int[8];

    /* renamed from: g  reason: collision with root package name */
    public float[] f5875g = new float[8];

    /* renamed from: h  reason: collision with root package name */
    public int f5876h = -1;

    /* renamed from: i  reason: collision with root package name */
    public int f5877i = -1;

    /* renamed from: j  reason: collision with root package name */
    public boolean f5878j = false;

    public C0819a(b bVar, yn ynVar) {
        this.f5871b = bVar;
        this.f5872c = ynVar;
    }

    @Override // u.b.a
    public final float a(int i4) {
        int i5 = this.f5876h;
        for (int i6 = 0; i5 != -1 && i6 < this.f5870a; i6++) {
            if (i6 == i4) {
                return this.f5875g[i5];
            }
            i5 = this.f[i5];
        }
        return 0.0f;
    }

    @Override // u.b.a
    public final float b(e eVar, boolean z4) {
        int i4 = this.f5876h;
        if (i4 == -1) {
            return 0.0f;
        }
        int i5 = 0;
        int i6 = -1;
        while (i4 != -1 && i5 < this.f5870a) {
            if (this.f5874e[i4] == eVar.f5905b) {
                if (i4 == this.f5876h) {
                    this.f5876h = this.f[i4];
                } else {
                    int[] iArr = this.f;
                    iArr[i6] = iArr[i4];
                }
                if (z4) {
                    eVar.b(this.f5871b);
                }
                eVar.f5914l--;
                this.f5870a--;
                this.f5874e[i4] = -1;
                if (this.f5878j) {
                    this.f5877i = i4;
                }
                return this.f5875g[i4];
            }
            i5++;
            i6 = i4;
            i4 = this.f[i4];
        }
        return 0.0f;
    }

    @Override // u.b.a
    public final float c(b bVar, boolean z4) {
        float g4 = g(bVar.f5879a);
        b(bVar.f5879a, z4);
        b.a aVar = bVar.f5882d;
        int d4 = aVar.d();
        for (int i4 = 0; i4 < d4; i4++) {
            e i5 = aVar.i(i4);
            f(i5, aVar.g(i5) * g4, z4);
        }
        return g4;
    }

    @Override // u.b.a
    public final void clear() {
        int i4 = this.f5876h;
        for (int i5 = 0; i4 != -1 && i5 < this.f5870a; i5++) {
            e eVar = ((e[]) this.f5872c.m)[this.f5874e[i4]];
            if (eVar != null) {
                eVar.b(this.f5871b);
            }
            i4 = this.f[i4];
        }
        this.f5876h = -1;
        this.f5877i = -1;
        this.f5878j = false;
        this.f5870a = 0;
    }

    @Override // u.b.a
    public final int d() {
        return this.f5870a;
    }

    @Override // u.b.a
    public final boolean e(e eVar) {
        int i4 = this.f5876h;
        if (i4 == -1) {
            return false;
        }
        for (int i5 = 0; i4 != -1 && i5 < this.f5870a; i5++) {
            if (this.f5874e[i4] == eVar.f5905b) {
                return true;
            }
            i4 = this.f[i4];
        }
        return false;
    }

    @Override // u.b.a
    public final void f(e eVar, float f, boolean z4) {
        if (f > -0.001f && f < 0.001f) {
            return;
        }
        int i4 = this.f5876h;
        b bVar = this.f5871b;
        if (i4 == -1) {
            this.f5876h = 0;
            this.f5875g[0] = f;
            this.f5874e[0] = eVar.f5905b;
            this.f[0] = -1;
            eVar.f5914l++;
            eVar.a(bVar);
            this.f5870a++;
            if (!this.f5878j) {
                int i5 = this.f5877i + 1;
                this.f5877i = i5;
                int[] iArr = this.f5874e;
                if (i5 >= iArr.length) {
                    this.f5878j = true;
                    this.f5877i = iArr.length - 1;
                    return;
                }
                return;
            }
            return;
        }
        int i6 = -1;
        for (int i7 = 0; i4 != -1 && i7 < this.f5870a; i7++) {
            int i8 = this.f5874e[i4];
            int i9 = eVar.f5905b;
            if (i8 == i9) {
                float[] fArr = this.f5875g;
                float f4 = fArr[i4] + f;
                if (f4 > -0.001f && f4 < 0.001f) {
                    f4 = 0.0f;
                }
                fArr[i4] = f4;
                if (f4 == 0.0f) {
                    if (i4 == this.f5876h) {
                        this.f5876h = this.f[i4];
                    } else {
                        int[] iArr2 = this.f;
                        iArr2[i6] = iArr2[i4];
                    }
                    if (z4) {
                        eVar.b(bVar);
                    }
                    if (this.f5878j) {
                        this.f5877i = i4;
                    }
                    eVar.f5914l--;
                    this.f5870a--;
                    return;
                }
                return;
            }
            if (i8 < i9) {
                i6 = i4;
            }
            i4 = this.f[i4];
        }
        int i10 = this.f5877i;
        int i11 = i10 + 1;
        if (this.f5878j) {
            int[] iArr3 = this.f5874e;
            if (iArr3[i10] != -1) {
                i10 = iArr3.length;
            }
        } else {
            i10 = i11;
        }
        int[] iArr4 = this.f5874e;
        if (i10 >= iArr4.length && this.f5870a < iArr4.length) {
            int i12 = 0;
            while (true) {
                int[] iArr5 = this.f5874e;
                if (i12 >= iArr5.length) {
                    break;
                } else if (iArr5[i12] == -1) {
                    i10 = i12;
                    break;
                } else {
                    i12++;
                }
            }
        }
        int[] iArr6 = this.f5874e;
        if (i10 >= iArr6.length) {
            i10 = iArr6.length;
            int i13 = this.f5873d * 2;
            this.f5873d = i13;
            this.f5878j = false;
            this.f5877i = i10 - 1;
            this.f5875g = Arrays.copyOf(this.f5875g, i13);
            this.f5874e = Arrays.copyOf(this.f5874e, this.f5873d);
            this.f = Arrays.copyOf(this.f, this.f5873d);
        }
        this.f5874e[i10] = eVar.f5905b;
        this.f5875g[i10] = f;
        if (i6 != -1) {
            int[] iArr7 = this.f;
            iArr7[i10] = iArr7[i6];
            iArr7[i6] = i10;
        } else {
            this.f[i10] = this.f5876h;
            this.f5876h = i10;
        }
        eVar.f5914l++;
        eVar.a(bVar);
        this.f5870a++;
        if (!this.f5878j) {
            this.f5877i++;
        }
        int i14 = this.f5877i;
        int[] iArr8 = this.f5874e;
        if (i14 >= iArr8.length) {
            this.f5878j = true;
            this.f5877i = iArr8.length - 1;
        }
    }

    @Override // u.b.a
    public final float g(e eVar) {
        int i4 = this.f5876h;
        for (int i5 = 0; i4 != -1 && i5 < this.f5870a; i5++) {
            if (this.f5874e[i4] == eVar.f5905b) {
                return this.f5875g[i4];
            }
            i4 = this.f[i4];
        }
        return 0.0f;
    }

    @Override // u.b.a
    public final void h(e eVar, float f) {
        if (f == 0.0f) {
            b(eVar, true);
            return;
        }
        int i4 = this.f5876h;
        b bVar = this.f5871b;
        if (i4 == -1) {
            this.f5876h = 0;
            this.f5875g[0] = f;
            this.f5874e[0] = eVar.f5905b;
            this.f[0] = -1;
            eVar.f5914l++;
            eVar.a(bVar);
            this.f5870a++;
            if (!this.f5878j) {
                int i5 = this.f5877i + 1;
                this.f5877i = i5;
                int[] iArr = this.f5874e;
                if (i5 >= iArr.length) {
                    this.f5878j = true;
                    this.f5877i = iArr.length - 1;
                    return;
                }
                return;
            }
            return;
        }
        int i6 = -1;
        for (int i7 = 0; i4 != -1 && i7 < this.f5870a; i7++) {
            int i8 = this.f5874e[i4];
            int i9 = eVar.f5905b;
            if (i8 == i9) {
                this.f5875g[i4] = f;
                return;
            }
            if (i8 < i9) {
                i6 = i4;
            }
            i4 = this.f[i4];
        }
        int i10 = this.f5877i;
        int i11 = i10 + 1;
        if (this.f5878j) {
            int[] iArr2 = this.f5874e;
            if (iArr2[i10] != -1) {
                i10 = iArr2.length;
            }
        } else {
            i10 = i11;
        }
        int[] iArr3 = this.f5874e;
        if (i10 >= iArr3.length && this.f5870a < iArr3.length) {
            int i12 = 0;
            while (true) {
                int[] iArr4 = this.f5874e;
                if (i12 >= iArr4.length) {
                    break;
                } else if (iArr4[i12] == -1) {
                    i10 = i12;
                    break;
                } else {
                    i12++;
                }
            }
        }
        int[] iArr5 = this.f5874e;
        if (i10 >= iArr5.length) {
            i10 = iArr5.length;
            int i13 = this.f5873d * 2;
            this.f5873d = i13;
            this.f5878j = false;
            this.f5877i = i10 - 1;
            this.f5875g = Arrays.copyOf(this.f5875g, i13);
            this.f5874e = Arrays.copyOf(this.f5874e, this.f5873d);
            this.f = Arrays.copyOf(this.f, this.f5873d);
        }
        this.f5874e[i10] = eVar.f5905b;
        this.f5875g[i10] = f;
        if (i6 != -1) {
            int[] iArr6 = this.f;
            iArr6[i10] = iArr6[i6];
            iArr6[i6] = i10;
        } else {
            this.f[i10] = this.f5876h;
            this.f5876h = i10;
        }
        eVar.f5914l++;
        eVar.a(bVar);
        int i14 = this.f5870a + 1;
        this.f5870a = i14;
        if (!this.f5878j) {
            this.f5877i++;
        }
        int[] iArr7 = this.f5874e;
        if (i14 >= iArr7.length) {
            this.f5878j = true;
        }
        if (this.f5877i >= iArr7.length) {
            this.f5878j = true;
            this.f5877i = iArr7.length - 1;
        }
    }

    @Override // u.b.a
    public final e i(int i4) {
        int i5 = this.f5876h;
        for (int i6 = 0; i5 != -1 && i6 < this.f5870a; i6++) {
            if (i6 == i4) {
                return ((e[]) this.f5872c.m)[this.f5874e[i5]];
            }
            i5 = this.f[i5];
        }
        return null;
    }

    @Override // u.b.a
    public final void j(float f) {
        int i4 = this.f5876h;
        for (int i5 = 0; i4 != -1 && i5 < this.f5870a; i5++) {
            float[] fArr = this.f5875g;
            fArr[i4] = fArr[i4] / f;
            i4 = this.f[i4];
        }
    }

    @Override // u.b.a
    public final void k() {
        int i4 = this.f5876h;
        for (int i5 = 0; i4 != -1 && i5 < this.f5870a; i5++) {
            float[] fArr = this.f5875g;
            fArr[i4] = fArr[i4] * (-1.0f);
            i4 = this.f[i4];
        }
    }

    public final String toString() {
        int i4 = this.f5876h;
        String str = "";
        for (int i5 = 0; i4 != -1 && i5 < this.f5870a; i5++) {
            str = (h.c(str, " -> ") + this.f5875g[i4] + " : ") + ((e[]) this.f5872c.m)[this.f5874e[i4]];
            i4 = this.f[i4];
        }
        return str;
    }
}
