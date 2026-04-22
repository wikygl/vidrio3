package i1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;
import e3.C0409a;
import java.util.Collections;
import java.util.HashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class g implements InterfaceC0359d<l1.f> {

    /* renamed from: a  reason: collision with root package name */
    public static final g f3622a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3623b;

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3624c;

    /* JADX WARN: Type inference failed for: r0v0, types: [i1.g, java.lang.Object] */
    static {
        C0409a c0409a = new C0409a(1);
        HashMap hashMap = new HashMap();
        hashMap.put(e3.d.class, c0409a);
        f3623b = new C0358c("startMs", Collections.unmodifiableMap(new HashMap(hashMap)));
        C0409a c0409a2 = new C0409a(2);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(e3.d.class, c0409a2);
        f3624c = new C0358c("endMs", Collections.unmodifiableMap(new HashMap(hashMap2)));
    }

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        l1.f fVar = (l1.f) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.a(f3623b, fVar.f5262a);
        interfaceC0360e2.a(f3624c, fVar.f5263b);
    }
}
