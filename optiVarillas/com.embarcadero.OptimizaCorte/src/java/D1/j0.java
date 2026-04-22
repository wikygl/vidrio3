package D1;

import a3.InterfaceFutureC0346a;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.U8;
import com.google.android.gms.internal.ads.jc;
import com.google.android.gms.internal.ads.kk;
import com.google.android.gms.internal.ads.xk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class j0 implements f0 {

    /* renamed from: b  reason: collision with root package name */
    public boolean f702b;

    /* renamed from: d  reason: collision with root package name */
    public InterfaceFutureC0346a f704d;
    public SharedPreferences f;

    /* renamed from: g  reason: collision with root package name */
    public SharedPreferences.Editor f706g;

    /* renamed from: i  reason: collision with root package name */
    public String f708i;

    /* renamed from: j  reason: collision with root package name */
    public String f709j;

    /* renamed from: a  reason: collision with root package name */
    public final Object f701a = new Object();

    /* renamed from: c  reason: collision with root package name */
    public final ArrayList f703c = new ArrayList();

    /* renamed from: e  reason: collision with root package name */
    public U8 f705e = null;

    /* renamed from: h  reason: collision with root package name */
    public boolean f707h = true;

    /* renamed from: k  reason: collision with root package name */
    public boolean f710k = true;

    /* renamed from: l  reason: collision with root package name */
    public String f711l = "-1";

    /* renamed from: m  reason: collision with root package name */
    public String f712m = "-1";

    /* renamed from: n  reason: collision with root package name */
    public String f713n = "-1";

    /* renamed from: o  reason: collision with root package name */
    public int f714o = -1;

    /* renamed from: p  reason: collision with root package name */
    public kk f715p = new kk("", 0);

    /* renamed from: q  reason: collision with root package name */
    public long f716q = 0;

    /* renamed from: r  reason: collision with root package name */
    public long f717r = 0;

    /* renamed from: s  reason: collision with root package name */
    public int f718s = -1;

    /* renamed from: t  reason: collision with root package name */
    public int f719t = 0;

    /* renamed from: u  reason: collision with root package name */
    public Set f720u = Collections.emptySet();

    /* renamed from: v  reason: collision with root package name */
    public JSONObject f721v = new JSONObject();

    /* renamed from: w  reason: collision with root package name */
    public boolean f722w = true;

    /* renamed from: x  reason: collision with root package name */
    public boolean f723x = true;

    /* renamed from: y  reason: collision with root package name */
    public String f724y = null;

    /* renamed from: z  reason: collision with root package name */
    public String f725z = "";

    /* renamed from: A  reason: collision with root package name */
    public boolean f695A = false;

    /* renamed from: B  reason: collision with root package name */
    public String f696B = "";

    /* renamed from: C  reason: collision with root package name */
    public String f697C = "{}";

    /* renamed from: D  reason: collision with root package name */
    public int f698D = -1;

    /* renamed from: E  reason: collision with root package name */
    public int f699E = -1;

    /* renamed from: F  reason: collision with root package name */
    public long f700F = 0;

    public final void A(String str) {
        p();
        synchronized (this.f701a) {
            try {
                if (str.equals(this.f709j)) {
                    return;
                }
                this.f709j = str;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putString("content_vertical_hashes", str);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void B(String str) {
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.j8)).booleanValue()) {
            return;
        }
        p();
        synchronized (this.f701a) {
            try {
                if (this.f696B.equals(str)) {
                    return;
                }
                this.f696B = str;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putString("linked_ad_unit", str);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final boolean E() {
        boolean z4;
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.n0)).booleanValue()) {
            return false;
        }
        p();
        synchronized (this.f701a) {
            z4 = this.f710k;
        }
        return z4;
    }

    @Override // D1.f0
    public final boolean G() {
        p();
        synchronized (this.f701a) {
            try {
                SharedPreferences sharedPreferences = this.f;
                boolean z4 = false;
                if (sharedPreferences == null) {
                    return false;
                }
                if (sharedPreferences.getLong("topics_consent_expiry_time_ms", 0L) < System.currentTimeMillis()) {
                    return false;
                }
                if (this.f.getBoolean("is_topics_ad_personalization_allowed", false) && !this.f710k) {
                    z4 = true;
                }
                return z4;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void H(boolean z4) {
        p();
        synchronized (this.f701a) {
            try {
                if (this.f723x == z4) {
                    return;
                }
                this.f723x = z4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putBoolean("content_vertical_opted_out", z4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void I(boolean z4) {
        p();
        synchronized (this.f701a) {
            try {
                if (z4 == this.f710k) {
                    return;
                }
                this.f710k = z4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putBoolean("gad_idless", z4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void J(long j4) {
        p();
        synchronized (this.f701a) {
            try {
                if (this.f700F == j4) {
                    return;
                }
                this.f700F = j4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putLong("sd_app_measure_npa_ts", j4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void K(long j4) {
        p();
        synchronized (this.f701a) {
            try {
                if (this.f717r == j4) {
                    return;
                }
                this.f717r = j4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putLong("first_ad_req_time_ms", j4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void L(String str, String str2, boolean z4) {
        p();
        synchronized (this.f701a) {
            try {
                JSONArray optJSONArray = this.f721v.optJSONArray(str);
                if (optJSONArray == null) {
                    optJSONArray = new JSONArray();
                }
                int length = optJSONArray.length();
                for (int i4 = 0; i4 < optJSONArray.length(); i4++) {
                    JSONObject optJSONObject = optJSONArray.optJSONObject(i4);
                    if (optJSONObject == null) {
                        return;
                    }
                    if (str2.equals(optJSONObject.optString("template_id"))) {
                        if (z4 && optJSONObject.optBoolean("uses_media_view", false)) {
                            return;
                        }
                        length = i4;
                    }
                }
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("template_id", str2);
                    jSONObject.put("uses_media_view", z4);
                    z1.p.f6575A.f6584j.getClass();
                    jSONObject.put("timestamp_ms", System.currentTimeMillis());
                    optJSONArray.put(length, jSONObject);
                    this.f721v.put(str, optJSONArray);
                } catch (JSONException e4) {
                    E1.m.h("Could not update native advanced settings", e4);
                }
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putString("native_advanced_settings", this.f721v.toString());
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void M(long j4) {
        p();
        synchronized (this.f701a) {
            try {
                if (this.f716q == j4) {
                    return;
                }
                this.f716q = j4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putLong("app_last_background_time_ms", j4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void N(boolean z4) {
        p();
        synchronized (this.f701a) {
            try {
                if (this.f722w == z4) {
                    return;
                }
                this.f722w = z4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putBoolean("content_url_opted_out", z4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void O(int i4) {
        p();
        synchronized (this.f701a) {
            try {
                if (this.f718s == i4) {
                    return;
                }
                this.f718s = i4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putInt("request_in_session_count", i4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void P(int i4) {
        p();
        synchronized (this.f701a) {
            try {
                if (this.f699E == i4) {
                    return;
                }
                this.f699E = i4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putInt("sd_app_measure_npa", i4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final void Q(int i4) {
        p();
        synchronized (this.f701a) {
            try {
                if (this.f719t == i4) {
                    return;
                }
                this.f719t = i4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putInt("version_code", i4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final int a() {
        int i4;
        p();
        synchronized (this.f701a) {
            i4 = this.f719t;
        }
        return i4;
    }

    @Override // D1.f0
    public final long b() {
        long j4;
        p();
        synchronized (this.f701a) {
            j4 = this.f700F;
        }
        return j4;
    }

    @Override // D1.f0
    public final int c() {
        int i4;
        p();
        synchronized (this.f701a) {
            i4 = this.f718s;
        }
        return i4;
    }

    public final void d(boolean z4) {
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.j8)).booleanValue()) {
            return;
        }
        p();
        synchronized (this.f701a) {
            try {
                if (this.f695A == z4) {
                    return;
                }
                this.f695A = z4;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putBoolean("linked_device", z4);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final long e() {
        long j4;
        p();
        synchronized (this.f701a) {
            j4 = this.f716q;
        }
        return j4;
    }

    public final void f(String str) {
        p();
        synchronized (this.f701a) {
            try {
                if (TextUtils.equals(this.f724y, str)) {
                    return;
                }
                this.f724y = str;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putString("display_cutout", str);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void g(String str) {
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.U7)).booleanValue()) {
            return;
        }
        p();
        synchronized (this.f701a) {
            try {
                if (this.f725z.equals(str)) {
                    return;
                }
                this.f725z = str;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putString("inspector_info", str);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // D1.f0
    public final kk h() {
        kk kkVar;
        p();
        synchronized (this.f701a) {
            try {
                if (((Boolean) A1.r.f168d.f171c.a(Gb.va)).booleanValue() && this.f715p.a()) {
                    Iterator it = this.f703c.iterator();
                    while (it.hasNext()) {
                        ((Runnable) it.next()).run();
                    }
                }
                kkVar = this.f715p;
            } catch (Throwable th) {
                throw th;
            }
        }
        return kkVar;
    }

    @Override // D1.f0
    public final long i() {
        long j4;
        p();
        synchronized (this.f701a) {
            j4 = this.f717r;
        }
        return j4;
    }

    public final void j(String str) {
        if (!((Boolean) A1.r.f168d.f171c.a(Gb.w8)).booleanValue()) {
            return;
        }
        p();
        synchronized (this.f701a) {
            try {
                if (this.f697C.equals(str)) {
                    return;
                }
                this.f697C = str;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putString("inspector_ui_storage", str);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void k(boolean z4) {
        p();
        synchronized (this.f701a) {
            try {
                long currentTimeMillis = System.currentTimeMillis() + ((Long) A1.r.f168d.f171c.a(Gb.k9)).longValue();
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putBoolean("is_topics_ad_personalization_allowed", z4);
                    this.f706g.putLong("topics_consent_expiry_time_ms", currentTimeMillis);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final boolean l() {
        boolean z4;
        p();
        synchronized (this.f701a) {
            z4 = this.f722w;
        }
        return z4;
    }

    public final boolean m() {
        boolean z4;
        p();
        synchronized (this.f701a) {
            z4 = this.f723x;
        }
        return z4;
    }

    @Override // D1.f0
    public final JSONObject n() {
        JSONObject jSONObject;
        p();
        synchronized (this.f701a) {
            jSONObject = this.f721v;
        }
        return jSONObject;
    }

    public final boolean o() {
        boolean z4;
        p();
        synchronized (this.f701a) {
            z4 = this.f695A;
        }
        return z4;
    }

    public final void p() {
        InterfaceFutureC0346a interfaceFutureC0346a = this.f704d;
        if (interfaceFutureC0346a != null && !interfaceFutureC0346a.isDone()) {
            try {
                this.f704d.get(1L, TimeUnit.SECONDS);
            } catch (InterruptedException e4) {
                Thread.currentThread().interrupt();
                E1.m.h("Interrupted while waiting for preferences loaded.", e4);
            } catch (CancellationException e5) {
                e = e5;
                E1.m.e("Fail to initialize AdSharedPreferenceManager.", e);
            } catch (ExecutionException e6) {
                e = e6;
                E1.m.e("Fail to initialize AdSharedPreferenceManager.", e);
            } catch (TimeoutException e7) {
                e = e7;
                E1.m.e("Fail to initialize AdSharedPreferenceManager.", e);
            }
        }
    }

    public final void q() {
        xk.a.execute(new RunnableC0176a(2, this));
    }

    public final U8 r() {
        if (!this.f702b) {
            return null;
        }
        if ((l() && m()) || !((Boolean) jc.b.e()).booleanValue()) {
            return null;
        }
        synchronized (this.f701a) {
            try {
                if (Looper.getMainLooper() == null) {
                    return null;
                }
                if (this.f705e == null) {
                    this.f705e = new U8();
                }
                this.f705e.c();
                E1.m.f("start fetching content...");
                return this.f705e;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final String s() {
        String str;
        p();
        synchronized (this.f701a) {
            str = this.f708i;
        }
        return str;
    }

    @Override // D1.f0
    public final void t() {
        p();
        synchronized (this.f701a) {
            try {
                this.f721v = new JSONObject();
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.remove("native_advanced_settings");
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final String u() {
        String str;
        p();
        synchronized (this.f701a) {
            str = this.f709j;
        }
        return str;
    }

    public final String v() {
        String str;
        p();
        synchronized (this.f701a) {
            str = this.f724y;
        }
        return str;
    }

    public final String w() {
        String str;
        p();
        synchronized (this.f701a) {
            str = this.f725z;
        }
        return str;
    }

    public final String x() {
        String str;
        p();
        synchronized (this.f701a) {
            str = this.f697C;
        }
        return str;
    }

    public final void y(Context context) {
        synchronized (this.f701a) {
            try {
                if (this.f != null) {
                    return;
                }
                this.f704d = xk.a.C(new g0(this, 0, context));
                this.f702b = true;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final void z(String str) {
        p();
        synchronized (this.f701a) {
            try {
                if (str.equals(this.f708i)) {
                    return;
                }
                this.f708i = str;
                SharedPreferences.Editor editor = this.f706g;
                if (editor != null) {
                    editor.putString("content_url_hashes", str);
                    this.f706g.apply();
                }
                q();
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
