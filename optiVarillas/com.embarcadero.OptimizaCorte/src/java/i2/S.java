package i2;

import java.util.NoSuchElementException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class S extends W {

    /* renamed from: k  reason: collision with root package name */
    public static final Object f3691k = new Object();

    /* renamed from: j  reason: collision with root package name */
    public Object f3692j;

    public S(Object obj) {
        this.f3692j = obj;
    }

    @Override // java.util.Iterator
    public final boolean hasNext() {
        if (this.f3692j != f3691k) {
            return true;
        }
        return false;
    }

    @Override // java.util.Iterator
    public final Object next() {
        Object obj = this.f3692j;
        Object obj2 = f3691k;
        if (obj != obj2) {
            this.f3692j = obj2;
            return obj;
        }
        throw new NoSuchElementException();
    }
}
