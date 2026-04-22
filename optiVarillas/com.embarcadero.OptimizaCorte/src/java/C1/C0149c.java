package C1;

import C3.AbstractC0151a;
import C3.C0167q;
import C3.C0168s;
import C3.G;
import C3.K;
import android.view.View;
import i2.F;
import i2.Z;
import i2.a0;
import java.util.TreeMap;
import n3.e;

/* renamed from: C1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class C0149c implements H1.b, com.google.gson.internal.i, Z {
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [T, java.lang.Object, n3.g, n3.f] */
    /* JADX WARN: Type inference failed for: r0v3, types: [T, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v5, types: [C3.G, n3.d, java.lang.Object, C3.a] */
    /* JADX WARN: Type inference failed for: r3v4, types: [java.lang.Object, v3.m] */
    public static G b(F3.d dVar, u3.p pVar) {
        n3.f k4;
        ?? r02 = n3.g.f5388j;
        C3.A a4 = C3.A.f424j;
        Boolean bool = Boolean.FALSE;
        C0168s c0168s = C0168s.f500k;
        n3.f fVar = dVar.f912j;
        boolean booleanValue = ((Boolean) fVar.B(bool, c0168s)).booleanValue();
        boolean booleanValue2 = bool.booleanValue();
        if (!booleanValue && !booleanValue2) {
            k4 = fVar.k(r02);
        } else {
            ?? obj = new Object();
            obj.f6314j = r02;
            n3.f fVar2 = (n3.f) fVar.B(r02, new C3.r(obj));
            if (booleanValue2) {
                obj.f6314j = ((n3.f) obj.f6314j).B(r02, C0167q.f496k);
            }
            k4 = fVar2.k((n3.f) obj.f6314j);
        }
        G3.c cVar = K.f431a;
        if (k4 != cVar && k4.E(e.a.f5386j) == null) {
            k4 = k4.k(cVar);
        }
        ?? abstractC0151a = new AbstractC0151a(k4, true);
        int ordinal = a4.ordinal();
        l3.g gVar = l3.g.f5271a;
        if (ordinal != 0) {
            if (ordinal != 1) {
                if (ordinal != 2) {
                    if (ordinal == 3) {
                        try {
                            n3.f fVar3 = abstractC0151a.f452l;
                            Object b4 = F3.x.b(fVar3, null);
                            v3.p.a(pVar);
                            Object f = pVar.f(abstractC0151a, abstractC0151a);
                            F3.x.a(fVar3, b4);
                            if (f != o3.a.f5500j) {
                                abstractC0151a.j(f);
                            }
                        } catch (Throwable th) {
                        }
                    } else {
                        throw new RuntimeException();
                    }
                } else {
                    C3.C.d(C3.C.b(pVar, abstractC0151a, abstractC0151a)).j(gVar);
                }
            }
        } else {
            try {
                F3.i.a(C3.C.d(C3.C.b(pVar, abstractC0151a, abstractC0151a)), null);
            } finally {
                abstractC0151a.j(B2.a.a(th));
            }
        }
        return abstractC0151a;
    }

    public static void c(String str, boolean z4) {
        if (z4) {
            return;
        }
        throw new IllegalArgumentException(str);
    }

    public static void d(int i4) {
        if (i4 >= 0) {
            return;
        }
        throw new IllegalArgumentException();
    }

    public static void e(Object obj, String str) {
        if (obj != null) {
            return;
        }
        throw new NullPointerException(str);
    }

    public static final void f(View view, androidx.lifecycle.k kVar) {
        v3.h.e(view, "<this>");
        view.setTag(2131231327, kVar);
    }

    @Override // i2.Z
    public /* bridge */ /* synthetic */ Object a() {
        F f = i2.G.f3680b;
        H.a.t(f);
        return new a0(f);
    }

    public Object k() {
        return new TreeMap();
    }
}
