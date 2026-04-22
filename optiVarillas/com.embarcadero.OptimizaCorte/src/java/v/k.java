package v;

import androidx.constraintlayout.widget.ConstraintLayout;
import v.e;
import w.C0834b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class k extends i {

    /* renamed from: g0  reason: collision with root package name */
    public int f6188g0 = 0;

    /* renamed from: h0  reason: collision with root package name */
    public int f6189h0 = 0;

    /* renamed from: i0  reason: collision with root package name */
    public int f6190i0 = 0;

    /* renamed from: j0  reason: collision with root package name */
    public int f6191j0 = 0;

    /* renamed from: k0  reason: collision with root package name */
    public int f6192k0 = 0;
    public int l0 = 0;

    /* renamed from: m0  reason: collision with root package name */
    public boolean f6193m0 = false;

    /* renamed from: n0  reason: collision with root package name */
    public int f6194n0 = 0;

    /* renamed from: o0  reason: collision with root package name */
    public int f6195o0 = 0;

    /* renamed from: p0  reason: collision with root package name */
    public final C0834b.a f6196p0 = new Object();

    /* renamed from: q0  reason: collision with root package name */
    public C0834b.InterfaceC0078b f6197q0 = null;

    @Override // v.i
    public final void B() {
        for (int i4 = 0; i4 < this.f6186f0; i4++) {
            e eVar = this.f6185e0[i4];
        }
    }

    public final void D(e eVar, e.a aVar, int i4, e.a aVar2, int i5) {
        ConstraintLayout.b bVar;
        boolean z4;
        e eVar2;
        while (true) {
            bVar = this.f6197q0;
            if (bVar != null || (eVar2 = this.f6076K) == null) {
                break;
            }
            this.f6197q0 = ((f) eVar2).f6128h0;
        }
        C0834b.a aVar3 = this.f6196p0;
        aVar3.f6320a = aVar;
        aVar3.f6321b = aVar2;
        aVar3.f6322c = i4;
        aVar3.f6323d = i5;
        bVar.a(eVar, aVar3);
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

    public void C(int i4, int i5, int i6, int i7) {
    }
}
