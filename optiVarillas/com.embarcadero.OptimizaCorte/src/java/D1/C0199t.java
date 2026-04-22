package D1;

import A1.C0124p;
import android.content.Context;
import com.google.android.gms.internal.ads.C5;
import com.google.android.gms.internal.ads.F5;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.I5;
import com.google.android.gms.internal.ads.RJ;
import com.google.android.gms.internal.ads.SJ;
import com.google.android.gms.internal.ads.Se;
import com.google.android.gms.internal.ads.X5;
import com.google.android.gms.internal.ads.c1;
import com.google.android.gms.internal.ads.zb;
import java.io.File;
import java.util.regex.Pattern;

/* renamed from: D1.t  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0199t extends c1 {

    /* renamed from: l  reason: collision with root package name */
    public final Context f773l;

    public C0199t(Context context) {
        this.f773l = context;
    }

    public static I5 i(Context context) {
        C0199t c0199t = new C0199t(context);
        synchronized (RJ.class) {
        }
        File cacheDir = context.getCacheDir();
        int i4 = SJ.a;
        I5 i5 = new I5(new X5(new File(new File(cacheDir, "admob_volley").getPath())), c0199t);
        i5.c();
        return i5;
    }

    public final C5 c(F5 f5) {
        if (f5.k == 0) {
            zb zbVar = Gb.T3;
            String str = f5.l;
            if (Pattern.matches((String) A1.r.f168d.f171c.a(zbVar), str)) {
                E1.f fVar = C0124p.f.f161a;
                T1.f fVar2 = T1.f.f2354b;
                Context context = this.f773l;
                if (fVar2.c(context, 13400000) == 0) {
                    C5 c4 = new Se(context).c(f5);
                    if (c4 != null) {
                        C0183d0.k("Got gmscore asset response: ".concat(String.valueOf(str)));
                        return c4;
                    }
                    C0183d0.k("Failed to get gmscore asset response: ".concat(String.valueOf(str)));
                }
            }
        }
        return super.c(f5);
    }
}
