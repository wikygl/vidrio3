package i2;

import X2.a;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import j$.util.Objects;
import java.util.concurrent.Executor;
import org.json.JSONObject;

/* renamed from: i2.v  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0475v implements L {

    /* renamed from: a  reason: collision with root package name */
    public final Application f3804a;

    /* renamed from: b  reason: collision with root package name */
    public final C0473t f3805b;

    /* renamed from: c  reason: collision with root package name */
    public final Handler f3806c;

    /* renamed from: d  reason: collision with root package name */
    public final Executor f3807d;

    /* renamed from: e  reason: collision with root package name */
    public final a0 f3808e;
    public final C0461g f;

    /* renamed from: g  reason: collision with root package name */
    public final C0467m f3809g;

    /* renamed from: h  reason: collision with root package name */
    public final C0463i f3810h;

    public C0475v(Application application, C0473t c0473t, Handler handler, F f, a0 a0Var, C0461g c0461g, C0467m c0467m, C0463i c0463i) {
        this.f3804a = application;
        this.f3805b = c0473t;
        this.f3806c = handler;
        this.f3807d = f;
        this.f3808e = a0Var;
        this.f = c0461g;
        this.f3809g = c0467m;
        this.f3810h = c0463i;
    }

    @Override // i2.L
    public final Executor a() {
        final Handler handler = this.f3806c;
        Objects.requireNonNull(handler);
        return new Executor() { // from class: i2.u
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                handler.post(runnable);
            }
        };
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // i2.L
    public final boolean b(String str, JSONObject jSONObject) {
        char c4;
        char c5 = 65535;
        switch (str.hashCode()) {
            case -1370505102:
                if (str.equals("load_complete")) {
                    c4 = 0;
                    break;
                }
                c4 = 65535;
                break;
            case -278739366:
                if (str.equals("configure_app_assets")) {
                    c4 = 3;
                    break;
                }
                c4 = 65535;
                break;
            case 150940456:
                if (str.equals("browser")) {
                    c4 = 2;
                    break;
                }
                c4 = 65535;
                break;
            case 1671672458:
                if (str.equals("dismiss")) {
                    c4 = 1;
                    break;
                }
                c4 = 65535;
                break;
            default:
                c4 = 65535;
                break;
        }
        C0467m c0467m = this.f3809g;
        if (c4 != 0) {
            if (c4 != 1) {
                if (c4 != 2) {
                    if (c4 != 3) {
                        return false;
                    }
                    this.f3807d.execute(new C1.j(1, this));
                    return true;
                }
                String optString = jSONObject.optString("url");
                if (TextUtils.isEmpty(optString)) {
                    Log.d("UserMessagingPlatform", "Action[browser]: empty url.");
                }
                Uri parse = Uri.parse(optString);
                if (parse.getScheme() == null) {
                    Log.d("UserMessagingPlatform", "Action[browser]: empty scheme: ".concat(String.valueOf(optString)));
                }
                try {
                    this.f3805b.startActivity(new Intent("android.intent.action.VIEW", parse));
                } catch (ActivityNotFoundException e4) {
                    Log.d("UserMessagingPlatform", "Action[browser]: can not open url: ".concat(String.valueOf(optString)), e4);
                }
                return true;
            }
            String optString2 = jSONObject.optString("status");
            switch (optString2.hashCode()) {
                case -954325659:
                    if (optString2.equals("CONSENT_SIGNAL_NON_PERSONALIZED_ADS")) {
                        c5 = 3;
                        break;
                    }
                    break;
                case -258041904:
                    if (optString2.equals("personalized")) {
                        c5 = 0;
                        break;
                    }
                    break;
                case 429411856:
                    if (optString2.equals("CONSENT_SIGNAL_SUFFICIENT")) {
                        c5 = 4;
                        break;
                    }
                    break;
                case 467888915:
                    if (optString2.equals("CONSENT_SIGNAL_PERSONALIZED_ADS")) {
                        c5 = 1;
                        break;
                    }
                    break;
                case 1666911234:
                    if (optString2.equals("non_personalized")) {
                        c5 = 2;
                        break;
                    }
                    break;
            }
            if (c5 != 0 && c5 != 1 && c5 != 2 && c5 != 3 && c5 != 4) {
                b0 b0Var = new b0("We are getting something wrong with the webview.", 1);
                c0467m.b();
                a.InterfaceC0034a interfaceC0034a = (a.InterfaceC0034a) c0467m.f3780j.getAndSet(null);
                if (interfaceC0034a != null) {
                    b0Var.a();
                    interfaceC0034a.a();
                }
            } else {
                c0467m.b();
                a.InterfaceC0034a interfaceC0034a2 = (a.InterfaceC0034a) c0467m.f3780j.getAndSet(null);
                if (interfaceC0034a2 != null) {
                    c0467m.f3774c.f3760b.edit().putInt("consent_status", 3).apply();
                    interfaceC0034a2.a();
                }
            }
            return true;
        }
        C0466l c0466l = (C0466l) c0467m.f3779i.getAndSet(null);
        if (c0466l != null) {
            c0466l.c(c0467m);
        }
        return true;
    }

    public final void c(String str) {
        Log.d("UserMessagingPlatform", "Receive consent action: ".concat(String.valueOf(str)));
        Uri parse = Uri.parse(str);
        String queryParameter = parse.getQueryParameter("action");
        String queryParameter2 = parse.getQueryParameter("args");
        L[] lArr = {this, this.f};
        a0 a0Var = this.f3808e;
        a0Var.getClass();
        a0Var.f3709a.execute(new C1.z(queryParameter, queryParameter2, lArr));
    }
}
