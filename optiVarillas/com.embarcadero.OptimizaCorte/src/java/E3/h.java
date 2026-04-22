package e3;

import b3.InterfaceC0359d;
import b3.InterfaceC0361f;
import c3.InterfaceC0375a;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import l1.C0717a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class h {

    /* renamed from: a  reason: collision with root package name */
    public final Map<Class<?>, InterfaceC0359d<?>> f3371a;

    /* renamed from: b  reason: collision with root package name */
    public final Map<Class<?>, InterfaceC0361f<?>> f3372b;

    /* renamed from: c  reason: collision with root package name */
    public final InterfaceC0359d<Object> f3373c;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class a implements InterfaceC0375a<a> {

        /* renamed from: a  reason: collision with root package name */
        public static final g f3374a = new Object();
    }

    public h(HashMap hashMap, HashMap hashMap2, g gVar) {
        this.f3371a = hashMap;
        this.f3372b = hashMap2;
        this.f3373c = gVar;
    }

    public final void a(C0717a c0717a, ByteArrayOutputStream byteArrayOutputStream) {
        Map<Class<?>, InterfaceC0359d<?>> map = this.f3371a;
        f fVar = new f(byteArrayOutputStream, map, this.f3372b, this.f3373c);
        InterfaceC0359d<?> interfaceC0359d = map.get(C0717a.class);
        if (interfaceC0359d != null) {
            interfaceC0359d.a(c0717a, fVar);
            return;
        }
        throw new RuntimeException("No encoder for " + C0717a.class);
    }
}
