package p3;

import v3.h;
import v3.n;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class g extends c implements v3.e<Object> {

    /* renamed from: m  reason: collision with root package name */
    public final int f5586m;

    public g(n3.d dVar) {
        super(dVar);
        this.f5586m = 2;
    }

    @Override // v3.e
    public final int e() {
        return this.f5586m;
    }

    @Override // p3.a
    public final String toString() {
        if (this.f5577j == null) {
            n.f6315a.getClass();
            String obj = getClass().getGenericInterfaces()[0].toString();
            if (obj.startsWith("kotlin.jvm.functions.")) {
                obj = obj.substring(21);
            }
            h.d(obj, "renderLambdaToString(this)");
            return obj;
        }
        return super.toString();
    }
}
