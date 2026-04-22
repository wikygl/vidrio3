package H2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import q2.C0771a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class f extends androidx.appcompat.widget.b {

    /* renamed from: A  reason: collision with root package name */
    public final Rect f1085A;

    /* renamed from: B  reason: collision with root package name */
    public int f1086B;

    /* renamed from: C  reason: collision with root package name */
    public final boolean f1087C;

    /* renamed from: D  reason: collision with root package name */
    public boolean f1088D;

    /* renamed from: y  reason: collision with root package name */
    public Drawable f1089y;

    /* renamed from: z  reason: collision with root package name */
    public final Rect f1090z;

    public f(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void draw(Canvas canvas) {
        super/*android.view.ViewGroup*/.draw(canvas);
        Drawable drawable = this.f1089y;
        if (drawable != null) {
            if (this.f1088D) {
                this.f1088D = false;
                int right = getRight() - getLeft();
                int bottom = getBottom() - getTop();
                boolean z4 = this.f1087C;
                Rect rect = this.f1090z;
                if (z4) {
                    rect.set(0, 0, right, bottom);
                } else {
                    rect.set(getPaddingLeft(), getPaddingTop(), right - getPaddingRight(), bottom - getPaddingBottom());
                }
                int i4 = this.f1086B;
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();
                Rect rect2 = this.f1085A;
                Gravity.apply(i4, intrinsicWidth, intrinsicHeight, rect, rect2);
                drawable.setBounds(rect2);
            }
            drawable.draw(canvas);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @TargetApi(21)
    public final void drawableHotspotChanged(float f, float f4) {
        super/*android.view.ViewGroup*/.drawableHotspotChanged(f, f4);
        Drawable drawable = this.f1089y;
        if (drawable != null) {
            drawable.setHotspot(f, f4);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void drawableStateChanged() {
        super/*android.view.ViewGroup*/.drawableStateChanged();
        Drawable drawable = this.f1089y;
        if (drawable != null && drawable.isStateful()) {
            this.f1089y.setState(getDrawableState());
        }
    }

    public Drawable getForeground() {
        return this.f1089y;
    }

    public int getForegroundGravity() {
        return this.f1086B;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void jumpDrawablesToCurrentState() {
        super/*android.view.ViewGroup*/.jumpDrawablesToCurrentState();
        Drawable drawable = this.f1089y;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    public final void onLayout(boolean z4, int i4, int i5, int i6, int i7) {
        super.onLayout(z4, i4, i5, i6, i7);
        this.f1088D = z4 | this.f1088D;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onSizeChanged(int i4, int i5, int i6, int i7) {
        super/*android.view.ViewGroup*/.onSizeChanged(i4, i5, i6, i7);
        this.f1088D = true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setForeground(Drawable drawable) {
        Drawable drawable2 = this.f1089y;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback(null);
                unscheduleDrawable(this.f1089y);
            }
            this.f1089y = drawable;
            this.f1088D = true;
            if (drawable != null) {
                setWillNotDraw(false);
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
                if (this.f1086B == 119) {
                    drawable.getPadding(new Rect());
                }
            } else {
                setWillNotDraw(true);
            }
            requestLayout();
            invalidate();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void setForegroundGravity(int i4) {
        if (this.f1086B != i4) {
            if ((8388615 & i4) == 0) {
                i4 |= 8388611;
            }
            if ((i4 & 112) == 0) {
                i4 |= 48;
            }
            this.f1086B = i4;
            if (i4 == 119 && this.f1089y != null) {
                this.f1089y.getPadding(new Rect());
            }
            requestLayout();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean verifyDrawable(Drawable drawable) {
        if (!super/*android.view.ViewGroup*/.verifyDrawable(drawable) && drawable != this.f1089y) {
            return false;
        }
        return true;
    }

    public f(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, 0);
        this.f1090z = new Rect();
        this.f1085A = new Rect();
        this.f1086B = 119;
        this.f1087C = true;
        this.f1088D = false;
        int[] iArr = C0771a.f5616j;
        p.a(context, attributeSet, 0, 0);
        p.b(context, attributeSet, iArr, 0, 0, new int[0]);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, iArr, 0, 0);
        this.f1086B = obtainStyledAttributes.getInt(1, this.f1086B);
        Drawable drawable = obtainStyledAttributes.getDrawable(0);
        if (drawable != null) {
            setForeground(drawable);
        }
        this.f1087C = obtainStyledAttributes.getBoolean(2, true);
        obtainStyledAttributes.recycle();
    }
}
