package A1;

import D1.C0195o;
import D1.C0198s;
import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.af;
import com.google.android.gms.internal.ads.zb;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: A1.g1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class RunnableC0104g1 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f124j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f125k;

    public /* synthetic */ RunnableC0104g1(int i4, Object obj) {
        this.f124j = i4;
        this.f125k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z4;
        String str;
        switch (this.f124j) {
            case 0:
                af afVar = ((C0107h1) this.f125k).f130j;
                if (afVar != null) {
                    try {
                        afVar.t2(Collections.emptyList());
                        return;
                    } catch (RemoteException e4) {
                        E1.m.h("Could not notify onComplete event.", e4);
                        return;
                    }
                }
                return;
            case 1:
                C0195o c0195o = (C0195o) this.f125k;
                c0195o.getClass();
                z1.p pVar = z1.p.f6575A;
                C0198s c0198s = pVar.f6587m;
                Context context = c0195o.f745a;
                String str2 = c0195o.f748d;
                String str3 = c0195o.f749e;
                c0198s.getClass();
                zb zbVar = Gb.e4;
                r rVar = r.f168d;
                String j4 = C0198s.j(context, c0198s.k(context, (String) rVar.f171c.a(zbVar), str2, str3).toString(), str3);
                if (TextUtils.isEmpty(j4)) {
                    E1.m.b("Not linked for in app preview.");
                } else {
                    try {
                        JSONObject jSONObject = new JSONObject(j4.trim());
                        String optString = jSONObject.optString("gct");
                        c0198s.f = jSONObject.optString("status");
                        if (((Boolean) rVar.f171c.a(Gb.j8)).booleanValue()) {
                            if ("0".equals(c0198s.f) || "2".equals(c0198s.f)) {
                                z4 = true;
                            } else {
                                z4 = false;
                            }
                            c0198s.d(z4);
                            D1.j0 c4 = pVar.f6581g.c();
                            if (!z4) {
                                str = "";
                            } else {
                                str = str2;
                            }
                            c4.B(str);
                        }
                        synchronized (c0198s.f767a) {
                            c0198s.f769c = optString;
                        }
                        if ("2".equals(c0198s.f)) {
                            E1.m.b("Creative is not pushed for this device.");
                            C0198s.e(context, "There was no creative pushed from DFP to the device.", false, false);
                            return;
                        } else if ("1".equals(c0198s.f)) {
                            E1.m.b("The app is not linked for creative preview.");
                            c0198s.b(context, str2, str3);
                            return;
                        } else if ("0".equals(c0198s.f)) {
                            E1.m.b("Device is linked for in app preview.");
                            C0198s.e(context, "The device is successfully linked for creative preview.", false, true);
                            return;
                        } else {
                            return;
                        }
                    } catch (JSONException e5) {
                        E1.m.h("Fail to get in app preview response json.", e5);
                    }
                }
                C0198s.e(context, "In-app preview failed to load because of a system error. Please try again later.", true, true);
                return;
            default:
                ((BaseTransientBottomBar) this.f125k).c();
                return;
        }
    }
}
