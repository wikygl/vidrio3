package n3;

import n3.e;
import u3.p;
import v3.h;
import v3.i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public interface f {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {

        /* renamed from: n3.f$a$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
        public static final class C0060a extends i implements p<f, b, f> {

            /* renamed from: k  reason: collision with root package name */
            public static final C0060a f5387k = new i(2);

            @Override // u3.p
            public final f f(f fVar, b bVar) {
                n3.c cVar;
                f fVar2 = fVar;
                b bVar2 = bVar;
                h.e(fVar2, "acc");
                h.e(bVar2, "element");
                f q4 = fVar2.q(bVar2.getKey());
                g gVar = g.f5388j;
                if (q4 != gVar) {
                    e.a aVar = e.a.f5386j;
                    e eVar = (e) q4.E(aVar);
                    if (eVar == null) {
                        cVar = new n3.c(bVar2, q4);
                    } else {
                        f q5 = q4.q(aVar);
                        if (q5 == gVar) {
                            return new n3.c(eVar, bVar2);
                        }
                        cVar = new n3.c(eVar, new n3.c(bVar2, q5));
                    }
                    return cVar;
                }
                return bVar2;
            }
        }

        public static f a(f fVar, f fVar2) {
            h.e(fVar2, "context");
            if (fVar2 != g.f5388j) {
                return (f) fVar2.B(fVar, C0060a.f5387k);
            }
            return fVar;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public interface b extends f {

        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
        public static final class a {
            /* JADX WARN: Multi-variable type inference failed */
            public static <E extends b> E a(b bVar, c<E> cVar) {
                h.e(cVar, "key");
                if (!h.a(bVar.getKey(), cVar)) {
                    return null;
                }
                return bVar;
            }

            public static f b(b bVar, c<?> cVar) {
                h.e(cVar, "key");
                if (h.a(bVar.getKey(), cVar)) {
                    return g.f5388j;
                }
                return bVar;
            }

            public static f c(b bVar, f fVar) {
                h.e(fVar, "context");
                return a.a(bVar, fVar);
            }
        }

        c<?> getKey();
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public interface c<E extends b> {
    }

    <R> R B(R r4, p<? super R, ? super b, ? extends R> pVar);

    <E extends b> E E(c<E> cVar);

    f k(f fVar);

    f q(c<?> cVar);
}
