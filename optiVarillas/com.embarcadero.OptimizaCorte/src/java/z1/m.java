package z1;

import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.WebView;
import com.google.android.gms.internal.ads.gc;
import com.google.android.gms.internal.ads.s7;
import com.google.android.gms.internal.ads.t7;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class m extends AsyncTask {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ o f6560a;

    @Override // android.os.AsyncTask
    public final Object doInBackground(Object[] objArr) {
        Void[] voidArr = (Void[]) objArr;
        o oVar = this.f6560a;
        try {
            oVar.f6573q = (s7) oVar.f6568l.get(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e4) {
            e = e4;
            E1.m.h("", e);
        } catch (ExecutionException e5) {
            e = e5;
            E1.m.h("", e);
        } catch (TimeoutException e6) {
            E1.m.h("", e6);
        }
        oVar.getClass();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https://").appendEncodedPath((String) gc.d.e());
        n nVar = oVar.f6570n;
        builder.appendQueryParameter("query", nVar.f6564d);
        builder.appendQueryParameter("pubId", nVar.f6562b);
        builder.appendQueryParameter("mappver", nVar.f);
        TreeMap treeMap = nVar.f6563c;
        for (String str : treeMap.keySet()) {
            builder.appendQueryParameter(str, (String) treeMap.get(str));
        }
        Uri build = builder.build();
        s7 s7Var = oVar.f6573q;
        if (s7Var != null) {
            try {
                build = s7.d(build, s7Var.b.e(oVar.f6569m));
            } catch (t7 e7) {
                E1.m.h("Unable to process ad data", e7);
            }
        }
        return X1.b.e(oVar.q(), "#", build.getEncodedQuery());
    }

    @Override // android.os.AsyncTask
    public final /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
        String str = (String) obj;
        WebView webView = this.f6560a.f6571o;
        if (webView != null && str != null) {
            webView.loadUrl(str);
        }
    }
}
