package i1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;
import e3.C0409a;
import java.util.Collections;
import java.util.HashMap;
import l1.C0720d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class d implements InterfaceC0359d<C0720d> {

    /* renamed from: a  reason: collision with root package name */
    public static final d f3614a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3615b;

    /* renamed from: c  reason: collision with root package name */
    public static final C0358c f3616c;

    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object, i1.d] */
    static {
        C0409a c0409a = new C0409a(1);
        HashMap hashMap = new HashMap();
        hashMap.put(e3.d.class, c0409a);
        f3615b = new C0358c("logSource", Collections.unmodifiableMap(new HashMap(hashMap)));
        C0409a c0409a2 = new C0409a(2);
        HashMap hashMap2 = new HashMap();
        hashMap2.put(e3.d.class, c0409a2);
        f3616c = new C0358c("logEventDropped", Collections.unmodifiableMap(new HashMap(hashMap2)));
    }

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        C0720d c0720d = (C0720d) obj;
        InterfaceC0360e interfaceC0360e2 = interfaceC0360e;
        interfaceC0360e2.d(f3615b, c0720d.f5258a);
        interfaceC0360e2.d(f3616c, c0720d.f5259b);
    }
}
