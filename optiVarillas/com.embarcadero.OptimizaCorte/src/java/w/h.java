package w;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class h extends m {
    @Override // w.m, w.d
    public final void a(d dVar) {
        f fVar = this.f6370h;
        if (!fVar.f6340c || fVar.f6346j) {
            return;
        }
        fVar.d((int) ((((f) fVar.f6348l.get(0)).f6343g * ((v.h) this.f6365b).f6180e0) + 0.5f));
    }

    @Override // w.m
    public final void d() {
        v.e eVar = this.f6365b;
        v.h hVar = (v.h) eVar;
        int i4 = hVar.f6181f0;
        int i5 = hVar.f6182g0;
        int i6 = hVar.f6184i0;
        f fVar = this.f6370h;
        if (i6 == 1) {
            if (i4 != -1) {
                fVar.f6348l.add(eVar.f6076K.f6098d.f6370h);
                this.f6365b.f6076K.f6098d.f6370h.f6347k.add(fVar);
                fVar.f = i4;
            } else if (i5 != -1) {
                fVar.f6348l.add(eVar.f6076K.f6098d.f6371i);
                this.f6365b.f6076K.f6098d.f6371i.f6347k.add(fVar);
                fVar.f = -i5;
            } else {
                fVar.f6339b = true;
                fVar.f6348l.add(eVar.f6076K.f6098d.f6371i);
                this.f6365b.f6076K.f6098d.f6371i.f6347k.add(fVar);
            }
            m(this.f6365b.f6098d.f6370h);
            m(this.f6365b.f6098d.f6371i);
            return;
        }
        if (i4 != -1) {
            fVar.f6348l.add(eVar.f6076K.f6100e.f6370h);
            this.f6365b.f6076K.f6100e.f6370h.f6347k.add(fVar);
            fVar.f = i4;
        } else if (i5 != -1) {
            fVar.f6348l.add(eVar.f6076K.f6100e.f6371i);
            this.f6365b.f6076K.f6100e.f6371i.f6347k.add(fVar);
            fVar.f = -i5;
        } else {
            fVar.f6339b = true;
            fVar.f6348l.add(eVar.f6076K.f6100e.f6371i);
            this.f6365b.f6076K.f6100e.f6371i.f6347k.add(fVar);
        }
        m(this.f6365b.f6100e.f6370h);
        m(this.f6365b.f6100e.f6371i);
    }

    @Override // w.m
    public final void e() {
        v.e eVar = this.f6365b;
        int i4 = ((v.h) eVar).f6184i0;
        f fVar = this.f6370h;
        if (i4 == 1) {
            eVar.f6081P = fVar.f6343g;
        } else {
            eVar.f6082Q = fVar.f6343g;
        }
    }

    @Override // w.m
    public final void f() {
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
