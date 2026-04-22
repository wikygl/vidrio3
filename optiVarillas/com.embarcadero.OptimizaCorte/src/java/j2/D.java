package j2;

import W1.C0324l;
import android.app.Activity;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class D extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ Bundle f4777n;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ Activity f4778o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ F f4779p;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public D(F f, Bundle bundle, Activity activity) {
        super(f.f4782j, true);
        this.f4779p = f;
        this.f4777n = bundle;
        this.f4778o = activity;
    }

    @Override // j2.C
    public final void a() {
        Bundle bundle;
        if (this.f4777n != null) {
            bundle = new Bundle();
            if (this.f4777n.containsKey("com.google.app_measurement.screen_service")) {
                Object obj = this.f4777n.get("com.google.app_measurement.screen_service");
                if (obj instanceof Bundle) {
                    bundle.putBundle("com.google.app_measurement.screen_service", (Bundle) obj);
                }
            }
        } else {
            bundle = null;
        }
        InterfaceC0675e interfaceC0675e = this.f4779p.f4782j.f4790h;
        C0324l.d(interfaceC0675e);
        interfaceC0675e.r4(new c2.b(this.f4778o), bundle, this.f4774k);
    }
}
