package I;

import android.os.LocaleList;
import java.util.Locale;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class k implements j {

    /* renamed from: a  reason: collision with root package name */
    public final LocaleList f1140a;

    public k(Object obj) {
        this.f1140a = G0.c.d(obj);
    }

    @Override // I.j
    public final String a() {
        String languageTags;
        languageTags = this.f1140a.toLanguageTags();
        return languageTags;
    }

    @Override // I.j
    public final Object b() {
        return this.f1140a;
    }

    public final boolean equals(Object obj) {
        boolean equals;
        equals = this.f1140a.equals(((j) obj).b());
        return equals;
    }

    @Override // I.j
    public final Locale get(int i4) {
        Locale locale;
        locale = this.f1140a.get(i4);
        return locale;
    }

    public final int hashCode() {
        int hashCode;
        hashCode = this.f1140a.hashCode();
        return hashCode;
    }

    @Override // I.j
    public final boolean isEmpty() {
        boolean isEmpty;
        isEmpty = this.f1140a.isEmpty();
        return isEmpty;
    }

    @Override // I.j
    public final int size() {
        int size;
        size = this.f1140a.size();
        return size;
    }

    public final String toString() {
        String localeList;
        localeList = this.f1140a.toString();
        return localeList;
    }
}
