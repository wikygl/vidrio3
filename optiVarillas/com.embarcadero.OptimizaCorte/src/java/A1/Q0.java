package A1;

import W1.C0324l;
import W1.InterfaceC0320h;
import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.internal.ads.Xh;
import h2.C0438a;
import java.util.Set;
import java.util.concurrent.Callable;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import p2.AbstractC0757f;
import p2.C0763l;
import p2.InterfaceC0754c;
import t1.AbstractC0806h;
import t1.C0802d;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class Q0 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f82j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f83k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f84l;

    public /* synthetic */ Q0(Object obj, int i4, Object obj2) {
        this.f82j = i4;
        this.f83k = obj;
        this.f84l = obj2;
    }

    private final void a() {
        synchronized (((C0763l) this.f84l).f5566k) {
            try {
                InterfaceC0754c interfaceC0754c = ((C0763l) this.f84l).f5567l;
                if (interfaceC0754c != null) {
                    interfaceC0754c.c((AbstractC0757f) this.f83k);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.lang.Runnable
    public final void run() {
        boolean z4;
        IInterface iInterface;
        InterfaceC0320h interfaceC0320h;
        Set<Scope> set;
        switch (this.f82j) {
            case 0:
                T0 t02 = (T0) this.f83k;
                Context context = (Context) this.f84l;
                synchronized (t02.f93e) {
                    t02.e(context);
                }
                return;
            case 1:
                o2.l lVar = (o2.l) this.f83k;
                T1.b bVar = lVar.f5498k;
                if (bVar.f2342k == 0) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                V1.G g4 = (V1.G) this.f84l;
                if (z4) {
                    W1.C c4 = lVar.f5499l;
                    C0324l.d(c4);
                    T1.b bVar2 = c4.f2638l;
                    if (bVar2.f2342k == 0) {
                        V1.F f = g4.f2549q;
                        IBinder iBinder = c4.f2637k;
                        if (iBinder == null) {
                            interfaceC0320h = null;
                        } else {
                            int i4 = InterfaceC0320h.a.f2744j;
                            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.common.internal.IAccountAccessor");
                            if (queryLocalInterface instanceof InterfaceC0320h) {
                                iInterface = (InterfaceC0320h) queryLocalInterface;
                            } else {
                                iInterface = new C0438a(iBinder, "com.google.android.gms.common.internal.IAccountAccessor");
                            }
                            interfaceC0320h = iInterface;
                        }
                        V1.x xVar = (V1.x) f;
                        xVar.getClass();
                        if (interfaceC0320h != null && (set = g4.f2546n) != null) {
                            xVar.f2623c = interfaceC0320h;
                            xVar.f2624d = set;
                            if (xVar.f2625e) {
                                xVar.f2621a.d(interfaceC0320h, set);
                            }
                        } else {
                            Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
                            xVar.b(new T1.b(4));
                        }
                    } else {
                        Log.wtf("SignInCoordinator", "Sign-in succeeded with resolve account failure: ".concat(String.valueOf(bVar2)), new Exception());
                        ((V1.x) g4.f2549q).b(bVar2);
                        g4.f2548p.n();
                        return;
                    }
                } else {
                    ((V1.x) g4.f2549q).b(bVar);
                }
                g4.f2548p.n();
                return;
            case 2:
                a();
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                p2.q qVar = (p2.q) this.f83k;
                try {
                    qVar.m(((Callable) this.f84l).call());
                    return;
                } catch (Exception e4) {
                    qVar.l(e4);
                    return;
                } catch (Throwable th) {
                    qVar.l(new RuntimeException(th));
                    return;
                }
            default:
                AbstractC0806h abstractC0806h = (AbstractC0806h) this.f83k;
                try {
                    abstractC0806h.f5800j.b(((C0802d) this.f84l).f5787a);
                    return;
                } catch (IllegalStateException e5) {
                    Xh.b(abstractC0806h.getContext()).a("BaseAdView.loadAd", e5);
                    return;
                }
        }
    }

    public /* synthetic */ Q0(Object obj, Object obj2, int i4, boolean z4) {
        this.f82j = i4;
        this.f84l = obj;
        this.f83k = obj2;
    }
}
