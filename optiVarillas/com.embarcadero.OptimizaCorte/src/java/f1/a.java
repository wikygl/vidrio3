package F1;

import A1.r;
import G3.g;
import W1.C0324l;
import android.app.Activity;
import android.content.Context;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.lf;
import com.google.android.gms.internal.ads.pc;
import t1.C0802d;
import t1.C0811m;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class a {
    public static void b(Context context, String str, C0802d c0802d, b bVar) {
        C0324l.e(context, "Context cannot be null.");
        C0324l.e(str, "AdUnitId cannot be null.");
        C0324l.e(c0802d, "AdRequest cannot be null.");
        C0324l.e(bVar, "LoadCallback cannot be null.");
        C0324l.b("#008 Must be called on the main UI thread.");
        Gb.a(context);
        if (((Boolean) pc.i.e()).booleanValue()) {
            if (((Boolean) r.f168d.f171c.a(Gb.T9)).booleanValue()) {
                E1.c.f852b.execute(new c(context, str, c0802d, bVar, 0));
                return;
            }
        }
        new lf(context, str).f(c0802d.f5787a, bVar);
    }

    public abstract C0811m a();

    public abstract void c(g gVar);

    public abstract void d(boolean z4);

    public abstract void e(Activity activity);
}
