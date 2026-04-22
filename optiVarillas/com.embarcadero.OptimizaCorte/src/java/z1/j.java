package z1;

import A1.C0124p;
import A1.InterfaceC0139x;
import A1.N0;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.gms.internal.ads.MG;
import com.google.android.gms.internal.ads.t7;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class j extends WebViewClient {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ o f6557a;

    public j(o oVar) {
        this.f6557a = oVar;
    }

    @Override // android.webkit.WebViewClient
    public final void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
        o oVar = this.f6557a;
        InterfaceC0139x interfaceC0139x = oVar.f6572p;
        if (interfaceC0139x != null) {
            try {
                interfaceC0139x.s(MG.d(1, (String) null, (N0) null));
            } catch (RemoteException e4) {
                E1.m.i("#007 Could not call remote method.", e4);
            }
        }
        InterfaceC0139x interfaceC0139x2 = oVar.f6572p;
        if (interfaceC0139x2 != null) {
            try {
                interfaceC0139x2.w(0);
            } catch (RemoteException e5) {
                E1.m.i("#007 Could not call remote method.", e5);
            }
        }
    }

    @Override // android.webkit.WebViewClient
    public final boolean shouldOverrideUrlLoading(WebView webView, String str) {
        o oVar = this.f6557a;
        int i4 = 0;
        if (str.startsWith(oVar.q())) {
            return false;
        }
        if (str.startsWith("gmsg://noAdLoaded")) {
            InterfaceC0139x interfaceC0139x = oVar.f6572p;
            if (interfaceC0139x != null) {
                try {
                    interfaceC0139x.s(MG.d(3, (String) null, (N0) null));
                } catch (RemoteException e4) {
                    E1.m.i("#007 Could not call remote method.", e4);
                }
            }
            InterfaceC0139x interfaceC0139x2 = oVar.f6572p;
            if (interfaceC0139x2 != null) {
                try {
                    interfaceC0139x2.w(3);
                } catch (RemoteException e5) {
                    E1.m.i("#007 Could not call remote method.", e5);
                }
            }
            oVar.C4(0);
            return true;
        } else if (str.startsWith("gmsg://scriptLoadFailed")) {
            InterfaceC0139x interfaceC0139x3 = oVar.f6572p;
            if (interfaceC0139x3 != null) {
                try {
                    interfaceC0139x3.s(MG.d(1, (String) null, (N0) null));
                } catch (RemoteException e6) {
                    E1.m.i("#007 Could not call remote method.", e6);
                }
            }
            InterfaceC0139x interfaceC0139x4 = oVar.f6572p;
            if (interfaceC0139x4 != null) {
                try {
                    interfaceC0139x4.w(0);
                } catch (RemoteException e7) {
                    E1.m.i("#007 Could not call remote method.", e7);
                }
            }
            oVar.C4(0);
            return true;
        } else {
            boolean startsWith = str.startsWith("gmsg://adResized");
            Context context = oVar.f6569m;
            if (startsWith) {
                InterfaceC0139x interfaceC0139x5 = oVar.f6572p;
                if (interfaceC0139x5 != null) {
                    try {
                        interfaceC0139x5.f();
                    } catch (RemoteException e8) {
                        E1.m.i("#007 Could not call remote method.", e8);
                    }
                }
                String queryParameter = Uri.parse(str).getQueryParameter("height");
                if (!TextUtils.isEmpty(queryParameter)) {
                    try {
                        E1.f fVar = C0124p.f.f161a;
                        i4 = E1.f.m(context, Integer.parseInt(queryParameter));
                    } catch (NumberFormatException unused) {
                    }
                }
                oVar.C4(i4);
                return true;
            } else if (str.startsWith("gmsg://")) {
                return true;
            } else {
                InterfaceC0139x interfaceC0139x6 = oVar.f6572p;
                if (interfaceC0139x6 != null) {
                    try {
                        interfaceC0139x6.r();
                        oVar.f6572p.g();
                    } catch (RemoteException e9) {
                        E1.m.i("#007 Could not call remote method.", e9);
                    }
                }
                if (oVar.f6573q != null) {
                    Uri parse = Uri.parse(str);
                    try {
                        parse = oVar.f6573q.a(parse, context, (View) null, (Activity) null);
                    } catch (t7 e10) {
                        E1.m.h("Unable to process ad data", e10);
                    }
                    str = parse.toString();
                }
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(str));
                context.startActivity(intent);
                return true;
            }
        }
    }
}
