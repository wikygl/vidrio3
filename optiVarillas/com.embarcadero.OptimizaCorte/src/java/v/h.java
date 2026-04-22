package v;

import java.util.ArrayList;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import v.d;
import v.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class h extends e {

    /* renamed from: e0  reason: collision with root package name */
    public float f6180e0 = -1.0f;

    /* renamed from: f0  reason: collision with root package name */
    public int f6181f0 = -1;

    /* renamed from: g0  reason: collision with root package name */
    public int f6182g0 = -1;

    /* renamed from: h0  reason: collision with root package name */
    public d f6183h0 = this.f6120z;

    /* renamed from: i0  reason: collision with root package name */
    public int f6184i0 = 0;

    public h() {
        this.f6073H.clear();
        this.f6073H.add(this.f6183h0);
        int length = this.f6072G.length;
        for (int i4 = 0; i4 < length; i4++) {
            this.f6072G[i4] = this.f6183h0;
        }
    }

    @Override // v.e
    public final void A(u.c cVar) {
        if (this.f6076K == null) {
            return;
        }
        d dVar = this.f6183h0;
        cVar.getClass();
        int m4 = u.c.m(dVar);
        if (this.f6184i0 == 1) {
            this.f6081P = m4;
            this.f6082Q = 0;
            v(this.f6076K.i());
            y(0);
            return;
        }
        this.f6081P = 0;
        this.f6082Q = m4;
        y(this.f6076K.l());
        v(0);
    }

    public final void B(int i4) {
        if (this.f6184i0 == i4) {
            return;
        }
        this.f6184i0 = i4;
        ArrayList<d> arrayList = this.f6073H;
        arrayList.clear();
        if (this.f6184i0 == 1) {
            this.f6183h0 = this.f6119y;
        } else {
            this.f6183h0 = this.f6120z;
        }
        arrayList.add(this.f6183h0);
        d[] dVarArr = this.f6072G;
        int length = dVarArr.length;
        for (int i5 = 0; i5 < length; i5++) {
            dVarArr[i5] = this.f6183h0;
        }
    }

    @Override // v.e
    public final void a(u.c cVar) {
        boolean z4;
        f fVar = (f) this.f6076K;
        if (fVar == null) {
            return;
        }
        d g4 = fVar.g(d.a.f6057j);
        d g5 = fVar.g(d.a.f6059l);
        e eVar = this.f6076K;
        e.a aVar = e.a.f6122k;
        boolean z5 = true;
        if (eVar != null && eVar.f6075J[0] == aVar) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (this.f6184i0 == 0) {
            g4 = fVar.g(d.a.f6058k);
            g5 = fVar.g(d.a.f6060m);
            e eVar2 = this.f6076K;
            z4 = (eVar2 == null || eVar2.f6075J[1] != aVar) ? false : false;
        }
        if (this.f6181f0 != -1) {
            u.e j4 = cVar.j(this.f6183h0);
            cVar.e(j4, cVar.j(g4), this.f6181f0, 8);
            if (z4) {
                cVar.f(cVar.j(g5), j4, 0, 5);
            }
        } else if (this.f6182g0 != -1) {
            u.e j5 = cVar.j(this.f6183h0);
            u.e j6 = cVar.j(g5);
            cVar.e(j5, j6, -this.f6182g0, 8);
            if (z4) {
                cVar.f(j5, cVar.j(g4), 0, 5);
                cVar.f(j6, j5, 0, 5);
            }
        } else if (this.f6180e0 != -1.0f) {
            u.e j7 = cVar.j(this.f6183h0);
            u.e j8 = cVar.j(g5);
            float f = this.f6180e0;
            u.b k4 = cVar.k();
            k4.f5882d.h(j7, -1.0f);
            k4.f5882d.h(j8, f);
            cVar.c(k4);
        }
    }

    @Override // v.e
    public final boolean b() {
        return true;
    }

    @Override // v.e
    public final d g(d.a aVar) {
        switch (aVar.ordinal()) {
            case 0:
            case 5:
            case 6:
            case 7:
            case 8:
                return null;
            case 1:
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                if (this.f6184i0 == 1) {
                    return this.f6183h0;
                }
                break;
            case 2:
            case 4:
                if (this.f6184i0 == 0) {
                    return this.f6183h0;
                }
                break;
        }
        throw new AssertionError(aVar.name());
    }
}
