package V1;

import W1.C0323k;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class v {

    /* renamed from: a  reason: collision with root package name */
    public final C0295a f2617a;

    /* renamed from: b  reason: collision with root package name */
    public final T1.d f2618b;

    public /* synthetic */ v(C0295a c0295a, T1.d dVar) {
        this.f2617a = c0295a;
        this.f2618b = dVar;
    }

    public final boolean equals(Object obj) {
        if (obj != null && (obj instanceof v)) {
            v vVar = (v) obj;
            if (C0323k.a(this.f2617a, vVar.f2617a) && C0323k.a(this.f2618b, vVar.f2618b)) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.f2617a, this.f2618b});
    }

    public final String toString() {
        C0323k.a aVar = new C0323k.a(this);
        aVar.a(this.f2617a, "key");
        aVar.a(this.f2618b, "feature");
        return aVar.toString();
    }
}
