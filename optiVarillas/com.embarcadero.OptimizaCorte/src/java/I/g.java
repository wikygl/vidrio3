package I;

import android.os.Build;
import android.os.LocaleList;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class g {

    /* renamed from: b  reason: collision with root package name */
    public static final g f1134b = a(new Locale[0]);

    /* renamed from: a  reason: collision with root package name */
    public final j f1135a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public static final Locale[] f1136a = {new Locale("en", "XA"), new Locale("ar", "XB")};

        public static Locale a(String str) {
            return Locale.forLanguageTag(str);
        }

        public static boolean b(Locale locale, Locale locale2) {
            if (locale.equals(locale2)) {
                return true;
            }
            if (!locale.getLanguage().equals(locale2.getLanguage())) {
                return false;
            }
            Locale[] localeArr = f1136a;
            int length = localeArr.length;
            int i4 = 0;
            while (true) {
                if (i4 < length) {
                    if (localeArr[i4].equals(locale)) {
                        break;
                    }
                    i4++;
                } else {
                    for (Locale locale3 : localeArr) {
                        if (!locale3.equals(locale2)) {
                        }
                    }
                    String a4 = K.b.a(locale);
                    if (a4.isEmpty()) {
                        String country = locale.getCountry();
                        if (country.isEmpty() || country.equals(locale2.getCountry())) {
                            return true;
                        }
                        return false;
                    }
                    return a4.equals(K.b.a(locale2));
                }
            }
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class b {
        public static LocaleList a(Locale... localeArr) {
            return new LocaleList(localeArr);
        }

        public static LocaleList b() {
            return LocaleList.getAdjustedDefault();
        }

        public static LocaleList c() {
            return LocaleList.getDefault();
        }
    }

    public g(j jVar) {
        this.f1135a = jVar;
    }

    public static g a(Locale... localeArr) {
        if (Build.VERSION.SDK_INT >= 24) {
            return new g(new k(b.a(localeArr)));
        }
        return new g(new i(localeArr));
    }

    public static g b(String str) {
        if (str != null && !str.isEmpty()) {
            String[] split = str.split(",", -1);
            int length = split.length;
            Locale[] localeArr = new Locale[length];
            for (int i4 = 0; i4 < length; i4++) {
                localeArr[i4] = a.a(split[i4]);
            }
            return a(localeArr);
        }
        return f1134b;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof g) {
            if (this.f1135a.equals(((g) obj).f1135a)) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return this.f1135a.hashCode();
    }

    public final String toString() {
        return this.f1135a.toString();
    }
}
