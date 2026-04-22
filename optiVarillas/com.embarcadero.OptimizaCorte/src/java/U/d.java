package u;

import java.util.Arrays;
import java.util.Comparator;
import u.b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class d extends u.b {
    public e[] f;

    /* renamed from: g  reason: collision with root package name */
    public e[] f5899g;

    /* renamed from: h  reason: collision with root package name */
    public int f5900h;

    /* renamed from: i  reason: collision with root package name */
    public b f5901i;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class a implements Comparator<e> {
        @Override // java.util.Comparator
        public final int compare(e eVar, e eVar2) {
            return eVar.f5905b - eVar2.f5905b;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class b implements Comparable {

        /* renamed from: j  reason: collision with root package name */
        public e f5902j;

        public b() {
        }

        @Override // java.lang.Comparable
        public final int compareTo(Object obj) {
            return this.f5902j.f5905b - ((e) obj).f5905b;
        }

        public final String toString() {
            String str = "[ ";
            if (this.f5902j != null) {
                for (int i4 = 0; i4 < 9; i4++) {
                    str = str + this.f5902j.f5910h[i4] + " ";
                }
            }
            return str + "] " + this.f5902j;
        }
    }

    @Override // u.b, u.c.a
    public final e a(boolean[] zArr) {
        int i4 = -1;
        for (int i5 = 0; i5 < this.f5900h; i5++) {
            e[] eVarArr = this.f;
            e eVar = eVarArr[i5];
            if (!zArr[eVar.f5905b]) {
                b bVar = this.f5901i;
                bVar.f5902j = eVar;
                int i6 = 8;
                if (i4 == -1) {
                    while (i6 >= 0) {
                        float f = bVar.f5902j.f5910h[i6];
                        if (f <= 0.0f) {
                            if (f < 0.0f) {
                                i4 = i5;
                                break;
                            }
                            i6--;
                        }
                    }
                } else {
                    e eVar2 = eVarArr[i4];
                    while (true) {
                        if (i6 >= 0) {
                            float f4 = eVar2.f5910h[i6];
                            float f5 = bVar.f5902j.f5910h[i6];
                            if (f5 == f4) {
                                i6--;
                            } else if (f5 >= f4) {
                            }
                        }
                    }
                }
            }
        }
        if (i4 == -1) {
            return null;
        }
        return this.f[i4];
    }

    @Override // u.b
    public final void h(u.b bVar, boolean z4) {
        e eVar = bVar.f5879a;
        if (eVar == null) {
            return;
        }
        b.a aVar = bVar.f5882d;
        int d4 = aVar.d();
        for (int i4 = 0; i4 < d4; i4++) {
            e i5 = aVar.i(i4);
            float a4 = aVar.a(i4);
            b bVar2 = this.f5901i;
            bVar2.f5902j = i5;
            boolean z5 = i5.f5904a;
            float[] fArr = eVar.f5910h;
            if (z5) {
                boolean z6 = true;
                for (int i6 = 0; i6 < 9; i6++) {
                    float[] fArr2 = bVar2.f5902j.f5910h;
                    float f = (fArr[i6] * a4) + fArr2[i6];
                    fArr2[i6] = f;
                    if (Math.abs(f) < 1.0E-4f) {
                        bVar2.f5902j.f5910h[i6] = 0.0f;
                    } else {
                        z6 = false;
                    }
                }
                if (z6) {
                    d.this.j(bVar2.f5902j);
                }
            } else {
                for (int i7 = 0; i7 < 9; i7++) {
                    float f4 = fArr[i7];
                    if (f4 != 0.0f) {
                        float f5 = f4 * a4;
                        if (Math.abs(f5) < 1.0E-4f) {
                            f5 = 0.0f;
                        }
                        bVar2.f5902j.f5910h[i7] = f5;
                    } else {
                        bVar2.f5902j.f5910h[i7] = 0.0f;
                    }
                }
                i(i5);
            }
            this.f5880b = (bVar.f5880b * a4) + this.f5880b;
        }
        j(eVar);
    }

    /* JADX WARN: Type inference failed for: r4v0, types: [java.lang.Object, java.util.Comparator] */
    public final void i(e eVar) {
        int i4;
        int i5 = this.f5900h + 1;
        e[] eVarArr = this.f;
        if (i5 > eVarArr.length) {
            e[] eVarArr2 = (e[]) Arrays.copyOf(eVarArr, eVarArr.length * 2);
            this.f = eVarArr2;
            this.f5899g = (e[]) Arrays.copyOf(eVarArr2, eVarArr2.length * 2);
        }
        e[] eVarArr3 = this.f;
        int i6 = this.f5900h;
        eVarArr3[i6] = eVar;
        int i7 = i6 + 1;
        this.f5900h = i7;
        if (i7 > 1 && eVarArr3[i6].f5905b > eVar.f5905b) {
            int i8 = 0;
            while (true) {
                i4 = this.f5900h;
                if (i8 >= i4) {
                    break;
                }
                this.f5899g[i8] = this.f[i8];
                i8++;
            }
            Arrays.sort(this.f5899g, 0, i4, new Object());
            for (int i9 = 0; i9 < this.f5900h; i9++) {
                this.f[i9] = this.f5899g[i9];
            }
        }
        eVar.f5904a = true;
        eVar.a(this);
    }

    public final void j(e eVar) {
        int i4 = 0;
        while (i4 < this.f5900h) {
            if (this.f[i4] == eVar) {
                while (true) {
                    int i5 = this.f5900h;
                    if (i4 < i5 - 1) {
                        e[] eVarArr = this.f;
                        int i6 = i4 + 1;
                        eVarArr[i4] = eVarArr[i6];
                        i4 = i6;
                    } else {
                        this.f5900h = i5 - 1;
                        eVar.f5904a = false;
                        return;
                    }
                }
            } else {
                i4++;
            }
        }
    }

    @Override // u.b
    public final String toString() {
        String str = " goal -> (" + this.f5880b + ") : ";
        for (int i4 = 0; i4 < this.f5900h; i4++) {
            e eVar = this.f[i4];
            b bVar = this.f5901i;
            bVar.f5902j = eVar;
            str = str + bVar + " ";
        }
        return str;
    }
}
