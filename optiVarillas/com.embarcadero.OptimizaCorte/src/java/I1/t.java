package i1;

import C3.C;
import com.google.android.gms.internal.play_billing.P1;
import f1.AbstractC0413c;
import f1.C0411a;
import f1.C0412b;
import java.util.HashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class t<T> {

    /* renamed from: a  reason: collision with root package name */
    public final s f3656a;

    /* renamed from: b  reason: collision with root package name */
    public final String f3657b = "PLAY_BILLING_LIBRARY";

    /* renamed from: c  reason: collision with root package name */
    public final C0412b f3658c;

    /* renamed from: d  reason: collision with root package name */
    public final C f3659d;

    /* renamed from: e  reason: collision with root package name */
    public final u f3660e;

    public t(j jVar, C0412b c0412b, C c4, u uVar) {
        this.f3656a = jVar;
        this.f3658c = c0412b;
        this.f3659d = c4;
        this.f3660e = uVar;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [e0.a, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.Object, i1.h$a] */
    public final void a(C0411a c0411a) {
        ?? obj = new Object();
        s sVar = this.f3656a;
        if (sVar != null) {
            String str = this.f3657b;
            if (str != null) {
                C c4 = this.f3659d;
                if (c4 != null) {
                    C0412b c0412b = this.f3658c;
                    if (c0412b != null) {
                        i iVar = new i(sVar, str, c0411a, c4, c0412b);
                        v vVar = (v) this.f3660e;
                        vVar.getClass();
                        AbstractC0413c<?> abstractC0413c = iVar.f3637c;
                        f1.d c5 = abstractC0413c.c();
                        s sVar2 = iVar.f3635a;
                        String a4 = sVar2.a();
                        if (a4 != null) {
                            if (c5 != null) {
                                j jVar = new j(a4, sVar2.b(), c5);
                                ?? obj2 = new Object();
                                obj2.f = new HashMap();
                                obj2.f3633d = Long.valueOf(vVar.f3662a.a());
                                obj2.f3634e = Long.valueOf(vVar.f3663b.a());
                                String str2 = iVar.f3636b;
                                if (str2 != null) {
                                    obj2.f3630a = str2;
                                    Object b4 = abstractC0413c.b();
                                    iVar.f3638d.getClass();
                                    obj2.f3632c = new m(iVar.f3639e, ((P1) b4).e());
                                    obj2.f3631b = abstractC0413c.a();
                                    vVar.f3664c.a(jVar, obj2.b(), obj);
                                    return;
                                }
                                throw new NullPointerException("Null transportName");
                            }
                            throw new NullPointerException("Null priority");
                        }
                        throw new NullPointerException("Null backendName");
                    }
                    throw new NullPointerException("Null encoding");
                }
                throw new NullPointerException("Null transformer");
            }
            throw new NullPointerException("Null transportName");
        }
        throw new NullPointerException("Null transportContext");
    }
}
