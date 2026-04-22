package l;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import d.C0376a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class W {

    /* renamed from: a  reason: collision with root package name */
    public static final ThreadLocal<TypedValue> f5080a = new ThreadLocal<>();

    /* renamed from: b  reason: collision with root package name */
    public static final int[] f5081b = {-16842910};

    /* renamed from: c  reason: collision with root package name */
    public static final int[] f5082c = {16842908};

    /* renamed from: d  reason: collision with root package name */
    public static final int[] f5083d = {16842919};

    /* renamed from: e  reason: collision with root package name */
    public static final int[] f5084e = {16842912};
    public static final int[] f = new int[0];

    /* renamed from: g  reason: collision with root package name */
    public static final int[] f5085g = new int[1];

    public static void a(Context context, View view) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(C0376a.f3137j);
        try {
            if (!obtainStyledAttributes.hasValue(117)) {
                Log.e("ThemeUtils", "View " + view.getClass() + " is an AppCompat widget that can only be used with a Theme.AppCompat theme (or descendant).");
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public static int b(Context context, int i4) {
        ColorStateList d4 = d(context, i4);
        if (d4 != null && d4.isStateful()) {
            return d4.getColorForState(f5081b, d4.getDefaultColor());
        }
        ThreadLocal<TypedValue> threadLocal = f5080a;
        TypedValue typedValue = threadLocal.get();
        if (typedValue == null) {
            typedValue = new TypedValue();
            threadLocal.set(typedValue);
        }
        context.getTheme().resolveAttribute(16842803, typedValue, true);
        float f4 = typedValue.getFloat();
        int c4 = c(context, i4);
        return E.a.d(c4, Math.round(Color.alpha(c4) * f4));
    }

    public static int c(Context context, int i4) {
        int[] iArr = f5085g;
        iArr[0] = i4;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes((AttributeSet) null, iArr);
        try {
            return obtainStyledAttributes.getColor(0, 0);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public static ColorStateList d(Context context, int i4) {
        ColorStateList colorStateList;
        int resourceId;
        int[] iArr = f5085g;
        iArr[0] = i4;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes((AttributeSet) null, iArr);
        try {
            if (!obtainStyledAttributes.hasValue(0) || (resourceId = obtainStyledAttributes.getResourceId(0, 0)) == 0 || (colorStateList = C.a.c(context, resourceId)) == null) {
                colorStateList = obtainStyledAttributes.getColorStateList(0);
            }
            return colorStateList;
        } finally {
            obtainStyledAttributes.recycle();
        }
    }
}
