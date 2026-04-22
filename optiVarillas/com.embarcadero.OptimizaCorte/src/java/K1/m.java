package K1;

import D1.t0;
import a3.InterfaceFutureC0346a;
import android.os.Binder;
import com.google.android.gms.internal.ads.Ei;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.IN;
import com.google.android.gms.internal.ads.QN;
import com.google.android.gms.internal.ads.VN;
import com.google.android.gms.internal.ads.WN;
import com.google.android.gms.internal.ads.b8;
import com.google.android.gms.internal.ads.bx;
import com.google.android.gms.internal.ads.lx;
import com.google.android.gms.internal.ads.mx;
import com.google.android.gms.internal.ads.wb;
import com.google.android.gms.internal.ads.wk;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class m implements IN {

    /* renamed from: a  reason: collision with root package name */
    public final Executor f1383a;

    /* renamed from: b  reason: collision with root package name */
    public final mx f1384b;

    public m(wk wkVar, mx mxVar) {
        this.f1383a = wkVar;
        this.f1384b = mxVar;
    }

    public final InterfaceFutureC0346a d(Object obj) {
        WN b4;
        Ei ei = (Ei) obj;
        mx mxVar = this.f1384b;
        mxVar.getClass();
        String str = ei.m;
        t0 t0Var = z1.p.f6575A.f6578c;
        if (t0.c(str)) {
            b4 = VN.w(new bx(1));
        } else {
            if (((Boolean) A1.r.f168d.f171c.a(Gb.J6)).booleanValue()) {
                b4 = mxVar.c.y(new b8(mxVar, 1, ei));
            } else {
                b4 = mxVar.d.b(ei);
            }
        }
        int callingUid = Binder.getCallingUid();
        QN r4 = QN.r(b4);
        wb wbVar = Gb.N4;
        return VN.A(VN.v(VN.B(r4, ((Integer) A1.r.f168d.f171c.a(wbVar)).intValue(), TimeUnit.SECONDS, mxVar.a), Throwable.class, new lx(mxVar, ei, callingUid), mxVar.b), new C0218l(0, ei), this.f1383a);
    }
}
