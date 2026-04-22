package v;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import v.e;
import w.C0834b;
import w.C0835c;
import w.m;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class f extends l {

    /* renamed from: f0  reason: collision with root package name */
    public final C0834b f6126f0 = new C0834b(this);

    /* renamed from: g0  reason: collision with root package name */
    public final w.e f6127g0;

    /* renamed from: h0  reason: collision with root package name */
    public C0834b.InterfaceC0078b f6128h0;

    /* renamed from: i0  reason: collision with root package name */
    public boolean f6129i0;

    /* renamed from: j0  reason: collision with root package name */
    public final u.c f6130j0;

    /* renamed from: k0  reason: collision with root package name */
    public int f6131k0;
    public int l0;

    /* renamed from: m0  reason: collision with root package name */
    public int f6132m0;

    /* renamed from: n0  reason: collision with root package name */
    public int f6133n0;

    /* renamed from: o0  reason: collision with root package name */
    public C0827c[] f6134o0;

    /* renamed from: p0  reason: collision with root package name */
    public C0827c[] f6135p0;

    /* renamed from: q0  reason: collision with root package name */
    public int f6136q0;

    /* renamed from: r0  reason: collision with root package name */
    public boolean f6137r0;

    /* renamed from: s0  reason: collision with root package name */
    public boolean f6138s0;

    /* JADX WARN: Type inference failed for: r0v1, types: [java.lang.Object, w.e] */
    /* JADX WARN: Type inference failed for: r2v0, types: [java.lang.Object, w.b$a] */
    public f() {
        ?? obj = new Object();
        obj.f6332b = true;
        obj.f6333c = true;
        obj.f6335e = new ArrayList<>();
        new ArrayList();
        obj.f = null;
        obj.f6336g = new Object();
        obj.f6337h = new ArrayList<>();
        obj.f6331a = this;
        obj.f6334d = this;
        this.f6127g0 = obj;
        this.f6128h0 = null;
        this.f6129i0 = false;
        this.f6130j0 = new u.c();
        this.f6132m0 = 0;
        this.f6133n0 = 0;
        this.f6134o0 = new C0827c[4];
        this.f6135p0 = new C0827c[4];
        this.f6136q0 = 263;
        this.f6137r0 = false;
        this.f6138s0 = false;
    }

    /* JADX WARN: Removed duplicated region for block: B:57:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x010d  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x01a1  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x01b9  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x01c2  */
    /* JADX WARN: Removed duplicated region for block: B:90:0x01c5  */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v12, types: [boolean] */
    /* JADX WARN: Type inference failed for: r2v14 */
    @Override // v.l
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void B() {
        /*
            Method dump skipped, instructions count: 525
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: v.f.B():void");
    }

    public final void C(e eVar, int i4) {
        if (i4 == 0) {
            int i5 = this.f6132m0 + 1;
            C0827c[] c0827cArr = this.f6135p0;
            if (i5 >= c0827cArr.length) {
                this.f6135p0 = (C0827c[]) Arrays.copyOf(c0827cArr, c0827cArr.length * 2);
            }
            C0827c[] c0827cArr2 = this.f6135p0;
            int i6 = this.f6132m0;
            c0827cArr2[i6] = new C0827c(eVar, 0, this.f6129i0);
            this.f6132m0 = i6 + 1;
        } else if (i4 == 1) {
            int i7 = this.f6133n0 + 1;
            C0827c[] c0827cArr3 = this.f6134o0;
            if (i7 >= c0827cArr3.length) {
                this.f6134o0 = (C0827c[]) Arrays.copyOf(c0827cArr3, c0827cArr3.length * 2);
            }
            C0827c[] c0827cArr4 = this.f6134o0;
            int i8 = this.f6133n0;
            c0827cArr4[i8] = new C0827c(eVar, 1, this.f6129i0);
            this.f6133n0 = i8 + 1;
        }
    }

    public final void D(u.c cVar) {
        a(cVar);
        int size = this.f6198e0.size();
        char c4 = 0;
        boolean z4 = false;
        for (int i4 = 0; i4 < size; i4++) {
            e eVar = this.f6198e0.get(i4);
            boolean[] zArr = eVar.f6074I;
            zArr[0] = false;
            zArr[1] = false;
            if (eVar instanceof C0825a) {
                z4 = true;
            }
        }
        if (z4) {
            for (int i5 = 0; i5 < size; i5++) {
                e eVar2 = this.f6198e0.get(i5);
                if (eVar2 instanceof C0825a) {
                    C0825a c0825a = (C0825a) eVar2;
                    for (int i6 = 0; i6 < c0825a.f6186f0; i6++) {
                        e eVar3 = c0825a.f6185e0[i6];
                        int i7 = c0825a.f6032g0;
                        if (i7 != 0 && i7 != 1) {
                            if (i7 == 2 || i7 == 3) {
                                eVar3.f6074I[1] = true;
                            }
                        } else {
                            eVar3.f6074I[0] = true;
                        }
                    }
                }
            }
        }
        for (int i8 = 0; i8 < size; i8++) {
            e eVar4 = this.f6198e0.get(i8);
            eVar4.getClass();
            if ((eVar4 instanceof k) || (eVar4 instanceof h)) {
                eVar4.a(cVar);
            }
        }
        int i9 = 0;
        while (i9 < size) {
            e eVar5 = this.f6198e0.get(i9);
            boolean z5 = eVar5 instanceof f;
            e.a aVar = e.a.f6122k;
            if (z5) {
                e.a[] aVarArr = eVar5.f6075J;
                e.a aVar2 = aVarArr[c4];
                e.a aVar3 = aVarArr[1];
                e.a aVar4 = e.a.f6121j;
                if (aVar2 == aVar) {
                    eVar5.w(aVar4);
                }
                if (aVar3 == aVar) {
                    eVar5.x(aVar4);
                }
                eVar5.a(cVar);
                if (aVar2 == aVar) {
                    eVar5.w(aVar2);
                }
                if (aVar3 == aVar) {
                    eVar5.x(aVar3);
                }
            } else {
                eVar5.f6102h = -1;
                eVar5.f6103i = -1;
                e.a[] aVarArr2 = this.f6075J;
                e.a aVar5 = aVarArr2[c4];
                e.a aVar6 = e.a.f6124m;
                e.a[] aVarArr3 = eVar5.f6075J;
                if (aVar5 != aVar && aVarArr3[c4] == aVar6) {
                    d dVar = eVar5.f6119y;
                    int i10 = dVar.f6055e;
                    int l2 = l();
                    d dVar2 = eVar5.f6066A;
                    int i11 = l2 - dVar2.f6055e;
                    dVar.f6056g = cVar.j(dVar);
                    dVar2.f6056g = cVar.j(dVar2);
                    cVar.d(dVar.f6056g, i10);
                    cVar.d(dVar2.f6056g, i11);
                    eVar5.f6102h = 2;
                    eVar5.f6081P = i10;
                    int i12 = i11 - i10;
                    eVar5.f6077L = i12;
                    int i13 = eVar5.f6084S;
                    if (i12 < i13) {
                        eVar5.f6077L = i13;
                    }
                }
                if (aVarArr2[1] != aVar && aVarArr3[1] == aVar6) {
                    d dVar3 = eVar5.f6120z;
                    int i14 = dVar3.f6055e;
                    int i15 = i();
                    d dVar4 = eVar5.f6067B;
                    int i16 = i15 - dVar4.f6055e;
                    dVar3.f6056g = cVar.j(dVar3);
                    dVar4.f6056g = cVar.j(dVar4);
                    cVar.d(dVar3.f6056g, i14);
                    cVar.d(dVar4.f6056g, i16);
                    if (eVar5.f6083R > 0 || eVar5.f6089X == 8) {
                        d dVar5 = eVar5.f6068C;
                        u.e j4 = cVar.j(dVar5);
                        dVar5.f6056g = j4;
                        cVar.d(j4, eVar5.f6083R + i14);
                    }
                    eVar5.f6103i = 2;
                    eVar5.f6082Q = i14;
                    int i17 = i16 - i14;
                    eVar5.f6078M = i17;
                    int i18 = eVar5.f6085T;
                    if (i17 < i18) {
                        eVar5.f6078M = i18;
                    }
                }
                if (!(eVar5 instanceof k) && !(eVar5 instanceof h)) {
                    eVar5.a(cVar);
                }
            }
            i9++;
            c4 = 0;
        }
        if (this.f6132m0 > 0) {
            C0826b.a(this, cVar, 0);
        }
        if (this.f6133n0 > 0) {
            C0826b.a(this, cVar, 1);
        }
    }

    public final boolean E(int i4, boolean z4) {
        boolean z5;
        boolean z6;
        e.a aVar;
        boolean z7;
        w.e eVar = this.f6127g0;
        f fVar = eVar.f6331a;
        e.a h4 = fVar.h(0);
        e.a h5 = fVar.h(1);
        int m4 = fVar.m();
        int n4 = fVar.n();
        ArrayList<m> arrayList = eVar.f6335e;
        e.a aVar2 = e.a.f6121j;
        w.l lVar = fVar.f6100e;
        w.j jVar = fVar.f6098d;
        if (z4 && (h4 == (aVar = e.a.f6122k) || h5 == aVar)) {
            Iterator<m> it = arrayList.iterator();
            while (true) {
                if (it.hasNext()) {
                    m next = it.next();
                    if (next.f == i4 && !next.k()) {
                        z7 = false;
                        break;
                    }
                } else {
                    z7 = z4;
                    break;
                }
            }
            if (i4 == 0) {
                if (z7 && h4 == aVar) {
                    fVar.w(aVar2);
                    fVar.y(eVar.d(fVar, 0));
                    jVar.f6368e.d(fVar.l());
                }
            } else if (z7 && h5 == aVar) {
                fVar.x(aVar2);
                fVar.v(eVar.d(fVar, 1));
                lVar.f6368e.d(fVar.i());
            }
        }
        e.a aVar3 = e.a.f6124m;
        e.a[] aVarArr = fVar.f6075J;
        if (i4 == 0) {
            e.a aVar4 = aVarArr[0];
            if (aVar4 == aVar2 || aVar4 == aVar3) {
                int l2 = fVar.l() + m4;
                jVar.f6371i.d(l2);
                jVar.f6368e.d(l2 - m4);
                z5 = true;
            } else {
                z5 = false;
            }
        } else {
            e.a aVar5 = aVarArr[1];
            if (aVar5 == aVar2 || aVar5 == aVar3) {
                int i5 = fVar.i() + n4;
                lVar.f6371i.d(i5);
                lVar.f6368e.d(i5 - n4);
                z5 = true;
            }
            z5 = false;
        }
        eVar.g();
        Iterator<m> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            m next2 = it2.next();
            if (next2.f == i4 && (next2.f6365b != fVar || next2.f6369g)) {
                next2.e();
            }
        }
        Iterator<m> it3 = arrayList.iterator();
        while (it3.hasNext()) {
            m next3 = it3.next();
            if (next3.f == i4 && (z5 || next3.f6365b != fVar)) {
                if (!next3.f6370h.f6346j || !next3.f6371i.f6346j || (!(next3 instanceof C0835c) && !next3.f6368e.f6346j)) {
                    z6 = false;
                    break;
                }
            }
        }
        z6 = true;
        fVar.w(h4);
        fVar.x(h5);
        return z6;
    }

    @Override // v.l, v.e
    public final void s() {
        this.f6130j0.r();
        this.f6131k0 = 0;
        this.l0 = 0;
        super.s();
    }

    @Override // v.e
    public final void z(boolean z4, boolean z5) {
        super.z(z4, z5);
        int size = this.f6198e0.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.f6198e0.get(i4).z(z4, z5);
        }
    }
}
