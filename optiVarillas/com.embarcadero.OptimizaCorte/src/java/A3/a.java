package A3;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class a<T> implements b<T> {

    /* renamed from: a  reason: collision with root package name */
    public final AtomicReference<b<T>> f211a;

    public a(f fVar) {
        this.f211a = new AtomicReference<>(fVar);
    }

    @Override // A3.b
    public final Iterator<T> iterator() {
        b<T> andSet = this.f211a.getAndSet(null);
        if (andSet != null) {
            return andSet.iterator();
        }
        throw new IllegalStateException("This sequence can be consumed only once.");
    }
}
