package h1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;

/* renamed from: h1.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0437d implements InterfaceC0359d<p> {

    /* renamed from: a  reason: collision with root package name */
    public static final C0437d f3521a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3522b = C0358c.a("clientType");

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3523c = C0358c.a("androidClientInfo");

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        p pVar = (p) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.d(f3522b, pVar.b());
        interfaceC0360e2.d(f3523c, pVar.a());
    }
}
