package F;

import android.content.res.ColorStateList;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e extends d {

    /* renamed from: q  reason: collision with root package name */
    public static Method f893q;

    public static void f() {
        if (f893q == null) {
            try {
                f893q = Drawable.class.getDeclaredMethod("isProjected", null);
            } catch (Exception e4) {
                Log.w("WrappedDrawableApi21", "Failed to retrieve Drawable#isProjected() method", e4);
            }
        }
    }

    @Override // F.d
    public final boolean c() {
        if (Build.VERSION.SDK_INT != 21) {
            return false;
        }
        Drawable drawable = this.f892o;
        if (!(drawable instanceof GradientDrawable) && !(drawable instanceof DrawableContainer) && !(drawable instanceof InsetDrawable) && !(drawable instanceof RippleDrawable)) {
            return false;
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public final Rect getDirtyBounds() {
        return this.f892o.getDirtyBounds();
    }

    @Override // android.graphics.drawable.Drawable
    public final void getOutline(Outline outline) {
        this.f892o.getOutline(outline);
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isProjected() {
        Method method;
        Drawable drawable = this.f892o;
        if (drawable != null && (method = f893q) != null) {
            try {
                return ((Boolean) method.invoke(drawable, null)).booleanValue();
            } catch (Exception e4) {
                Log.w("WrappedDrawableApi21", "Error calling Drawable#isProjected() method", e4);
                return false;
            }
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setHotspot(float f, float f4) {
        this.f892o.setHotspot(f, f4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setHotspotBounds(int i4, int i5, int i6, int i7) {
        this.f892o.setHotspotBounds(i4, i5, i6, i7);
    }

    @Override // F.d, android.graphics.drawable.Drawable
    public final boolean setState(int[] iArr) {
        if (super.setState(iArr)) {
            invalidateSelf();
            return true;
        }
        return false;
    }

    @Override // F.d, android.graphics.drawable.Drawable
    public final void setTint(int i4) {
        if (c()) {
            super.setTint(i4);
        } else {
            this.f892o.setTint(i4);
        }
    }

    @Override // F.d, android.graphics.drawable.Drawable
    public final void setTintList(ColorStateList colorStateList) {
        if (c()) {
            super.setTintList(colorStateList);
        } else {
            this.f892o.setTintList(colorStateList);
        }
    }

    @Override // F.d, android.graphics.drawable.Drawable
    public final void setTintMode(PorterDuff.Mode mode) {
        if (c()) {
            super.setTintMode(mode);
        } else {
            this.f892o.setTintMode(mode);
        }
    }
}
