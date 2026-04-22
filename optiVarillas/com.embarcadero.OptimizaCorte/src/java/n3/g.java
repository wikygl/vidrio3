package n3;

import java.io.Serializable;
import n3.f;
import u3.p;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class g implements f, Serializable {

    /* renamed from: j  reason: collision with root package name */
    public static final g f5388j = new Object();

    @Override // n3.f
    public final <E extends f.b> E E(f.c<E> cVar) {
        h.e(cVar, "key");
        return null;
    }

    public final int hashCode() {
        return 0;
    }

    @Override // n3.f
    public final f k(f fVar) {
        h.e(fVar, "context");
        return fVar;
    }

    @Override // n3.f
    public final f q(f.c<?> cVar) {
        h.e(cVar, "key");
        return this;
    }

    public final String toString() {
        return "EmptyCoroutineContext";
    }

    @Override // n3.f
    public final <R> R B(R r4, p<? super R, ? super f.b, ? extends R> pVar) {
        return r4;
    }
}
