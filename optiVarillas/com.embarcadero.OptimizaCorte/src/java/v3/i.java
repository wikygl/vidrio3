package v3;

import java.io.Serializable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class i<R> implements e<R>, Serializable {

    /* renamed from: j  reason: collision with root package name */
    public final int f6311j;

    public i(int i4) {
        this.f6311j = i4;
    }

    @Override // v3.e
    public final int e() {
        return this.f6311j;
    }

    public final String toString() {
        n.f6315a.getClass();
        String obj = getClass().getGenericInterfaces()[0].toString();
        if (obj.startsWith("kotlin.jvm.functions.")) {
            obj = obj.substring(21);
        }
        h.d(obj, "renderLambdaToString(this)");
        return obj;
    }
}
