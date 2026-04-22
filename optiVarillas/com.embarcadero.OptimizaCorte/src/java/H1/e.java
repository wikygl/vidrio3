package h1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e implements InterfaceC0359d<q> {

    /* renamed from: a  reason: collision with root package name */
    public static final e f3524a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3525b = C0358c.a("eventTimeMs");

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3526c = C0358c.a("eventCode");

    /* renamed from: d  reason: collision with root package name */
    public static final C0358c f3527d = C0358c.a("eventUptimeMs");

    /* renamed from: e  reason: collision with root package name */
    public static final C0358c f3528e = C0358c.a("sourceExtension");
    public static final C0358c f = C0358c.a("sourceExtensionJsonProto3");

    /* renamed from: g  reason: collision with root package name */
    public static final C0358c f3529g = C0358c.a("timezoneOffsetSeconds");

    /* renamed from: h  reason: collision with root package name */
    public static final C0358c f3530h = C0358c.a("networkConnectionInfo");

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        q qVar = (q) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.a(f3525b, qVar.b());
        interfaceC0360e2.d(f3526c, qVar.a());
        interfaceC0360e2.a(f3527d, qVar.c());
        interfaceC0360e2.d(f3528e, qVar.e());
        interfaceC0360e2.d(f, qVar.f());
        interfaceC0360e2.a(f3529g, qVar.g());
        interfaceC0360e2.d(f3530h, qVar.d());
    }
}
