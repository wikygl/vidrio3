package R2;

import A1.D0;
import A1.k1;
import M.InterfaceC0241x;
import M.e0;
import a3.InterfaceFutureC0346a;
import android.os.Handler;
import android.os.RemoteException;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.gms.internal.ads.DG;
import com.google.android.gms.internal.ads.Dr;
import com.google.android.gms.internal.ads.Ei;
import com.google.android.gms.internal.ads.Ek;
import com.google.android.gms.internal.ads.H7;
import com.google.android.gms.internal.ads.HZ;
import com.google.android.gms.internal.ads.Ia;
import com.google.android.gms.internal.ads.Kx;
import com.google.android.gms.internal.ads.LE;
import com.google.android.gms.internal.ads.Lx;
import com.google.android.gms.internal.ads.Mp;
import com.google.android.gms.internal.ads.Mu;
import com.google.android.gms.internal.ads.O20;
import com.google.android.gms.internal.ads.Op;
import com.google.android.gms.internal.ads.Pc;
import com.google.android.gms.internal.ads.Qr;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.UB;
import com.google.android.gms.internal.ads.Ur;
import com.google.android.gms.internal.ads.VN;
import com.google.android.gms.internal.ads.W5;
import com.google.android.gms.internal.ads.WN;
import com.google.android.gms.internal.ads.Xp;
import com.google.android.gms.internal.ads.Yg;
import com.google.android.gms.internal.ads.ZF;
import com.google.android.gms.internal.ads.bx;
import com.google.android.gms.internal.ads.fZ;
import com.google.android.gms.internal.ads.gZ;
import com.google.android.gms.internal.ads.hL;
import com.google.android.gms.internal.ads.i00;
import com.google.android.gms.internal.ads.mG;
import com.google.android.gms.internal.ads.oH;
import com.google.android.gms.internal.ads.rT;
import com.google.android.gms.internal.ads.tH;
import com.google.android.gms.internal.ads.vT;
import com.google.android.gms.internal.ads.vx;
import com.google.android.gms.internal.ads.wH;
import com.google.android.gms.internal.ads.wz;
import com.google.android.gms.internal.ads.xk;
import com.google.android.gms.internal.ads.xo;
import com.google.android.gms.internal.ads.xt;
import com.google.android.gms.internal.ads.zx;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import i2.C0459e;
import i2.C0460f;
import i2.Z;
import java.io.File;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import org.json.JSONObject;
import z1.p;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class d implements InterfaceC0241x, W5, TN, Ur, Pc, Kx, Mp, UB, rT, hL, LE, Z {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2062j;

    /* renamed from: k  reason: collision with root package name */
    public Object f2063k;

    public /* synthetic */ d(int i4) {
        this.f2062j = i4;
    }

    @Override // i2.Z
    public File a() {
        return (File) this.f2063k;
    }

    public void b(Exception exc) {
        ZF.d("MediaCodecAudioRenderer", "Audio sink error", exc);
        HZ hz = ((i00) this.f2063k).G0;
        Handler handler = hz.a;
        if (handler != null) {
            handler.post(new H7(hz, exc));
        }
    }

    public JSONObject c() {
        return null;
    }

    public void d(Object obj) {
        switch (this.f2062j) {
            case 5:
                ((Dr) obj).j0((Ei) this.f2063k);
                return;
            case 6:
                ((Qr) obj).K0((Ia) this.f2063k);
                return;
            case 12:
                oH oHVar = (oH) this.f2063k;
                tH tHVar = (tH) oHVar.j;
                ((wH) obj).a(oHVar.k);
                return;
            default:
                ((gZ) obj).n();
                return;
        }
    }

    public void e() {
        xt xtVar = ((Mu) this.f2063k).m;
        if (xtVar != null) {
            xtVar.e("_videoMediaView");
        }
    }

    @Override // M.InterfaceC0241x
    public e0 f(View view, e0 e0Var) {
        int a4 = e0Var.a();
        BaseTransientBottomBar baseTransientBottomBar = (BaseTransientBottomBar) this.f2063k;
        baseTransientBottomBar.m = a4;
        baseTransientBottomBar.n = e0Var.b();
        baseTransientBottomBar.o = e0Var.c();
        baseTransientBottomBar.f();
        return e0Var;
    }

    public void g(Object obj) {
        switch (this.f2062j) {
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((AtomicInteger) ((Ek) this.f2063k).l).set(1);
                return;
            case 4:
                String str = (String) obj;
                xo xoVar = (xo) this.f2063k;
                DG dg = xoVar.q;
                mG mGVar = xoVar.o;
                ArrayList b4 = xoVar.p.b(xoVar.n, mGVar, false, "", str, mGVar.c);
                int i4 = 1;
                if (true == p.f6575A.f6581g.j(xoVar.j)) {
                    i4 = 2;
                }
                dg.c(i4, b4);
                return;
            default:
                synchronized (((Xp) this.f2063k)) {
                    ((Xp) this.f2063k).l = ((Op) obj).f;
                    ((Op) obj).a();
                }
                return;
        }
    }

    public Object i(String str) {
        String[] strArr = {"GmsCore_OpenSSL", "AndroidOpenSSL", "Conscrypt"};
        ArrayList arrayList = new ArrayList();
        for (int i4 = 0; i4 < 3; i4++) {
            Provider provider = Security.getProvider(strArr[i4]);
            if (provider != null) {
                arrayList.add(provider);
            }
        }
        Iterator it = arrayList.iterator();
        Exception exc = null;
        while (it.hasNext()) {
            try {
                return ((vT) this.f2063k).b(str, (Provider) it.next());
            } catch (Exception e4) {
                if (exc == null) {
                    exc = e4;
                }
            }
        }
        throw new GeneralSecurityException("No good Provider found.", exc);
    }

    public InterfaceFutureC0346a k(Ei ei) {
        WN wn;
        zx zxVar = ((Lx) this.f2063k).b;
        String str = ei.q;
        synchronized (((vx) zxVar).b) {
            try {
                int i4 = zxVar.h;
                if (i4 != 1 && i4 != 3) {
                    wn = VN.w(new bx(2));
                } else if (((vx) zxVar).c) {
                    wn = ((vx) zxVar).a;
                } else {
                    zxVar.h = 3;
                    ((vx) zxVar).c = true;
                    zxVar.g = str;
                    ((vx) zxVar).f.q();
                    ((vx) zxVar).a.a(new k1(6, zxVar), xk.f);
                    wn = ((vx) zxVar).a;
                }
            } finally {
            }
        }
        return wn;
    }

    public void m(Throwable th) {
        switch (this.f2062j) {
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((AtomicInteger) ((Ek) this.f2063k).l).set(-1);
                return;
            default:
                return;
        }
    }

    public /* synthetic */ d(int i4, Object obj) {
        this.f2062j = i4;
        this.f2063k = obj;
    }

    @Override // i2.Z
    public Object a() {
        switch (this.f2062j) {
            case 14:
                return (O20) this.f2063k;
            default:
                return new C0460f(((C0459e) this.f2063k).f3722b);
        }
    }

    public /* synthetic */ d(fZ fZVar, Object obj, long j4) {
        this.f2062j = 15;
        this.f2063k = obj;
    }

    @Override // i2.Z
    public JSONObject a() {
        return null;
    }

    @Override // i2.Z
    public D0 a() {
        try {
            return ((Yg) ((wz) this.f2063k).b).b();
        } catch (RemoteException e4) {
            throw new Exception(e4);
        }
    }

    /* renamed from: a  reason: collision with other method in class */
    public void m2a() {
        synchronized (((Xp) this.f2063k)) {
        }
    }

    private final void j(Throwable th) {
    }

    public void h(MotionEvent motionEvent) {
    }
}
