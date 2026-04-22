package C1;

import D1.C0183d0;
import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.CK;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.IK;
import com.google.android.gms.internal.ads.KK;
import com.google.android.gms.internal.ads.em;
import com.google.android.gms.internal.ads.iq;
import com.google.android.gms.internal.ads.mK;
import com.google.android.gms.internal.ads.nK;
import com.google.android.gms.internal.ads.oK;
import com.google.android.gms.internal.ads.qK;
import com.google.android.gms.internal.ads.uK;
import com.google.android.gms.internal.ads.xk;
import com.google.android.gms.internal.ads.zK;
import java.util.HashMap;
import p2.C0758g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class B {

    /* renamed from: a  reason: collision with root package name */
    public String f353a;

    /* renamed from: b  reason: collision with root package name */
    public String f354b;

    /* renamed from: c  reason: collision with root package name */
    public em f355c;

    /* renamed from: d  reason: collision with root package name */
    public iq f356d;

    /* renamed from: e  reason: collision with root package name */
    public boolean f357e;
    public A f;

    public final void a(String str, HashMap hashMap) {
        xk.e.execute(new z(this, str, hashMap, 0));
    }

    public final void b(String str, String str2) {
        C0183d0.k(str);
        if (this.f355c != null) {
            HashMap hashMap = new HashMap();
            hashMap.put("message", str);
            hashMap.put("action", str2);
            a("onError", hashMap);
        }
    }

    public final void c(em emVar, mK mKVar) {
        if (emVar == null) {
            b("adWebview missing", "onLMDShow");
            return;
        }
        this.f355c = emVar;
        if (!this.f357e && !d(emVar.getContext())) {
            b("LMDOverlay not bound", "on_play_store_bind");
            return;
        }
        boolean booleanValue = ((Boolean) A1.r.f168d.f171c.a(Gb.ka)).booleanValue();
        String str = mKVar.b;
        if (booleanValue) {
            this.f354b = str;
        }
        if (this.f == null) {
            this.f = new A(0, this);
        }
        iq iqVar = this.f356d;
        if (iqVar != null) {
            A a4 = this.f;
            zK zKVar = uK.c;
            uK uKVar = (uK) iqVar.k;
            IK ik = uKVar.a;
            if (ik == null) {
                zKVar.a("error: %s", new Object[]{"Play Store not found."});
            } else if (str == null) {
                zKVar.a("Failed to convert OverlayDisplayShowRequest when to create a new session: appId cannot be null.", new Object[0]);
                a4.b(new nK((String) null, 8160));
            } else {
                C0758g c0758g = new C0758g();
                ik.a().post(new CK(ik, c0758g, c0758g, new qK(uKVar, c0758g, mKVar, a4, c0758g)));
            }
        }
    }

    public final synchronized boolean d(Context context) {
        if (!KK.a(context)) {
            return false;
        }
        try {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                context = applicationContext;
            }
            this.f356d = new iq(15, new uK(context));
        } catch (NullPointerException e4) {
            C0183d0.k("Error connecting LMD Overlay service");
            z1.p.f6575A.f6581g.h("LastMileDeliveryOverlay.bindLastMileDeliveryService", e4);
        }
        if (this.f356d == null) {
            this.f357e = false;
            return false;
        }
        if (this.f == null) {
            this.f = new A(0, this);
        }
        this.f357e = true;
        return true;
    }

    public final oK e() {
        String str;
        String str2 = null;
        if (((Boolean) A1.r.f168d.f171c.a(Gb.ka)).booleanValue() && !TextUtils.isEmpty(this.f354b)) {
            str = this.f354b;
        } else {
            String str3 = this.f353a;
            if (str3 != null) {
                str2 = str3;
                str = null;
            } else {
                b("Missing session token and/or appId", "onLMDupdate");
                str = null;
            }
        }
        return new oK(str2, str);
    }
}
