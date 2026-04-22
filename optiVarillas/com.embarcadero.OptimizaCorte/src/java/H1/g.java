package h1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class g implements InterfaceC0359d<t> {

    /* renamed from: a  reason: collision with root package name */
    public static final g f3538a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3539b = C0358c.a("networkType");

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3540c = C0358c.a("mobileSubtype");

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        t tVar = (t) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.d(f3539b, tVar.b());
        interfaceC0360e2.d(f3540c, tVar.a());
    }
}
