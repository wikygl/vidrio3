package C0;

import android.net.Uri;
import java.util.HashSet;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class d {

    /* renamed from: a  reason: collision with root package name */
    public final HashSet f311a = new HashSet();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public final Uri f312a;

        /* renamed from: b  reason: collision with root package name */
        public final boolean f313b;

        public a(Uri uri, boolean z4) {
            this.f312a = uri;
            this.f313b = z4;
        }

        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || a.class != obj.getClass()) {
                return false;
            }
            a aVar = (a) obj;
            if (this.f313b == aVar.f313b && this.f312a.equals(aVar.f312a)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return (this.f312a.hashCode() * 31) + (this.f313b ? 1 : 0);
        }
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && d.class == obj.getClass()) {
            return this.f311a.equals(((d) obj).f311a);
        }
        return false;
    }

    public final int hashCode() {
        return this.f311a.hashCode();
    }
}
