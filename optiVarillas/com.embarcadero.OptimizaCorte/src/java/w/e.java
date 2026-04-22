package w;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import v.e;
import w.C0834b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class e {

    /* renamed from: a  reason: collision with root package name */
    public v.f f6331a;

    /* renamed from: b  reason: collision with root package name */
    public boolean f6332b;

    /* renamed from: c  reason: collision with root package name */
    public boolean f6333c;

    /* renamed from: d  reason: collision with root package name */
    public v.f f6334d;

    /* renamed from: e  reason: collision with root package name */
    public ArrayList<m> f6335e;
    public C0834b.InterfaceC0078b f;

    /* renamed from: g  reason: collision with root package name */
    public C0834b.a f6336g;

    /* renamed from: h  reason: collision with root package name */
    public ArrayList<k> f6337h;

    /* JADX WARN: Type inference failed for: r10v2, types: [w.k, java.lang.Object] */
    public final void a(f fVar, int i4, ArrayList arrayList, k kVar) {
        m mVar = fVar.f6341d;
        if (mVar.f6366c == null) {
            v.f fVar2 = this.f6331a;
            if (mVar != fVar2.f6098d) {
                k kVar2 = kVar;
                if (mVar != fVar2.f6100e) {
                    if (kVar == null) {
                        ?? obj = new Object();
                        obj.f6360a = null;
                        obj.f6361b = new ArrayList<>();
                        obj.f6360a = mVar;
                        arrayList.add(obj);
                        kVar2 = obj;
                    }
                    mVar.f6366c = kVar2;
                    kVar2.f6361b.add(mVar);
                    f fVar3 = mVar.f6370h;
                    Iterator it = fVar3.f6347k.iterator();
                    while (it.hasNext()) {
                        d dVar = (d) it.next();
                        if (dVar instanceof f) {
                            a((f) dVar, i4, arrayList, kVar2);
                        }
                    }
                    f fVar4 = mVar.f6371i;
                    Iterator it2 = fVar4.f6347k.iterator();
                    while (it2.hasNext()) {
                        d dVar2 = (d) it2.next();
                        if (dVar2 instanceof f) {
                            a((f) dVar2, i4, arrayList, kVar2);
                        }
                    }
                    if (i4 == 1 && (mVar instanceof l)) {
                        Iterator it3 = ((l) mVar).f6362k.f6347k.iterator();
                        while (it3.hasNext()) {
                            d dVar3 = (d) it3.next();
                            if (dVar3 instanceof f) {
                                a((f) dVar3, i4, arrayList, kVar2);
                            }
                        }
                    }
                    Iterator it4 = fVar3.f6348l.iterator();
                    while (it4.hasNext()) {
                        a((f) it4.next(), i4, arrayList, kVar2);
                    }
                    Iterator it5 = fVar4.f6348l.iterator();
                    while (it5.hasNext()) {
                        a((f) it5.next(), i4, arrayList, kVar2);
                    }
                    if (i4 == 1 && (mVar instanceof l)) {
                        Iterator it6 = ((l) mVar).f6362k.f6348l.iterator();
                        while (it6.hasNext()) {
                            a((f) it6.next(), i4, arrayList, kVar2);
                        }
                    }
                }
            }
        }
    }

    public final void b(v.f fVar) {
        e.a aVar;
        int i4;
        e.a aVar2;
        l lVar;
        j jVar;
        int i5;
        e.a aVar3;
        e.a aVar4;
        l lVar2;
        j jVar2;
        v.f fVar2 = fVar;
        Iterator<v.e> it = fVar2.f6198e0.iterator();
        while (it.hasNext()) {
            v.e next = it.next();
            e.a[] aVarArr = next.f6075J;
            e.a aVar5 = aVarArr[0];
            e.a aVar6 = aVarArr[1];
            if (next.f6089X == 8) {
                next.f6092a = true;
            } else {
                float f = next.f6109o;
                e.a aVar7 = e.a.f6123l;
                if (f < 1.0f && aVar5 == aVar7) {
                    next.f6104j = 2;
                }
                float f4 = next.f6112r;
                if (f4 < 1.0f && aVar6 == aVar7) {
                    next.f6105k = 2;
                }
                float f5 = next.f6079N;
                e.a aVar8 = e.a.f6122k;
                e.a aVar9 = e.a.f6121j;
                if (f5 > 0.0f) {
                    if (aVar5 == aVar7 && (aVar6 == aVar8 || aVar6 == aVar9)) {
                        next.f6104j = 3;
                    } else if (aVar6 == aVar7 && (aVar5 == aVar8 || aVar5 == aVar9)) {
                        next.f6105k = 3;
                    } else if (aVar5 == aVar7 && aVar6 == aVar7) {
                        if (next.f6104j == 0) {
                            next.f6104j = 3;
                        }
                        if (next.f6105k == 0) {
                            next.f6105k = 3;
                        }
                    }
                }
                v.d dVar = next.f6066A;
                v.d dVar2 = next.f6119y;
                if (aVar5 == aVar7 && next.f6104j == 1 && (dVar2.f6054d == null || dVar.f6054d == null)) {
                    aVar5 = aVar8;
                }
                v.d dVar3 = next.f6067B;
                v.d dVar4 = next.f6120z;
                if (aVar6 == aVar7 && next.f6105k == 1 && (dVar4.f6054d == null || dVar3.f6054d == null)) {
                    aVar = aVar8;
                } else {
                    aVar = aVar6;
                }
                j jVar3 = next.f6098d;
                jVar3.f6367d = aVar5;
                int i6 = next.f6104j;
                jVar3.f6364a = i6;
                l lVar3 = next.f6100e;
                lVar3.f6367d = aVar;
                Iterator<v.e> it2 = it;
                int i7 = next.f6105k;
                lVar3.f6364a = i7;
                e.a aVar10 = e.a.f6124m;
                if ((aVar5 != aVar10 && aVar5 != aVar9 && aVar5 != aVar8) || (aVar != aVar10 && aVar != aVar9 && aVar != aVar8)) {
                    e.a[] aVarArr2 = fVar2.f6075J;
                    v.d[] dVarArr = next.f6072G;
                    if (aVar5 != aVar7 || (aVar != aVar8 && aVar != aVar9)) {
                        lVar = lVar3;
                        jVar = jVar3;
                        i5 = i6;
                    } else if (i6 == 3) {
                        if (aVar == aVar8) {
                            jVar2 = jVar3;
                            lVar2 = lVar3;
                            f(next, aVar8, 0, aVar8, 0);
                        } else {
                            lVar2 = lVar3;
                            jVar2 = jVar3;
                        }
                        int i8 = next.i();
                        f(next, aVar9, (int) ((i8 * next.f6079N) + 0.5f), aVar9, i8);
                        jVar2.f6368e.d(next.l());
                        lVar2.f6368e.d(next.i());
                        next.f6092a = true;
                        it = it2;
                    } else {
                        lVar = lVar3;
                        jVar = jVar3;
                        if (i6 == 1) {
                            f(next, aVar8, 0, aVar, 0);
                            jVar.f6368e.f6358m = next.l();
                        } else if (i6 == 2) {
                            i5 = i6;
                            e.a aVar11 = aVarArr2[0];
                            if (aVar11 == aVar9 || aVar11 == aVar10) {
                                f(next, aVar9, (int) ((f * fVar.l()) + 0.5f), aVar, next.i());
                                jVar.f6368e.d(next.l());
                                lVar.f6368e.d(next.i());
                                next.f6092a = true;
                            }
                        } else {
                            i5 = i6;
                            if (dVarArr[0].f6054d == null || dVarArr[1].f6054d == null) {
                                f(next, aVar8, 0, aVar, 0);
                                jVar.f6368e.d(next.l());
                                lVar.f6368e.d(next.i());
                                next.f6092a = true;
                            }
                        }
                    }
                    if (aVar == aVar7 && (aVar5 == aVar8 || aVar5 == aVar9)) {
                        if (i7 == 3) {
                            if (aVar5 == aVar8) {
                                f(next, aVar8, 0, aVar8, 0);
                            }
                            int l2 = next.l();
                            float f6 = next.f6079N;
                            if (next.f6080O == -1) {
                                f6 = 1.0f / f6;
                            }
                            f(next, aVar9, l2, aVar9, (int) ((l2 * f6) + 0.5f));
                            jVar.f6368e.d(next.l());
                            lVar.f6368e.d(next.i());
                            next.f6092a = true;
                        } else if (i7 == 1) {
                            f(next, aVar5, 0, aVar8, 0);
                            lVar.f6368e.f6358m = next.i();
                        } else if (i7 == 2) {
                            e.a aVar12 = aVarArr2[1];
                            if (aVar12 == aVar9 || aVar12 == aVar10) {
                                f(next, aVar5, next.l(), aVar9, (int) ((f4 * fVar.i()) + 0.5f));
                                jVar.f6368e.d(next.l());
                                lVar.f6368e.d(next.i());
                                next.f6092a = true;
                            }
                        } else if (dVarArr[2].f6054d == null || dVarArr[3].f6054d == null) {
                            f(next, aVar8, 0, aVar, 0);
                            jVar.f6368e.d(next.l());
                            lVar.f6368e.d(next.i());
                            next.f6092a = true;
                        }
                    }
                    if (aVar5 == aVar7 && aVar == aVar7) {
                        int i9 = i5;
                        if (i9 != 1 && i7 != 1) {
                            if (i7 == 2 && i9 == 2 && (((aVar3 = aVarArr2[0]) == aVar9 || aVar3 == aVar9) && ((aVar4 = aVarArr2[1]) == aVar9 || aVar4 == aVar9))) {
                                f(next, aVar9, (int) ((f * fVar.l()) + 0.5f), aVar9, (int) ((f4 * fVar.i()) + 0.5f));
                                jVar.f6368e.d(next.l());
                                lVar.f6368e.d(next.i());
                                next.f6092a = true;
                            }
                        } else {
                            f(next, aVar8, 0, aVar8, 0);
                            jVar.f6368e.f6358m = next.l();
                            lVar.f6368e.f6358m = next.i();
                        }
                    }
                } else {
                    int l4 = next.l();
                    if (aVar5 == aVar10) {
                        l4 = (fVar.l() - dVar2.f6055e) - dVar.f6055e;
                        aVar5 = aVar9;
                    }
                    int i10 = next.i();
                    if (aVar == aVar10) {
                        i4 = (fVar.i() - dVar4.f6055e) - dVar3.f6055e;
                        aVar2 = aVar9;
                    } else {
                        i4 = i10;
                        aVar2 = aVar;
                    }
                    f(next, aVar5, l4, aVar2, i4);
                    jVar3.f6368e.d(next.l());
                    lVar3.f6368e.d(next.i());
                    next.f6092a = true;
                }
                fVar2 = fVar;
                it = it2;
            }
        }
    }

    public final void c() {
        ArrayList<m> arrayList = this.f6335e;
        arrayList.clear();
        v.f fVar = this.f6334d;
        fVar.f6098d.f();
        l lVar = fVar.f6100e;
        lVar.f();
        arrayList.add(fVar.f6098d);
        arrayList.add(lVar);
        Iterator<v.e> it = fVar.f6198e0.iterator();
        HashSet hashSet = null;
        while (it.hasNext()) {
            v.e next = it.next();
            if (next instanceof v.h) {
                m mVar = new m(next);
                next.f6098d.f();
                next.f6100e.f();
                mVar.f = ((v.h) next).f6184i0;
                arrayList.add(mVar);
            } else {
                if (next.q()) {
                    if (next.f6094b == null) {
                        next.f6094b = new C0835c(next, 0);
                    }
                    if (hashSet == null) {
                        hashSet = new HashSet();
                    }
                    hashSet.add(next.f6094b);
                } else {
                    arrayList.add(next.f6098d);
                }
                if (next.r()) {
                    if (next.f6096c == null) {
                        next.f6096c = new C0835c(next, 1);
                    }
                    if (hashSet == null) {
                        hashSet = new HashSet();
                    }
                    hashSet.add(next.f6096c);
                } else {
                    arrayList.add(next.f6100e);
                }
                if (next instanceof v.i) {
                    arrayList.add(new m(next));
                }
            }
        }
        if (hashSet != null) {
            arrayList.addAll(hashSet);
        }
        Iterator<m> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            it2.next().f();
        }
        Iterator<m> it3 = arrayList.iterator();
        while (it3.hasNext()) {
            m next2 = it3.next();
            if (next2.f6365b != fVar) {
                next2.d();
            }
        }
        ArrayList<k> arrayList2 = this.f6337h;
        arrayList2.clear();
        v.f fVar2 = this.f6331a;
        e(fVar2.f6098d, 0, arrayList2);
        e(fVar2.f6100e, 1, arrayList2);
        this.f6332b = false;
    }

    public final int d(v.f fVar, int i4) {
        m mVar;
        m mVar2;
        ArrayList<k> arrayList;
        int i5;
        int i6;
        long j4;
        float f;
        long j5;
        v.f fVar2 = fVar;
        ArrayList<k> arrayList2 = this.f6337h;
        int size = arrayList2.size();
        int i7 = 0;
        long j6 = 0;
        while (i7 < size) {
            m mVar3 = arrayList2.get(i7).f6360a;
            if (!(mVar3 instanceof C0835c) ? !(i4 != 0 ? (mVar3 instanceof l) : (mVar3 instanceof j)) : ((C0835c) mVar3).f != i4) {
                arrayList = arrayList2;
                i5 = size;
                i6 = i7;
                j4 = 0;
            } else {
                if (i4 == 0) {
                    mVar = fVar2.f6098d;
                } else {
                    mVar = fVar2.f6100e;
                }
                f fVar3 = mVar.f6370h;
                if (i4 == 0) {
                    mVar2 = fVar2.f6098d;
                } else {
                    mVar2 = fVar2.f6100e;
                }
                f fVar4 = mVar2.f6371i;
                boolean contains = mVar3.f6370h.f6348l.contains(fVar3);
                f fVar5 = mVar3.f6371i;
                boolean contains2 = fVar5.f6348l.contains(fVar4);
                long j7 = mVar3.j();
                f fVar6 = mVar3.f6370h;
                if (contains && contains2) {
                    long b4 = k.b(fVar6, 0L);
                    ArrayList<k> arrayList3 = arrayList2;
                    i5 = size;
                    long a4 = k.a(fVar5, 0L);
                    long j8 = b4 - j7;
                    int i8 = fVar5.f;
                    arrayList = arrayList3;
                    i6 = i7;
                    if (j8 >= (-i8)) {
                        j8 += i8;
                    }
                    long j9 = fVar6.f;
                    long j10 = ((-a4) - j7) - j9;
                    if (j10 >= j9) {
                        j10 -= j9;
                    }
                    v.e eVar = mVar3.f6365b;
                    if (i4 == 0) {
                        f = eVar.f6086U;
                    } else if (i4 == 1) {
                        f = eVar.f6087V;
                    } else {
                        eVar.getClass();
                        f = -1.0f;
                    }
                    if (f > 0.0f) {
                        j5 = (((float) j8) / (1.0f - f)) + (((float) j10) / f);
                    } else {
                        j5 = 0;
                    }
                    float f4 = (float) j5;
                    j4 = (fVar6.f + ((((f4 * f) + 0.5f) + j7) + (((1.0f - f) * f4) + 0.5f))) - fVar5.f;
                } else {
                    arrayList = arrayList2;
                    i5 = size;
                    i6 = i7;
                    if (contains) {
                        j4 = Math.max(k.b(fVar6, fVar6.f), fVar6.f + j7);
                    } else if (contains2) {
                        j4 = Math.max(-k.a(fVar5, fVar5.f), (-fVar5.f) + j7);
                    } else {
                        j4 = (mVar3.j() + fVar6.f) - fVar5.f;
                    }
                }
            }
            j6 = Math.max(j6, j4);
            i7 = i6 + 1;
            fVar2 = fVar;
            size = i5;
            arrayList2 = arrayList;
        }
        return (int) j6;
    }

    public final void e(m mVar, int i4, ArrayList<k> arrayList) {
        f fVar;
        Iterator it = mVar.f6370h.f6347k.iterator();
        while (true) {
            boolean hasNext = it.hasNext();
            fVar = mVar.f6371i;
            if (!hasNext) {
                break;
            }
            d dVar = (d) it.next();
            if (dVar instanceof f) {
                a((f) dVar, i4, arrayList, null);
            } else if (dVar instanceof m) {
                a(((m) dVar).f6370h, i4, arrayList, null);
            }
        }
        Iterator it2 = fVar.f6347k.iterator();
        while (it2.hasNext()) {
            d dVar2 = (d) it2.next();
            if (dVar2 instanceof f) {
                a((f) dVar2, i4, arrayList, null);
            } else if (dVar2 instanceof m) {
                a(((m) dVar2).f6371i, i4, arrayList, null);
            }
        }
        if (i4 == 1) {
            Iterator it3 = ((l) mVar).f6362k.f6347k.iterator();
            while (it3.hasNext()) {
                d dVar3 = (d) it3.next();
                if (dVar3 instanceof f) {
                    a((f) dVar3, i4, arrayList, null);
                }
            }
        }
    }

    public final void f(v.e eVar, e.a aVar, int i4, e.a aVar2, int i5) {
        boolean z4;
        C0834b.a aVar3 = this.f6336g;
        aVar3.f6320a = aVar;
        aVar3.f6321b = aVar2;
        aVar3.f6322c = i4;
        aVar3.f6323d = i5;
        this.f.a(eVar, aVar3);
        eVar.y(aVar3.f6324e);
        eVar.v(aVar3.f);
        eVar.f6117w = aVar3.f6326h;
        int i6 = aVar3.f6325g;
        eVar.f6083R = i6;
        if (i6 > 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        eVar.f6117w = z4;
    }

    public final void g() {
        boolean z4;
        C0833a c0833a;
        Iterator<v.e> it = this.f6331a.f6198e0.iterator();
        while (it.hasNext()) {
            v.e next = it.next();
            if (!next.f6092a) {
                e.a[] aVarArr = next.f6075J;
                boolean z5 = false;
                e.a aVar = aVarArr[0];
                e.a aVar2 = aVarArr[1];
                int i4 = next.f6104j;
                int i5 = next.f6105k;
                e.a aVar3 = e.a.f6122k;
                e.a aVar4 = e.a.f6123l;
                if (aVar != aVar3 && (aVar != aVar4 || i4 != 1)) {
                    z4 = false;
                } else {
                    z4 = true;
                }
                if (aVar2 == aVar3 || (aVar2 == aVar4 && i5 == 1)) {
                    z5 = true;
                }
                j jVar = next.f6098d;
                g gVar = jVar.f6368e;
                boolean z6 = gVar.f6346j;
                l lVar = next.f6100e;
                g gVar2 = lVar.f6368e;
                boolean z7 = gVar2.f6346j;
                e.a aVar5 = e.a.f6121j;
                if (z6 && z7) {
                    f(next, aVar5, gVar.f6343g, aVar5, gVar2.f6343g);
                    next.f6092a = true;
                } else if (z6 && z5) {
                    f(next, aVar5, gVar.f6343g, aVar3, gVar2.f6343g);
                    if (aVar2 == aVar4) {
                        lVar.f6368e.f6358m = next.i();
                    } else {
                        lVar.f6368e.d(next.i());
                        next.f6092a = true;
                    }
                } else if (z7 && z4) {
                    f(next, aVar3, gVar.f6343g, aVar5, gVar2.f6343g);
                    if (aVar == aVar4) {
                        jVar.f6368e.f6358m = next.l();
                    } else {
                        jVar.f6368e.d(next.l());
                        next.f6092a = true;
                    }
                }
                if (next.f6092a && (c0833a = lVar.f6363l) != null) {
                    c0833a.d(next.f6083R);
                }
            }
        }
    }
}
