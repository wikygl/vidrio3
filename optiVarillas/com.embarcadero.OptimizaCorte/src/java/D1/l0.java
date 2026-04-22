package D1;

import A1.k1;
import a3.InterfaceFutureC0346a;
import android.content.Context;
import com.google.android.gms.internal.ads.Bk;
import com.google.android.gms.internal.ads.DG;
import com.google.android.gms.internal.ads.Dk;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.HN;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.Tx;
import com.google.android.gms.internal.ads.Ur;
import com.google.android.gms.internal.ads.VN;
import com.google.android.gms.internal.ads.b4;
import com.google.android.gms.internal.ads.eH;
import com.google.android.gms.internal.ads.ey;
import com.google.android.gms.internal.ads.fH;
import com.google.android.gms.internal.ads.gi;
import com.google.android.gms.internal.ads.hy;
import com.google.android.gms.internal.ads.w3;
import com.google.android.gms.internal.ads.xI;
import com.google.android.gms.internal.ads.xk;
import com.google.android.gms.internal.ads.xo;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import u1.InterfaceC0823c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class l0 implements E1.e, w3, TN, Ur, HN {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f735j;

    /* renamed from: k  reason: collision with root package name */
    public Object f736k;

    /* renamed from: l  reason: collision with root package name */
    public Object f737l;

    public l0(Context context) {
        this.f735j = 9;
        this.f736k = context;
    }

    public InterfaceFutureC0346a a() {
        InterfaceFutureC0346a B4;
        Tx tx = (Tx) this.f736k;
        gi giVar = (gi) this.f737l;
        tx.getClass();
        long longValue = ((Long) A1.r.f168d.f171c.a(Gb.oa)).longValue();
        ey eyVar = tx.c;
        synchronized (eyVar) {
            if (((hy) eyVar).b) {
                B4 = VN.B(((hy) eyVar).a, longValue, TimeUnit.MILLISECONDS, ((hy) eyVar).g);
            } else {
                ((hy) eyVar).b = true;
                eyVar.h = giVar;
                eyVar.a();
                B4 = VN.B(((hy) eyVar).a, longValue, TimeUnit.MILLISECONDS, ((hy) eyVar).g);
                B4.a(new k1(7, eyVar), xk.f);
            }
        }
        return B4;
    }

    /* JADX WARN: Code restructure failed: missing block: B:202:0x038a, code lost:
        r0.addAll(r12);
     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x0120, code lost:
        if (")".equals(com.google.android.gms.internal.ads.b4.a(r7, r9)) == false) goto L52;
     */
    /* JADX WARN: Removed duplicated region for block: B:187:0x0349  */
    /* JADX WARN: Removed duplicated region for block: B:195:0x035e  */
    /* JADX WARN: Removed duplicated region for block: B:220:0x03d8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void b(byte[] r20, int r21, int r22, com.google.android.gms.internal.ads.y3 r23) {
        /*
            Method dump skipped, instructions count: 1036
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: D1.l0.b(byte[], int, int, com.google.android.gms.internal.ads.y3):void");
    }

    public synchronized Map c() {
        try {
            if (((Map) this.f737l) == null) {
                this.f737l = Collections.unmodifiableMap(new HashMap((HashMap) this.f736k));
            }
        } catch (Throwable th) {
            throw th;
        }
        return (Map) this.f737l;
    }

    public void d(Object obj) {
        ((InterfaceC0823c) obj).i((String) this.f737l, (String) this.f736k);
    }

    public void g(Object obj) {
        switch (this.f735j) {
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((Dk) this.f736k).d(obj);
                return;
            case 4:
                String str = (String) obj;
                xo xoVar = (xo) this.f736k;
                DG dg = xoVar.q;
                List a4 = xoVar.a();
                dg.a(xoVar.p.b(xoVar.n, xoVar.o, false, (String) this.f737l, str, a4));
                return;
            default:
                Void r9 = (Void) obj;
                synchronized (((fH) this.f737l)) {
                    fH fHVar = (fH) this.f737l;
                    fHVar.d = null;
                    fHVar.c.addFirst((eH) this.f736k);
                    fH fHVar2 = (fH) this.f737l;
                    if (fHVar2.e == 1) {
                        fHVar2.b();
                    }
                }
                return;
        }
    }

    @Override // E1.e
    public boolean i(String str) {
        C0185e0 c0185e0 = t0.f774l;
        t0 t0Var = z1.p.f6575A.f6578c;
        t0.j((Context) this.f736k, (String) this.f737l, str);
        return true;
    }

    public void m(Throwable th) {
        switch (this.f735j) {
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((Bk) this.f737l).a();
                return;
            case 4:
                xo xoVar = (xo) this.f736k;
                DG dg = xoVar.q;
                List a4 = xoVar.a();
                dg.a(xoVar.p.b(xoVar.n, xoVar.o, false, (String) this.f737l, (String) null, a4));
                return;
            default:
                synchronized (((fH) this.f737l)) {
                    ((fH) this.f737l).d = null;
                }
                return;
        }
    }

    public /* synthetic */ l0(Object obj, int i4, Object obj2) {
        this.f735j = i4;
        this.f736k = obj;
        this.f737l = obj2;
    }

    public /* synthetic */ l0(Object obj, Object obj2, int i4, boolean z4) {
        this.f735j = i4;
        this.f737l = obj;
        this.f736k = obj2;
    }

    public l0(int i4) {
        this.f735j = i4;
        switch (i4) {
            case 2:
                this.f736k = new xI();
                this.f737l = new b4();
                return;
            case 8:
                this.f736k = new HashMap();
                return;
            default:
                return;
        }
    }
}
