package u;

import com.google.android.gms.internal.ads.yn;
import java.util.ArrayList;
import u.c;
import u.e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class b implements c.a {

    /* renamed from: d  reason: collision with root package name */
    public a f5882d;

    /* renamed from: a  reason: collision with root package name */
    public e f5879a = null;

    /* renamed from: b  reason: collision with root package name */
    public float f5880b = 0.0f;

    /* renamed from: c  reason: collision with root package name */
    public final ArrayList<e> f5881c = new ArrayList<>();

    /* renamed from: e  reason: collision with root package name */
    public boolean f5883e = false;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface a {
        float a(int i4);

        float b(e eVar, boolean z4);

        float c(b bVar, boolean z4);

        void clear();

        int d();

        boolean e(e eVar);

        void f(e eVar, float f, boolean z4);

        float g(e eVar);

        void h(e eVar, float f);

        e i(int i4);

        void j(float f);

        void k();
    }

    public b() {
    }

    @Override // u.c.a
    public e a(boolean[] zArr) {
        return e(zArr, null);
    }

    public final void b(c cVar, int i4) {
        this.f5882d.h(cVar.i(i4), 1.0f);
        this.f5882d.h(cVar.i(i4), -1.0f);
    }

    public final void c(e eVar, e eVar2, e eVar3, int i4) {
        boolean z4 = false;
        if (i4 != 0) {
            if (i4 < 0) {
                i4 *= -1;
                z4 = true;
            }
            this.f5880b = i4;
        }
        if (!z4) {
            this.f5882d.h(eVar, -1.0f);
            this.f5882d.h(eVar2, 1.0f);
            this.f5882d.h(eVar3, 1.0f);
            return;
        }
        this.f5882d.h(eVar, 1.0f);
        this.f5882d.h(eVar2, -1.0f);
        this.f5882d.h(eVar3, -1.0f);
    }

    public final void d(e eVar, e eVar2, e eVar3, int i4) {
        boolean z4 = false;
        if (i4 != 0) {
            if (i4 < 0) {
                i4 *= -1;
                z4 = true;
            }
            this.f5880b = i4;
        }
        if (!z4) {
            this.f5882d.h(eVar, -1.0f);
            this.f5882d.h(eVar2, 1.0f);
            this.f5882d.h(eVar3, -1.0f);
            return;
        }
        this.f5882d.h(eVar, 1.0f);
        this.f5882d.h(eVar2, -1.0f);
        this.f5882d.h(eVar3, 1.0f);
    }

    public final e e(boolean[] zArr, e eVar) {
        e.a aVar;
        int d4 = this.f5882d.d();
        e eVar2 = null;
        float f = 0.0f;
        for (int i4 = 0; i4 < d4; i4++) {
            float a4 = this.f5882d.a(i4);
            if (a4 < 0.0f) {
                e i5 = this.f5882d.i(i4);
                if ((zArr == null || !zArr[i5.f5905b]) && i5 != eVar && (((aVar = i5.f5911i) == e.a.f5916k || aVar == e.a.f5917l) && a4 < f)) {
                    f = a4;
                    eVar2 = i5;
                }
            }
        }
        return eVar2;
    }

    public final void f(e eVar) {
        e eVar2 = this.f5879a;
        if (eVar2 != null) {
            this.f5882d.h(eVar2, -1.0f);
            this.f5879a = null;
        }
        float b4 = this.f5882d.b(eVar, true) * (-1.0f);
        this.f5879a = eVar;
        if (b4 == 1.0f) {
            return;
        }
        this.f5880b /= b4;
        this.f5882d.j(b4);
    }

    public final void g(e eVar, boolean z4) {
        if (!eVar.f) {
            return;
        }
        float g4 = this.f5882d.g(eVar);
        this.f5880b = (eVar.f5908e * g4) + this.f5880b;
        this.f5882d.b(eVar, z4);
        if (z4) {
            eVar.b(this);
        }
    }

    public void h(b bVar, boolean z4) {
        float c4 = this.f5882d.c(bVar, z4);
        this.f5880b = (bVar.f5880b * c4) + this.f5880b;
        if (z4) {
            bVar.f5879a.b(this);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0086  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String toString() {
        /*
            r10 = this;
            u.e r0 = r10.f5879a
            if (r0 != 0) goto L7
            java.lang.String r0 = "0"
            goto L17
        L7:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = ""
            r0.<init>(r1)
            u.e r1 = r10.f5879a
            r0.append(r1)
            java.lang.String r0 = r0.toString()
        L17:
            java.lang.String r1 = " = "
            java.lang.String r0 = I.h.c(r0, r1)
            float r1 = r10.f5880b
            r2 = 1
            r3 = 0
            r4 = 0
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 == 0) goto L39
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            float r0 = r10.f5880b
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 1
            goto L3a
        L39:
            r1 = 0
        L3a:
            u.b$a r5 = r10.f5882d
            int r5 = r5.d()
        L40:
            if (r3 >= r5) goto La1
            u.b$a r6 = r10.f5882d
            u.e r6 = r6.i(r3)
            if (r6 != 0) goto L4b
            goto L9e
        L4b:
            u.b$a r7 = r10.f5882d
            float r7 = r7.a(r3)
            int r8 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r8 != 0) goto L56
            goto L9e
        L56:
            java.lang.String r6 = r6.toString()
            r9 = -1082130432(0xffffffffbf800000, float:-1.0)
            if (r1 != 0) goto L6b
            int r1 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r1 >= 0) goto L7b
            java.lang.String r1 = "- "
            java.lang.String r0 = I.h.c(r0, r1)
        L68:
            float r7 = r7 * r9
            goto L7b
        L6b:
            if (r8 <= 0) goto L74
            java.lang.String r1 = " + "
            java.lang.String r0 = I.h.c(r0, r1)
            goto L7b
        L74:
            java.lang.String r1 = " - "
            java.lang.String r0 = I.h.c(r0, r1)
            goto L68
        L7b:
            r1 = 1065353216(0x3f800000, float:1.0)
            int r1 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r1 != 0) goto L86
            java.lang.String r0 = I.h.c(r0, r6)
            goto L9d
        L86:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r7)
            java.lang.String r0 = " "
            r1.append(r0)
            r1.append(r6)
            java.lang.String r0 = r1.toString()
        L9d:
            r1 = 1
        L9e:
            int r3 = r3 + 1
            goto L40
        La1:
            if (r1 != 0) goto La9
            java.lang.String r1 = "0.0"
            java.lang.String r0 = I.h.c(r0, r1)
        La9:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: u.b.toString():java.lang.String");
    }

    public b(yn ynVar) {
        this.f5882d = new C0819a(this, ynVar);
    }
}
