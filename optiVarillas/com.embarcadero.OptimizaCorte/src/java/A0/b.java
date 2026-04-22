package A0;

import B0.s;
import B0.t;
import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.net.Uri;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public static final /* synthetic */ int f5a = 0;

    static {
        Uri.parse("*");
        Uri.parse("");
    }

    @SuppressLint({"PrivateApi"})
    public static PackageInfo a() {
        return (PackageInfo) Class.forName("android.webkit.WebViewFactory").getMethod("getLoadedPackageInfo", null).invoke(null, null);
    }

    public static boolean b() {
        if (s.f289a.d()) {
            return t.a.f292a.getStatics().isMultiProcessEnabled();
        }
        throw new UnsupportedOperationException("This method is not supported by the current version of the framework and the current WebView APK");
    }
}
