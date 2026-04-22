package l;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import l.C0688A;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class b0 {

    /* renamed from: a  reason: collision with root package name */
    public final Context f5103a;

    /* renamed from: b  reason: collision with root package name */
    public final TypedArray f5104b;

    /* renamed from: c  reason: collision with root package name */
    public TypedValue f5105c;

    public b0(Context context, TypedArray typedArray) {
        this.f5103a = context;
        this.f5104b = typedArray;
    }

    public static b0 e(Context context, AttributeSet attributeSet, int[] iArr, int i4, int i5) {
        return new b0(context, context.obtainStyledAttributes(attributeSet, iArr, i4, i5));
    }

    public final ColorStateList a(int i4) {
        int resourceId;
        ColorStateList c4;
        TypedArray typedArray = this.f5104b;
        if (typedArray.hasValue(i4) && (resourceId = typedArray.getResourceId(i4, 0)) != 0 && (c4 = C.a.c(this.f5103a, resourceId)) != null) {
            return c4;
        }
        return typedArray.getColorStateList(i4);
    }

    public final Drawable b(int i4) {
        int resourceId;
        TypedArray typedArray = this.f5104b;
        if (typedArray.hasValue(i4) && (resourceId = typedArray.getResourceId(i4, 0)) != 0) {
            return B2.a.f(this.f5103a, resourceId);
        }
        return typedArray.getDrawable(i4);
    }

    public final Drawable c(int i4) {
        int resourceId;
        Drawable g4;
        if (this.f5104b.hasValue(i4) && (resourceId = this.f5104b.getResourceId(i4, 0)) != 0) {
            C0701j a4 = C0701j.a();
            Context context = this.f5103a;
            synchronized (a4) {
                g4 = a4.f5163a.g(context, resourceId, true);
            }
            return g4;
        }
        return null;
    }

    public final Typeface d(int i4, int i5, C0688A.a aVar) {
        int resourceId = this.f5104b.getResourceId(i4, 0);
        if (resourceId == 0) {
            return null;
        }
        if (this.f5105c == null) {
            this.f5105c = new TypedValue();
        }
        TypedValue typedValue = this.f5105c;
        ThreadLocal<TypedValue> threadLocal = D.f.f527a;
        Context context = this.f5103a;
        if (context.isRestricted()) {
            return null;
        }
        return D.f.c(context, resourceId, typedValue, i5, aVar, true, false);
    }

    public final void f() {
        this.f5104b.recycle();
    }
}
