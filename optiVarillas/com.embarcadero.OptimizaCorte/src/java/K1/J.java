package K1;

import a3.InterfaceFutureC0346a;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.FH;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.TN;
import com.google.android.gms.internal.ads.Wj;
import com.google.android.gms.internal.ads.dk;
import com.google.android.gms.internal.ads.kc;
import com.google.android.gms.internal.ads.zH;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class J implements TN {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ InterfaceFutureC0346a f1303j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ dk f1304k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Wj f1305l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ zH f1306m;

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ C0208b f1307n;

    public J(C0208b c0208b, InterfaceFutureC0346a interfaceFutureC0346a, dk dkVar, Wj wj, zH zHVar) {
        this.f1303j = interfaceFutureC0346a;
        this.f1304k = dkVar;
        this.f1305l = wj;
        this.f1306m = zHVar;
        this.f1307n = c0208b;
    }

    public final void g(Object obj) {
        o oVar = (o) obj;
        FH L4 = C0208b.L4(this.f1303j, this.f1304k);
        C0208b c0208b = this.f1307n;
        AtomicBoolean atomicBoolean = c0208b.f1338M;
        String str = c0208b.f1327B;
        String str2 = c0208b.f1328C;
        atomicBoolean.set(true);
        boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.M6)).booleanValue();
        Wj wj = this.f1305l;
        zH zHVar = this.f1306m;
        if (!booleanValue) {
            try {
                wj.F("QueryInfo generation has been disabled.");
            } catch (RemoteException e4) {
                E1.m.d("QueryInfo generation has been disabled.".concat(e4.toString()));
            }
            if (((Boolean) kc.e.e()).booleanValue() && L4 != null) {
                zHVar.K("QueryInfo generation has been disabled.");
                zHVar.e(false);
                L4.a(zHVar);
                L4.h();
                return;
            }
            return;
        }
        try {
            try {
                if (oVar == null) {
                    wj.o2((String) null, (String) null, (Bundle) null);
                    zHVar.e(true);
                    if (((Boolean) kc.e.e()).booleanValue() && L4 != null) {
                        L4.a(zHVar);
                        L4.h();
                        return;
                    }
                    return;
                }
                try {
                    if (TextUtils.isEmpty(new JSONObject(oVar.f1388b).optString("request_id", ""))) {
                        E1.m.g("The request ID is empty in request JSON.");
                        wj.F("Internal error: request ID is empty in request JSON.");
                        zHVar.K("Request ID empty");
                        zHVar.e(false);
                        if (((Boolean) kc.e.e()).booleanValue() && L4 != null) {
                            L4.a(zHVar);
                            L4.h();
                            return;
                        }
                        return;
                    }
                    Bundle bundle = oVar.f1390d;
                    if (c0208b.f1326A && bundle != null && bundle.getInt(str2, -1) == -1) {
                        bundle.putInt(str2, c0208b.f1329D.get());
                    }
                    if (c0208b.f1356z && bundle != null && TextUtils.isEmpty(bundle.getString(str))) {
                        if (TextUtils.isEmpty(c0208b.f1331F)) {
                            c0208b.f1331F = z1.p.f6575A.f6578c.w(c0208b.f1342l, c0208b.f1330E.f844j);
                        }
                        bundle.putString(str, c0208b.f1331F);
                    }
                    wj.o2(oVar.f1387a, oVar.f1388b, bundle);
                    zHVar.e(true);
                    if (((Boolean) kc.e.e()).booleanValue() && L4 != null) {
                        L4.a(zHVar);
                        L4.h();
                    }
                } catch (JSONException e5) {
                    E1.m.g("Failed to create JSON object from the request string.");
                    String obj2 = e5.toString();
                    wj.F("Internal error for request JSON: " + obj2);
                    zHVar.d(e5);
                    zHVar.e(false);
                    z1.p.f6575A.f6581g.h("SignalGeneratorImpl.generateSignals.onSuccess", e5);
                    if (((Boolean) kc.e.e()).booleanValue() && L4 != null) {
                        L4.a(zHVar);
                        L4.h();
                    }
                }
            } catch (Throwable th) {
                if (((Boolean) kc.e.e()).booleanValue() && L4 != null) {
                    L4.a(zHVar);
                    L4.h();
                }
                throw th;
            }
        } catch (RemoteException e6) {
            zHVar.d(e6);
            zHVar.e(false);
            E1.m.e("", e6);
            z1.p.f6575A.f6581g.h("SignalGeneratorImpl.generateSignals.onSuccess", e6);
            if (((Boolean) kc.e.e()).booleanValue() && L4 != null) {
                L4.a(zHVar);
                L4.h();
            }
        }
    }

    public final void m(Throwable th) {
        String message = th.getMessage();
        if (((Boolean) A1.r.f168d.f171c.a(Gb.R6)).booleanValue()) {
            z1.p.f6575A.f6581g.g("SignalGeneratorImpl.generateSignals", th);
        } else {
            z1.p.f6575A.f6581g.h("SignalGeneratorImpl.generateSignals", th);
        }
        FH L4 = C0208b.L4(this.f1303j, this.f1304k);
        if (((Boolean) kc.e.e()).booleanValue() && L4 != null) {
            zH zHVar = this.f1306m;
            zHVar.d(th);
            zHVar.e(false);
            L4.a(zHVar);
            L4.h();
        }
        try {
            if (!"Unknown format is no longer supported.".equals(message)) {
                message = "Internal error. " + message;
            }
            this.f1305l.F(message);
        } catch (RemoteException e4) {
            E1.m.e("", e4);
        }
    }
}
