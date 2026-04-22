package j$.util.concurrent;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class d extends AbstractC0500a implements Iterator {
    @Override // java.util.Iterator
    public final Object next() {
        l lVar = this.f4173b;
        if (lVar != null) {
            Object obj = lVar.f4165b;
            Object obj2 = lVar.f4166c;
            this.f4153j = lVar;
            a();
            return new k(obj, obj2, this.f4152i);
        }
        throw new NoSuchElementException();
    }
}
