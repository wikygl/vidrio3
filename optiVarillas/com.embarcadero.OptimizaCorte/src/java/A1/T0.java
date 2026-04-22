package A1;

import W1.C0324l;
import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.Ml;
import com.google.android.gms.internal.ads.O1;
import com.google.android.gms.internal.ads.Te;
import com.google.android.gms.internal.ads.UN;
import com.google.android.gms.internal.ads.Yf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import t1.C0810l;
import y1.InterfaceC0861a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class T0 {

    /* renamed from: h  reason: collision with root package name */
    public static T0 f88h;
    public InterfaceC0100f0 f;

    /* renamed from: a  reason: collision with root package name */
    public final Object f89a = new Object();

    /* renamed from: c  reason: collision with root package name */
    public boolean f91c = false;

    /* renamed from: d  reason: collision with root package name */
    public boolean f92d = false;

    /* renamed from: e  reason: collision with root package name */
    public final Object f93e = new Object();

    /* renamed from: g  reason: collision with root package name */
    public C0810l f94g = new C0810l(new ArrayList());

    /* renamed from: b  reason: collision with root package name */
    public final ArrayList f90b = new ArrayList();

    public static T0 c() {
        T0 t02;
        synchronized (T0.class) {
            try {
                if (f88h == null) {
                    f88h = new T0();
                }
                t02 = f88h;
            } catch (Throwable th) {
                throw th;
            }
        }
        return t02;
    }

    public static O1 d(List list) {
        HashMap hashMap = new HashMap();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            hashMap.put(((Te) it.next()).j, new Ml(24));
        }
        return new O1(22);
    }

    public final void a(Context context) {
        if (this.f == null) {
            this.f = (InterfaceC0100f0) new C0114k(C0124p.f.f162b, context).d(context, false);
        }
    }

    /* JADX WARN: Type inference failed for: r1v5, types: [java.lang.Object, y1.a] */
    public final InterfaceC0861a b() {
        boolean z4;
        O1 d4;
        synchronized (this.f93e) {
            try {
                if (this.f != null) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                C0324l.f("MobileAds.initialize() must be called prior to getting initialization status.", z4);
                try {
                    d4 = d(this.f.h());
                } catch (RemoteException unused) {
                    E1.m.d("Unable to get Initialization status.");
                    return new Object();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return d4;
    }

    public final void e(Context context) {
        try {
            if (Yf.b == null) {
                Yf.b = new Yf();
            }
            if (Yf.b.a.compareAndSet(false, true)) {
                new Thread((Runnable) new UN(context, 6, (Object) null)).start();
            }
            this.f.k();
            this.f.x2(new c2.b(null), null);
        } catch (RemoteException e4) {
            E1.m.h("MobileAdsSettingManager initialization failed", e4);
        }
    }
}
