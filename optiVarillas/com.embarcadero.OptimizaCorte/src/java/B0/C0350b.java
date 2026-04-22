package b0;

import androidx.lifecycle.C;
import androidx.lifecycle.E;
import v3.h;

/* renamed from: b0.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0350b implements E.a {

    /* renamed from: a  reason: collision with root package name */
    public final C0352d<?>[] f2872a;

    public C0350b(C0352d<?>... c0352dArr) {
        h.e(c0352dArr, "initializers");
        this.f2872a = c0352dArr;
    }

    public final C a(Class cls) {
        throw new UnsupportedOperationException("Factory.create(String) is unsupported.  This Factory requires `CreationExtras` to be passed into `create` method.");
    }

    public final C b(Class cls, C0351c c0351c) {
        C0352d<?>[] c0352dArr;
        C c4 = null;
        for (C0352d<?> c0352d : this.f2872a) {
            if (h.a(c0352d.f2873a, cls)) {
                Object g4 = c0352d.f2874b.g(c0351c);
                if (g4 instanceof C) {
                    c4 = (C) g4;
                } else {
                    c4 = null;
                }
            }
        }
        if (c4 != null) {
            return c4;
        }
        throw new IllegalArgumentException("No initializer set for given class ".concat(cls.getName()));
    }
}
