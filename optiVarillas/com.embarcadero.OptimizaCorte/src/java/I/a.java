package I;

import android.os.Build;
import android.os.ext.SdkExtensions;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a {

    /* renamed from: I.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class C0008a {

        /* renamed from: a  reason: collision with root package name */
        public static final C0008a f1129a = new Object();

        public final int a(int i4) {
            return SdkExtensions.getExtensionVersion(i4);
        }
    }

    static {
        int i4 = Build.VERSION.SDK_INT;
        C0008a c0008a = C0008a.f1129a;
        if (i4 >= 30) {
            c0008a.a(30);
        }
        if (i4 >= 30) {
            c0008a.a(31);
        }
        if (i4 >= 30) {
            c0008a.a(33);
        }
        if (i4 >= 30) {
            c0008a.a(1000000);
        }
    }

    public static final boolean a() {
        int i4 = Build.VERSION.SDK_INT;
        if (i4 < 31) {
            if (i4 >= 30) {
                String str = Build.VERSION.CODENAME;
                v3.h.d(str, "CODENAME");
                if (!"REL".equals(str)) {
                    Locale locale = Locale.ROOT;
                    String upperCase = str.toUpperCase(locale);
                    v3.h.d(upperCase, "this as java.lang.String).toUpperCase(Locale.ROOT)");
                    String upperCase2 = "S".toUpperCase(locale);
                    v3.h.d(upperCase2, "this as java.lang.String).toUpperCase(Locale.ROOT)");
                    if (upperCase.compareTo(upperCase2) >= 0) {
                    }
                }
            }
            return false;
        }
        return true;
    }
}
