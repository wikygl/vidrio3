package F;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class d extends Drawable implements Drawable.Callback, c, b {

    /* renamed from: p  reason: collision with root package name */
    public static final PorterDuff.Mode f886p = PorterDuff.Mode.SRC_IN;

    /* renamed from: j  reason: collision with root package name */
    public int f887j;

    /* renamed from: k  reason: collision with root package name */
    public PorterDuff.Mode f888k;

    /* renamed from: l  reason: collision with root package name */
    public boolean f889l;

    /* renamed from: m  reason: collision with root package name */
    public f f890m;

    /* renamed from: n  reason: collision with root package name */
    public boolean f891n;

    /* renamed from: o  reason: collision with root package name */
    public Drawable f892o;

    @Override // F.c
    public final void a(Drawable drawable) {
        Drawable drawable2 = this.f892o;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.f892o = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            setVisible(drawable.isVisible(), true);
            setState(drawable.getState());
            setLevel(drawable.getLevel());
            setBounds(drawable.getBounds());
            f fVar = this.f890m;
            if (fVar != null) {
                fVar.f895b = drawable.getConstantState();
            }
        }
        invalidateSelf();
    }

    @Override // F.c
    public final Drawable b() {
        return this.f892o;
    }

    public boolean c() {
        throw null;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.graphics.drawable.Drawable$ConstantState, F.f] */
    public final f d() {
        f fVar = this.f890m;
        ?? constantState = new Drawable.ConstantState();
        constantState.f896c = null;
        constantState.f897d = f886p;
        if (fVar != null) {
            constantState.f894a = fVar.f894a;
            constantState.f895b = fVar.f895b;
            constantState.f896c = fVar.f896c;
            constantState.f897d = fVar.f897d;
        }
        return constantState;
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        this.f892o.draw(canvas);
    }

    public final boolean e(int[] iArr) {
        if (!c()) {
            return false;
        }
        f fVar = this.f890m;
        ColorStateList colorStateList = fVar.f896c;
        PorterDuff.Mode mode = fVar.f897d;
        if (colorStateList != null && mode != null) {
            int colorForState = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
            if (!this.f889l || colorForState != this.f887j || mode != this.f888k) {
                setColorFilter(colorForState, mode);
                this.f887j = colorForState;
                this.f888k = mode;
                this.f889l = true;
                return true;
            }
        } else {
            this.f889l = false;
            clearColorFilter();
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getChangingConfigurations() {
        int i4;
        int changingConfigurations = super.getChangingConfigurations();
        f fVar = this.f890m;
        if (fVar != null) {
            i4 = fVar.getChangingConfigurations();
        } else {
            i4 = 0;
        }
        return changingConfigurations | i4 | this.f892o.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        f fVar = this.f890m;
        if (fVar != null && fVar.f895b != null) {
            fVar.f894a = getChangingConfigurations();
            return this.f890m;
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable getCurrent() {
        return this.f892o.getCurrent();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicHeight() {
        return this.f892o.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getIntrinsicWidth() {
        return this.f892o.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getLayoutDirection() {
        return a.b(this.f892o);
    }

    @Override // android.graphics.drawable.Drawable
    public final int getMinimumHeight() {
        return this.f892o.getMinimumHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getMinimumWidth() {
        return this.f892o.getMinimumWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return this.f892o.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean getPadding(Rect rect) {
        return this.f892o.getPadding(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public final int[] getState() {
        return this.f892o.getState();
    }

    @Override // android.graphics.drawable.Drawable
    public final Region getTransparentRegion() {
        return this.f892o.getTransparentRegion();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public final void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isAutoMirrored() {
        return this.f892o.isAutoMirrored();
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        ColorStateList colorStateList;
        f fVar;
        if (c() && (fVar = this.f890m) != null) {
            colorStateList = fVar.f896c;
        } else {
            colorStateList = null;
        }
        if ((colorStateList != null && colorStateList.isStateful()) || this.f892o.isStateful()) {
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public final void jumpToCurrentState() {
        this.f892o.jumpToCurrentState();
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable mutate() {
        Drawable.ConstantState constantState;
        if (!this.f891n && super.mutate() == this) {
            this.f890m = d();
            Drawable drawable = this.f892o;
            if (drawable != null) {
                drawable.mutate();
            }
            f fVar = this.f890m;
            if (fVar != null) {
                Drawable drawable2 = this.f892o;
                if (drawable2 != null) {
                    constantState = drawable2.getConstantState();
                } else {
                    constantState = null;
                }
                fVar.f895b = constantState;
            }
            this.f891n = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public final void onBoundsChange(Rect rect) {
        Drawable drawable = this.f892o;
        if (drawable != null) {
            drawable.setBounds(rect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean onLayoutDirectionChanged(int i4) {
        return a.c(this.f892o, i4);
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean onLevelChange(int i4) {
        return this.f892o.setLevel(i4);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public final void scheduleDrawable(Drawable drawable, Runnable runnable, long j4) {
        scheduleSelf(runnable, j4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i4) {
        this.f892o.setAlpha(i4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAutoMirrored(boolean z4) {
        this.f892o.setAutoMirrored(z4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setChangingConfigurations(int i4) {
        this.f892o.setChangingConfigurations(i4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        this.f892o.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setDither(boolean z4) {
        this.f892o.setDither(z4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setFilterBitmap(boolean z4) {
        this.f892o.setFilterBitmap(z4);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setState(int[] iArr) {
        boolean state = this.f892o.setState(iArr);
        if (!e(iArr) && !state) {
            return false;
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setTint(int i4) {
        setTintList(ColorStateList.valueOf(i4));
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList colorStateList) {
        this.f890m.f896c = colorStateList;
        e(this.f892o.getState());
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintMode(PorterDuff.Mode mode) {
        this.f890m.f897d = mode;
        e(this.f892o.getState());
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean setVisible(boolean z4, boolean z5) {
        if (!super.setVisible(z4, z5) && !this.f892o.setVisible(z4, z5)) {
            return false;
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public final void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        unscheduleSelf(runnable);
    }
}
