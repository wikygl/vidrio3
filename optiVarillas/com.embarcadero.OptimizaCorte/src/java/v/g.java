package v;

import java.util.ArrayList;
import v.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class g extends k {

    /* renamed from: O0  reason: collision with root package name */
    public e[] f6153O0;

    /* renamed from: r0  reason: collision with root package name */
    public int f6154r0 = -1;

    /* renamed from: s0  reason: collision with root package name */
    public int f6155s0 = -1;

    /* renamed from: t0  reason: collision with root package name */
    public int f6156t0 = -1;

    /* renamed from: u0  reason: collision with root package name */
    public int f6157u0 = -1;

    /* renamed from: v0  reason: collision with root package name */
    public int f6158v0 = -1;

    /* renamed from: w0  reason: collision with root package name */
    public int f6159w0 = -1;

    /* renamed from: x0  reason: collision with root package name */
    public float f6160x0 = 0.5f;

    /* renamed from: y0  reason: collision with root package name */
    public float f6161y0 = 0.5f;

    /* renamed from: z0  reason: collision with root package name */
    public float f6162z0 = 0.5f;

    /* renamed from: A0  reason: collision with root package name */
    public float f6139A0 = 0.5f;

    /* renamed from: B0  reason: collision with root package name */
    public float f6140B0 = 0.5f;

    /* renamed from: C0  reason: collision with root package name */
    public float f6141C0 = 0.5f;

    /* renamed from: D0  reason: collision with root package name */
    public int f6142D0 = 0;

    /* renamed from: E0  reason: collision with root package name */
    public int f6143E0 = 0;

    /* renamed from: F0  reason: collision with root package name */
    public int f6144F0 = 2;

    /* renamed from: G0  reason: collision with root package name */
    public int f6145G0 = 2;

    /* renamed from: H0  reason: collision with root package name */
    public int f6146H0 = 0;

    /* renamed from: I0  reason: collision with root package name */
    public int f6147I0 = -1;

    /* renamed from: J0  reason: collision with root package name */
    public int f6148J0 = 0;

    /* renamed from: K0  reason: collision with root package name */
    public final ArrayList<a> f6149K0 = new ArrayList<>();

    /* renamed from: L0  reason: collision with root package name */
    public e[] f6150L0 = null;

    /* renamed from: M0  reason: collision with root package name */
    public e[] f6151M0 = null;

    /* renamed from: N0  reason: collision with root package name */
    public int[] f6152N0 = null;
    public int P0 = 0;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class a {

        /* renamed from: a  reason: collision with root package name */
        public int f6163a;

        /* renamed from: d  reason: collision with root package name */
        public d f6166d;

        /* renamed from: e  reason: collision with root package name */
        public d f6167e;
        public d f;

        /* renamed from: g  reason: collision with root package name */
        public d f6168g;

        /* renamed from: h  reason: collision with root package name */
        public int f6169h;

        /* renamed from: i  reason: collision with root package name */
        public int f6170i;

        /* renamed from: j  reason: collision with root package name */
        public int f6171j;

        /* renamed from: k  reason: collision with root package name */
        public int f6172k;

        /* renamed from: q  reason: collision with root package name */
        public int f6178q;

        /* renamed from: b  reason: collision with root package name */
        public e f6164b = null;

        /* renamed from: c  reason: collision with root package name */
        public int f6165c = 0;

        /* renamed from: l  reason: collision with root package name */
        public int f6173l = 0;

        /* renamed from: m  reason: collision with root package name */
        public int f6174m = 0;

        /* renamed from: n  reason: collision with root package name */
        public int f6175n = 0;

        /* renamed from: o  reason: collision with root package name */
        public int f6176o = 0;

        /* renamed from: p  reason: collision with root package name */
        public int f6177p = 0;

        public a(int i4, d dVar, d dVar2, d dVar3, d dVar4, int i5) {
            this.f6169h = 0;
            this.f6170i = 0;
            this.f6171j = 0;
            this.f6172k = 0;
            this.f6178q = 0;
            this.f6163a = i4;
            this.f6166d = dVar;
            this.f6167e = dVar2;
            this.f = dVar3;
            this.f6168g = dVar4;
            this.f6169h = g.this.f6192k0;
            this.f6170i = g.this.f6188g0;
            this.f6171j = g.this.l0;
            this.f6172k = g.this.f6189h0;
            this.f6178q = i5;
        }

        public final void a(e eVar) {
            int i4 = this.f6163a;
            e.a aVar = e.a.f6123l;
            int i5 = 0;
            g gVar = g.this;
            if (i4 == 0) {
                int F4 = gVar.F(eVar, this.f6178q);
                if (eVar.f6075J[0] == aVar) {
                    this.f6177p++;
                    F4 = 0;
                }
                int i6 = gVar.f6142D0;
                if (eVar.f6089X != 8) {
                    i5 = i6;
                }
                this.f6173l = F4 + i5 + this.f6173l;
                int E4 = gVar.E(eVar, this.f6178q);
                if (this.f6164b == null || this.f6165c < E4) {
                    this.f6164b = eVar;
                    this.f6165c = E4;
                    this.f6174m = E4;
                }
            } else {
                int F5 = gVar.F(eVar, this.f6178q);
                int E5 = gVar.E(eVar, this.f6178q);
                if (eVar.f6075J[1] == aVar) {
                    this.f6177p++;
                    E5 = 0;
                }
                int i7 = gVar.f6143E0;
                if (eVar.f6089X != 8) {
                    i5 = i7;
                }
                this.f6174m = E5 + i5 + this.f6174m;
                if (this.f6164b == null || this.f6165c < F5) {
                    this.f6164b = eVar;
                    this.f6165c = F5;
                    this.f6173l = F5;
                }
            }
            this.f6176o++;
        }

        public final void b(int i4, boolean z4, boolean z5) {
            g gVar;
            boolean z6;
            int i5;
            int i6;
            e eVar;
            int i7;
            int i8;
            int i9;
            int i10;
            int i11;
            int i12;
            int i13;
            int i14;
            int i15 = this.f6176o;
            int i16 = 0;
            while (true) {
                gVar = g.this;
                if (i16 >= i15 || (i14 = this.f6175n + i16) >= gVar.P0) {
                    break;
                }
                e eVar2 = gVar.f6153O0[i14];
                if (eVar2 != null) {
                    eVar2.t();
                }
                i16++;
            }
            if (i15 != 0 && this.f6164b != null) {
                if (z5 && i4 == 0) {
                    z6 = true;
                } else {
                    z6 = false;
                }
                int i17 = -1;
                int i18 = -1;
                for (int i19 = 0; i19 < i15; i19++) {
                    if (z4) {
                        i13 = (i15 - 1) - i19;
                    } else {
                        i13 = i19;
                    }
                    int i20 = this.f6175n + i13;
                    if (i20 >= gVar.P0) {
                        break;
                    }
                    if (gVar.f6153O0[i20].f6089X == 0) {
                        if (i17 == -1) {
                            i17 = i19;
                        }
                        i18 = i19;
                    }
                }
                if (this.f6163a == 0) {
                    e eVar3 = this.f6164b;
                    eVar3.f6093a0 = gVar.f6155s0;
                    int i21 = this.f6170i;
                    if (i4 > 0) {
                        i21 += gVar.f6143E0;
                    }
                    d dVar = this.f6167e;
                    d dVar2 = eVar3.f6120z;
                    dVar2.a(dVar, i21);
                    d dVar3 = eVar3.f6067B;
                    if (z5) {
                        dVar3.a(this.f6168g, this.f6172k);
                    }
                    if (i4 > 0) {
                        this.f6167e.f6052b.f6067B.a(dVar2, 0);
                    }
                    if (gVar.f6145G0 == 3 && !eVar3.f6117w) {
                        for (int i22 = 0; i22 < i15; i22++) {
                            if (z4) {
                                i12 = (i15 - 1) - i22;
                            } else {
                                i12 = i22;
                            }
                            int i23 = this.f6175n + i12;
                            if (i23 >= gVar.P0) {
                                break;
                            }
                            eVar = gVar.f6153O0[i23];
                            if (eVar.f6117w) {
                                break;
                            }
                        }
                    }
                    eVar = eVar3;
                    e eVar4 = null;
                    int i24 = 0;
                    while (i24 < i15) {
                        if (z4) {
                            i7 = (i15 - 1) - i24;
                        } else {
                            i7 = i24;
                        }
                        int i25 = this.f6175n + i7;
                        if (i25 < gVar.P0) {
                            e eVar5 = gVar.f6153O0[i25];
                            if (i24 == 0) {
                                eVar5.e(eVar5.f6119y, this.f6166d, this.f6169h);
                            }
                            if (i7 == 0) {
                                int i26 = gVar.f6154r0;
                                float f = gVar.f6160x0;
                                if (this.f6175n == 0) {
                                    i11 = gVar.f6156t0;
                                    i8 = i26;
                                    i9 = -1;
                                    if (i11 != -1) {
                                        f = gVar.f6162z0;
                                        i10 = i11;
                                        eVar5.f6091Z = i10;
                                        eVar5.f6086U = f;
                                    }
                                } else {
                                    i8 = i26;
                                    i9 = -1;
                                }
                                if (z5 && (i11 = gVar.f6158v0) != i9) {
                                    f = gVar.f6140B0;
                                    i10 = i11;
                                    eVar5.f6091Z = i10;
                                    eVar5.f6086U = f;
                                } else {
                                    i10 = i8;
                                    eVar5.f6091Z = i10;
                                    eVar5.f6086U = f;
                                }
                            }
                            if (i24 == i15 - 1) {
                                eVar5.e(eVar5.f6066A, this.f, this.f6171j);
                            }
                            if (eVar4 != null) {
                                d dVar4 = eVar5.f6119y;
                                int i27 = gVar.f6142D0;
                                d dVar5 = eVar4.f6066A;
                                dVar4.a(dVar5, i27);
                                d dVar6 = eVar5.f6119y;
                                if (i24 == i17) {
                                    int i28 = this.f6169h;
                                    if (dVar6.f()) {
                                        dVar6.f = i28;
                                    }
                                }
                                dVar5.a(dVar6, 0);
                                if (i24 == i18 + 1) {
                                    int i29 = this.f6171j;
                                    if (dVar5.f()) {
                                        dVar5.f = i29;
                                    }
                                }
                            }
                            if (eVar5 != eVar3) {
                                int i30 = gVar.f6145G0;
                                if (i30 == 3 && eVar.f6117w && eVar5 != eVar && eVar5.f6117w) {
                                    eVar5.f6068C.a(eVar.f6068C, 0);
                                } else if (i30 != 0) {
                                    if (i30 != 1) {
                                        if (z6) {
                                            eVar5.f6120z.a(this.f6167e, this.f6170i);
                                            eVar5.f6067B.a(this.f6168g, this.f6172k);
                                        } else {
                                            eVar5.f6120z.a(dVar2, 0);
                                            eVar5.f6067B.a(dVar3, 0);
                                        }
                                    } else {
                                        eVar5.f6067B.a(dVar3, 0);
                                    }
                                } else {
                                    eVar5.f6120z.a(dVar2, 0);
                                }
                            }
                            i24++;
                            eVar4 = eVar5;
                        } else {
                            return;
                        }
                    }
                    return;
                }
                e eVar6 = this.f6164b;
                eVar6.f6091Z = gVar.f6154r0;
                int i31 = this.f6169h;
                if (i4 > 0) {
                    i31 += gVar.f6142D0;
                }
                d dVar7 = eVar6.f6119y;
                d dVar8 = eVar6.f6066A;
                if (z4) {
                    dVar8.a(this.f, i31);
                    if (z5) {
                        dVar7.a(this.f6166d, this.f6171j);
                    }
                    if (i4 > 0) {
                        this.f.f6052b.f6119y.a(dVar8, 0);
                    }
                } else {
                    dVar7.a(this.f6166d, i31);
                    if (z5) {
                        dVar8.a(this.f, this.f6171j);
                    }
                    if (i4 > 0) {
                        this.f6166d.f6052b.f6066A.a(dVar7, 0);
                    }
                }
                int i32 = 0;
                e eVar7 = null;
                while (i32 < i15) {
                    int i33 = this.f6175n + i32;
                    if (i33 < gVar.P0) {
                        e eVar8 = gVar.f6153O0[i33];
                        if (i32 == 0) {
                            eVar8.e(eVar8.f6120z, this.f6167e, this.f6170i);
                            int i34 = gVar.f6155s0;
                            float f4 = gVar.f6161y0;
                            if (this.f6175n == 0) {
                                i6 = gVar.f6157u0;
                                i5 = -1;
                                if (i6 != -1) {
                                    f4 = gVar.f6139A0;
                                    i34 = i6;
                                    eVar8.f6093a0 = i34;
                                    eVar8.f6087V = f4;
                                }
                            } else {
                                i5 = -1;
                            }
                            if (z5 && (i6 = gVar.f6159w0) != i5) {
                                f4 = gVar.f6141C0;
                                i34 = i6;
                            }
                            eVar8.f6093a0 = i34;
                            eVar8.f6087V = f4;
                        }
                        if (i32 == i15 - 1) {
                            eVar8.e(eVar8.f6067B, this.f6168g, this.f6172k);
                        }
                        if (eVar7 != null) {
                            d dVar9 = eVar8.f6120z;
                            int i35 = gVar.f6143E0;
                            d dVar10 = eVar7.f6067B;
                            dVar9.a(dVar10, i35);
                            d dVar11 = eVar8.f6120z;
                            if (i32 == i17) {
                                int i36 = this.f6170i;
                                if (dVar11.f()) {
                                    dVar11.f = i36;
                                }
                            }
                            dVar10.a(dVar11, 0);
                            if (i32 == i18 + 1) {
                                int i37 = this.f6172k;
                                if (dVar10.f()) {
                                    dVar10.f = i37;
                                }
                            }
                        }
                        if (eVar8 != eVar6) {
                            if (z4) {
                                int i38 = gVar.f6144F0;
                                if (i38 != 0) {
                                    if (i38 != 1) {
                                        if (i38 == 2) {
                                            eVar8.f6119y.a(dVar7, 0);
                                            eVar8.f6066A.a(dVar8, 0);
                                        }
                                    } else {
                                        eVar8.f6119y.a(dVar7, 0);
                                    }
                                } else {
                                    eVar8.f6066A.a(dVar8, 0);
                                }
                            } else {
                                int i39 = gVar.f6144F0;
                                if (i39 != 0) {
                                    if (i39 != 1) {
                                        if (i39 == 2) {
                                            if (z6) {
                                                eVar8.f6119y.a(this.f6166d, this.f6169h);
                                                eVar8.f6066A.a(this.f, this.f6171j);
                                            } else {
                                                eVar8.f6119y.a(dVar7, 0);
                                                eVar8.f6066A.a(dVar8, 0);
                                            }
                                        }
                                    } else {
                                        eVar8.f6066A.a(dVar8, 0);
                                    }
                                } else {
                                    eVar8.f6119y.a(dVar7, 0);
                                }
                                i32++;
                                eVar7 = eVar8;
                            }
                        }
                        i32++;
                        eVar7 = eVar8;
                    } else {
                        return;
                    }
                }
            }
        }

        public final int c() {
            if (this.f6163a == 1) {
                return this.f6174m - g.this.f6143E0;
            }
            return this.f6174m;
        }

        public final int d() {
            if (this.f6163a == 0) {
                return this.f6173l - g.this.f6142D0;
            }
            return this.f6173l;
        }

        public final void e(int i4) {
            int i5 = this.f6177p;
            if (i5 == 0) {
                return;
            }
            int i6 = this.f6176o;
            int i7 = i4 / i5;
            for (int i8 = 0; i8 < i6; i8++) {
                int i9 = this.f6175n;
                int i10 = i9 + i8;
                g gVar = g.this;
                if (i10 >= gVar.P0) {
                    break;
                }
                e eVar = gVar.f6153O0[i9 + i8];
                int i11 = this.f6163a;
                e.a aVar = e.a.f6121j;
                e.a aVar2 = e.a.f6123l;
                if (i11 == 0) {
                    if (eVar != null) {
                        e.a[] aVarArr = eVar.f6075J;
                        if (aVarArr[0] == aVar2 && eVar.f6104j == 0) {
                            gVar.D(eVar, aVar, i7, aVarArr[1], eVar.i());
                        }
                    }
                } else if (eVar != null) {
                    e.a[] aVarArr2 = eVar.f6075J;
                    if (aVarArr2[1] == aVar2 && eVar.f6105k == 0) {
                        gVar.D(eVar, aVarArr2[0], eVar.l(), aVar, i7);
                    }
                }
            }
            this.f6173l = 0;
            this.f6174m = 0;
            this.f6164b = null;
            this.f6165c = 0;
            int i12 = this.f6176o;
            for (int i13 = 0; i13 < i12; i13++) {
                int i14 = this.f6175n + i13;
                g gVar2 = g.this;
                if (i14 < gVar2.P0) {
                    e eVar2 = gVar2.f6153O0[i14];
                    if (this.f6163a == 0) {
                        int l2 = eVar2.l();
                        int i15 = gVar2.f6142D0;
                        if (eVar2.f6089X == 8) {
                            i15 = 0;
                        }
                        this.f6173l = l2 + i15 + this.f6173l;
                        int E4 = gVar2.E(eVar2, this.f6178q);
                        if (this.f6164b == null || this.f6165c < E4) {
                            this.f6164b = eVar2;
                            this.f6165c = E4;
                            this.f6174m = E4;
                        }
                    } else {
                        int F4 = gVar2.F(eVar2, this.f6178q);
                        int E5 = gVar2.E(eVar2, this.f6178q);
                        int i16 = gVar2.f6143E0;
                        if (eVar2.f6089X == 8) {
                            i16 = 0;
                        }
                        this.f6174m = E5 + i16 + this.f6174m;
                        if (this.f6164b == null || this.f6165c < F4) {
                            this.f6164b = eVar2;
                            this.f6165c = F4;
                            this.f6173l = F4;
                        }
                    }
                } else {
                    return;
                }
            }
        }

        public final void f(int i4, d dVar, d dVar2, d dVar3, d dVar4, int i5, int i6, int i7, int i8, int i9) {
            this.f6163a = i4;
            this.f6166d = dVar;
            this.f6167e = dVar2;
            this.f = dVar3;
            this.f6168g = dVar4;
            this.f6169h = i5;
            this.f6170i = i6;
            this.f6171j = i7;
            this.f6172k = i8;
            this.f6178q = i9;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:315:0x058e  */
    /* JADX WARN: Removed duplicated region for block: B:316:0x0591  */
    /* JADX WARN: Removed duplicated region for block: B:323:0x05ac  */
    /* JADX WARN: Removed duplicated region for block: B:324:0x05ae  */
    @Override // v.k
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void C(int r38, int r39, int r40, int r41) {
        /*
            Method dump skipped, instructions count: 1458
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: v.g.C(int, int, int, int):void");
    }

    public final int E(e eVar, int i4) {
        if (eVar == null) {
            return 0;
        }
        e.a[] aVarArr = eVar.f6075J;
        if (aVarArr[1] == e.a.f6123l) {
            int i5 = eVar.f6105k;
            if (i5 == 0) {
                return 0;
            }
            if (i5 == 2) {
                int i6 = (int) (eVar.f6112r * i4);
                if (i6 != eVar.i()) {
                    D(eVar, aVarArr[0], eVar.l(), e.a.f6121j, i6);
                }
                return i6;
            } else if (i5 == 1) {
                return eVar.i();
            } else {
                if (i5 == 3) {
                    return (int) ((eVar.l() * eVar.f6079N) + 0.5f);
                }
            }
        }
        return eVar.i();
    }

    public final int F(e eVar, int i4) {
        if (eVar == null) {
            return 0;
        }
        e.a[] aVarArr = eVar.f6075J;
        if (aVarArr[0] == e.a.f6123l) {
            int i5 = eVar.f6104j;
            if (i5 == 0) {
                return 0;
            }
            if (i5 == 2) {
                int i6 = (int) (eVar.f6109o * i4);
                if (i6 != eVar.l()) {
                    D(eVar, e.a.f6121j, i6, aVarArr[1], eVar.i());
                }
                return i6;
            } else if (i5 == 1) {
                return eVar.l();
            } else {
                if (i5 == 3) {
                    return (int) ((eVar.i() * eVar.f6079N) + 0.5f);
                }
            }
        }
        return eVar.l();
    }

    @Override // v.e
    public final void a(u.c cVar) {
        boolean z4;
        boolean z5;
        e eVar;
        int i4;
        super.a(cVar);
        e eVar2 = this.f6076K;
        if (eVar2 != null) {
            z4 = ((f) eVar2).f6129i0;
        } else {
            z4 = false;
        }
        int i5 = this.f6146H0;
        ArrayList<a> arrayList = this.f6149K0;
        if (i5 != 0) {
            if (i5 != 1) {
                if (i5 == 2 && this.f6152N0 != null && this.f6151M0 != null && this.f6150L0 != null) {
                    for (int i6 = 0; i6 < this.P0; i6++) {
                        this.f6153O0[i6].t();
                    }
                    int[] iArr = this.f6152N0;
                    int i7 = iArr[0];
                    int i8 = iArr[1];
                    e eVar3 = null;
                    for (int i9 = 0; i9 < i7; i9++) {
                        if (z4) {
                            i4 = (i7 - i9) - 1;
                        } else {
                            i4 = i9;
                        }
                        e eVar4 = this.f6151M0[i4];
                        if (eVar4 != null && eVar4.f6089X != 8) {
                            d dVar = eVar4.f6119y;
                            if (i9 == 0) {
                                eVar4.e(dVar, this.f6119y, this.f6192k0);
                                eVar4.f6091Z = this.f6154r0;
                                eVar4.f6086U = this.f6160x0;
                            }
                            if (i9 == i7 - 1) {
                                eVar4.e(eVar4.f6066A, this.f6066A, this.l0);
                            }
                            if (i9 > 0) {
                                eVar4.e(dVar, eVar3.f6066A, this.f6142D0);
                                eVar3.e(eVar3.f6066A, dVar, 0);
                            }
                            eVar3 = eVar4;
                        }
                    }
                    for (int i10 = 0; i10 < i8; i10++) {
                        e eVar5 = this.f6150L0[i10];
                        if (eVar5 != null && eVar5.f6089X != 8) {
                            d dVar2 = eVar5.f6120z;
                            if (i10 == 0) {
                                eVar5.e(dVar2, this.f6120z, this.f6188g0);
                                eVar5.f6093a0 = this.f6155s0;
                                eVar5.f6087V = this.f6161y0;
                            }
                            if (i10 == i8 - 1) {
                                eVar5.e(eVar5.f6067B, this.f6067B, this.f6189h0);
                            }
                            if (i10 > 0) {
                                eVar5.e(dVar2, eVar3.f6067B, this.f6143E0);
                                eVar3.e(eVar3.f6067B, dVar2, 0);
                            }
                            eVar3 = eVar5;
                        }
                    }
                    for (int i11 = 0; i11 < i7; i11++) {
                        for (int i12 = 0; i12 < i8; i12++) {
                            int i13 = (i12 * i7) + i11;
                            if (this.f6148J0 == 1) {
                                i13 = (i11 * i8) + i12;
                            }
                            e[] eVarArr = this.f6153O0;
                            if (i13 < eVarArr.length && (eVar = eVarArr[i13]) != null && eVar.f6089X != 8) {
                                e eVar6 = this.f6151M0[i11];
                                e eVar7 = this.f6150L0[i12];
                                if (eVar != eVar6) {
                                    eVar.e(eVar.f6119y, eVar6.f6119y, 0);
                                    eVar.e(eVar.f6066A, eVar6.f6066A, 0);
                                }
                                if (eVar != eVar7) {
                                    eVar.e(eVar.f6120z, eVar7.f6120z, 0);
                                    eVar.e(eVar.f6067B, eVar7.f6067B, 0);
                                }
                            }
                        }
                    }
                }
            } else {
                int size = arrayList.size();
                for (int i14 = 0; i14 < size; i14++) {
                    a aVar = arrayList.get(i14);
                    if (i14 == size - 1) {
                        z5 = true;
                    } else {
                        z5 = false;
                    }
                    aVar.b(i14, z4, z5);
                }
            }
        } else if (arrayList.size() > 0) {
            arrayList.get(0).b(0, z4, true);
        }
        this.f6193m0 = false;
    }
}
