package D1;

import android.content.Context;
import android.os.StrictMode;
import com.google.android.gms.internal.ads.Xh;
import java.util.concurrent.Callable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class V {
    @Deprecated
    public static Object a(Context context, Callable callable) {
        try {
            StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(threadPolicy).permitDiskReads().permitDiskWrites().build());
            Object call = callable.call();
            StrictMode.setThreadPolicy(threadPolicy);
            return call;
        } catch (Throwable th) {
            E1.m.e("Unexpected exception.", th);
            Xh.b(context).a("StrictModeUtil.runWithLaxStrictMode", th);
            return null;
        }
    }
}
