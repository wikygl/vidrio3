package w;

import java.util.Iterator;
import v.C0825a;
import w.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class i extends m {
    @Override // w.m, w.d
    public final void a(d dVar) {
        C0825a c0825a = (C0825a) this.f6365b;
        int i4 = c0825a.f6032g0;
        f fVar = this.f6370h;
        Iterator it = fVar.f6348l.iterator();
        int i5 = 0;
        int i6 = -1;
        while (it.hasNext()) {
            int i7 = ((f) it.next()).f6343g;
            if (i6 == -1 || i7 < i6) {
                i6 = i7;
            }
            if (i5 < i7) {
                i5 = i7;
            }
        }
        if (i4 != 0 && i4 != 2) {
            fVar.d(i5 + c0825a.f6034i0);
        } else {
            fVar.d(i6 + c0825a.f6034i0);
        }
    }

    @Override // w.m
    public final void d() {
        v.e eVar = this.f6365b;
        if (eVar instanceof C0825a) {
            f fVar = this.f6370h;
            fVar.f6339b = true;
            C0825a c0825a = (C0825a) eVar;
            int i4 = c0825a.f6032g0;
            boolean z4 = c0825a.f6033h0;
            int i5 = 0;
            if (i4 != 0) {
                if (i4 != 1) {
                    if (i4 != 2) {
                        if (i4 == 3) {
                            fVar.f6342e = f.a.f6355p;
                            while (i5 < c0825a.f6186f0) {
                                v.e eVar2 = c0825a.f6185e0[i5];
                                if (z4 || eVar2.f6089X != 8) {
                                    f fVar2 = eVar2.f6100e.f6371i;
                                    fVar2.f6347k.add(fVar);
                                    fVar.f6348l.add(fVar2);
                                }
                                i5++;
                            }
                            m(this.f6365b.f6100e.f6370h);
                            m(this.f6365b.f6100e.f6371i);
                            return;
                        }
                        return;
                    }
                    fVar.f6342e = f.a.f6354o;
                    while (i5 < c0825a.f6186f0) {
                        v.e eVar3 = c0825a.f6185e0[i5];
                        if (z4 || eVar3.f6089X != 8) {
                            f fVar3 = eVar3.f6100e.f6370h;
                            fVar3.f6347k.add(fVar);
                            fVar.f6348l.add(fVar3);
                        }
                        i5++;
                    }
                    m(this.f6365b.f6100e.f6370h);
                    m(this.f6365b.f6100e.f6371i);
                    return;
                }
                fVar.f6342e = f.a.f6353n;
                while (i5 < c0825a.f6186f0) {
                    v.e eVar4 = c0825a.f6185e0[i5];
                    if (z4 || eVar4.f6089X != 8) {
                        f fVar4 = eVar4.f6098d.f6371i;
                        fVar4.f6347k.add(fVar);
                        fVar.f6348l.add(fVar4);
                    }
                    i5++;
                }
                m(this.f6365b.f6098d.f6370h);
                m(this.f6365b.f6098d.f6371i);
                return;
            }
            fVar.f6342e = f.a.f6352m;
            while (i5 < c0825a.f6186f0) {
                v.e eVar5 = c0825a.f6185e0[i5];
                if (z4 || eVar5.f6089X != 8) {
                    f fVar5 = eVar5.f6098d.f6370h;
                    fVar5.f6347k.add(fVar);
                    fVar.f6348l.add(fVar5);
                }
                i5++;
            }
            m(this.f6365b.f6098d.f6370h);
            m(this.f6365b.f6098d.f6371i);
        }
    }

    @Override // w.m
    public final void e() {
        v.e eVar = this.f6365b;
        if (eVar instanceof C0825a) {
            int i4 = ((C0825a) eVar).f6032g0;
            f fVar = this.f6370h;
            if (i4 != 0 && i4 != 1) {
                eVar.f6082Q = fVar.f6343g;
            } else {
                eVar.f6081P = fVar.f6343g;
            }
        }
    }

    @Override // w.m
    public final void f() {
        this.f6366c = null;
        this.f6370h.c();
    }

    @Override // w.m
    public final boolean k() {
        return false;
    }

    public final void m(f fVar) {
        f fVar2 = this.f6370h;
        fVar2.f6347k.add(fVar);
        fVar.f6348l.add(fVar2);
    }
}
