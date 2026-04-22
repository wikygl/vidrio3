package F3;

import C3.AbstractC0171v;
import C3.C0162l;
import C3.C0163m;
import C3.C0169t;
import C3.J;
import C3.N;
import C3.Y;
import C3.m0;
import C3.o0;
import java.util.concurrent.CancellationException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class i {

    /* renamed from: a  reason: collision with root package name */
    public static final C1.A f920a = new C1.A(1, "UNDEFINED");

    /* renamed from: b  reason: collision with root package name */
    public static final C1.A f921b = new C1.A(1, "REUSABLE_CLAIMED");

    public static final void a(n3.d dVar, u3.l lVar) {
        Object c0162l;
        o0 o0Var;
        l3.g gVar = l3.g.f5271a;
        if (dVar instanceof h) {
            h hVar = (h) dVar;
            Throwable a4 = l3.c.a(gVar);
            if (a4 == null) {
                if (lVar != null) {
                    c0162l = new C0163m(gVar, lVar);
                } else {
                    c0162l = gVar;
                }
            } else {
                c0162l = new C0162l(a4, false);
            }
            n3.d<T> dVar2 = hVar.f917n;
            dVar2.getContext();
            AbstractC0171v abstractC0171v = hVar.f916m;
            if (abstractC0171v.G()) {
                hVar.f918o = c0162l;
                hVar.f430l = 1;
                abstractC0171v.F(dVar2.getContext(), hVar);
                return;
            }
            N a5 = m0.a();
            if (a5.f434l >= 4294967296L) {
                hVar.f918o = c0162l;
                hVar.f430l = 1;
                m3.a<J<?>> aVar = a5.f436n;
                if (aVar == null) {
                    aVar = new m3.a<>();
                    a5.f436n = aVar;
                }
                aVar.j(hVar);
                return;
            }
            a5.I(true);
            try {
                Y y4 = (Y) dVar2.getContext().E(Y.b.f450j);
                if (y4 != null && !y4.a()) {
                    CancellationException t3 = y4.t();
                    hVar.a(c0162l, t3);
                    hVar.j(B2.a.a(t3));
                } else {
                    Object obj = hVar.f919p;
                    n3.f context = dVar2.getContext();
                    Object b4 = x.b(context, obj);
                    if (b4 != x.f947a) {
                        o0Var = C0169t.a(dVar2, context);
                    } else {
                        o0Var = null;
                    }
                    dVar2.j(gVar);
                    if (o0Var == null) {
                        x.a(context, b4);
                    } else {
                        o0Var.T();
                        throw null;
                    }
                }
                do {
                } while (a5.J());
            } finally {
                try {
                    return;
                } finally {
                }
            }
            return;
        }
        dVar.j(gVar);
    }
}
