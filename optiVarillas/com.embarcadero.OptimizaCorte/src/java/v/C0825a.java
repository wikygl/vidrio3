package v;

import v.e;

/* renamed from: v.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0825a extends i {

    /* renamed from: g0  reason: collision with root package name */
    public int f6032g0 = 0;

    /* renamed from: h0  reason: collision with root package name */
    public boolean f6033h0 = true;

    /* renamed from: i0  reason: collision with root package name */
    public int f6034i0 = 0;

    @Override // v.e
    public final void a(u.c cVar) {
        boolean z4;
        boolean z5;
        boolean z6;
        int i4;
        int i5;
        int i6;
        d[] dVarArr = this.f6072G;
        d dVar = this.f6119y;
        dVarArr[0] = dVar;
        d dVar2 = this.f6120z;
        int i7 = 2;
        dVarArr[2] = dVar2;
        d dVar3 = this.f6066A;
        dVarArr[1] = dVar3;
        d dVar4 = this.f6067B;
        dVarArr[3] = dVar4;
        for (d dVar5 : dVarArr) {
            dVar5.f6056g = cVar.j(dVar5);
        }
        int i8 = this.f6032g0;
        if (i8 >= 0 && i8 < 4) {
            d dVar6 = dVarArr[i8];
            for (int i9 = 0; i9 < this.f6186f0; i9++) {
                e eVar = this.f6185e0[i9];
                if (this.f6033h0 || eVar.b()) {
                    int i10 = this.f6032g0;
                    e.a aVar = e.a.f6123l;
                    if (((i10 == 0 || i10 == 1) && eVar.f6075J[0] == aVar && eVar.f6119y.f6054d != null && eVar.f6066A.f6054d != null) || ((i10 == 2 || i10 == 3) && eVar.f6075J[1] == aVar && eVar.f6120z.f6054d != null && eVar.f6067B.f6054d != null)) {
                        z4 = true;
                        break;
                    }
                }
            }
            z4 = false;
            if (!dVar.e() && !dVar3.e()) {
                z5 = false;
            } else {
                z5 = true;
            }
            if (!dVar2.e() && !dVar4.e()) {
                z6 = false;
            } else {
                z6 = true;
            }
            if (!z4 && (((i6 = this.f6032g0) == 0 && z5) || ((i6 == 2 && z6) || ((i6 == 1 && z5) || (i6 == 3 && z6))))) {
                i4 = 5;
            } else {
                i4 = 4;
            }
            int i11 = 0;
            while (i11 < this.f6186f0) {
                e eVar2 = this.f6185e0[i11];
                if (this.f6033h0 || eVar2.b()) {
                    u.e j4 = cVar.j(eVar2.f6072G[this.f6032g0]);
                    int i12 = this.f6032g0;
                    d dVar7 = eVar2.f6072G[i12];
                    dVar7.f6056g = j4;
                    d dVar8 = dVar7.f6054d;
                    if (dVar8 != null && dVar8.f6052b == this) {
                        i5 = dVar7.f6055e;
                    } else {
                        i5 = 0;
                    }
                    if (i12 != 0 && i12 != i7) {
                        u.b k4 = cVar.k();
                        u.e l2 = cVar.l();
                        l2.f5907d = 0;
                        k4.c(dVar6.f6056g, j4, l2, this.f6034i0 + i5);
                        cVar.c(k4);
                    } else {
                        u.b k5 = cVar.k();
                        u.e l4 = cVar.l();
                        l4.f5907d = 0;
                        k5.d(dVar6.f6056g, j4, l4, this.f6034i0 - i5);
                        cVar.c(k5);
                    }
                    cVar.e(dVar6.f6056g, j4, this.f6034i0 + i5, i4);
                }
                i11++;
                i7 = 2;
            }
            int i13 = this.f6032g0;
            if (i13 == 0) {
                cVar.e(dVar3.f6056g, dVar.f6056g, 0, 8);
                cVar.e(dVar.f6056g, this.f6076K.f6066A.f6056g, 0, 4);
                cVar.e(dVar.f6056g, this.f6076K.f6119y.f6056g, 0, 0);
            } else if (i13 == 1) {
                cVar.e(dVar.f6056g, dVar3.f6056g, 0, 8);
                cVar.e(dVar.f6056g, this.f6076K.f6119y.f6056g, 0, 4);
                cVar.e(dVar.f6056g, this.f6076K.f6066A.f6056g, 0, 0);
            } else if (i13 == 2) {
                cVar.e(dVar4.f6056g, dVar2.f6056g, 0, 8);
                cVar.e(dVar2.f6056g, this.f6076K.f6067B.f6056g, 0, 4);
                cVar.e(dVar2.f6056g, this.f6076K.f6120z.f6056g, 0, 0);
            } else if (i13 == 3) {
                cVar.e(dVar2.f6056g, dVar4.f6056g, 0, 8);
                cVar.e(dVar2.f6056g, this.f6076K.f6120z.f6056g, 0, 4);
                cVar.e(dVar2.f6056g, this.f6076K.f6067B.f6056g, 0, 0);
            }
        }
    }

    @Override // v.e
    public final boolean b() {
        return true;
    }

    @Override // v.e
    public final String toString() {
        String c4 = C.b.c(new StringBuilder("[Barrier] "), this.f6090Y, " {");
        for (int i4 = 0; i4 < this.f6186f0; i4++) {
            e eVar = this.f6185e0[i4];
            if (i4 > 0) {
                c4 = I.h.c(c4, ", ");
            }
            c4 = c4 + eVar.f6090Y;
        }
        return I.h.c(c4, "}");
    }
}
