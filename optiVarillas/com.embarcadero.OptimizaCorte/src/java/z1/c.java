package z1;

import A1.W0;
import A1.r;
import D1.C0183d0;
import D1.g0;
import a2.C0345c;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.AN;
import com.google.android.gms.internal.ads.GH;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.IH;
import com.google.android.gms.internal.ads.Jf;
import com.google.android.gms.internal.ads.Jk;
import com.google.android.gms.internal.ads.Jx;
import com.google.android.gms.internal.ads.Kf;
import com.google.android.gms.internal.ads.Nf;
import com.google.android.gms.internal.ads.RJ;
import com.google.android.gms.internal.ads.UN;
import com.google.android.gms.internal.ads.VN;
import com.google.android.gms.internal.ads.Vv;
import com.google.android.gms.internal.ads.Wv;
import com.google.android.gms.internal.ads.fn;
import com.google.android.gms.internal.ads.kk;
import com.google.android.gms.internal.ads.wk;
import com.google.android.gms.internal.ads.xk;
import com.google.android.gms.internal.ads.zH;
import com.google.android.gms.internal.ads.zb;
import com.google.android.gms.internal.ads.zk;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c {

    /* renamed from: a  reason: collision with root package name */
    public Context f6529a;

    /* renamed from: b  reason: collision with root package name */
    public long f6530b;

    public static final void b(Wv wv, String str, long j4) {
        if (wv != null) {
            if (((Boolean) r.f168d.f171c.a(Gb.nb)).booleanValue()) {
                Vv a4 = wv.a();
                a4.a("action", "lat_init");
                a4.a(str, Long.toString(j4));
                a4.b();
            }
        }
    }

    public final void a(Context context, E1.a aVar, boolean z4, kk kkVar, String str, String str2, Jk jk, GH gh, Wv wv, Long l2) {
        PackageInfo b4;
        p pVar = p.f6575A;
        pVar.f6584j.getClass();
        if (SystemClock.elapsedRealtime() - this.f6530b < 5000) {
            E1.m.g("Not retrying to fetch app settings");
            return;
        }
        C0345c c0345c = pVar.f6584j;
        c0345c.getClass();
        this.f6530b = SystemClock.elapsedRealtime();
        if (kkVar != null && !TextUtils.isEmpty(kkVar.e)) {
            long j4 = kkVar.f;
            c0345c.getClass();
            if (System.currentTimeMillis() - j4 <= ((Long) r.f168d.f171c.a(Gb.G3)).longValue() && kkVar.h) {
                return;
            }
        }
        if (context == null) {
            E1.m.g("Context not provided to fetch application settings");
        } else if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
            E1.m.g("App settings could not be fetched. Required parameters missing");
        } else {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext == null) {
                applicationContext = context;
            }
            this.f6529a = applicationContext;
            zH n4 = fn.n(context, IH.k);
            n4.f();
            Kf a4 = pVar.f6590p.a(aVar, this.f6529a, gh);
            RJ rj = Jf.b;
            Nf a5 = a4.a("google.afma.config.fetchAppSettings", rj, rj);
            try {
                JSONObject jSONObject = new JSONObject();
                if (!TextUtils.isEmpty(str)) {
                    jSONObject.put("app_id", str);
                } else if (!TextUtils.isEmpty(str2)) {
                    jSONObject.put("ad_unit_id", str2);
                }
                jSONObject.put("is_init", z4);
                jSONObject.put("pn", context.getPackageName());
                zb zbVar = Gb.a;
                jSONObject.put("experiment_ids", TextUtils.join(",", r.f168d.f169a.a()));
                jSONObject.put("js", aVar.f844j);
                try {
                    ApplicationInfo applicationInfo = this.f6529a.getApplicationInfo();
                    if (applicationInfo != null && (b4 = b2.c.a(context).b(applicationInfo.packageName, 0)) != null) {
                        jSONObject.put("version", b4.versionCode);
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    C0183d0.k("Error fetching PackageInfo.");
                }
                zk a6 = a5.a(jSONObject);
                Jx jx = new Jx(l2, wv, gh, n4, 1);
                wk wkVar = xk.f;
                AN A4 = VN.A(a6, jx, wkVar);
                if (jk != null) {
                    a6.a(jk, wkVar);
                }
                if (l2 != null) {
                    a6.a(new g0(wv, 9, l2), wkVar);
                }
                if (((Boolean) r.f168d.f171c.a(Gb.R6)).booleanValue()) {
                    A4.a(new UN(A4, 0, new W0("ConfigLoader.maybeFetchNewAppSettings")), wkVar);
                } else {
                    com.google.android.gms.internal.ads.i.o(A4, "ConfigLoader.maybeFetchNewAppSettings");
                }
            } catch (Exception e4) {
                E1.m.e("Error requesting application settings", e4);
                n4.d(e4);
                n4.e(false);
                gh.b(n4.o());
            }
        }
    }
}
