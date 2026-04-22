package i1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;
import e3.C0409a;
import java.util.Collections;
import java.util.HashMap;
import l1.C0721e;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class f implements InterfaceC0359d<C0721e> {

    /* renamed from: a  reason: collision with root package name */
    public static final f f3619a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3620b;

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3621c;

    /* JADX WARN: Type inference failed for: r0v0, types: [i1.f, java.lang.Object] */
    static {
        C0409a c0409a = new C0409a(1);
        HashMap hashMap = new HashMap();
        hashMap.put(e3.d.class, c0409a);
        f3620b = new C0358c("currentCacheSizeBytes", Collections.unmodifiableMap(new HashMap(hashMap)));
        C0409a c0409a2 = new C0409a(2);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(e3.d.class, c0409a2);
        f3621c = new C0358c("maxCacheSizeBytes", Collections.unmodifiableMap(new HashMap(hashMap2)));
    }

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        C0721e c0721e = (C0721e) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.a(f3620b, c0721e.f5260a);
        interfaceC0360e2.a(f3621c, c0721e.f5261b);
    }
}
