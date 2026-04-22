package M2;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public static final int[] f1733a = {16842919};

    /* renamed from: b  reason: collision with root package name */
    public static final int[] f1734b = {16842908};

    /* renamed from: c  reason: collision with root package name */
    public static final int[] f1735c = {16842913, 16842919};

    /* renamed from: d  reason: collision with root package name */
    public static final int[] f1736d = {16842913};

    /* renamed from: e  reason: collision with root package name */
    public static final int[] f1737e = {16842910, 16842919};
    public static final String f = b.class.getSimpleName();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {
        /* JADX INFO: Access modifiers changed from: private */
        public static Drawable b(Context context, int i4) {
            ColorStateList colorStateList;
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(-1);
            gradientDrawable.setShape(1);
            InsetDrawable insetDrawable = new InsetDrawable((Drawable) gradientDrawable, i4, i4, i4, i4);
            ColorStateList valueOf = ColorStateList.valueOf(0);
            TypedValue a4 = L2.b.a(context, 2130903270);
            if (a4 != null) {
                int i5 = a4.resourceId;
                if (i5 != 0) {
                    colorStateList = C.a.c(context, i5);
                } else {
                    colorStateList = ColorStateList.valueOf(a4.data);
                }
            } else {
                colorStateList = null;
            }
            if (colorStateList != null) {
                valueOf = colorStateList;
            }
            return new RippleDrawable(valueOf, null, insetDrawable);
        }
    }

    public static int a(ColorStateList colorStateList, int[] iArr) {
        int i4;
        if (colorStateList != null) {
            i4 = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
        } else {
            i4 = 0;
        }
        return E.a.d(i4, Math.min(Color.alpha(i4) * 2, 255));
    }

    public static ColorStateList b(ColorStateList colorStateList) {
        if (colorStateList != null) {
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 22 && i4 <= 27 && Color.alpha(colorStateList.getDefaultColor()) == 0 && Color.alpha(colorStateList.getColorForState(f1737e, 0)) != 0) {
                Log.w(f, "Use a non-transparent color for the default color as it will be used to finish ripple animations.");
            }
            return colorStateList;
        }
        return ColorStateList.valueOf(0);
    }

    public static boolean c(int[] iArr) {
        boolean z4 = false;
        boolean z5 = false;
        for (int i4 : iArr) {
            if (i4 == 16842910) {
                z4 = true;
            } else if (i4 == 16842908 || i4 == 16842919 || i4 == 16843623) {
                z5 = true;
            }
        }
        if (!z4 || !z5) {
            return false;
        }
        return true;
    }
}
