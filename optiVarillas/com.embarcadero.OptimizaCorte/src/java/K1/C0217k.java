package K1;

import D1.t0;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.Aj;
import com.google.android.gms.internal.ads.Bq;
import com.google.android.gms.internal.ads.Cs;
import com.google.android.gms.internal.ads.Eb;
import com.google.android.gms.internal.ads.Eq;
import com.google.android.gms.internal.ads.Fq;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Qm;
import com.google.android.gms.internal.ads.Rz;
import com.google.android.gms.internal.ads.Sz;
import com.google.android.gms.internal.ads.UN;
import com.google.android.gms.internal.ads.Yp;
import com.google.android.gms.internal.ads.Zp;
import com.google.android.gms.internal.ads.bn;
import com.google.android.gms.internal.ads.bw;
import com.google.android.gms.internal.ads.cO;
import com.google.android.gms.internal.ads.gr;
import com.google.android.gms.internal.ads.iB;
import com.google.android.gms.internal.ads.mB;
import com.google.android.gms.internal.ads.mG;
import com.google.android.gms.internal.ads.mQ;
import com.google.android.gms.internal.ads.oH;
import com.google.android.gms.internal.ads.pk;
import com.google.android.gms.internal.ads.qX;
import com.google.android.gms.internal.ads.rG;
import com.google.android.gms.internal.ads.rp;
import com.google.android.gms.internal.ads.tH;
import com.google.android.gms.internal.ads.vH;
import com.google.android.gms.internal.ads.vX;
import com.google.android.gms.internal.ads.vb;
import com.google.android.gms.internal.ads.wk;
import com.google.android.gms.internal.ads.xk;
import com.google.android.gms.internal.ads.zX;
import com.google.android.gms.internal.ads.zb;
import com.google.android.gms.internal.ads.zj;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: K1.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0217k implements qX {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f1376a;

    /* renamed from: b  reason: collision with root package name */
    public final zX f1377b;

    /* renamed from: c  reason: collision with root package name */
    public final zX f1378c;

    /* renamed from: d  reason: collision with root package name */
    public final zX f1379d;

    /* renamed from: e  reason: collision with root package name */
    public final zX f1380e;

    public /* synthetic */ C0217k(zX zXVar, zX zXVar2, zX zXVar3, vX vXVar, int i4) {
        this.f1376a = i4;
        this.f1377b = zXVar;
        this.f1378c = zXVar2;
        this.f1379d = zXVar3;
        this.f1380e = vXVar;
    }

    public final Object c() {
        Object obj;
        Qm qm = this.f1380e;
        L1.d dVar = this.f1379d;
        L1.b bVar = this.f1378c;
        zX zXVar = this.f1377b;
        switch (this.f1376a) {
            case 0:
                n nVar = (n) bVar;
                nVar.getClass();
                wk wkVar = xk.a;
                mQ.e(wkVar);
                m mVar = new m(wkVar, nVar.f1386b.a());
                Eq a4 = ((Fq) dVar).a();
                oH a5 = ((vH) zXVar.c()).b(a4.a(), tH.E).c(mVar).d(((Integer) A1.r.f168d.f171c.a(Gb.N4)).intValue(), TimeUnit.SECONDS).a();
                a5.a(new UN(a5, 0, new C0210d((Cs) qm.c())), wkVar);
                return a5;
            case 1:
                Sz sz = (Sz) dVar;
                return new mB((vH) zXVar.c(), (cO) bVar.c(), ((iB) qm).a(), new Rz((Context) sz.a.c(), (rp) sz.b.c()));
            case 2:
                wk wkVar2 = xk.a;
                mQ.e(wkVar2);
                return new Bq((gr) zXVar.c(), ((Yp) bVar).a(), (ScheduledExecutorService) dVar.c(), wkVar2, (String) ((Zp) qm).a.j);
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                Context context = (Context) zXVar.c();
                E1.a a6 = ((bn) bVar).a();
                mG a7 = ((Yp) dVar).a();
                String str = null;
                Aj aj = a7.A;
                if (aj == null) {
                    return null;
                }
                rG rGVar = a7.s;
                if (rGVar != null) {
                    str = rGVar.b;
                }
                return new zj(context, a6, aj, str);
            default:
                wk wkVar3 = xk.a;
                mQ.e(wkVar3);
                L1.b bVar2 = bVar;
                L1.a aVar = new L1.a((Context) bVar2.f1484a.c(), (E1.a) bVar2.f1485b.c());
                dVar.getClass();
                bw bwVar = new bw(wkVar3, (E1.q) zXVar.c(), new L1.c(), qm.a());
                HashMap hashMap = bwVar.a;
                hashMap.put("s", "gmob_sdk");
                hashMap.put("v", "3");
                hashMap.put("os", Build.VERSION.RELEASE);
                hashMap.put("api_v", Build.VERSION.SDK);
                z1.p pVar = z1.p.f6575A;
                t0 t0Var = pVar.f6578c;
                hashMap.put("device", t0.G());
                hashMap.put("app", aVar.f1482b);
                Context context2 = aVar.f1481a;
                String str2 = "1";
                if (true == t0.d(context2)) {
                    obj = "1";
                } else {
                    obj = "0";
                }
                hashMap.put("is_lite_sdk", obj);
                zb zbVar = Gb.a;
                A1.r rVar = A1.r.f168d;
                ArrayList b4 = rVar.f169a.b();
                vb vbVar = Gb.h6;
                Eb eb = rVar.f171c;
                boolean booleanValue = ((Boolean) eb.a(vbVar)).booleanValue();
                pk pkVar = pVar.f6581g;
                if (booleanValue) {
                    b4.addAll(pkVar.c().h().i);
                }
                hashMap.put("e", TextUtils.join(",", b4));
                hashMap.put("sdkVersion", aVar.f1483c);
                if (((Boolean) eb.a(Gb.ca)).booleanValue()) {
                    if (true != t0.b(context2)) {
                        str2 = "0";
                    }
                    hashMap.put("is_bstar", str2);
                }
                if (((Boolean) eb.a(Gb.u8)).booleanValue() && ((Boolean) eb.a(Gb.U1)).booleanValue()) {
                    String str3 = pkVar.g;
                    if (str3 == null) {
                        str3 = "";
                    }
                    hashMap.put("plugin", str3);
                }
                return bwVar;
        }
    }
}
