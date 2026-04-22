package J1;

import android.content.Context;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class f implements k3.a {

    /* renamed from: j  reason: collision with root package name */
    public final Object f1242j;

    @Override // k3.a
    public Object get() {
        String packageName = ((Context) ((k3.a) this.f1242j).get()).getPackageName();
        if (packageName != null) {
            return packageName;
        }
        throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
}
