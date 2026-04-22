package h1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class f implements InterfaceC0359d<r> {

    /* renamed from: a  reason: collision with root package name */
    public static final f f3531a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3532b = C0358c.a("requestTimeMs");

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3533c = C0358c.a("requestUptimeMs");

    /* renamed from: d  reason: collision with root package name */
    public static final C0358c f3534d = C0358c.a("clientInfo");

    /* renamed from: e  reason: collision with root package name */
    public static final C0358c f3535e = C0358c.a("logSource");
    public static final C0358c f = C0358c.a("logSourceName");

    /* renamed from: g  reason: collision with root package name */
    public static final C0358c f3536g = C0358c.a("logEvent");

    /* renamed from: h  reason: collision with root package name */
    public static final C0358c f3537h = C0358c.a("qosTier");

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        r rVar = (r) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.a(f3532b, rVar.f());
        interfaceC0360e2.a(f3533c, rVar.g());
        interfaceC0360e2.d(f3534d, rVar.a());
        interfaceC0360e2.d(f3535e, rVar.c());
        interfaceC0360e2.d(f, rVar.d());
        interfaceC0360e2.d(f3536g, rVar.b());
        interfaceC0360e2.d(f3537h, rVar.e());
    }
}
