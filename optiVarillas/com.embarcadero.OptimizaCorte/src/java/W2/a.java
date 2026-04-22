package W2;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import j.C0649c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a {

    /* renamed from: a  reason: collision with root package name */
    public static final int[] f2778a = {16842752, 2130904102};

    /* renamed from: b  reason: collision with root package name */
    public static final int[] f2779b = {2130903773};

    public static Context a(Context context, AttributeSet attributeSet, int i4, int i5) {
        boolean z4;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, f2779b, i4, i5);
        int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
        if ((context instanceof C0649c) && ((C0649c) context).f4651a == resourceId) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (resourceId != 0 && !z4) {
            C0649c c0649c = new C0649c(context, resourceId);
            TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, f2778a);
            int resourceId2 = obtainStyledAttributes2.getResourceId(0, 0);
            int resourceId3 = obtainStyledAttributes2.getResourceId(1, 0);
            obtainStyledAttributes2.recycle();
            if (resourceId2 == 0) {
                resourceId2 = resourceId3;
            }
            if (resourceId2 != 0) {
                c0649c.getTheme().applyStyle(resourceId2, true);
            }
            return c0649c;
        }
        return context;
    }
}
