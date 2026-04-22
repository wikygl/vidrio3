package i1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;
import e3.C0409a;
import java.util.Collections;
import java.util.HashMap;
import l1.C0719c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c implements InterfaceC0359d<C0719c> {

    /* renamed from: a  reason: collision with root package name */
    public static final c f3611a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3612b;

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3613c;

    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object, i1.c] */
    static {
        C0409a c0409a = new C0409a(1);
        HashMap hashMap = new HashMap();
        hashMap.put(e3.d.class, c0409a);
        f3612b = new C0358c("eventsDroppedCount", Collections.unmodifiableMap(new HashMap(hashMap)));
        C0409a c0409a2 = new C0409a(3);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(e3.d.class, c0409a2);
        f3613c = new C0358c("reason", Collections.unmodifiableMap(new HashMap(hashMap2)));
    }

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        C0719c c0719c = (C0719c) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.a(f3612b, c0719c.f5246a);
        interfaceC0360e2.d(f3613c, c0719c.f5247b);
    }
}
