package D1;

import android.content.Context;
import java.io.IOException;
import x1.C0855a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class N extends AbstractC0200u {

    /* renamed from: b  reason: collision with root package name */
    public final Context f642b;

    public N(Context context) {
        this.f642b = context;
    }

    @Override // D1.AbstractC0200u
    public final void a() {
        boolean z4;
        try {
            z4 = C0855a.b(this.f642b);
        } catch (T1.g | IOException | IllegalStateException e4) {
            E1.m.e("Fail to get isAdIdFakeForDebugLogging", e4);
            z4 = false;
        }
        synchronized (E1.l.f870b) {
            E1.l.f871c = true;
            E1.l.f872d = z4;
        }
        E1.m.g("Update ad debug logging enablement as " + z4);
    }
}
