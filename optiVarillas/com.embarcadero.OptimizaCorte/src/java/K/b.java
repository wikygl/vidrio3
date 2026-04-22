package K;

import android.icu.util.ULocale;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public static final Method f1254a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static String a(Locale locale) {
            return locale.getScript();
        }
    }

    /* renamed from: K.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class C0011b {
        public static ULocale a(Object obj) {
            return ULocale.addLikelySubtags((ULocale) obj);
        }

        public static ULocale b(Locale locale) {
            return ULocale.forLocale(locale);
        }

        public static String c(Object obj) {
            return ((ULocale) obj).getScript();
        }
    }

    static {
        if (Build.VERSION.SDK_INT < 24) {
            try {
                f1254a = Class.forName("libcore.icu.ICU").getMethod("addLikelySubtags", Locale.class);
            } catch (Exception e4) {
                throw new IllegalStateException(e4);
            }
        }
    }

    public static String a(Locale locale) {
        if (Build.VERSION.SDK_INT >= 24) {
            return C0011b.c(C0011b.a(C0011b.b(locale)));
        }
        try {
            return a.a((Locale) f1254a.invoke(null, locale));
        } catch (IllegalAccessException e4) {
            Log.w("ICUCompat", e4);
            return a.a(locale);
        } catch (InvocationTargetException e5) {
            Log.w("ICUCompat", e5);
            return a.a(locale);
        }
    }
}
