package B;

import android.app.Person;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Icon;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import j$.util.Objects;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class z {

    /* renamed from: a  reason: collision with root package name */
    public CharSequence f278a;

    /* renamed from: b  reason: collision with root package name */
    public IconCompat f279b;

    /* renamed from: c  reason: collision with root package name */
    public String f280c;

    /* renamed from: d  reason: collision with root package name */
    public String f281d;

    /* renamed from: e  reason: collision with root package name */
    public boolean f282e;
    public boolean f;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        /* JADX WARN: Type inference failed for: r5v0, types: [java.lang.Object, B.z] */
        public static z a(Person person) {
            IconCompat iconCompat;
            CharSequence name = person.getName();
            IconCompat iconCompat2 = null;
            if (person.getIcon() != null) {
                Icon icon = person.getIcon();
                PorterDuff.Mode mode = IconCompat.k;
                icon.getClass();
                int c4 = IconCompat.a.c(icon);
                if (c4 != 2) {
                    if (c4 != 4) {
                        if (c4 != 6) {
                            iconCompat2 = new IconCompat(-1);
                            iconCompat2.b = icon;
                        } else {
                            Uri d4 = IconCompat.a.d(icon);
                            d4.getClass();
                            String uri = d4.toString();
                            uri.getClass();
                            iconCompat = new IconCompat(6);
                            iconCompat.b = uri;
                        }
                    } else {
                        Uri d5 = IconCompat.a.d(icon);
                        d5.getClass();
                        String uri2 = d5.toString();
                        uri2.getClass();
                        iconCompat = new IconCompat(4);
                        iconCompat.b = uri2;
                    }
                    iconCompat2 = iconCompat;
                } else {
                    iconCompat2 = IconCompat.b((Resources) null, IconCompat.a.b(icon), IconCompat.a.a(icon));
                }
            }
            String uri3 = person.getUri();
            String key = person.getKey();
            boolean isBot = person.isBot();
            boolean isImportant = person.isImportant();
            ?? obj = new Object();
            obj.f278a = name;
            obj.f279b = iconCompat2;
            obj.f280c = uri3;
            obj.f281d = key;
            obj.f282e = isBot;
            obj.f = isImportant;
            return obj;
        }

        public static Person b(z zVar) {
            Person.Builder name = new Person.Builder().setName(zVar.f278a);
            Icon icon = null;
            IconCompat iconCompat = zVar.f279b;
            if (iconCompat != null) {
                icon = iconCompat.g((Context) null);
            }
            return name.setIcon(icon).setUri(zVar.f280c).setKey(zVar.f281d).setBot(zVar.f282e).setImportant(zVar.f).build();
        }
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof z)) {
            return false;
        }
        z zVar = (z) obj;
        String str = this.f281d;
        String str2 = zVar.f281d;
        if (str == null && str2 == null) {
            if (!Objects.equals(Objects.toString(this.f278a), Objects.toString(zVar.f278a)) || !Objects.equals(this.f280c, zVar.f280c) || !Boolean.valueOf(this.f282e).equals(Boolean.valueOf(zVar.f282e)) || !Boolean.valueOf(this.f).equals(Boolean.valueOf(zVar.f))) {
                return false;
            }
            return true;
        }
        return Objects.equals(str, str2);
    }

    public final int hashCode() {
        String str = this.f281d;
        if (str != null) {
            return str.hashCode();
        }
        return Objects.hash(this.f278a, this.f280c, Boolean.valueOf(this.f282e), Boolean.valueOf(this.f));
    }
}
