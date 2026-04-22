package n3;

import java.io.Serializable;
import n3.f;
import u3.p;
import v3.h;
import v3.i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class c implements f, Serializable {

    /* renamed from: j  reason: collision with root package name */
    public final f f5383j;

    /* renamed from: k  reason: collision with root package name */
    public final f.b f5384k;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static final class a extends i implements p<String, f.b, String> {

        /* renamed from: k  reason: collision with root package name */
        public static final a f5385k = new i(2);

        @Override // u3.p
        public final String f(String str, f.b bVar) {
            String str2 = str;
            f.b bVar2 = bVar;
            h.e(str2, "acc");
            h.e(bVar2, "element");
            if (str2.length() == 0) {
                return bVar2.toString();
            }
            return str2 + ", " + bVar2;
        }
    }

    public c(f.b bVar, f fVar) {
        h.e(fVar, "left");
        h.e(bVar, "element");
        this.f5383j = fVar;
        this.f5384k = bVar;
    }

    @Override // n3.f
    public final <R> R B(R r4, p<? super R, ? super f.b, ? extends R> pVar) {
        return pVar.f((Object) this.f5383j.B(r4, pVar), this.f5384k);
    }

    @Override // n3.f
    public final <E extends f.b> E E(f.c<E> cVar) {
        h.e(cVar, "key");
        c cVar2 = this;
        while (true) {
            E e4 = (E) cVar2.f5384k.E(cVar);
            if (e4 != null) {
                return e4;
            }
            f fVar = cVar2.f5383j;
            if (fVar instanceof c) {
                cVar2 = (c) fVar;
            } else {
                return (E) fVar.E(cVar);
            }
        }
    }

    public final boolean equals(Object obj) {
        boolean z4;
        if (this != obj) {
            if (!(obj instanceof c)) {
                return false;
            }
            c cVar = (c) obj;
            cVar.getClass();
            int i4 = 2;
            c cVar2 = cVar;
            int i5 = 2;
            while (true) {
                f fVar = cVar2.f5383j;
                if (fVar instanceof c) {
                    cVar2 = (c) fVar;
                } else {
                    cVar2 = null;
                }
                if (cVar2 == null) {
                    break;
                }
                i5++;
            }
            c cVar3 = this;
            while (true) {
                f fVar2 = cVar3.f5383j;
                if (fVar2 instanceof c) {
                    cVar3 = (c) fVar2;
                } else {
                    cVar3 = null;
                }
                if (cVar3 == null) {
                    break;
                }
                i4++;
            }
            if (i5 != i4) {
                return false;
            }
            c cVar4 = this;
            while (true) {
                f.b bVar = cVar4.f5384k;
                if (!h.a(cVar.E(bVar.getKey()), bVar)) {
                    z4 = false;
                    break;
                }
                f fVar3 = cVar4.f5383j;
                if (fVar3 instanceof c) {
                    cVar4 = (c) fVar3;
                } else {
                    h.c(fVar3, "null cannot be cast to non-null type kotlin.coroutines.CoroutineContext.Element");
                    f.b bVar2 = (f.b) fVar3;
                    z4 = h.a(cVar.E(bVar2.getKey()), bVar2);
                    break;
                }
            }
            if (!z4) {
                return false;
            }
        }
        return true;
    }

    public final int hashCode() {
        return this.f5384k.hashCode() + this.f5383j.hashCode();
    }

    @Override // n3.f
    public final f k(f fVar) {
        return f.a.a(this, fVar);
    }

    @Override // n3.f
    public final f q(f.c<?> cVar) {
        h.e(cVar, "key");
        f.b bVar = this.f5384k;
        f.b E4 = bVar.E(cVar);
        f fVar = this.f5383j;
        if (E4 != null) {
            return fVar;
        }
        f q4 = fVar.q(cVar);
        if (q4 == fVar) {
            return this;
        }
        if (q4 != g.f5388j) {
            return new c(bVar, q4);
        }
        return bVar;
    }

    public final String toString() {
        return "[" + ((String) B("", a.f5385k)) + ']';
    }
}
