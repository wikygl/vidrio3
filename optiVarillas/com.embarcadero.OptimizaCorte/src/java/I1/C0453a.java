package i1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;
import e3.C0409a;
import java.util.Collections;
import java.util.HashMap;
import l1.C0717a;

/* renamed from: i1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0453a implements InterfaceC0359d<C0717a> {

    /* renamed from: a  reason: collision with root package name */
    public static final C0453a f3604a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3605b;

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3606c;

    /* renamed from: d  reason: collision with root package name */
    public static final C0358c f3607d;

    /* renamed from: e  reason: collision with root package name */
    public static final C0358c f3608e;

    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object, i1.a] */
    static {
        C0409a c0409a = new C0409a(1);
        HashMap hashMap = new HashMap();
        hashMap.put(e3.d.class, c0409a);
        f3605b = new C0358c("window", Collections.unmodifiableMap(new HashMap(hashMap)));
        C0409a c0409a2 = new C0409a(2);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(e3.d.class, c0409a2);
        f3606c = new C0358c("logSourceMetrics", Collections.unmodifiableMap(new HashMap(hashMap2)));
        C0409a c0409a3 = new C0409a(3);
        HashMap hashMap3 = new HashMap();
        hashMap3.put(e3.d.class, c0409a3);
        f3607d = new C0358c("globalMetrics", Collections.unmodifiableMap(new HashMap(hashMap3)));
        C0409a c0409a4 = new C0409a(4);
        HashMap hashMap4 = new HashMap();
        hashMap4.put(e3.d.class, c0409a4);
        f3608e = new C0358c("appNamespace", Collections.unmodifiableMap(new HashMap(hashMap4)));
    }

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        C0717a c0717a = (C0717a) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.d(f3605b, c0717a.f5237a);
        interfaceC0360e2.d(f3606c, c0717a.f5238b);
        interfaceC0360e2.d(f3607d, c0717a.f5239c);
        interfaceC0360e2.d(f3608e, c0717a.f5240d);
    }
}
