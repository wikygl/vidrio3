package q;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class b extends Drawable {

    /* renamed from: a  reason: collision with root package name */
    public float f5587a;

    /* renamed from: b  reason: collision with root package name */
    public final Paint f5588b;

    /* renamed from: c  reason: collision with root package name */
    public final RectF f5589c;

    /* renamed from: d  reason: collision with root package name */
    public final Rect f5590d;

    /* renamed from: e  reason: collision with root package name */
    public float f5591e;

    /* renamed from: h  reason: collision with root package name */
    public ColorStateList f5593h;

    /* renamed from: i  reason: collision with root package name */
    public PorterDuffColorFilter f5594i;

    /* renamed from: j  reason: collision with root package name */
    public ColorStateList f5595j;
    public boolean f = false;

    /* renamed from: g  reason: collision with root package name */
    public boolean f5592g = true;

    /* renamed from: k  reason: collision with root package name */
    public PorterDuff.Mode f5596k = PorterDuff.Mode.SRC_IN;

    public b(ColorStateList colorStateList, float f) {
        this.f5587a = f;
        Paint paint = new Paint(5);
        this.f5588b = paint;
        colorStateList = colorStateList == null ? ColorStateList.valueOf(0) : colorStateList;
        this.f5593h = colorStateList;
        paint.setColor(colorStateList.getColorForState(getState(), this.f5593h.getDefaultColor()));
        this.f5589c = new RectF();
        this.f5590d = new Rect();
    }

    public final PorterDuffColorFilter a(ColorStateList colorStateList, PorterDuff.Mode mode) {
        if (colorStateList != null && mode != null) {
            return new PorterDuffColorFilter(colorStateList.getColorForState(getState(), 0), mode);
        }
        return null;
    }

    public final void b(Rect rect) {
        if (rect == null) {
            rect = getBounds();
        }
        RectF rectF = this.f5589c;
        rectF.set(rect.left, rect.top, rect.right, rect.bottom);
        Rect rect2 = this.f5590d;
        rect2.set(rect);
        if (this.f) {
            rect2.inset((int) Math.ceil(c.a(this.f5591e, this.f5587a, this.f5592g)), (int) Math.ceil(c.b(this.f5591e, this.f5587a, this.f5592g)));
            rectF.set(rect2);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        boolean z4;
        Paint paint = this.f5588b;
        if (this.f5594i != null && paint.getColorFilter() == null) {
            paint.setColorFilter(this.f5594i);
            z4 = true;
        } else {
            z4 = false;
        }
        RectF rectF = this.f5589c;
        float f = this.f5587a;
        canvas.drawRoundRect(rectF, f, f, paint);
        if (z4) {
            paint.setColorFilter(null);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public final void getOutline(Outline outline) {
        outline.setRoundRect(this.f5590d, this.f5587a);
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        ColorStateList colorStateList;
        ColorStateList colorStateList2 = this.f5595j;
        if ((colorStateList2 != null && colorStateList2.isStateful()) || (((colorStateList = this.f5593h) != null && colorStateList.isStateful()) || super.isStateful())) {
            return true;
        }
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public final void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        b(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean onStateChange(int[] iArr) {
        boolean z4;
        PorterDuff.Mode mode;
        ColorStateList colorStateList = this.f5593h;
        int colorForState = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
        Paint paint = this.f5588b;
        if (colorForState != paint.getColor()) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (z4) {
            paint.setColor(colorForState);
        }
        ColorStateList colorStateList2 = this.f5595j;
        if (colorStateList2 != null && (mode = this.f5596k) != null) {
            this.f5594i = a(colorStateList2, mode);
            return true;
        }
        return z4;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i4) {
        this.f5588b.setAlpha(i4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        this.f5588b.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintList(ColorStateList colorStateList) {
        this.f5595j = colorStateList;
        this.f5594i = a(colorStateList, this.f5596k);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintMode(PorterDuff.Mode mode) {
        this.f5596k = mode;
        this.f5594i = a(this.f5595j, mode);
        invalidateSelf();
    }
}
