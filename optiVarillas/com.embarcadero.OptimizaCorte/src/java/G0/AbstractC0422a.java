package g0;

import C1.C0149c;
import C3.C0175z;
import C3.InterfaceC0174y;
import C3.K;
import F3.q;
import G3.c;
import G3.g;
import a3.InterfaceFutureC0346a;
import i0.C0445a;
import i0.C0446b;
import i0.C0452h;
import n3.d;
import p3.e;
import u3.p;
import v3.h;

/* renamed from: g0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0422a {

    /* renamed from: g0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class C0049a extends AbstractC0422a {

        /* renamed from: a  reason: collision with root package name */
        public final g f3475a;

        @e(c = "androidx.privacysandbox.ads.adservices.java.topics.TopicsManagerFutures$Api33Ext4JavaImpl$getTopicsAsync$1", f = "TopicsManagerFutures.kt", l = {56}, m = "invokeSuspend")
        /* renamed from: g0.a$a$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
        public static final class C0050a extends p3.g implements p<InterfaceC0174y, d<? super C0446b>, Object> {

            /* renamed from: n  reason: collision with root package name */
            public int f3476n;

            /* renamed from: p  reason: collision with root package name */
            public final /* synthetic */ C0445a f3478p;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public C0050a(C0445a c0445a, d<? super C0050a> dVar) {
                super(dVar);
                this.f3478p = c0445a;
            }

            @Override // p3.a
            public final d a(d dVar) {
                return new C0050a(this.f3478p, dVar);
            }

            @Override // u3.p
            public final Object f(InterfaceC0174y interfaceC0174y, d<? super C0446b> dVar) {
                return ((C0050a) a(dVar)).i(l3.g.f5271a);
            }

            @Override // p3.a
            public final Object i(Object obj) {
                o3.a aVar = o3.a.f5500j;
                int i4 = this.f3476n;
                if (i4 != 0) {
                    if (i4 == 1) {
                        B2.a.n(obj);
                    } else {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                } else {
                    B2.a.n(obj);
                    g gVar = C0049a.this.f3475a;
                    this.f3476n = 1;
                    obj = gVar.t(this.f3478p, this);
                    if (obj == aVar) {
                        return aVar;
                    }
                }
                return obj;
            }
        }

        public C0049a(C0452h c0452h) {
            this.f3475a = c0452h;
        }

        public InterfaceFutureC0346a<C0446b> a(C0445a c0445a) {
            h.e(c0445a, "request");
            c cVar = K.f431a;
            return A0.c.a(C0149c.b(C0175z.a(q.f944a), new C0050a(c0445a, null)));
        }
    }
}
