package O1;

import A1.r;
import E1.c;
import M0.q;
import W1.C0324l;
import android.app.Activity;
import android.content.Context;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Vw;
import com.google.android.gms.internal.ads.pc;
import com.google.android.gms.internal.ads.vj;
import t1.C0802d;
import t1.C0811m;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class a {
    public static void b(Context context, String str, C0802d c0802d, Vw vw) {
        C0324l.e(context, "Context cannot be null.");
        C0324l.e(str, "AdUnitId cannot be null.");
        C0324l.b("#008 Must be called on the main UI thread.");
        Gb.a(context);
        if (((Boolean) pc.k.e()).booleanValue()) {
            if (((Boolean) r.f168d.f171c.a(Gb.T9)).booleanValue()) {
                c.f852b.execute(new q(context, str, c0802d, vw, 1));
                return;
            }
        }
        new vj(context, str).d(c0802d.f5787a, vw);
    }

    public abstract C0811m a();

    public abstract void c(Activity activity);
}
