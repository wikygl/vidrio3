package I;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class i implements j {

    /* renamed from: c  reason: collision with root package name */
    public static final Locale[] f1137c = new Locale[0];

    /* renamed from: a  reason: collision with root package name */
    public final Locale[] f1138a;

    /* renamed from: b  reason: collision with root package name */
    public final String f1139b;

    static {
        new Locale("en", "XA");
        new Locale("ar", "XB");
        String[] split = "en-Latn".split("-", -1);
        if (split.length > 2) {
            new Locale(split[0], split[1], split[2]);
        } else if (split.length > 1) {
            new Locale(split[0], split[1]);
        } else if (split.length == 1) {
            new Locale(split[0]);
        } else {
            throw new IllegalArgumentException("Can not parse language tag: [en-Latn]");
        }
    }

    public i(Locale... localeArr) {
        if (localeArr.length == 0) {
            this.f1138a = f1137c;
            this.f1139b = "";
            return;
        }
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        StringBuilder sb = new StringBuilder();
        for (int i4 = 0; i4 < localeArr.length; i4++) {
            Locale locale = localeArr[i4];
            if (locale != null) {
                if (!hashSet.contains(locale)) {
                    Locale locale2 = (Locale) locale.clone();
                    arrayList.add(locale2);
                    sb.append(locale2.getLanguage());
                    String country = locale2.getCountry();
                    if (country != null && !country.isEmpty()) {
                        sb.append('-');
                        sb.append(locale2.getCountry());
                    }
                    if (i4 < localeArr.length - 1) {
                        sb.append(',');
                    }
                    hashSet.add(locale2);
                }
            } else {
                throw new NullPointerException(h.b(i4, "list[", "] is null"));
            }
        }
        this.f1138a = (Locale[]) arrayList.toArray(new Locale[0]);
        this.f1139b = sb.toString();
    }

    @Override // I.j
    public final String a() {
        return this.f1139b;
    }

    @Override // I.j
    public final Object b() {
        return null;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof i)) {
            return false;
        }
        Locale[] localeArr = ((i) obj).f1138a;
        Locale[] localeArr2 = this.f1138a;
        if (localeArr2.length != localeArr.length) {
            return false;
        }
        for (int i4 = 0; i4 < localeArr2.length; i4++) {
            if (!localeArr2[i4].equals(localeArr[i4])) {
                return false;
            }
        }
        return true;
    }

    @Override // I.j
    public final Locale get(int i4) {
        if (i4 >= 0) {
            Locale[] localeArr = this.f1138a;
            if (i4 < localeArr.length) {
                return localeArr[i4];
            }
        }
        return null;
    }

    public final int hashCode() {
        int i4 = 1;
        for (Locale locale : this.f1138a) {
            i4 = (i4 * 31) + locale.hashCode();
        }
        return i4;
    }

    @Override // I.j
    public final boolean isEmpty() {
        if (this.f1138a.length == 0) {
            return true;
        }
        return false;
    }

    @Override // I.j
    public final int size() {
        return this.f1138a.length;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("[");
        int i4 = 0;
        while (true) {
            Locale[] localeArr = this.f1138a;
            if (i4 < localeArr.length) {
                sb.append(localeArr[i4]);
                if (i4 < localeArr.length - 1) {
                    sb.append(',');
                }
                i4++;
            } else {
                sb.append("]");
                return sb.toString();
            }
        }
    }
}
