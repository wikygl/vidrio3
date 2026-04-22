package n3;

import n3.f;
import n3.f.b;
import u3.l;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class b<B extends f.b, E extends B> implements f.c<E> {

    /* renamed from: j  reason: collision with root package name */
    public final l<f.b, E> f5381j;

    /* renamed from: k  reason: collision with root package name */
    public final f.c<?> f5382k;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [u3.l<n3.f$b, E extends B>, u3.l<? super n3.f$b, ? extends E extends B>] */
    public b(f.c<B> cVar, l<? super f.b, ? extends E> lVar) {
        h.e(cVar, "baseKey");
        this.f5381j = lVar;
        this.f5382k = cVar instanceof b ? (f.c<B>) ((b) cVar).f5382k : cVar;
    }

    /* JADX WARN: Incorrect return type in method signature: (Ln3/f$b;)TE; */
    public final f.b a(f.b bVar) {
        h.e(bVar, "element");
        return (f.b) this.f5381j.g(bVar);
    }
}
