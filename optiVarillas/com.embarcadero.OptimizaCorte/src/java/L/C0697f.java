package l;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.CheckBox;

/* renamed from: l.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class C0697f extends CheckBox implements S.i, S.j {

    /* renamed from: j  reason: collision with root package name */
    public final C0700i f5121j;

    /* renamed from: k  reason: collision with root package name */
    public final C0695d f5122k;

    /* renamed from: l  reason: collision with root package name */
    public final C0688A f5123l;

    /* renamed from: m  reason: collision with root package name */
    public C0703l f5124m;

    public C0697f(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 2130903205);
    }

    private C0703l getEmojiTextViewHelper() {
        if (this.f5124m == null) {
            this.f5124m = new C0703l(this);
        }
        return this.f5124m;
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        C0695d c0695d = this.f5122k;
        if (c0695d != null) {
            c0695d.a();
        }
        C0688A c0688a = this.f5123l;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f5122k;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f5122k;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    @Override // S.i
    public ColorStateList getSupportButtonTintList() {
        C0700i c0700i = this.f5121j;
        if (c0700i != null) {
            return c0700i.f5154b;
        }
        return null;
    }

    public PorterDuff.Mode getSupportButtonTintMode() {
        C0700i c0700i = this.f5121j;
        if (c0700i != null) {
            return c0700i.f5155c;
        }
        return null;
    }

    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f5123l.d();
    }

    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f5123l.e();
    }

    @Override // android.widget.TextView
    public void setAllCaps(boolean z4) {
        super.setAllCaps(z4);
        getEmojiTextViewHelper().c(z4);
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f5122k;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f5122k;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.CompoundButton
    public void setButtonDrawable(Drawable drawable) {
        super.setButtonDrawable(drawable);
        C0700i c0700i = this.f5121j;
        if (c0700i != null) {
            if (c0700i.f) {
                c0700i.f = false;
                return;
            }
            c0700i.f = true;
            c0700i.a();
        }
    }

    @Override // android.widget.TextView
    public void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f5123l;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f5123l;
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
        C0695d c0695d = this.f5122k;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f5122k;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    @Override // S.i
    public void setSupportButtonTintList(ColorStateList colorStateList) {
        C0700i c0700i = this.f5121j;
        if (c0700i != null) {
            c0700i.f5154b = colorStateList;
            c0700i.f5156d = true;
            c0700i.a();
        }
    }

    @Override // S.i
    public void setSupportButtonTintMode(PorterDuff.Mode mode) {
        C0700i c0700i = this.f5121j;
        if (c0700i != null) {
            c0700i.f5155c = mode;
            c0700i.f5157e = true;
            c0700i.a();
        }
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintList(ColorStateList colorStateList) {
        C0688A c0688a = this.f5123l;
        c0688a.l(colorStateList);
        c0688a.b();
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintMode(PorterDuff.Mode mode) {
        C0688A c0688a = this.f5123l;
        c0688a.m(mode);
        c0688a.b();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0697f(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, i4);
        Y.a(context);
        W.a(getContext(), this);
        C0700i c0700i = new C0700i(this);
        this.f5121j = c0700i;
        c0700i.b(attributeSet, i4);
        C0695d c0695d = new C0695d(this);
        this.f5122k = c0695d;
        c0695d.d(attributeSet, i4);
        C0688A c0688a = new C0688A(this);
        this.f5123l = c0688a;
        c0688a.f(attributeSet, i4);
        getEmojiTextViewHelper().b(attributeSet, i4);
    }

    @Override // android.widget.CompoundButton
    public void setButtonDrawable(int i4) {
        setButtonDrawable(B2.a.f(getContext(), i4));
    }
}
