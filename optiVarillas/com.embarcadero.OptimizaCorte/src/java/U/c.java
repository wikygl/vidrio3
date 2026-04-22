package u;

import Q0.q;
import com.google.android.gms.internal.ads.yn;
import java.util.Arrays;
import u.d;
import u.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c {

    /* renamed from: o  reason: collision with root package name */
    public static int f5884o = 1000;

    /* renamed from: p  reason: collision with root package name */
    public static boolean f5885p = true;

    /* renamed from: b  reason: collision with root package name */
    public final d f5887b;

    /* renamed from: e  reason: collision with root package name */
    public u.b[] f5890e;

    /* renamed from: k  reason: collision with root package name */
    public final yn f5895k;

    /* renamed from: n  reason: collision with root package name */
    public u.b f5898n;

    /* renamed from: a  reason: collision with root package name */
    public int f5886a = 0;

    /* renamed from: c  reason: collision with root package name */
    public int f5888c = 32;

    /* renamed from: d  reason: collision with root package name */
    public int f5889d = 32;
    public boolean f = false;

    /* renamed from: g  reason: collision with root package name */
    public boolean[] f5891g = new boolean[32];

    /* renamed from: h  reason: collision with root package name */
    public int f5892h = 1;

    /* renamed from: i  reason: collision with root package name */
    public int f5893i = 0;

    /* renamed from: j  reason: collision with root package name */
    public int f5894j = 32;

    /* renamed from: l  reason: collision with root package name */
    public e[] f5896l = new e[f5884o];

    /* renamed from: m  reason: collision with root package name */
    public int f5897m = 0;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface a {
        e a(boolean[] zArr);
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class b extends u.b {
        public b(yn ynVar) {
            this.f5882d = new f(this, ynVar);
        }
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [u.b, u.d] */
    /* JADX WARN: Type inference failed for: r2v6, types: [com.google.android.gms.internal.ads.yn, java.lang.Object] */
    public c() {
        this.f5890e = null;
        this.f5890e = new u.b[32];
        q();
        ?? obj = new Object();
        ((yn) obj).j = new q();
        ((yn) obj).k = new q();
        ((yn) obj).l = new q();
        ((yn) obj).m = new e[32];
        this.f5895k = obj;
        ?? bVar = new u.b(obj);
        bVar.f = new e[128];
        bVar.f5899g = new e[128];
        bVar.f5900h = 0;
        bVar.f5901i = new d.b();
        this.f5887b = bVar;
        if (f5885p) {
            this.f5898n = new b(obj);
        } else {
            this.f5898n = new u.b(obj);
        }
    }

    public static int m(Object obj) {
        e eVar = ((v.d) obj).f6056g;
        if (eVar != null) {
            return (int) (eVar.f5908e + 0.5f);
        }
        return 0;
    }

    public final e a(e.a aVar) {
        e eVar = (e) ((q) this.f5895k.l).a();
        if (eVar == null) {
            eVar = new e(aVar);
            eVar.f5911i = aVar;
        } else {
            eVar.c();
            eVar.f5911i = aVar;
        }
        int i4 = this.f5897m;
        int i5 = f5884o;
        if (i4 >= i5) {
            int i6 = i5 * 2;
            f5884o = i6;
            this.f5896l = (e[]) Arrays.copyOf(this.f5896l, i6);
        }
        e[] eVarArr = this.f5896l;
        int i7 = this.f5897m;
        this.f5897m = i7 + 1;
        eVarArr[i7] = eVar;
        return eVar;
    }

    public final void b(e eVar, e eVar2, int i4, float f, e eVar3, e eVar4, int i5, int i6) {
        u.b k4 = k();
        if (eVar2 == eVar3) {
            k4.f5882d.h(eVar, 1.0f);
            k4.f5882d.h(eVar4, 1.0f);
            k4.f5882d.h(eVar2, -2.0f);
        } else if (f == 0.5f) {
            k4.f5882d.h(eVar, 1.0f);
            k4.f5882d.h(eVar2, -1.0f);
            k4.f5882d.h(eVar3, -1.0f);
            k4.f5882d.h(eVar4, 1.0f);
            if (i4 > 0 || i5 > 0) {
                k4.f5880b = (-i4) + i5;
            }
        } else if (f <= 0.0f) {
            k4.f5882d.h(eVar, -1.0f);
            k4.f5882d.h(eVar2, 1.0f);
            k4.f5880b = i4;
        } else if (f >= 1.0f) {
            k4.f5882d.h(eVar4, -1.0f);
            k4.f5882d.h(eVar3, 1.0f);
            k4.f5880b = -i5;
        } else {
            float f4 = 1.0f - f;
            k4.f5882d.h(eVar, f4 * 1.0f);
            k4.f5882d.h(eVar2, f4 * (-1.0f));
            k4.f5882d.h(eVar3, (-1.0f) * f);
            k4.f5882d.h(eVar4, 1.0f * f);
            if (i4 > 0 || i5 > 0) {
                k4.f5880b = (i5 * f) + ((-i4) * f4);
            }
        }
        if (i6 != 8) {
            k4.b(this, i6);
        }
        c(k4);
    }

    /* JADX WARN: Code restructure failed: missing block: B:51:0x00bb, code lost:
        if (r5.f5914l <= 1) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00bd, code lost:
        r12 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00bf, code lost:
        r12 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00c9, code lost:
        if (r5.f5914l <= 1) goto L65;
     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x00e0, code lost:
        if (r5.f5914l <= 1) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x00e2, code lost:
        r14 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x00e4, code lost:
        r14 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x00ee, code lost:
        if (r5.f5914l <= 1) goto L87;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void c(u.b r17) {
        /*
            Method dump skipped, instructions count: 425
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: u.c.c(u.b):void");
    }

    public final void d(e eVar, int i4) {
        int i5 = eVar.f5906c;
        if (i5 == -1) {
            eVar.f5908e = i4;
            eVar.f = true;
            int i6 = eVar.f5913k;
            for (int i7 = 0; i7 < i6; i7++) {
                eVar.f5912j[i7].g(eVar, false);
            }
            eVar.f5913k = 0;
        } else if (i5 != -1) {
            u.b bVar = this.f5890e[i5];
            if (bVar.f5883e) {
                bVar.f5880b = i4;
            } else if (bVar.f5882d.d() == 0) {
                bVar.f5883e = true;
                bVar.f5880b = i4;
            } else {
                u.b k4 = k();
                if (i4 < 0) {
                    k4.f5880b = i4 * (-1);
                    k4.f5882d.h(eVar, 1.0f);
                } else {
                    k4.f5880b = i4;
                    k4.f5882d.h(eVar, -1.0f);
                }
                c(k4);
            }
        } else {
            u.b k5 = k();
            k5.f5879a = eVar;
            float f = i4;
            eVar.f5908e = f;
            k5.f5880b = f;
            k5.f5883e = true;
            c(k5);
        }
    }

    public final void e(e eVar, e eVar2, int i4, int i5) {
        boolean z4 = false;
        if (i5 == 8 && eVar2.f && eVar.f5906c == -1) {
            eVar.f5908e = eVar2.f5908e + i4;
            eVar.f = true;
            int i6 = eVar.f5913k;
            for (int i7 = 0; i7 < i6; i7++) {
                eVar.f5912j[i7].g(eVar, false);
            }
            eVar.f5913k = 0;
            return;
        }
        u.b k4 = k();
        if (i4 != 0) {
            if (i4 < 0) {
                i4 *= -1;
                z4 = true;
            }
            k4.f5880b = i4;
        }
        if (!z4) {
            k4.f5882d.h(eVar, -1.0f);
            k4.f5882d.h(eVar2, 1.0f);
        } else {
            k4.f5882d.h(eVar, 1.0f);
            k4.f5882d.h(eVar2, -1.0f);
        }
        if (i5 != 8) {
            k4.b(this, i5);
        }
        c(k4);
    }

    public final void f(e eVar, e eVar2, int i4, int i5) {
        u.b k4 = k();
        e l2 = l();
        l2.f5907d = 0;
        k4.c(eVar, eVar2, l2, i4);
        if (i5 != 8) {
            k4.f5882d.h(i(i5), (int) (k4.f5882d.g(l2) * (-1.0f)));
        }
        c(k4);
    }

    public final void g(e eVar, e eVar2, int i4, int i5) {
        u.b k4 = k();
        e l2 = l();
        l2.f5907d = 0;
        k4.d(eVar, eVar2, l2, i4);
        if (i5 != 8) {
            k4.f5882d.h(i(i5), (int) (k4.f5882d.g(l2) * (-1.0f)));
        }
        c(k4);
    }

    public final void h(u.b bVar) {
        boolean z4 = f5885p;
        yn ynVar = this.f5895k;
        if (z4) {
            u.b bVar2 = this.f5890e[this.f5893i];
            if (bVar2 != null) {
                ((q) ynVar.j).b(bVar2);
            }
        } else {
            u.b bVar3 = this.f5890e[this.f5893i];
            if (bVar3 != null) {
                ((q) ynVar.k).b(bVar3);
            }
        }
        u.b[] bVarArr = this.f5890e;
        int i4 = this.f5893i;
        bVarArr[i4] = bVar;
        e eVar = bVar.f5879a;
        eVar.f5906c = i4;
        this.f5893i = i4 + 1;
        eVar.d(bVar);
    }

    public final e i(int i4) {
        if (this.f5892h + 1 >= this.f5889d) {
            n();
        }
        e a4 = a(e.a.f5917l);
        int i5 = this.f5886a + 1;
        this.f5886a = i5;
        this.f5892h++;
        a4.f5905b = i5;
        a4.f5907d = i4;
        ((e[]) this.f5895k.m)[i5] = a4;
        d dVar = this.f5887b;
        dVar.f5901i.f5902j = a4;
        float[] fArr = a4.f5910h;
        Arrays.fill(fArr, 0.0f);
        fArr[a4.f5907d] = 1.0f;
        dVar.i(a4);
        return a4;
    }

    public final e j(Object obj) {
        e eVar = null;
        if (obj == null) {
            return null;
        }
        if (this.f5892h + 1 >= this.f5889d) {
            n();
        }
        if (obj instanceof v.d) {
            v.d dVar = (v.d) obj;
            eVar = dVar.f6056g;
            if (eVar == null) {
                dVar.i();
                eVar = dVar.f6056g;
            }
            int i4 = eVar.f5905b;
            yn ynVar = this.f5895k;
            if (i4 == -1 || i4 > this.f5886a || ((e[]) ynVar.m)[i4] == null) {
                if (i4 != -1) {
                    eVar.c();
                }
                int i5 = this.f5886a + 1;
                this.f5886a = i5;
                this.f5892h++;
                eVar.f5905b = i5;
                eVar.f5911i = e.a.f5915j;
                ((e[]) ynVar.m)[i5] = eVar;
            }
        }
        return eVar;
    }

    public final u.b k() {
        boolean z4 = f5885p;
        yn ynVar = this.f5895k;
        if (z4) {
            u.b bVar = (u.b) ((q) ynVar.j).a();
            if (bVar == null) {
                return new b(ynVar);
            }
            bVar.f5879a = null;
            bVar.f5882d.clear();
            bVar.f5880b = 0.0f;
            bVar.f5883e = false;
            return bVar;
        }
        u.b bVar2 = (u.b) ((q) ynVar.k).a();
        if (bVar2 == null) {
            return new u.b(ynVar);
        }
        bVar2.f5879a = null;
        bVar2.f5882d.clear();
        bVar2.f5880b = 0.0f;
        bVar2.f5883e = false;
        return bVar2;
    }

    public final e l() {
        if (this.f5892h + 1 >= this.f5889d) {
            n();
        }
        e a4 = a(e.a.f5916k);
        int i4 = this.f5886a + 1;
        this.f5886a = i4;
        this.f5892h++;
        a4.f5905b = i4;
        ((e[]) this.f5895k.m)[i4] = a4;
        return a4;
    }

    public final void n() {
        int i4 = this.f5888c * 2;
        this.f5888c = i4;
        this.f5890e = (u.b[]) Arrays.copyOf(this.f5890e, i4);
        yn ynVar = this.f5895k;
        ynVar.m = (e[]) Arrays.copyOf((e[]) ynVar.m, this.f5888c);
        int i5 = this.f5888c;
        this.f5891g = new boolean[i5];
        this.f5889d = i5;
        this.f5894j = i5;
    }

    public final void o(d dVar) {
        yn ynVar;
        int i4 = 0;
        while (true) {
            if (i4 >= this.f5893i) {
                break;
            }
            u.b bVar = this.f5890e[i4];
            e.a aVar = bVar.f5879a.f5911i;
            e.a aVar2 = e.a.f5915j;
            if (aVar != aVar2) {
                float f = 0.0f;
                if (bVar.f5880b < 0.0f) {
                    boolean z4 = false;
                    int i5 = 0;
                    while (!z4) {
                        i5++;
                        float f4 = Float.MAX_VALUE;
                        int i6 = 0;
                        int i7 = -1;
                        int i8 = -1;
                        int i9 = 0;
                        while (true) {
                            int i10 = this.f5893i;
                            ynVar = this.f5895k;
                            if (i6 >= i10) {
                                break;
                            }
                            u.b bVar2 = this.f5890e[i6];
                            if (bVar2.f5879a.f5911i != aVar2 && !bVar2.f5883e && bVar2.f5880b < f) {
                                int i11 = 1;
                                while (i11 < this.f5892h) {
                                    e eVar = ((e[]) ynVar.m)[i11];
                                    float g4 = bVar2.f5882d.g(eVar);
                                    if (g4 > f) {
                                        for (int i12 = 0; i12 < 9; i12++) {
                                            float f5 = eVar.f5909g[i12] / g4;
                                            if ((f5 < f4 && i12 == i9) || i12 > i9) {
                                                i9 = i12;
                                                f4 = f5;
                                                i7 = i6;
                                                i8 = i11;
                                            }
                                        }
                                    }
                                    i11++;
                                    f = 0.0f;
                                }
                            }
                            i6++;
                            f = 0.0f;
                        }
                        if (i7 != -1) {
                            u.b bVar3 = this.f5890e[i7];
                            bVar3.f5879a.f5906c = -1;
                            bVar3.f(((e[]) ynVar.m)[i8]);
                            e eVar2 = bVar3.f5879a;
                            eVar2.f5906c = i7;
                            eVar2.d(bVar3);
                        } else {
                            z4 = true;
                        }
                        if (i5 > this.f5892h / 2) {
                            z4 = true;
                        }
                        f = 0.0f;
                    }
                }
            }
            i4++;
        }
        p(dVar);
        for (int i13 = 0; i13 < this.f5893i; i13++) {
            u.b bVar4 = this.f5890e[i13];
            bVar4.f5879a.f5908e = bVar4.f5880b;
        }
    }

    public final void p(a aVar) {
        for (int i4 = 0; i4 < this.f5892h; i4++) {
            this.f5891g[i4] = false;
        }
        boolean z4 = false;
        int i5 = 0;
        while (!z4) {
            i5++;
            if (i5 >= this.f5892h * 2) {
                return;
            }
            e eVar = ((u.b) aVar).f5879a;
            if (eVar != null) {
                this.f5891g[eVar.f5905b] = true;
            }
            e a4 = aVar.a(this.f5891g);
            if (a4 != null) {
                boolean[] zArr = this.f5891g;
                int i6 = a4.f5905b;
                if (zArr[i6]) {
                    return;
                }
                zArr[i6] = true;
            }
            if (a4 != null) {
                float f = Float.MAX_VALUE;
                int i7 = -1;
                for (int i8 = 0; i8 < this.f5893i; i8++) {
                    u.b bVar = this.f5890e[i8];
                    if (bVar.f5879a.f5911i != e.a.f5915j && !bVar.f5883e && bVar.f5882d.e(a4)) {
                        float g4 = bVar.f5882d.g(a4);
                        if (g4 < 0.0f) {
                            float f4 = (-bVar.f5880b) / g4;
                            if (f4 < f) {
                                i7 = i8;
                                f = f4;
                            }
                        }
                    }
                }
                if (i7 > -1) {
                    u.b bVar2 = this.f5890e[i7];
                    bVar2.f5879a.f5906c = -1;
                    bVar2.f(a4);
                    e eVar2 = bVar2.f5879a;
                    eVar2.f5906c = i7;
                    eVar2.d(bVar2);
                }
            } else {
                z4 = true;
            }
        }
    }

    public final void q() {
        boolean z4 = f5885p;
        yn ynVar = this.f5895k;
        int i4 = 0;
        if (z4) {
            while (true) {
                u.b[] bVarArr = this.f5890e;
                if (i4 < bVarArr.length) {
                    u.b bVar = bVarArr[i4];
                    if (bVar != null) {
                        ((q) ynVar.j).b(bVar);
                    }
                    this.f5890e[i4] = null;
                    i4++;
                } else {
                    return;
                }
            }
        } else {
            while (true) {
                u.b[] bVarArr2 = this.f5890e;
                if (i4 < bVarArr2.length) {
                    u.b bVar2 = bVarArr2[i4];
                    if (bVar2 != null) {
                        ((q) ynVar.k).b(bVar2);
                    }
                    this.f5890e[i4] = null;
                    i4++;
                } else {
                    return;
                }
            }
        }
    }

    public final void r() {
        yn ynVar;
        int i4 = 0;
        while (true) {
            ynVar = this.f5895k;
            e[] eVarArr = (e[]) ynVar.m;
            if (i4 >= eVarArr.length) {
                break;
            }
            e eVar = eVarArr[i4];
            if (eVar != null) {
                eVar.c();
            }
            i4++;
        }
        q qVar = (q) ynVar.l;
        e[] eVarArr2 = this.f5896l;
        int i5 = this.f5897m;
        qVar.getClass();
        if (i5 > eVarArr2.length) {
            i5 = eVarArr2.length;
        }
        for (int i6 = 0; i6 < i5; i6++) {
            e eVar2 = eVarArr2[i6];
            int i7 = qVar.f1994a;
            Object[] objArr = (Object[]) qVar.f1995b;
            if (i7 < objArr.length) {
                objArr[i7] = eVar2;
                qVar.f1994a = i7 + 1;
            }
        }
        this.f5897m = 0;
        Arrays.fill((e[]) ynVar.m, (Object) null);
        this.f5886a = 0;
        d dVar = this.f5887b;
        dVar.f5900h = 0;
        dVar.f5880b = 0.0f;
        this.f5892h = 1;
        for (int i8 = 0; i8 < this.f5893i; i8++) {
            this.f5890e[i8].getClass();
        }
        q();
        this.f5893i = 0;
        if (f5885p) {
            this.f5898n = new b(ynVar);
        } else {
            this.f5898n = new u.b(ynVar);
        }
    }
}
