package D1;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Mw;
import com.google.android.gms.internal.ads.Nw;
import com.google.android.gms.internal.ads.wb;
import com.google.android.gms.internal.ads.zb;
import com.google.android.gms.internal.ads.zk;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: D1.s  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0198s {

    /* renamed from: a  reason: collision with root package name */
    public final Object f767a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public String f768b = "";

    /* renamed from: c  reason: collision with root package name */
    public String f769c = "";

    /* renamed from: d  reason: collision with root package name */
    public boolean f770d = false;

    /* renamed from: e  reason: collision with root package name */
    public boolean f771e = false;
    public String f = "";

    /* renamed from: g  reason: collision with root package name */
    public Nw f772g;

    public static void e(Context context, String str, boolean z4, boolean z5) {
        if (!(context instanceof Activity)) {
            E1.m.f("Can not create dialog without Activity Context");
        } else {
            t0.f774l.post(new r(context, str, z4, z5));
        }
    }

    public static final String j(Context context, String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put("User-Agent", z1.p.f6575A.f6578c.w(context, str2));
        new G(context);
        D a4 = G.a(0, str, hashMap, null);
        try {
            wb wbVar = Gb.h4;
            TimeUnit timeUnit = TimeUnit.MILLISECONDS;
            return (String) ((zk) a4).j.get(((Integer) A1.r.f168d.f171c.a(wbVar)).intValue(), timeUnit);
        } catch (InterruptedException e4) {
            E1.m.e("Interrupted while retrieving a response from: ".concat(String.valueOf(str)), e4);
            a4.cancel(true);
            return null;
        } catch (TimeoutException e5) {
            E1.m.e("Timeout while retrieving a response from: ".concat(String.valueOf(str)), e5);
            a4.cancel(true);
            return null;
        } catch (Exception e6) {
            E1.m.e("Error retrieving a response from: ".concat(String.valueOf(str)), e6);
            return null;
        }
    }

    public final void a(Context context) {
        Nw nw;
        if (((Boolean) A1.r.f168d.f171c.a(Gb.j8)).booleanValue() && (nw = this.f772g) != null) {
            nw.d(new C0196p(this, context), Mw.m);
        }
    }

    public final void b(Context context, String str, String str2) {
        t0 t0Var = z1.p.f6575A.f6578c;
        t0.q(context, k(context, (String) A1.r.f168d.f171c.a(Gb.d4), str, str2));
    }

    public final void c(Context context, String str, String str2, String str3) {
        Uri.Builder buildUpon = k(context, (String) A1.r.f168d.f171c.a(Gb.g4), str3, str).buildUpon();
        buildUpon.appendQueryParameter("debugData", str2);
        t0 t0Var = z1.p.f6575A.f6578c;
        t0.j(context, str, buildUpon.build().toString());
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0030, code lost:
        if (r4 != false) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void d(boolean r4) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.f767a
            monitor-enter(r0)
            r3.f771e = r4     // Catch: java.lang.Throwable -> L45
            com.google.android.gms.internal.ads.vb r1 = com.google.android.gms.internal.ads.Gb.j8     // Catch: java.lang.Throwable -> L45
            A1.r r2 = A1.r.f168d     // Catch: java.lang.Throwable -> L45
            com.google.android.gms.internal.ads.Eb r2 = r2.f171c     // Catch: java.lang.Throwable -> L45
            java.lang.Object r1 = r2.a(r1)     // Catch: java.lang.Throwable -> L45
            java.lang.Boolean r1 = (java.lang.Boolean) r1     // Catch: java.lang.Throwable -> L45
            boolean r1 = r1.booleanValue()     // Catch: java.lang.Throwable -> L45
            if (r1 == 0) goto L47
            z1.p r1 = z1.p.f6575A     // Catch: java.lang.Throwable -> L45
            com.google.android.gms.internal.ads.pk r1 = r1.f6581g     // Catch: java.lang.Throwable -> L45
            D1.j0 r1 = r1.c()     // Catch: java.lang.Throwable -> L45
            r1.d(r4)     // Catch: java.lang.Throwable -> L45
            com.google.android.gms.internal.ads.Nw r1 = r3.f772g     // Catch: java.lang.Throwable -> L45
            if (r1 == 0) goto L47
            boolean r2 = r1.u     // Catch: java.lang.Throwable -> L45
            if (r2 != 0) goto L30
            if (r4 == 0) goto L3b
            r1.i()     // Catch: java.lang.Throwable -> L45
            goto L32
        L30:
            if (r4 == 0) goto L3b
        L32:
            boolean r4 = r1.s     // Catch: java.lang.Throwable -> L45
            if (r4 == 0) goto L37
            goto L3b
        L37:
            r1.n()     // Catch: java.lang.Throwable -> L45
            goto L47
        L3b:
            boolean r4 = r1.f()     // Catch: java.lang.Throwable -> L45
            if (r4 != 0) goto L47
            r1.m()     // Catch: java.lang.Throwable -> L45
            goto L47
        L45:
            r4 = move-exception
            goto L49
        L47:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L45
            return
        L49:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L45
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: D1.C0198s.d(boolean):void");
    }

    public final boolean f(Context context, String str, String str2) {
        zb zbVar = Gb.f4;
        A1.r rVar = A1.r.f168d;
        String j4 = j(context, k(context, (String) rVar.f171c.a(zbVar), str, str2).toString(), str2);
        if (TextUtils.isEmpty(j4)) {
            E1.m.b("Not linked for debug signals.");
            return false;
        }
        try {
            boolean equals = "1".equals(new JSONObject(j4.trim()).optString("debug_mode"));
            d(equals);
            if (((Boolean) rVar.f171c.a(Gb.j8)).booleanValue()) {
                j0 c4 = z1.p.f6575A.f6581g.c();
                if (true != equals) {
                    str = "";
                }
                c4.B(str);
            }
            return equals;
        } catch (JSONException e4) {
            E1.m.h("Fail to get debug mode response json.", e4);
            return false;
        }
    }

    public final boolean g() {
        boolean z4;
        synchronized (this.f767a) {
            z4 = this.f771e;
        }
        return z4;
    }

    public final boolean h() {
        boolean z4;
        synchronized (this.f767a) {
            z4 = this.f770d;
        }
        return z4;
    }

    public final boolean i(Context context, String str, String str2, String str3) {
        if (!TextUtils.isEmpty(str2) && h()) {
            E1.m.b("Sending troubleshooting signals to the server.");
            c(context, str, str2, str3);
            return true;
        }
        return false;
    }

    public final Uri k(Context context, String str, String str2, String str3) {
        String str4;
        String str5;
        Uri.Builder buildUpon = Uri.parse(str).buildUpon();
        synchronized (this.f767a) {
            if (TextUtils.isEmpty(this.f768b)) {
                t0 t0Var = z1.p.f6575A.f6578c;
                try {
                    FileInputStream openFileInput = context.openFileInput("debug_signals_id.txt");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    a2.f.b(openFileInput, byteArrayOutputStream, true);
                    str5 = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
                } catch (IOException unused) {
                    E1.m.b("Error reading from internal storage.");
                    str5 = "";
                }
                this.f768b = str5;
                if (TextUtils.isEmpty(str5)) {
                    t0 t0Var2 = z1.p.f6575A.f6578c;
                    String uuid = UUID.randomUUID().toString();
                    this.f768b = uuid;
                    try {
                        FileOutputStream openFileOutput = context.openFileOutput("debug_signals_id.txt", 0);
                        openFileOutput.write(uuid.getBytes("UTF-8"));
                        openFileOutput.close();
                    } catch (Exception e4) {
                        E1.m.e("Error writing to file in internal storage.", e4);
                    }
                }
            }
            str4 = this.f768b;
        }
        buildUpon.appendQueryParameter("linkedDeviceId", str4);
        buildUpon.appendQueryParameter("adSlotPath", str2);
        buildUpon.appendQueryParameter("afmaVersion", str3);
        return buildUpon.build();
    }
}
