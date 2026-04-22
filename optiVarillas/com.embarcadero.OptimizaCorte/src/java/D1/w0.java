package D1;

import A1.C0124p;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.google.android.gms.internal.ads.Eb;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.vb;

@TargetApi(24)
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class w0 extends u0 {
    @Override // D1.C0178b
    public final boolean a(Activity activity, Configuration configuration) {
        int i4;
        boolean z4;
        boolean isInMultiWindowMode;
        vb vbVar = Gb.k4;
        A1.r rVar = A1.r.f168d;
        if (!((Boolean) rVar.f171c.a(vbVar)).booleanValue()) {
            return false;
        }
        vb vbVar2 = Gb.m4;
        Eb eb = rVar.f171c;
        if (((Boolean) eb.a(vbVar2)).booleanValue()) {
            isInMultiWindowMode = activity.isInMultiWindowMode();
            return isInMultiWindowMode;
        }
        E1.f fVar = C0124p.f.f161a;
        int m4 = E1.f.m(activity, configuration.screenHeightDp);
        int j4 = E1.f.j(activity.getResources().getDisplayMetrics(), configuration.screenWidthDp);
        t0 t0Var = z1.p.f6575A.f6578c;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) activity.getApplicationContext().getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        int i5 = displayMetrics.heightPixels;
        int i6 = displayMetrics.widthPixels;
        int identifier = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            i4 = activity.getResources().getDimensionPixelSize(identifier);
        } else {
            i4 = 0;
        }
        int intValue = ((Integer) eb.a(Gb.i4)).intValue() * ((int) Math.round(activity.getResources().getDisplayMetrics().density + 0.5d));
        if (Math.abs(i5 - (m4 + i4)) <= intValue) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (z4 && Math.abs(i6 - j4) <= intValue) {
            return false;
        }
        return true;
    }
}
