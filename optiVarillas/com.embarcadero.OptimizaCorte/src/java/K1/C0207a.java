package K1;

import A1.L0;
import D1.t0;
import K1.C0207a;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.YH;
import com.google.android.gms.internal.ads.Zv;
import com.google.android.gms.internal.ads.s7;
import com.google.android.gms.internal.ads.wb;
import com.google.android.gms.internal.ads.wk;
import com.google.android.gms.internal.ads.xk;
import com.google.android.gms.internal.ads.zG;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import t1.C0802d;

/* renamed from: K1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0207a {

    /* renamed from: a  reason: collision with root package name */
    public final Context f1314a;

    /* renamed from: b  reason: collision with root package name */
    public final WebView f1315b;

    /* renamed from: c  reason: collision with root package name */
    public final s7 f1316c;

    /* renamed from: d  reason: collision with root package name */
    public final zG f1317d;

    /* renamed from: e  reason: collision with root package name */
    public final int f1318e;
    public final Zv f;

    /* renamed from: g  reason: collision with root package name */
    public final boolean f1319g;

    /* renamed from: h  reason: collision with root package name */
    public final wk f1320h = xk.e;

    /* renamed from: i  reason: collision with root package name */
    public final YH f1321i;

    public C0207a(WebView webView, s7 s7Var, Zv zv, YH yh, zG zGVar) {
        this.f1315b = webView;
        Context context = webView.getContext();
        this.f1314a = context;
        this.f1316c = s7Var;
        this.f = zv;
        Gb.a(context);
        wb wbVar = Gb.C8;
        A1.r rVar = A1.r.f168d;
        this.f1318e = ((Integer) rVar.f171c.a(wbVar)).intValue();
        this.f1319g = ((Boolean) rVar.f171c.a(Gb.D8)).booleanValue();
        this.f1321i = yh;
        this.f1317d = zGVar;
    }

    @JavascriptInterface
    @TargetApi(21)
    public String getClickSignals(String str) {
        try {
            z1.p pVar = z1.p.f6575A;
            pVar.f6584j.getClass();
            long currentTimeMillis = System.currentTimeMillis();
            String g4 = this.f1316c.b.g(this.f1314a, str, this.f1315b);
            if (this.f1319g) {
                pVar.f6584j.getClass();
                w.c(this.f, "csg", new Pair("clat", String.valueOf(System.currentTimeMillis() - currentTimeMillis)));
            }
            return g4;
        } catch (RuntimeException e4) {
            E1.m.e("Exception getting click signals. ", e4);
            z1.p.f6575A.f6581g.h("TaggingLibraryJsInterface.getClickSignals", e4);
            return "";
        }
    }

    @JavascriptInterface
    @TargetApi(21)
    public String getClickSignalsWithTimeout(final String str, int i4) {
        if (i4 <= 0) {
            E1.m.d("Invalid timeout for getting click signals. Timeout=" + i4);
            return "";
        }
        int min = Math.min(i4, this.f1318e);
        try {
            return (String) xk.a.y(new Callable() { // from class: D1.k0
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    Object obj = str;
                    Object obj2 = this;
                    switch (r2) {
                        case 0:
                            C0185e0 c0185e0 = t0.f774l;
                            WebSettings webSettings = (WebSettings) obj2;
                            webSettings.setDatabasePath(((Context) obj).getDatabasePath("com.google.android.gms.ads.db").getAbsolutePath());
                            webSettings.setDatabaseEnabled(true);
                            webSettings.setDomStorageEnabled(true);
                            webSettings.setDisplayZoomControls(false);
                            webSettings.setBuiltInZoomControls(true);
                            webSettings.setSupportZoom(true);
                            if (((Boolean) A1.r.f168d.f171c.a(Gb.B0)).booleanValue()) {
                                webSettings.setTextZoom(100);
                            }
                            webSettings.setAllowContentAccess(false);
                            return Boolean.TRUE;
                        case 1:
                            return ((C0207a) obj2).getClickSignals((String) obj);
                        default:
                            return ((s7) obj2).b.e((Context) obj);
                    }
                }
            }).get(min, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e4) {
            E1.m.e("Exception getting click signals with timeout. ", e4);
            z1.p.f6575A.f6581g.h("TaggingLibraryJsInterface.getClickSignalsWithTimeout", e4);
            if (!(e4 instanceof TimeoutException)) {
                return "";
            }
            return "17";
        }
    }

    @JavascriptInterface
    @TargetApi(21)
    public String getQueryInfo() {
        t0 t0Var = z1.p.f6575A.f6578c;
        String uuid = UUID.randomUUID().toString();
        Bundle bundle = new Bundle();
        bundle.putString("query_info_type", "requester_type_6");
        r rVar = new r(this, uuid);
        if (((Boolean) A1.r.f168d.f171c.a(Gb.F8)).booleanValue()) {
            this.f1320h.execute(new q(this, bundle, rVar, 0));
        } else {
            M1.a.a(this.f1314a, new C0802d(new C0802d.a().a(bundle)), rVar);
        }
        return uuid;
    }

    @JavascriptInterface
    @TargetApi(21)
    public String getViewSignals() {
        try {
            z1.p pVar = z1.p.f6575A;
            pVar.f6584j.getClass();
            long currentTimeMillis = System.currentTimeMillis();
            String d4 = this.f1316c.b.d(this.f1314a, this.f1315b, (Activity) null);
            if (this.f1319g) {
                pVar.f6584j.getClass();
                w.c(this.f, "vsg", new Pair("vlat", String.valueOf(System.currentTimeMillis() - currentTimeMillis)));
            }
            return d4;
        } catch (RuntimeException e4) {
            E1.m.e("Exception getting view signals. ", e4);
            z1.p.f6575A.f6581g.h("TaggingLibraryJsInterface.getViewSignals", e4);
            return "";
        }
    }

    @JavascriptInterface
    @TargetApi(21)
    public String getViewSignalsWithTimeout(int i4) {
        if (i4 <= 0) {
            E1.m.d("Invalid timeout for getting view signals. Timeout=" + i4);
            return "";
        }
        int min = Math.min(i4, this.f1318e);
        try {
            return (String) xk.a.y(new Callable() { // from class: K1.p
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return C0207a.this.getViewSignals();
                }
            }).get(min, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e4) {
            E1.m.e("Exception getting view signals with timeout. ", e4);
            z1.p.f6575A.f6581g.h("TaggingLibraryJsInterface.getViewSignalsWithTimeout", e4);
            if (!(e4 instanceof TimeoutException)) {
                return "";
            }
            return "17";
        }
    }

    @JavascriptInterface
    @TargetApi(21)
    public void recordClick(String str) {
        if (((Boolean) A1.r.f168d.f171c.a(Gb.H8)).booleanValue() && !TextUtils.isEmpty(str)) {
            xk.a.execute(new L0(this, 3, str));
        }
    }

    @JavascriptInterface
    @TargetApi(21)
    public void reportTouchEvent(String str) {
        int i4;
        int i5;
        int i6;
        float f;
        int i7;
        try {
            JSONObject jSONObject = new JSONObject(str);
            i4 = jSONObject.getInt("x");
            i5 = jSONObject.getInt("y");
            i6 = jSONObject.getInt("duration_ms");
            f = (float) jSONObject.getDouble("force");
            int i8 = jSONObject.getInt("type");
            if (i8 != 0) {
                if (i8 != 1) {
                    if (i8 != 2) {
                        if (i8 != 3) {
                            i7 = -1;
                        } else {
                            i7 = 3;
                        }
                    } else {
                        i7 = 2;
                    }
                } else {
                    i7 = 1;
                }
            } else {
                i7 = 0;
            }
        } catch (RuntimeException | JSONException e4) {
            e = e4;
        }
        try {
            this.f1316c.b.a(MotionEvent.obtain(0L, i6, i7, i4, i5, f, 1.0f, 0, 1.0f, 1.0f, 0, 0));
        } catch (RuntimeException e5) {
            e = e5;
            E1.m.e("Failed to parse the touch string. ", e);
            z1.p.f6575A.f6581g.h("TaggingLibraryJsInterface.reportTouchEvent", e);
        } catch (JSONException e6) {
            e = e6;
            E1.m.e("Failed to parse the touch string. ", e);
            z1.p.f6575A.f6581g.h("TaggingLibraryJsInterface.reportTouchEvent", e);
        }
    }
}
