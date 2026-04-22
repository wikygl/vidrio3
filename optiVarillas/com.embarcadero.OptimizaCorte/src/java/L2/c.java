package L2;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import l.b0;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class c {
    public static ColorStateList a(Context context, TypedArray typedArray, int i4) {
        int resourceId;
        ColorStateList c4;
        if (typedArray.hasValue(i4) && (resourceId = typedArray.getResourceId(i4, 0)) != 0 && (c4 = C.a.c(context, resourceId)) != null) {
            return c4;
        }
        return typedArray.getColorStateList(i4);
    }

    public static ColorStateList b(Context context, b0 b0Var, int i4) {
        int resourceId;
        ColorStateList c4;
        TypedArray typedArray = b0Var.f5104b;
        if (typedArray.hasValue(i4) && (resourceId = typedArray.getResourceId(i4, 0)) != 0 && (c4 = C.a.c(context, resourceId)) != null) {
            return c4;
        }
        return b0Var.a(i4);
    }

    public static Drawable c(Context context, TypedArray typedArray, int i4) {
        int resourceId;
        Drawable f;
        if (typedArray.hasValue(i4) && (resourceId = typedArray.getResourceId(i4, 0)) != 0 && (f = B2.a.f(context, resourceId)) != null) {
            return f;
        }
        return typedArray.getDrawable(i4);
    }

    public static boolean d(Context context) {
        if (context.getResources().getConfiguration().fontScale >= 1.3f) {
            return true;
        }
        return false;
    }
}
