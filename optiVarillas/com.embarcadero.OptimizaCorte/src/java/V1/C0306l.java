package V1;

import android.os.RemoteException;
import android.view.View;
import com.google.android.gms.internal.ads.Dw;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Pg;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.Xw;
import com.google.android.gms.internal.ads.aI;
import com.google.android.gms.internal.ads.e0;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.gA;
import com.google.android.gms.internal.ads.l0;
import com.google.android.gms.internal.ads.xt;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONException;
import org.json.JSONObject;
import p2.AbstractC0757f;
import p2.C0758g;
import p2.InterfaceC0754c;

/* renamed from: V1.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class C0306l implements InterfaceC0754c, G1.c, TN {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2593j;

    /* renamed from: k  reason: collision with root package name */
    public final Object f2594k;

    /* renamed from: l  reason: collision with root package name */
    public final Object f2595l;

    public /* synthetic */ C0306l(Object obj, int i4, Object obj2) {
        this.f2593j = i4;
        this.f2595l = obj;
        this.f2594k = obj2;
    }

    public l0 b(Object... objArr) {
        Constructor a4;
        synchronized (((AtomicBoolean) this.f2595l)) {
            if (!((AtomicBoolean) this.f2595l).get()) {
                try {
                    a4 = ((e0) this.f2594k).a();
                } catch (ClassNotFoundException unused) {
                    ((AtomicBoolean) this.f2595l).set(true);
                } catch (Exception e4) {
                    throw new RuntimeException("Error instantiating extension", e4);
                }
            }
            a4 = null;
        }
        if (a4 == null) {
            return null;
        }
        try {
            return (l0) a4.newInstance(objArr);
        } catch (Exception e5) {
            throw new IllegalStateException("Unexpected error creating extractor", e5);
        }
    }

    @Override // p2.InterfaceC0754c
    public void c(AbstractC0757f abstractC0757f) {
        ((C0307m) this.f2595l).f2597b.remove((C0758g) this.f2594k);
    }

    @Override // G1.c
    public void e(gA gAVar) {
        try {
            ((Pg) this.f2594k).s(gAVar.a());
        } catch (RemoteException e4) {
            E1.m.e("", e4);
        }
    }

    public void f(String str) {
        try {
            JSONObject put = new JSONObject().put("message", str).put("action", (String) this.f2595l);
            em emVar = (em) this.f2594k;
            if (emVar != null) {
                emVar.V("onError", put);
            }
        } catch (JSONException e4) {
            E1.m.e("Error occurred while dispatching error event.", e4);
        }
    }

    public void g(Object obj) {
        switch (this.f2593j) {
            case 8:
                ((xt) this.f2595l).v((View) this.f2594k, (aI) obj);
                return;
            default:
                ((Dw) obj).v = true;
                ((Xw) this.f2595l).m.b((String) this.f2594k);
                return;
        }
    }

    public void h(int i4, int i5, int i6, int i7, float f, int i8) {
        try {
            ((em) this.f2594k).V("onScreenInfoChanged", new JSONObject().put("width", i4).put("height", i5).put("maxSizeWidth", i6).put("maxSizeHeight", i7).put("density", f).put("rotation", i8));
        } catch (JSONException e4) {
            E1.m.e("Error occurred while obtaining screen information.", e4);
        }
    }

    public void i(int i4, int i5, int i6, int i7) {
        try {
            ((em) this.f2594k).V("onSizeChanged", new JSONObject().put("x", i4).put("y", i5).put("width", i6).put("height", i7));
        } catch (JSONException e4) {
            E1.m.e("Error occurred while dispatching size change.", e4);
        }
    }

    public void j(String str) {
        try {
            ((em) this.f2594k).V("onStateChanged", new JSONObject().put("state", str));
        } catch (JSONException e4) {
            E1.m.e("Error occurred while dispatching state change.", e4);
        }
    }

    public void m(Throwable th) {
        switch (this.f2593j) {
            case 8:
                if (((Boolean) A1.r.f168d.f171c.a(Gb.B4)).booleanValue()) {
                    z1.p.f6575A.f6581g.g("omid native display exp", th);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public /* synthetic */ C0306l(Object obj, Object obj2, int i4, boolean z4) {
        this.f2593j = i4;
        this.f2594k = obj;
        this.f2595l = obj2;
    }

    public C0306l(e0 e0Var) {
        this.f2593j = 2;
        this.f2594k = e0Var;
        this.f2595l = new AtomicBoolean(false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:22:0x004c, code lost:
        ((java.util.ArrayList) r4.f2595l).add(new com.google.android.gms.internal.ads.ad(r3));
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public C0306l(com.google.android.gms.internal.ads.Uc r5) {
        /*
            r4 = this;
            r0 = 5
            r4.f2593j = r0
            java.lang.String r0 = ""
            r4.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r4.f2595l = r1
            r4.f2594k = r5
            r5.h()     // Catch: android.os.RemoteException -> L15
            goto L19
        L15:
            r1 = move-exception
            E1.m.e(r0, r1)
        L19:
            java.util.ArrayList r5 = r5.g()     // Catch: android.os.RemoteException -> L43
            java.util.Iterator r5 = r5.iterator()     // Catch: android.os.RemoteException -> L43
        L21:
            boolean r1 = r5.hasNext()     // Catch: android.os.RemoteException -> L43
            if (r1 == 0) goto L5c
            java.lang.Object r1 = r5.next()     // Catch: android.os.RemoteException -> L43
            boolean r2 = r1 instanceof android.os.IBinder     // Catch: android.os.RemoteException -> L43
            r3 = 0
            if (r2 == 0) goto L4a
            android.os.IBinder r1 = (android.os.IBinder) r1     // Catch: android.os.RemoteException -> L43
            if (r1 != 0) goto L35
            goto L4a
        L35:
            java.lang.String r2 = "com.google.android.gms.ads.internal.formats.client.INativeAdImage"
            android.os.IInterface r2 = r1.queryLocalInterface(r2)     // Catch: android.os.RemoteException -> L43
            boolean r3 = r2 instanceof com.google.android.gms.internal.ads.Zc     // Catch: android.os.RemoteException -> L43
            if (r3 == 0) goto L45
            r3 = r2
            com.google.android.gms.internal.ads.Zc r3 = (com.google.android.gms.internal.ads.Zc) r3     // Catch: android.os.RemoteException -> L43
            goto L4a
        L43:
            r5 = move-exception
            goto L59
        L45:
            com.google.android.gms.internal.ads.Yc r3 = new com.google.android.gms.internal.ads.Yc     // Catch: android.os.RemoteException -> L43
            r3.<init>(r1)     // Catch: android.os.RemoteException -> L43
        L4a:
            if (r3 == 0) goto L21
            java.lang.Object r1 = r4.f2595l     // Catch: android.os.RemoteException -> L43
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch: android.os.RemoteException -> L43
            com.google.android.gms.internal.ads.ad r2 = new com.google.android.gms.internal.ads.ad     // Catch: android.os.RemoteException -> L43
            r2.<init>(r3)     // Catch: android.os.RemoteException -> L43
            r1.add(r2)     // Catch: android.os.RemoteException -> L43
            goto L21
        L59:
            E1.m.e(r0, r5)
        L5c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: V1.C0306l.<init>(com.google.android.gms.internal.ads.Uc):void");
    }

    private final void d(Throwable th) {
    }
}
