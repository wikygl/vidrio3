package m2;

import android.content.Context;
import android.content.res.Resources;

/* renamed from: m2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0737a {
    public static String a(Context context) {
        try {
            return context.getResources().getResourcePackageName(2131820628);
        } catch (Resources.NotFoundException unused) {
            return context.getPackageName();
        }
    }
}
