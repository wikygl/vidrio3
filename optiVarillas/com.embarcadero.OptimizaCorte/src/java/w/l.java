package w;

import v.d;
import v.e;
import w.m;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class l extends m {

    /* renamed from: k  reason: collision with root package name */
    public f f6362k;

    /* renamed from: l  reason: collision with root package name */
    public C0833a f6363l;

    @Override // w.m, w.d
    public final void a(d dVar) {
        g gVar;
        float f;
        float f4;
        float f5;
        int i4;
        if (this.f6372j.ordinal() != 3) {
            g gVar2 = this.f6368e;
            boolean z4 = gVar2.f6340c;
            e.a aVar = e.a.f6123l;
            if (z4 && !gVar2.f6346j && this.f6367d == aVar) {
                v.e eVar = this.f6365b;
                int i5 = eVar.f6105k;
                if (i5 != 2) {
                    if (i5 == 3) {
                        g gVar3 = eVar.f6098d.f6368e;
                        if (gVar3.f6346j) {
                            int i6 = eVar.f6080O;
                            if (i6 != -1) {
                                if (i6 != 0) {
                                    if (i6 != 1) {
                                        i4 = 0;
                                        gVar2.d(i4);
                                    } else {
                                        f = gVar3.f6343g;
                                        f4 = eVar.f6079N;
                                    }
                                } else {
                                    f5 = gVar3.f6343g * eVar.f6079N;
                                    i4 = (int) (f5 + 0.5f);
                                    gVar2.d(i4);
                                }
                            } else {
                                f = gVar3.f6343g;
                                f4 = eVar.f6079N;
                            }
                            f5 = f / f4;
                            i4 = (int) (f5 + 0.5f);
                            gVar2.d(i4);
                        }
                    }
                } else {
                    v.e eVar2 = eVar.f6076K;
                    if (eVar2 != null) {
                        if (eVar2.f6100e.f6368e.f6346j) {
                            gVar2.d((int) ((gVar.f6343g * eVar.f6112r) + 0.5f));
                        }
                    }
                }
            }
            f fVar = this.f6370h;
            if (fVar.f6340c) {
                f fVar2 = this.f6371i;
                if (fVar2.f6340c) {
                    if (fVar.f6346j && fVar2.f6346j && gVar2.f6346j) {
                        return;
                    }
                    if (!gVar2.f6346j && this.f6367d == aVar) {
                        v.e eVar3 = this.f6365b;
                        if (eVar3.f6104j == 0 && !eVar3.r()) {
                            int i7 = ((f) fVar.f6348l.get(0)).f6343g + fVar.f;
                            int i8 = ((f) fVar2.f6348l.get(0)).f6343g + fVar2.f;
                            fVar.d(i7);
                            fVar2.d(i8);
                            gVar2.d(i8 - i7);
                            return;
                        }
                    }
                    if (!gVar2.f6346j && this.f6367d == aVar && this.f6364a == 1 && fVar.f6348l.size() > 0 && fVar2.f6348l.size() > 0) {
                        int i9 = (((f) fVar2.f6348l.get(0)).f6343g + fVar2.f) - (((f) fVar.f6348l.get(0)).f6343g + fVar.f);
                        int i10 = gVar2.f6358m;
                        if (i9 < i10) {
                            gVar2.d(i9);
                        } else {
                            gVar2.d(i10);
                        }
                    }
                    if (gVar2.f6346j && fVar.f6348l.size() > 0 && fVar2.f6348l.size() > 0) {
                        f fVar3 = (f) fVar.f6348l.get(0);
                        f fVar4 = (f) fVar2.f6348l.get(0);
                        int i11 = fVar3.f6343g;
                        int i12 = fVar.f + i11;
                        int i13 = fVar4.f6343g;
                        int i14 = fVar2.f + i13;
                        float f6 = this.f6365b.f6087V;
                        if (fVar3 == fVar4) {
                            f6 = 0.5f;
                        } else {
                            i11 = i12;
                            i13 = i14;
                        }
                        fVar.d((int) ((((i13 - i11) - gVar2.f6343g) * f6) + i11 + 0.5f));
                        fVar2.d(fVar.f6343g + gVar2.f6343g);
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        v.e eVar4 = this.f6365b;
        l(eVar4.f6120z, eVar4.f6067B, 1);
    }

    /* JADX WARN: Type inference failed for: r0v124, types: [w.g, w.a] */
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
            gVar.d(eVar5.i());
        }
        boolean z5 = gVar.f6346j;
        e.a aVar = e.a.f6124m;
        e.a aVar2 = e.a.f6121j;
        e.a aVar3 = e.a.f6123l;
        f fVar = this.f6371i;
        f fVar2 = this.f6370h;
        if (!z5) {
            v.e eVar6 = this.f6365b;
            this.f6367d = eVar6.f6075J[1];
            if (eVar6.f6117w) {
                this.f6363l = new g(this);
            }
            e.a aVar4 = this.f6367d;
            if (aVar4 != aVar3) {
                if (aVar4 == aVar && (eVar4 = this.f6365b.f6076K) != null && eVar4.f6075J[1] == aVar2) {
                    int i4 = (eVar4.i() - this.f6365b.f6120z.c()) - this.f6365b.f6067B.c();
                    l lVar = eVar4.f6100e;
                    m.b(fVar2, lVar.f6370h, this.f6365b.f6120z.c());
                    m.b(fVar, lVar.f6371i, -this.f6365b.f6067B.c());
                    gVar.d(i4);
                    return;
                } else if (aVar4 == aVar2) {
                    gVar.d(this.f6365b.i());
                }
            }
        } else if (this.f6367d == aVar && (eVar2 = (eVar = this.f6365b).f6076K) != null && eVar2.f6075J[1] == aVar2) {
            l lVar2 = eVar2.f6100e;
            m.b(fVar2, lVar2.f6370h, eVar.f6120z.c());
            m.b(fVar, lVar2.f6371i, -this.f6365b.f6067B.c());
            return;
        }
        boolean z6 = gVar.f6346j;
        f fVar3 = this.f6362k;
        if (z6) {
            v.e eVar7 = this.f6365b;
            if (eVar7.f6092a) {
                v.d[] dVarArr = eVar7.f6072G;
                v.d dVar = dVarArr[2];
                v.d dVar2 = dVar.f6054d;
                if (dVar2 != null && dVarArr[3].f6054d != null) {
                    if (eVar7.r()) {
                        fVar2.f = this.f6365b.f6072G[2].c();
                        fVar.f = -this.f6365b.f6072G[3].c();
                    } else {
                        f h4 = m.h(this.f6365b.f6072G[2]);
                        if (h4 != null) {
                            m.b(fVar2, h4, this.f6365b.f6072G[2].c());
                        }
                        f h5 = m.h(this.f6365b.f6072G[3]);
                        if (h5 != null) {
                            m.b(fVar, h5, -this.f6365b.f6072G[3].c());
                        }
                        fVar2.f6339b = true;
                        fVar.f6339b = true;
                    }
                    v.e eVar8 = this.f6365b;
                    if (eVar8.f6117w) {
                        m.b(fVar3, fVar2, eVar8.f6083R);
                        return;
                    }
                    return;
                } else if (dVar2 != null) {
                    f h6 = m.h(dVar);
                    if (h6 != null) {
                        m.b(fVar2, h6, this.f6365b.f6072G[2].c());
                        m.b(fVar, fVar2, gVar.f6343g);
                        v.e eVar9 = this.f6365b;
                        if (eVar9.f6117w) {
                            m.b(fVar3, fVar2, eVar9.f6083R);
                            return;
                        }
                        return;
                    }
                    return;
                } else {
                    v.d dVar3 = dVarArr[3];
                    if (dVar3.f6054d != null) {
                        f h7 = m.h(dVar3);
                        if (h7 != null) {
                            m.b(fVar, h7, -this.f6365b.f6072G[3].c());
                            m.b(fVar2, fVar, -gVar.f6343g);
                        }
                        v.e eVar10 = this.f6365b;
                        if (eVar10.f6117w) {
                            m.b(fVar3, fVar2, eVar10.f6083R);
                            return;
                        }
                        return;
                    }
                    v.d dVar4 = dVarArr[4];
                    if (dVar4.f6054d != null) {
                        f h8 = m.h(dVar4);
                        if (h8 != null) {
                            m.b(fVar3, h8, 0);
                            m.b(fVar2, fVar3, -this.f6365b.f6083R);
                            m.b(fVar, fVar2, gVar.f6343g);
                            return;
                        }
                        return;
                    } else if (!(eVar7 instanceof v.i) && eVar7.f6076K != null && eVar7.g(d.a.f6062o).f6054d == null) {
                        v.e eVar11 = this.f6365b;
                        m.b(fVar2, eVar11.f6076K.f6100e.f6370h, eVar11.n());
                        m.b(fVar, fVar2, gVar.f6343g);
                        v.e eVar12 = this.f6365b;
                        if (eVar12.f6117w) {
                            m.b(fVar3, fVar2, eVar12.f6083R);
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                }
            }
        }
        if (!z6 && this.f6367d == aVar3) {
            v.e eVar13 = this.f6365b;
            int i5 = eVar13.f6105k;
            if (i5 != 2) {
                if (i5 == 3 && !eVar13.r()) {
                    v.e eVar14 = this.f6365b;
                    if (eVar14.f6104j != 3) {
                        g gVar2 = eVar14.f6098d.f6368e;
                        gVar.f6348l.add(gVar2);
                        gVar2.f6347k.add(gVar);
                        gVar.f6339b = true;
                        gVar.f6347k.add(fVar2);
                        gVar.f6347k.add(fVar);
                    }
                }
            } else {
                v.e eVar15 = eVar13.f6076K;
                if (eVar15 != null) {
                    g gVar3 = eVar15.f6100e.f6368e;
                    gVar.f6348l.add(gVar3);
                    gVar3.f6347k.add(gVar);
                    gVar.f6339b = true;
                    gVar.f6347k.add(fVar2);
                    gVar.f6347k.add(fVar);
                }
            }
        } else {
            gVar.b(this);
        }
        v.e eVar16 = this.f6365b;
        v.d[] dVarArr2 = eVar16.f6072G;
        v.d dVar5 = dVarArr2[2];
        v.d dVar6 = dVar5.f6054d;
        if (dVar6 != null && dVarArr2[3].f6054d != null) {
            if (eVar16.r()) {
                fVar2.f = this.f6365b.f6072G[2].c();
                fVar.f = -this.f6365b.f6072G[3].c();
            } else {
                f h9 = m.h(this.f6365b.f6072G[2]);
                f h10 = m.h(this.f6365b.f6072G[3]);
                h9.b(this);
                h10.b(this);
                this.f6372j = m.a.f6374k;
            }
            if (this.f6365b.f6117w) {
                c(fVar3, fVar2, 1, this.f6363l);
            }
        } else if (dVar6 != null) {
            f h11 = m.h(dVar5);
            if (h11 != null) {
                m.b(fVar2, h11, this.f6365b.f6072G[2].c());
                c(fVar, fVar2, 1, gVar);
                if (this.f6365b.f6117w) {
                    c(fVar3, fVar2, 1, this.f6363l);
                }
                if (this.f6367d == aVar3) {
                    v.e eVar17 = this.f6365b;
                    if (eVar17.f6079N > 0.0f) {
                        j jVar = eVar17.f6098d;
                        if (jVar.f6367d == aVar3) {
                            jVar.f6368e.f6347k.add(gVar);
                            gVar.f6348l.add(this.f6365b.f6098d.f6368e);
                            gVar.f6338a = this;
                        }
                    }
                }
            }
        } else {
            v.d dVar7 = dVarArr2[3];
            if (dVar7.f6054d != null) {
                f h12 = m.h(dVar7);
                if (h12 != null) {
                    m.b(fVar, h12, -this.f6365b.f6072G[3].c());
                    c(fVar2, fVar, -1, gVar);
                    if (this.f6365b.f6117w) {
                        c(fVar3, fVar2, 1, this.f6363l);
                    }
                }
            } else {
                v.d dVar8 = dVarArr2[4];
                if (dVar8.f6054d != null) {
                    f h13 = m.h(dVar8);
                    if (h13 != null) {
                        m.b(fVar3, h13, 0);
                        c(fVar2, fVar3, -1, this.f6363l);
                        c(fVar, fVar2, 1, gVar);
                    }
                } else if (!(eVar16 instanceof v.i) && (eVar3 = eVar16.f6076K) != null) {
                    m.b(fVar2, eVar3.f6100e.f6370h, eVar16.n());
                    c(fVar, fVar2, 1, gVar);
                    if (this.f6365b.f6117w) {
                        c(fVar3, fVar2, 1, this.f6363l);
                    }
                    if (this.f6367d == aVar3) {
                        v.e eVar18 = this.f6365b;
                        if (eVar18.f6079N > 0.0f) {
                            j jVar2 = eVar18.f6098d;
                            if (jVar2.f6367d == aVar3) {
                                jVar2.f6368e.f6347k.add(gVar);
                                gVar.f6348l.add(this.f6365b.f6098d.f6368e);
                                gVar.f6338a = this;
                            }
                        }
                    }
                }
            }
        }
        if (gVar.f6348l.size() == 0) {
            gVar.f6340c = true;
        }
    }

    @Override // w.m
    public final void e() {
        f fVar = this.f6370h;
        if (fVar.f6346j) {
            this.f6365b.f6082Q = fVar.f6343g;
        }
    }

    @Override // w.m
    public final void f() {
        this.f6366c = null;
        this.f6370h.c();
        this.f6371i.c();
        this.f6362k.c();
        this.f6368e.c();
        this.f6369g = false;
    }

    @Override // w.m
    public final boolean k() {
        if (this.f6367d != e.a.f6123l || this.f6365b.f6105k == 0) {
            return true;
        }
        return false;
    }

    public final void m() {
        this.f6369g = false;
        f fVar = this.f6370h;
        fVar.c();
        fVar.f6346j = false;
        f fVar2 = this.f6371i;
        fVar2.c();
        fVar2.f6346j = false;
        f fVar3 = this.f6362k;
        fVar3.c();
        fVar3.f6346j = false;
        this.f6368e.f6346j = false;
    }

    public final String toString() {
        return "VerticalRun " + this.f6365b.f6090Y;
    }
}
