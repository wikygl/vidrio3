package E1;

import android.util.Log;
import com.google.android.gms.internal.ads.FI;
import com.google.android.gms.internal.ads.M0;
import com.google.android.gms.internal.ads.dL;
import java.util.Iterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class m {

    /* renamed from: a  reason: collision with root package name */
    public static final M0 f875a = new M0(16, new FI(1, (byte) 0));

    public static String a(String str) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 4) {
            int lineNumber = stackTrace[3].getLineNumber();
            return str + " @" + lineNumber;
        }
        return str;
    }

    public static void b(String str) {
        if (j(3)) {
            if (str != null && str.length() > 4000) {
                Iterator it = new dL(f875a, str).iterator();
                boolean z4 = true;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    if (z4) {
                        Log.d("Ads", str2);
                    } else {
                        Log.d("Ads-cont", str2);
                    }
                    z4 = false;
                }
                return;
            }
            Log.d("Ads", str);
        }
    }

    public static void c(String str, Throwable th) {
        if (j(3)) {
            Log.d("Ads", str, th);
        }
    }

    public static void d(String str) {
        if (j(6)) {
            if (str != null && str.length() > 4000) {
                Iterator it = new dL(f875a, str).iterator();
                boolean z4 = true;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    if (z4) {
                        Log.e("Ads", str2);
                    } else {
                        Log.e("Ads-cont", str2);
                    }
                    z4 = false;
                }
                return;
            }
            Log.e("Ads", str);
        }
    }

    public static void e(String str, Throwable th) {
        if (j(6)) {
            Log.e("Ads", str, th);
        }
    }

    public static void f(String str) {
        if (j(4)) {
            if (str != null && str.length() > 4000) {
                Iterator it = new dL(f875a, str).iterator();
                boolean z4 = true;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    if (z4) {
                        Log.i("Ads", str2);
                    } else {
                        Log.i("Ads-cont", str2);
                    }
                    z4 = false;
                }
                return;
            }
            Log.i("Ads", str);
        }
    }

    public static void g(String str) {
        if (j(5)) {
            if (str != null && str.length() > 4000) {
                Iterator it = new dL(f875a, str).iterator();
                boolean z4 = true;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    if (z4) {
                        Log.w("Ads", str2);
                    } else {
                        Log.w("Ads-cont", str2);
                    }
                    z4 = false;
                }
                return;
            }
            Log.w("Ads", str);
        }
    }

    public static void h(String str, Throwable th) {
        if (j(5)) {
            Log.w("Ads", str, th);
        }
    }

    public static void i(String str, Exception exc) {
        if (j(5)) {
            if (exc != null) {
                h(a(str), exc);
            } else {
                g(a(str));
            }
        }
    }

    public static boolean j(int i4) {
        if (i4 < 5 && !Log.isLoggable("Ads", i4)) {
            return false;
        }
        return true;
    }
}
