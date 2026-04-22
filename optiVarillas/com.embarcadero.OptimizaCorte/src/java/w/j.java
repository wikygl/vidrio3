package w;

import v.d;
import v.e;
import w.m;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class j extends m {

    /* renamed from: k  reason: collision with root package name */
    public static final int[] f6359k = new int[2];

    public static void m(int[] iArr, int i4, int i5, int i6, int i7, float f, int i8) {
        int i9 = i5 - i4;
        int i10 = i7 - i6;
        if (i8 != -1) {
            if (i8 != 0) {
                if (i8 == 1) {
                    iArr[0] = i9;
                    iArr[1] = (int) ((i9 * f) + 0.5f);
                    return;
                }
                return;
            }
            iArr[0] = (int) ((i10 * f) + 0.5f);
            iArr[1] = i10;
            return;
        }
        int i11 = (int) ((i10 * f) + 0.5f);
        int i12 = (int) ((i9 / f) + 0.5f);
        if (i11 <= i9) {
            iArr[0] = i11;
            iArr[1] = i10;
        } else if (i12 <= i10) {
            iArr[0] = i9;
            iArr[1] = i12;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:112:0x024d, code lost:
        if (r8 != 1) goto L127;
     */
    @Override // w.m, w.d
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void a(w.d r24) {
        /*
            Method dump skipped, instructions count: 927
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: w.j.a(w.d):void");
    }

    @Override // w.m
    public final void d() {
        v.e eVar;
        v.e eVar2;
        v.e eVar3;
        v.e eVar4;
        v.e eVar5 = this.f6365b;
        boolean z4 = eVar5.f6092a;
        g gVar = this.f6368e;
        if (z4) {
            gVar.d(eVar5.l());
        }
        boolean z5 = gVar.f6346j;
        e.a aVar = e.a.f6124m;
        e.a aVar2 = e.a.f6123l;
        e.a aVar3 = e.a.f6121j;
        f fVar = this.f6371i;
        f fVar2 = this.f6370h;
        if (!z5) {
            v.e eVar6 = this.f6365b;
            e.a aVar4 = eVar6.f6075J[0];
            this.f6367d = aVar4;
            if (aVar4 != aVar2) {
                if (aVar4 == aVar && (((eVar4 = eVar6.f6076K) != null && eVar4.f6075J[0] == aVar3) || eVar4.f6075J[0] == aVar)) {
                    int l2 = (eVar4.l() - this.f6365b.f6119y.c()) - this.f6365b.f6066A.c();
                    j jVar = eVar4.f6098d;
                    m.b(fVar2, jVar.f6370h, this.f6365b.f6119y.c());
                    m.b(fVar, jVar.f6371i, -this.f6365b.f6066A.c());
                    gVar.d(l2);
                    return;
                } else if (aVar4 == aVar3) {
                    gVar.d(eVar6.l());
                }
            }
        } else if (this.f6367d == aVar && (((eVar2 = (eVar = this.f6365b).f6076K) != null && eVar2.f6075J[0] == aVar3) || eVar2.f6075J[0] == aVar)) {
            m.b(fVar2, eVar2.f6098d.f6370h, eVar.f6119y.c());
            m.b(fVar, eVar2.f6098d.f6371i, -this.f6365b.f6066A.c());
            return;
        }
        if (gVar.f6346j) {
            v.e eVar7 = this.f6365b;
            if (eVar7.f6092a) {
                v.d[] dVarArr = eVar7.f6072G;
                v.d dVar = dVarArr[0];
                v.d dVar2 = dVar.f6054d;
                if (dVar2 != null && dVarArr[1].f6054d != null) {
                    if (eVar7.q()) {
                        fVar2.f = this.f6365b.f6072G[0].c();
                        fVar.f = -this.f6365b.f6072G[1].c();
                        return;
                    }
                    f h4 = m.h(this.f6365b.f6072G[0]);
                    if (h4 != null) {
                        m.b(fVar2, h4, this.f6365b.f6072G[0].c());
                    }
                    f h5 = m.h(this.f6365b.f6072G[1]);
                    if (h5 != null) {
                        m.b(fVar, h5, -this.f6365b.f6072G[1].c());
                    }
                    fVar2.f6339b = true;
                    fVar.f6339b = true;
                    return;
                } else if (dVar2 != null) {
                    f h6 = m.h(dVar);
                    if (h6 != null) {
                        m.b(fVar2, h6, this.f6365b.f6072G[0].c());
                        m.b(fVar, fVar2, gVar.f6343g);
                        return;
                    }
                    return;
                } else {
                    v.d dVar3 = dVarArr[1];
                    if (dVar3.f6054d != null) {
                        f h7 = m.h(dVar3);
                        if (h7 != null) {
                            m.b(fVar, h7, -this.f6365b.f6072G[1].c());
                            m.b(fVar2, fVar, -gVar.f6343g);
                            return;
                        }
                        return;
                    } else if (!(eVar7 instanceof v.i) && eVar7.f6076K != null && eVar7.g(d.a.f6062o).f6054d == null) {
                        v.e eVar8 = this.f6365b;
                        m.b(fVar2, eVar8.f6076K.f6098d.f6370h, eVar8.m());
                        m.b(fVar, fVar2, gVar.f6343g);
                        return;
                    } else {
                        return;
                    }
                }
            }
        }
        if (this.f6367d == aVar2) {
            v.e eVar9 = this.f6365b;
            int i4 = eVar9.f6104j;
            if (i4 != 2) {
                if (i4 == 3) {
                    if (eVar9.f6105k == 3) {
                        fVar2.f6338a = this;
                        fVar.f6338a = this;
                        l lVar = eVar9.f6100e;
                        lVar.f6370h.f6338a = this;
                        lVar.f6371i.f6338a = this;
                        gVar.f6338a = this;
                        if (eVar9.r()) {
                            gVar.f6348l.add(this.f6365b.f6100e.f6368e);
                            this.f6365b.f6100e.f6368e.f6347k.add(gVar);
                            l lVar2 = this.f6365b.f6100e;
                            lVar2.f6368e.f6338a = this;
                            gVar.f6348l.add(lVar2.f6370h);
                            gVar.f6348l.add(this.f6365b.f6100e.f6371i);
                            this.f6365b.f6100e.f6370h.f6347k.add(gVar);
                            this.f6365b.f6100e.f6371i.f6347k.add(gVar);
                        } else if (this.f6365b.q()) {
                            this.f6365b.f6100e.f6368e.f6348l.add(gVar);
                            gVar.f6347k.add(this.f6365b.f6100e.f6368e);
                        } else {
                            this.f6365b.f6100e.f6368e.f6348l.add(gVar);
                        }
                    } else {
                        g gVar2 = eVar9.f6100e.f6368e;
                        gVar.f6348l.add(gVar2);
                        gVar2.f6347k.add(gVar);
                        this.f6365b.f6100e.f6370h.f6347k.add(gVar);
                        this.f6365b.f6100e.f6371i.f6347k.add(gVar);
                        gVar.f6339b = true;
                        gVar.f6347k.add(fVar2);
                        gVar.f6347k.add(fVar);
                        fVar2.f6348l.add(gVar);
                        fVar.f6348l.add(gVar);
                    }
                }
            } else {
                v.e eVar10 = eVar9.f6076K;
                if (eVar10 != null) {
                    g gVar3 = eVar10.f6100e.f6368e;
                    gVar.f6348l.add(gVar3);
                    gVar3.f6347k.add(gVar);
                    gVar.f6339b = true;
                    gVar.f6347k.add(fVar2);
                    gVar.f6347k.add(fVar);
                }
            }
        }
        v.e eVar11 = this.f6365b;
        v.d[] dVarArr2 = eVar11.f6072G;
        v.d dVar4 = dVarArr2[0];
        v.d dVar5 = dVar4.f6054d;
        if (dVar5 != null && dVarArr2[1].f6054d != null) {
            if (eVar11.q()) {
                fVar2.f = this.f6365b.f6072G[0].c();
                fVar.f = -this.f6365b.f6072G[1].c();
                return;
            }
            f h8 = m.h(this.f6365b.f6072G[0]);
            f h9 = m.h(this.f6365b.f6072G[1]);
            h8.b(this);
            h9.b(this);
            this.f6372j = m.a.f6374k;
        } else if (dVar5 != null) {
            f h10 = m.h(dVar4);
            if (h10 != null) {
                m.b(fVar2, h10, this.f6365b.f6072G[0].c());
                c(fVar, fVar2, 1, gVar);
            }
        } else {
            v.d dVar6 = dVarArr2[1];
            if (dVar6.f6054d != null) {
                f h11 = m.h(dVar6);
                if (h11 != null) {
                    m.b(fVar, h11, -this.f6365b.f6072G[1].c());
                    c(fVar2, fVar, -1, gVar);
                }
            } else if (!(eVar11 instanceof v.i) && (eVar3 = eVar11.f6076K) != null) {
                m.b(fVar2, eVar3.f6098d.f6370h, eVar11.m());
                c(fVar, fVar2, 1, gVar);
            }
        }
    }

    @Override // w.m
    public final void e() {
        f fVar = this.f6370h;
        if (fVar.f6346j) {
            this.f6365b.f6081P = fVar.f6343g;
        }
    }

    @Override // w.m
    public final void f() {
        this.f6366c = null;
        this.f6370h.c();
        this.f6371i.c();
        this.f6368e.c();
        this.f6369g = false;
    }

    @Override // w.m
    public final boolean k() {
        if (this.f6367d != e.a.f6123l || this.f6365b.f6104j == 0) {
            return true;
        }
        return false;
    }

    public final void n() {
        this.f6369g = false;
        f fVar = this.f6370h;
        fVar.c();
        fVar.f6346j = false;
        f fVar2 = this.f6371i;
        fVar2.c();
        fVar2.f6346j = false;
        this.f6368e.f6346j = false;
    }

    public final String toString() {
        return "HorizontalRun " + this.f6365b.f6090Y;
    }
}
