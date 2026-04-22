package F3;

import C3.InterfaceC0172w;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceConfigurationError;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f {

    /* renamed from: a  reason: collision with root package name */
    public static final Collection<InterfaceC0172w> f913a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v7, types: [A3.a] */
    static {
        try {
            Iterator it = Arrays.asList(new D3.b()).iterator();
            v3.h.e(it, "<this>");
            A3.f fVar = new A3.f(it);
            if (!(fVar instanceof A3.a)) {
                fVar = new A3.a(fVar);
            }
            f913a = A3.c.m(fVar);
        } catch (Throwable th) {
            throw new ServiceConfigurationError(th.getMessage(), th);
        }
    }
}
