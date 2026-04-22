package D1;

import A1.N0;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.security.NetworkSecurityPolicy;
import android.util.Log;
import com.google.android.gms.internal.ads.DN;
import com.google.android.gms.internal.ads.OH;
import com.google.android.gms.internal.ads.W20;
import com.google.android.gms.internal.ads.WL;
import com.google.android.gms.internal.ads.Wv;
import com.google.android.gms.internal.ads.X20;
import com.google.android.gms.internal.ads.XB;
import com.google.android.gms.internal.ads.YH;
import com.google.android.gms.internal.ads.kk;
import com.google.android.gms.internal.ads.qf;
import com.google.android.gms.internal.ads.zk;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Future;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;
import org.json.JSONException;
import org.json.JSONObject;
import p2.C0758g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class g0 implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f684j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f685k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ Object f686l;

    public /* synthetic */ g0(Object obj, int i4, Object obj2) {
        this.f684j = i4;
        this.f685k = obj;
        this.f686l = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        NetworkSecurityPolicy networkSecurityPolicy;
        String str;
        String str2;
        switch (this.f684j) {
            case 0:
                j0 j0Var = (j0) this.f685k;
                j0Var.getClass();
                SharedPreferences sharedPreferences = ((Context) this.f686l).getSharedPreferences("admob", 0);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                try {
                    synchronized (j0Var.f701a) {
                        j0Var.f = sharedPreferences;
                        j0Var.f706g = edit;
                        if (Build.VERSION.SDK_INT >= 23) {
                            networkSecurityPolicy = NetworkSecurityPolicy.getInstance();
                            networkSecurityPolicy.isCleartextTrafficPermitted();
                        }
                        j0Var.f707h = j0Var.f.getBoolean("use_https", j0Var.f707h);
                        j0Var.f722w = j0Var.f.getBoolean("content_url_opted_out", j0Var.f722w);
                        j0Var.f708i = j0Var.f.getString("content_url_hashes", j0Var.f708i);
                        j0Var.f710k = j0Var.f.getBoolean("gad_idless", j0Var.f710k);
                        j0Var.f723x = j0Var.f.getBoolean("content_vertical_opted_out", j0Var.f723x);
                        j0Var.f709j = j0Var.f.getString("content_vertical_hashes", j0Var.f709j);
                        j0Var.f719t = j0Var.f.getInt("version_code", j0Var.f719t);
                        j0Var.f715p = new kk(j0Var.f.getString("app_settings_json", j0Var.f715p.e), j0Var.f.getLong("app_settings_last_update_ms", j0Var.f715p.f));
                        j0Var.f716q = j0Var.f.getLong("app_last_background_time_ms", j0Var.f716q);
                        j0Var.f718s = j0Var.f.getInt("request_in_session_count", j0Var.f718s);
                        j0Var.f717r = j0Var.f.getLong("first_ad_req_time_ms", j0Var.f717r);
                        j0Var.f720u = j0Var.f.getStringSet("never_pool_slots", j0Var.f720u);
                        j0Var.f724y = j0Var.f.getString("display_cutout", j0Var.f724y);
                        j0Var.f698D = j0Var.f.getInt("app_measurement_npa", j0Var.f698D);
                        j0Var.f699E = j0Var.f.getInt("sd_app_measure_npa", j0Var.f699E);
                        j0Var.f700F = j0Var.f.getLong("sd_app_measure_npa_ts", j0Var.f700F);
                        j0Var.f725z = j0Var.f.getString("inspector_info", j0Var.f725z);
                        j0Var.f695A = j0Var.f.getBoolean("linked_device", j0Var.f695A);
                        j0Var.f696B = j0Var.f.getString("linked_ad_unit", j0Var.f696B);
                        j0Var.f697C = j0Var.f.getString("inspector_ui_storage", j0Var.f697C);
                        j0Var.f711l = j0Var.f.getString("IABTCF_gdprApplies", j0Var.f711l);
                        j0Var.f713n = j0Var.f.getString("IABTCF_PurposeConsents", j0Var.f713n);
                        j0Var.f712m = j0Var.f.getString("IABTCF_TCString", j0Var.f712m);
                        j0Var.f714o = j0Var.f.getInt("gad_has_consent_for_cookies", j0Var.f714o);
                        try {
                            j0Var.f721v = new JSONObject(j0Var.f.getString("native_advanced_settings", "{}"));
                        } catch (JSONException e4) {
                            E1.m.h("Could not convert native advanced settings to json object", e4);
                        }
                        j0Var.q();
                    }
                    return;
                } catch (Throwable th) {
                    z1.p.f6575A.f6581g.h("AdSharedPreferenceManagerImpl.initializeOnBackgroundThread", th);
                    C0183d0.l("AdSharedPreferenceManagerImpl.initializeOnBackgroundThread, errorMessage = ", th);
                    return;
                }
            case 1:
                Q0.a aVar = (Q0.a) this.f685k;
                aVar.getClass();
                com.android.billingclient.api.a aVar2 = com.android.billingclient.api.b.k;
                aVar.M(Q0.m.a(24, 7, aVar2));
                ((Q0.e) this.f686l).b(aVar2, new ArrayList());
                return;
            case 2:
                if (((zk) this.f685k).isCancelled()) {
                    ((Future) this.f686l).cancel(true);
                    return;
                }
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ((qf) this.f685k).j.i((String) this.f686l);
                return;
            case 4:
                ((XB) this.f685k).n.d.c.i0((N0) this.f686l);
                return;
            case 5:
                X20.b(((W20) this.f686l).a, ((OH) this.f685k).a());
                return;
            case 6:
                ((YH) this.f685k).c.i((String) this.f686l);
                return;
            case 7:
                ((DN) this.f685k).r((WL) this.f686l);
                return;
            case 8:
                f2.g gVar = (f2.g) this.f685k;
                Context context = gVar.f3402a;
                String string = f2.g.b(context).getString("app_set_id", null);
                long j4 = -1;
                long j5 = f2.g.b(gVar.f3402a).getLong("app_set_id_last_used_time", -1L);
                if (j5 != -1) {
                    j4 = 33696000000L + j5;
                }
                C0758g c0758g = (C0758g) this.f686l;
                if (string != null && System.currentTimeMillis() <= j4) {
                    try {
                        f2.g.c(context);
                    } catch (f2.f e5) {
                        c0758g.f5552a.l(e5);
                        return;
                    }
                } else {
                    string = UUID.randomUUID().toString();
                    try {
                        if (!context.getSharedPreferences("app_set_id_storage", 0).edit().putString("app_set_id", string).commit()) {
                            String valueOf = String.valueOf(context.getPackageName());
                            if (valueOf.length() != 0) {
                                str2 = "Failed to store app set ID generated for App ".concat(valueOf);
                            } else {
                                str2 = new String("Failed to store app set ID generated for App ");
                            }
                            Log.e("AppSet", str2);
                            throw new Exception("Failed to store the app set ID.");
                        }
                        f2.g.c(context);
                        SharedPreferences sharedPreferences2 = context.getSharedPreferences("app_set_id_storage", 0);
                        if (!sharedPreferences2.edit().putLong("app_set_id_creation_time", System.currentTimeMillis()).commit()) {
                            String valueOf2 = String.valueOf(context.getPackageName());
                            if (valueOf2.length() != 0) {
                                str = "Failed to store app set ID creation time for App ".concat(valueOf2);
                            } else {
                                str = new String("Failed to store app set ID creation time for App ");
                            }
                            Log.e("AppSet", str);
                            throw new Exception("Failed to store the app set ID creation time.");
                        }
                    } catch (f2.f e6) {
                        c0758g.f5552a.l(e6);
                        return;
                    }
                }
                c0758g.f5552a.m(new Q1.b(string, 1));
                return;
            default:
                z1.p.f6575A.f6584j.getClass();
                z1.c.b((Wv) this.f685k, "cld_r", SystemClock.elapsedRealtime() - ((Long) this.f686l).longValue());
                return;
        }
    }
}
