package i1;

import A1.C0120n;
import D1.C0205z;
import D1.o0;
import K1.y;
import android.content.Context;
import androidx.fragment.app.I;
import com.google.android.gms.internal.ads.Ip;
import com.google.android.gms.internal.ads.yn;
import com.google.android.gms.internal.ads.zJ;
import i1.o;
import i2.Y;
import java.util.concurrent.Executor;
import k1.C0686a;
import r1.c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class k extends w {

    /* renamed from: j  reason: collision with root package name */
    public k3.a<Executor> f3643j;

    /* renamed from: k  reason: collision with root package name */
    public Y f3644k;

    /* renamed from: l  reason: collision with root package name */
    public k3.a f3645l;

    /* renamed from: m  reason: collision with root package name */
    public C0205z f3646m;

    /* renamed from: n  reason: collision with root package name */
    public k3.a<p1.q> f3647n;

    /* renamed from: o  reason: collision with root package name */
    public k3.a<v> f3648o;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public Context f3649a;

        /* JADX WARN: Type inference failed for: r12v0, types: [com.google.android.gms.internal.ads.yn, java.lang.Object] */
        /* JADX WARN: Type inference failed for: r1v2, types: [i1.k, java.lang.Object] */
        public final k a() {
            Context context = this.f3649a;
            if (context != null) {
                ?? obj = new Object();
                obj.f3643j = C0686a.a(o.a.f3652a);
                Y y4 = new Y(context);
                obj.f3644k = y4;
                obj.f3645l = C0686a.a(new Ip(y4, new o0(y4)));
                Y y5 = obj.f3644k;
                obj.f3646m = new C0205z(y5);
                k3.a<p1.q> a4 = C0686a.a(new p1.r(obj.f3646m, C0686a.a(new J1.f(y5))));
                obj.f3647n = a4;
                y yVar = new y();
                Y y6 = obj.f3644k;
                r1.c cVar = c.a.f5708a;
                ?? obj2 = new Object();
                ((yn) obj2).j = y6;
                ((yn) obj2).k = a4;
                ((yn) obj2).l = yVar;
                ((yn) obj2).m = cVar;
                k3.a<Executor> aVar = obj.f3643j;
                k3.a aVar2 = obj.f3645l;
                obj.f3648o = C0686a.a(new zJ(new C0120n(aVar, aVar2, obj2, a4, a4), new o1.q(y6, aVar2, a4, obj2, aVar, a4, a4), new I(aVar, a4, (Object) obj2, a4)));
                return obj;
            }
            throw new IllegalStateException(Context.class.getCanonicalName() + " must be set");
        }
    }
}
