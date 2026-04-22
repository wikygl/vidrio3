package B;

import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class u {

    /* renamed from: a  reason: collision with root package name */
    public s f271a;

    public void a(Bundle bundle) {
        bundle.putString("androidx.core.app.extra.COMPAT_TEMPLATE", c());
    }

    public abstract void b(v vVar);

    public abstract String c();

    public final void d(s sVar) {
        if (this.f271a != sVar) {
            this.f271a = sVar;
            if (sVar != null) {
                sVar.d(this);
            }
        }
    }
}
