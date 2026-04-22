package l3;

import androidx.lifecycle.z;
import java.io.Serializable;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class d<T> implements Serializable {

    /* renamed from: j  reason: collision with root package name */
    public u3.a<? extends T> f5267j;

    /* renamed from: k  reason: collision with root package name */
    public volatile Object f5268k = e.f5270a;

    /* renamed from: l  reason: collision with root package name */
    public final Object f5269l = this;

    public d(z.a aVar) {
        this.f5267j = aVar;
    }

    public final T a() {
        T t3;
        T t4 = (T) this.f5268k;
        e eVar = e.f5270a;
        if (t4 != eVar) {
            return t4;
        }
        synchronized (this.f5269l) {
            t3 = (T) this.f5268k;
            if (t3 == eVar) {
                u3.a<? extends T> aVar = this.f5267j;
                h.b(aVar);
                t3 = aVar.b();
                this.f5268k = t3;
                this.f5267j = null;
            }
        }
        return t3;
    }

    public final String toString() {
        if (this.f5268k != e.f5270a) {
            return String.valueOf(a());
        }
        return "Lazy value not initialized yet.";
    }
}
