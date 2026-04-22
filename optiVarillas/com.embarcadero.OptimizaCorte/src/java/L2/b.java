package L2;

import android.content.Context;
import android.util.TypedValue;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class b {
    public static TypedValue a(Context context, int i4) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(i4, typedValue, true)) {
            return typedValue;
        }
        return null;
    }

    public static boolean b(Context context, int i4, boolean z4) {
        TypedValue a4 = a(context, i4);
        if (a4 != null && a4.type == 18) {
            if (a4.data != 0) {
                return true;
            }
            return false;
        }
        return z4;
    }

    public static TypedValue c(int i4, Context context, String str) {
        TypedValue a4 = a(context, i4);
        if (a4 != null) {
            return a4;
        }
        throw new IllegalArgumentException(String.format("%1$s requires a value for the %2$s attribute to be set in your app theme. You can either set the attribute in your theme or update your theme to inherit from Theme.MaterialComponents (or a descendant).", str, context.getResources().getResourceName(i4)));
    }
}
