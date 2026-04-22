package o1;

import java.util.HashMap;
import java.util.Map;
import o1.f;
import r1.InterfaceC0782a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b extends f {

    /* renamed from: a  reason: collision with root package name */
    public final InterfaceC0782a f5427a;

    /* renamed from: b  reason: collision with root package name */
    public final Map<f1.d, f.a> f5428b;

    public b(InterfaceC0782a interfaceC0782a, HashMap hashMap) {
        this.f5427a = interfaceC0782a;
        this.f5428b = hashMap;
    }

    @Override // o1.f
    public final InterfaceC0782a a() {
        return this.f5427a;
    }

    @Override // o1.f
    public final Map<f1.d, f.a> c() {
        return this.f5428b;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof f)) {
            return false;
        }
        f fVar = (f) obj;
        if (this.f5427a.equals(fVar.a()) && this.f5428b.equals(fVar.c())) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        return ((this.f5427a.hashCode() ^ 1000003) * 1000003) ^ this.f5428b.hashCode();
    }

    public final String toString() {
        return "SchedulerConfig{clock=" + this.f5427a + ", values=" + this.f5428b + "}";
    }
}
