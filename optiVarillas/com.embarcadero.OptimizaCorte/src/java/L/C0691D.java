package l;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/* renamed from: l.D  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0691D extends ToggleButton implements S.j {

    /* renamed from: j  reason: collision with root package name */
    public final C0695d f4969j;

    /* renamed from: k  reason: collision with root package name */
    public final C0688A f4970k;

    /* renamed from: l  reason: collision with root package name */
    public C0703l f4971l;

    public C0691D(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 16842827);
        W.a(getContext(), this);
        C0695d c0695d = new C0695d(this);
        this.f4969j = c0695d;
        c0695d.d(attributeSet, 16842827);
        C0688A c0688a = new C0688A(this);
        this.f4970k = c0688a;
        c0688a.f(attributeSet, 16842827);
        getEmojiTextViewHelper().b(attributeSet, 16842827);
    }

    private C0703l getEmojiTextViewHelper() {
        if (this.f4971l == null) {
            this.f4971l = new C0703l(this);
        }
        return this.f4971l;
    }

    @Override // android.widget.ToggleButton, android.widget.CompoundButton, android.widget.TextView, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0695d c0695d = this.f4969j;
        if (c0695d != null) {
            c0695d.a();
        }
        C0688A c0688a = this.f4970k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f4969j;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f4969j;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f4970k.d();
    }

    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f4970k.e();
    }

    @Override // android.widget.TextView
    public void setAllCaps(boolean z4) {
        super.setAllCaps(z4);
        getEmojiTextViewHelper().c(z4);
    }

    @Override // android.widget.ToggleButton, android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f4969j;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f4969j;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f4970k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f4970k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    public void setEmojiCompatEnabled(boolean z4) {
        getEmojiTextViewHelper().d(z4);
    }

    @Override // android.widget.TextView
    public void setFilters(InputFilter[] inputFilterArr) {
        super.setFilters(getEmojiTextViewHelper().a(inputFilterArr));
    }

    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        C0695d c0695d = this.f4969j;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f4969j;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintList(ColorStateList colorStateList) {
        C0688A c0688a = this.f4970k;
        c0688a.l(colorStateList);
        c0688a.b();
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintMode(PorterDuff.Mode mode) {
        C0688A c0688a = this.f4970k;
        c0688a.m(mode);
        c0688a.b();
    }
}
