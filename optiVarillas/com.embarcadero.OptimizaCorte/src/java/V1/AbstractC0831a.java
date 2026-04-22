package v1;

import A1.r;
import E1.c;
import G3.g;
import W1.C0324l;
import android.app.Activity;
import android.content.Context;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.Rw;
import com.google.android.gms.internal.ads.oo;
import com.google.android.gms.internal.ads.pc;
import com.google.android.gms.internal.ads.q9;
import t1.C0802d;
import t1.C0811m;

/* renamed from: v1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0831a {

    /* renamed from: v1.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static abstract class AbstractC0075a extends g {
    }

    @Deprecated
    public static void b(Context context, String str, C0802d c0802d, Rw rw) {
        C0324l.e(context, "Context cannot be null.");
        C0324l.e(str, "adUnitId cannot be null.");
        C0324l.b("#008 Must be called on the main UI thread.");
        Gb.a(context);
        if (((Boolean) pc.d.e()).booleanValue()) {
            if (((Boolean) r.f168d.f171c.a(Gb.T9)).booleanValue()) {
                c.f852b.execute(new oo(context, str, c0802d, rw));
                return;
            }
        }
        new q9(context, str, c0802d.f5787a, rw).a();
    }

    public abstract C0811m a();

    public abstract void c(Activity activity);
}
