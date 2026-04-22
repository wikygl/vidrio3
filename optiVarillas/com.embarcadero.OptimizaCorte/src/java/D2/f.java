package D2;

import F.a;
import android.content.res.ColorStateList;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class a {
        public static void a(Outline outline, Path path) {
            outline.setConvexPath(path);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
    public static class b {
        public static void a(Outline outline, Path path) {
            outline.setPath(path);
        }
    }

    public static Drawable a(Drawable drawable, ColorStateList colorStateList, PorterDuff.Mode mode) {
        boolean z4;
        if (Build.VERSION.SDK_INT < 23) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (drawable == null) {
            return null;
        }
        if (colorStateList != null) {
            Drawable mutate = F.a.g(drawable).mutate();
            if (mode != null) {
                a.C0006a.i(mutate, mode);
                return mutate;
            }
            return mutate;
        } else if (z4) {
            drawable.mutate();
            return drawable;
        } else {
            return drawable;
        }
    }

    public static ColorStateList b(Drawable drawable) {
        ColorStateList colorStateList;
        if (drawable instanceof ColorDrawable) {
            return ColorStateList.valueOf(((ColorDrawable) drawable).getColor());
        }
        if (Build.VERSION.SDK_INT >= 29 && c.i(drawable)) {
            colorStateList = d.b(drawable).getColorStateList();
            return colorStateList;
        }
        return null;
    }

    public static void c(Drawable drawable, int i4) {
        boolean z4;
        if (i4 != 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (Build.VERSION.SDK_INT == 21) {
            if (z4) {
                drawable.setColorFilter(i4, PorterDuff.Mode.SRC_IN);
            } else {
                drawable.setColorFilter(null);
            }
        } else if (z4) {
            a.C0006a.g(drawable, i4);
        } else {
            a.C0006a.h(drawable, null);
        }
    }
}
