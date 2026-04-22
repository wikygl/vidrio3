package I;

import android.content.res.Configuration;
import android.os.LocaleList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class d {
    public static LocaleList a(Configuration configuration) {
        return configuration.getLocales();
    }

    public static void b(Configuration configuration, g gVar) {
        configuration.setLocales((LocaleList) gVar.f1135a.b());
    }
}
