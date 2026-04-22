package i2;

import android.annotation.TargetApi;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Locale;

/* renamed from: i2.q  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0471q extends WebViewClient {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ r f3795a;

    public /* synthetic */ C0471q(r rVar) {
        this.f3795a = rVar;
    }

    @Override // android.webkit.WebViewClient
    public final void onLoadResource(WebView webView, String str) {
        int i4 = r.f3796m;
        if (str != null && str.startsWith("consent://")) {
            this.f3795a.f3798k.c(str);
        }
    }

    @Override // android.webkit.WebViewClient
    public final void onPageFinished(WebView webView, String str) {
        r rVar = this.f3795a;
        if (!rVar.f3799l) {
            Log.d("UserMessagingPlatform", "Wall html loaded.");
            rVar.f3799l = true;
        }
    }

    @Override // android.webkit.WebViewClient
    public final void onReceivedError(WebView webView, int i4, String str, String str2) {
        C0475v c0475v = this.f3795a.f3798k;
        c0475v.getClass();
        Locale locale = Locale.US;
        b0 b0Var = new b0("WebResourceError(" + i4 + ", " + str2 + "): " + str, 2);
        C0466l c0466l = (C0466l) c0475v.f3809g.f3779i.getAndSet(null);
        if (c0466l != null) {
            c0466l.b(b0Var.a());
        }
    }

    @Override // android.webkit.WebViewClient
    @TargetApi(24)
    public final boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        String uri = webResourceRequest.getUrl().toString();
        int i4 = r.f3796m;
        if (uri == null || !uri.startsWith("consent://")) {
            return false;
        }
        this.f3795a.f3798k.c(uri);
        return true;
    }

    @Override // android.webkit.WebViewClient
    public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
        int i4 = r.f3796m;
        if (str == null || !str.startsWith("consent://")) {
            return false;
        }
        this.f3795a.f3798k.c(str);
        return true;
    }
}
