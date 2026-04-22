package w;

import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: w.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0835c extends m {

    /* renamed from: k  reason: collision with root package name */
    public final ArrayList<m> f6329k;

    /* renamed from: l  reason: collision with root package name */
    public int f6330l;

    public C0835c(v.e eVar, int i4) {
        super(eVar);
        v.e eVar2;
        m mVar;
        int i5;
        m mVar2;
        this.f6329k = new ArrayList<>();
        this.f = i4;
        v.e eVar3 = this.f6365b;
        v.e k4 = eVar3.k(i4);
        while (true) {
            v.e eVar4 = k4;
            eVar2 = eVar3;
            eVar3 = eVar4;
            if (eVar3 == null) {
                break;
            }
            k4 = eVar3.k(this.f);
        }
        this.f6365b = eVar2;
        int i6 = this.f;
        if (i6 == 0) {
            mVar = eVar2.f6098d;
        } else if (i6 == 1) {
            mVar = eVar2.f6100e;
        } else {
            mVar = null;
        }
        ArrayList<m> arrayList = this.f6329k;
        arrayList.add(mVar);
        v.e j4 = eVar2.j(this.f);
        while (j4 != null) {
            int i7 = this.f;
            if (i7 == 0) {
                mVar2 = j4.f6098d;
            } else if (i7 == 1) {
                mVar2 = j4.f6100e;
            } else {
                mVar2 = null;
            }
            arrayList.add(mVar2);
            j4 = j4.j(this.f);
        }
        Iterator<m> it = arrayList.iterator();
        while (it.hasNext()) {
            m next = it.next();
            int i8 = this.f;
            if (i8 == 0) {
                next.f6365b.f6094b = this;
            } else if (i8 == 1) {
                next.f6365b.f6096c = this;
            }
        }
        if (this.f == 0 && ((v.f) this.f6365b.f6076K).f6129i0 && arrayList.size() > 1) {
            this.f6365b = arrayList.get(arrayList.size() - 1).f6365b;
        }
        if (this.f == 0) {
            i5 = this.f6365b.f6091Z;
        } else {
            i5 = this.f6365b.f6093a0;
        }
        this.f6330l = i5;
    }

    /* JADX WARN: Code restructure failed: missing block: B:282:0x03f3, code lost:
        r3 = r3 - r13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x0111, code lost:
        r4 = r17;
        r6 = r18;
     */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0257  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x0266  */
    /* JADX WARN: Removed duplicated region for block: B:203:0x02fc  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x00d3  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x00e3  */
    @Override // w.m, w.d
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void a(w.d r28) {
        /*
            Method dump skipped, instructions count: 1042
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: w.C0835c.a(w.d):void");
    }

    @Override // w.m
    public final void d() {
        ArrayList<m> arrayList = this.f6329k;
        Iterator<m> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().d();
        }
        int size = arrayList.size();
        if (size < 1) {
            return;
        }
        v.e eVar = arrayList.get(0).f6365b;
        v.e eVar2 = arrayList.get(size - 1).f6365b;
        int i4 = this.f;
        f fVar = this.f6371i;
        f fVar2 = this.f6370h;
        if (i4 == 0) {
            v.d dVar = eVar.f6119y;
            v.d dVar2 = eVar2.f6066A;
            f i5 = m.i(dVar, 0);
            int c4 = dVar.c();
            v.e m4 = m();
            if (m4 != null) {
                c4 = m4.f6119y.c();
            }
            if (i5 != null) {
                m.b(fVar2, i5, c4);
            }
            f i6 = m.i(dVar2, 0);
            int c5 = dVar2.c();
            v.e n4 = n();
            if (n4 != null) {
                c5 = n4.f6066A.c();
            }
            if (i6 != null) {
                m.b(fVar, i6, -c5);
            }
        } else {
            v.d dVar3 = eVar.f6120z;
            v.d dVar4 = eVar2.f6067B;
            f i7 = m.i(dVar3, 1);
            int c6 = dVar3.c();
            v.e m5 = m();
            if (m5 != null) {
                c6 = m5.f6120z.c();
            }
            if (i7 != null) {
                m.b(fVar2, i7, c6);
            }
            f i8 = m.i(dVar4, 1);
            int c7 = dVar4.c();
            v.e n5 = n();
            if (n5 != null) {
                c7 = n5.f6067B.c();
            }
            if (i8 != null) {
                m.b(fVar, i8, -c7);
            }
        }
        fVar2.f6338a = this;
        fVar.f6338a = this;
    }

    @Override // w.m
    public final void e() {
        int i4 = 0;
        while (true) {
            ArrayList<m> arrayList = this.f6329k;
            if (i4 < arrayList.size()) {
                arrayList.get(i4).e();
                i4++;
            } else {
                return;
            }
        }
    }

    @Override // w.m
    public final void f() {
        this.f6366c = null;
        Iterator<m> it = this.f6329k.iterator();
        while (it.hasNext()) {
            it.next().f();
        }
    }

    @Override // w.m
    public final long j() {
        ArrayList<m> arrayList = this.f6329k;
        int size = arrayList.size();
        long j4 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            m mVar = arrayList.get(i4);
            j4 = mVar.f6371i.f + mVar.j() + j4 + mVar.f6370h.f;
        }
        return j4;
    }

    @Override // w.m
    public final boolean k() {
        ArrayList<m> arrayList = this.f6329k;
        int size = arrayList.size();
        for (int i4 = 0; i4 < size; i4++) {
            if (!arrayList.get(i4).k()) {
                return false;
            }
        }
        return true;
    }

    public final v.e m() {
        int i4 = 0;
        while (true) {
            ArrayList<m> arrayList = this.f6329k;
            if (i4 < arrayList.size()) {
                v.e eVar = arrayList.get(i4).f6365b;
                if (eVar.f6089X != 8) {
                    return eVar;
                }
                i4++;
            } else {
                return null;
            }
        }
    }

    public final v.e n() {
        ArrayList<m> arrayList = this.f6329k;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            v.e eVar = arrayList.get(size).f6365b;
            if (eVar.f6089X != 8) {
                return eVar;
            }
        }
        return null;
    }

    public final String toString() {
        String str;
        if (this.f == 0) {
            str = "horizontal : ";
        } else {
            str = "vertical : ";
        }
        String concat = "ChainRun ".concat(str);
        Iterator<m> it = this.f6329k.iterator();
        while (it.hasNext()) {
            String c4 = I.h.c(concat, "<");
            concat = I.h.c(c4 + it.next(), "> ");
        }
        return concat;
    }
}
