package i1;

import b3.C0358c;
import b3.InterfaceC0359d;
import b3.InterfaceC0360e;
import e3.C0409a;
import java.util.Collections;
import java.util.HashMap;
import l1.C0718b;

/* renamed from: i1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0454b implements InterfaceC0359d<C0718b> {

    /* renamed from: a  reason: collision with root package name */
    public static final C0454b f3609a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final C0358c f3610b;

    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object, i1.b] */
    static {
        C0409a c0409a = new C0409a(1);
        HashMap hashMap = new HashMap();
        hashMap.put(e3.d.class, c0409a);
        f3610b = new C0358c("storageMetrics", Collections.unmodifiableMap(new HashMap(hashMap)));
    }

    @Override // b3.InterfaceC0356a
    public final void a(Object obj, InterfaceC0360e interfaceC0360e) {
        interfaceC0360e.d(f3610b, ((C0718b) obj).f5245a);
    }
}
