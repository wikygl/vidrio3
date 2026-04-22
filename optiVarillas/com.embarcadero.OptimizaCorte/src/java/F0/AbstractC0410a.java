package f0;

import C1.C0149c;
import C3.C0175z;
import C3.InterfaceC0174y;
import C3.K;
import a3.InterfaceFutureC0346a;
import android.net.Uri;
import android.view.InputEvent;
import h0.C0430a;
import h0.j;
import h0.k;
import h0.l;
import n3.d;
import p3.e;
import p3.g;
import u3.p;
import v3.h;

/* renamed from: f0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0410a {

    /* renamed from: f0.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static final class C0045a extends AbstractC0410a {

        /* renamed from: a  reason: collision with root package name */
        public final j f3379a;

        @e(c = "androidx.privacysandbox.ads.adservices.java.measurement.MeasurementManagerFutures$Api33Ext5JavaImpl$getMeasurementApiStatusAsync$1", f = "MeasurementManagerFutures.kt", l = {169}, m = "invokeSuspend")
        /* renamed from: f0.a$a$a  reason: collision with other inner class name */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
        public static final class C0046a extends g implements p<InterfaceC0174y, d<? super Integer>, Object> {

            /* renamed from: n  reason: collision with root package name */
            public int f3380n;

            public C0046a(d<? super C0046a> dVar) {
                super(dVar);
            }

            @Override // p3.a
            public final d a(d dVar) {
                return new C0046a(dVar);
            }

            @Override // u3.p
            public final Object f(InterfaceC0174y interfaceC0174y, d<? super Integer> dVar) {
                return ((C0046a) a(dVar)).i(l3.g.f5271a);
            }

            @Override // p3.a
            public final Object i(Object obj) {
                o3.a aVar = o3.a.f5500j;
                int i4 = this.f3380n;
                if (i4 != 0) {
                    if (i4 == 1) {
                        B2.a.n(obj);
                    } else {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                } else {
                    B2.a.n(obj);
                    j jVar = C0045a.this.f3379a;
                    this.f3380n = 1;
                    obj = jVar.a(this);
                    if (obj == aVar) {
                        return aVar;
                    }
                }
                return obj;
            }
        }

        @e(c = "androidx.privacysandbox.ads.adservices.java.measurement.MeasurementManagerFutures$Api33Ext5JavaImpl$registerSourceAsync$1", f = "MeasurementManagerFutures.kt", l = {133}, m = "invokeSuspend")
        /* renamed from: f0.a$a$b */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
        public static final class b extends g implements p<InterfaceC0174y, d<? super l3.g>, Object> {

            /* renamed from: n  reason: collision with root package name */
            public int f3382n;

            /* renamed from: p  reason: collision with root package name */
            public final /* synthetic */ Uri f3384p;

            /* renamed from: q  reason: collision with root package name */
            public final /* synthetic */ InputEvent f3385q;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public b(Uri uri, InputEvent inputEvent, d<? super b> dVar) {
                super(dVar);
                this.f3384p = uri;
                this.f3385q = inputEvent;
            }

            @Override // p3.a
            public final d a(d dVar) {
                return new b(this.f3384p, this.f3385q, dVar);
            }

            @Override // u3.p
            public final Object f(InterfaceC0174y interfaceC0174y, d<? super l3.g> dVar) {
                return ((b) a(dVar)).i(l3.g.f5271a);
            }

            @Override // p3.a
            public final Object i(Object obj) {
                o3.a aVar = o3.a.f5500j;
                int i4 = this.f3382n;
                if (i4 != 0) {
                    if (i4 == 1) {
                        B2.a.n(obj);
                    } else {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                } else {
                    B2.a.n(obj);
                    j jVar = C0045a.this.f3379a;
                    this.f3382n = 1;
                    if (jVar.b(this.f3384p, this.f3385q, this) == aVar) {
                        return aVar;
                    }
                }
                return l3.g.f5271a;
            }
        }

        @e(c = "androidx.privacysandbox.ads.adservices.java.measurement.MeasurementManagerFutures$Api33Ext5JavaImpl$registerTriggerAsync$1", f = "MeasurementManagerFutures.kt", l = {141}, m = "invokeSuspend")
        /* renamed from: f0.a$a$c */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
        public static final class c extends g implements p<InterfaceC0174y, d<? super l3.g>, Object> {

            /* renamed from: n  reason: collision with root package name */
            public int f3386n;

            /* renamed from: p  reason: collision with root package name */
            public final /* synthetic */ Uri f3388p;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public c(Uri uri, d<? super c> dVar) {
                super(dVar);
                this.f3388p = uri;
            }

            @Override // p3.a
            public final d a(d dVar) {
                return new c(this.f3388p, dVar);
            }

            @Override // u3.p
            public final Object f(InterfaceC0174y interfaceC0174y, d<? super l3.g> dVar) {
                return ((c) a(dVar)).i(l3.g.f5271a);
            }

            @Override // p3.a
            public final Object i(Object obj) {
                o3.a aVar = o3.a.f5500j;
                int i4 = this.f3386n;
                if (i4 != 0) {
                    if (i4 == 1) {
                        B2.a.n(obj);
                    } else {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                } else {
                    B2.a.n(obj);
                    j jVar = C0045a.this.f3379a;
                    this.f3386n = 1;
                    if (jVar.c(this.f3388p, this) == aVar) {
                        return aVar;
                    }
                }
                return l3.g.f5271a;
            }
        }

        public C0045a(j.a aVar) {
            this.f3379a = aVar;
        }

        public InterfaceFutureC0346a<l3.g> a(C0430a c0430a) {
            h.e(c0430a, "deletionRequest");
            throw null;
        }

        public InterfaceFutureC0346a<Integer> b() {
            return A0.c.a(C0149c.b(C0175z.a(K.f431a), new C0046a(null)));
        }

        public InterfaceFutureC0346a<l3.g> c(Uri uri, InputEvent inputEvent) {
            h.e(uri, "attributionSource");
            return A0.c.a(C0149c.b(C0175z.a(K.f431a), new b(uri, inputEvent, null)));
        }

        public InterfaceFutureC0346a<l3.g> d(Uri uri) {
            h.e(uri, "trigger");
            return A0.c.a(C0149c.b(C0175z.a(K.f431a), new c(uri, null)));
        }

        public InterfaceFutureC0346a<l3.g> e(k kVar) {
            h.e(kVar, "request");
            throw null;
        }

        public InterfaceFutureC0346a<l3.g> f(l lVar) {
            h.e(lVar, "request");
            throw null;
        }
    }
}
