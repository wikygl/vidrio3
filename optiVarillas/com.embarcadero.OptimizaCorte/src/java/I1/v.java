package i1;

import S0.A;
import android.content.Context;
import com.google.android.gms.internal.ads.x3;
import f1.C0412b;
import java.util.Collections;
import java.util.Set;
import r1.InterfaceC0782a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class v implements u {

    /* renamed from: e  reason: collision with root package name */
    public static volatile k f3661e;

    /* renamed from: a  reason: collision with root package name */
    public final InterfaceC0782a f3662a;

    /* renamed from: b  reason: collision with root package name */
    public final InterfaceC0782a f3663b;

    /* renamed from: c  reason: collision with root package name */
    public final n1.d f3664c;

    /* renamed from: d  reason: collision with root package name */
    public final o1.p f3665d;

    public v(InterfaceC0782a interfaceC0782a, InterfaceC0782a interfaceC0782a2, n1.d dVar, o1.p pVar, o1.s sVar) {
        this.f3662a = interfaceC0782a;
        this.f3663b = interfaceC0782a2;
        this.f3664c = dVar;
        this.f3665d = pVar;
        sVar.getClass();
        sVar.f5482a.execute(new A(8, sVar));
    }

    public static v a() {
        k kVar = f3661e;
        if (kVar != null) {
            return kVar.f3648o.get();
        }
        throw new IllegalStateException("Not initialized!");
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [i1.k$a, java.lang.Object] */
    public static void b(Context context) {
        if (f3661e == null) {
            synchronized (v.class) {
                try {
                    if (f3661e == null) {
                        ?? obj = new Object();
                        context.getClass();
                        obj.f3649a = context;
                        f3661e = obj.a();
                    }
                } finally {
                }
            }
        }
    }

    public final x3 c(l lVar) {
        Set singleton;
        if (lVar instanceof l) {
            singleton = Collections.unmodifiableSet(lVar.a());
        } else {
            singleton = Collections.singleton(new C0412b("proto"));
        }
        f1.d dVar = f1.d.f3393j;
        lVar.getClass();
        return new x3(singleton, new j("cct", lVar.b(), dVar), this);
    }
}
