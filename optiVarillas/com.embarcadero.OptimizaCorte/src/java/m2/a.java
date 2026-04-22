package M2;

import P2.f;
import P2.i;
import P2.m;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class a extends Drawable implements m, F.b {

    /* renamed from: j  reason: collision with root package name */
    public C0016a f1730j;

    /* renamed from: M2.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static final class C0016a extends Drawable.ConstantState {

        /* renamed from: a  reason: collision with root package name */
        public f f1731a;

        /* renamed from: b  reason: collision with root package name */
        public boolean f1732b;

        public C0016a(C0016a c0016a) {
            this.f1731a = (f) c0016a.f1731a.f1820j.newDrawable();
            this.f1732b = c0016a.f1732b;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final int getChangingConfigurations() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return new a(new C0016a(this));
        }
    }

    public a(C0016a c0016a) {
        this.f1730j = c0016a;
    }

    @Override // android.graphics.drawable.Drawable
    public final void draw(Canvas canvas) {
        C0016a c0016a = this.f1730j;
        if (c0016a.f1732b) {
            c0016a.f1731a.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable.ConstantState getConstantState() {
        return this.f1730j;
    }

    @Override // android.graphics.drawable.Drawable
    public final int getOpacity() {
        this.f1730j.f1731a.getClass();
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public final Drawable mutate() {
        this.f1730j = new C0016a(this.f1730j);
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public final void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.f1730j.f1731a.setBounds(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public final boolean onStateChange(int[] iArr) {
        boolean onStateChange = super.onStateChange(iArr);
        if (this.f1730j.f1731a.setState(iArr)) {
            onStateChange = true;
        }
        boolean c4 = b.c(iArr);
        C0016a c0016a = this.f1730j;
        if (c0016a.f1732b != c4) {
            c0016a.f1732b = c4;
            return true;
        }
        return onStateChange;
    }

    @Override // android.graphics.drawable.Drawable
    public final void setAlpha(int i4) {
        this.f1730j.f1731a.setAlpha(i4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setColorFilter(ColorFilter colorFilter) {
        this.f1730j.f1731a.setColorFilter(colorFilter);
    }

    @Override // P2.m
    public final void setShapeAppearanceModel(i iVar) {
        this.f1730j.f1731a.setShapeAppearanceModel(iVar);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTint(int i4) {
        this.f1730j.f1731a.setTint(i4);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintList(ColorStateList colorStateList) {
        this.f1730j.f1731a.setTintList(colorStateList);
    }

    @Override // android.graphics.drawable.Drawable
    public final void setTintMode(PorterDuff.Mode mode) {
        this.f1730j.f1731a.setTintMode(mode);
    }
}
