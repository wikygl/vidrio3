package l;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

/* renamed from: l.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class C0696e extends Button implements S.j {

    /* renamed from: j  reason: collision with root package name */
    public final C0695d f5118j;

    /* renamed from: k  reason: collision with root package name */
    public final C0688A f5119k;

    /* renamed from: l  reason: collision with root package name */
    public C0703l f5120l;

    public C0696e(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 2130903187);
    }

    private C0703l getEmojiTextViewHelper() {
        if (this.f5120l == null) {
            this.f5120l = new C0703l(this);
        }
        return this.f5120l;
    }

    @Override // android.widget.TextView, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0695d c0695d = this.f5118j;
        if (c0695d != null) {
            c0695d.a();
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public int getAutoSizeMaxTextSize() {
        if (i0.f5160c) {
            return super.getAutoSizeMaxTextSize();
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            return Math.round(c0688a.f4938i.f4963e);
        }
        return -1;
    }

    @Override // android.widget.TextView
    public int getAutoSizeMinTextSize() {
        if (i0.f5160c) {
            return super.getAutoSizeMinTextSize();
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            return Math.round(c0688a.f4938i.f4962d);
        }
        return -1;
    }

    @Override // android.widget.TextView
    public int getAutoSizeStepGranularity() {
        if (i0.f5160c) {
            return super.getAutoSizeStepGranularity();
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            return Math.round(c0688a.f4938i.f4961c);
        }
        return -1;
    }

    @Override // android.widget.TextView
    public int[] getAutoSizeTextAvailableSizes() {
        if (i0.f5160c) {
            return super.getAutoSizeTextAvailableSizes();
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            return c0688a.f4938i.f;
        }
        return new int[0];
    }

    @Override // android.widget.TextView
    @SuppressLint({"WrongConstant"})
    public int getAutoSizeTextType() {
        if (i0.f5160c) {
            if (super.getAutoSizeTextType() != 1) {
                return 0;
            }
            return 1;
        }
        C0688A c0688a = this.f5119k;
        if (c0688a == null) {
            return 0;
        }
        return c0688a.f4938i.f4959a;
    }

    @Override // android.widget.TextView
    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        return S.g.g(super.getCustomSelectionActionModeCallback());
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f5118j;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f5118j;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f5119k.d();
    }

    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f5119k.e();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(Button.class.getName());
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(Button.class.getName());
    }

    @Override // android.widget.TextView, android.view.View
    public void onLayout(boolean z4, int i4, int i5, int i6, int i7) {
        super.onLayout(z4, i4, i5, i6, i7);
        C0688A c0688a = this.f5119k;
        if (c0688a != null && !i0.f5160c) {
            c0688a.f4938i.a();
        }
    }

    @Override // android.widget.TextView
    public void onTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
        super.onTextChanged(charSequence, i4, i5, i6);
        C0688A c0688a = this.f5119k;
        if (c0688a != null && !i0.f5160c) {
            C0690C c0690c = c0688a.f4938i;
            if (c0690c.f()) {
                c0690c.a();
            }
        }
    }

    @Override // android.widget.TextView
    public void setAllCaps(boolean z4) {
        super.setAllCaps(z4);
        getEmojiTextViewHelper().c(z4);
    }

    @Override // android.widget.TextView
    public final void setAutoSizeTextTypeUniformWithConfiguration(int i4, int i5, int i6, int i7) {
        if (i0.f5160c) {
            super.setAutoSizeTextTypeUniformWithConfiguration(i4, i5, i6, i7);
            return;
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            c0688a.i(i4, i5, i6, i7);
        }
    }

    @Override // android.widget.TextView
    public final void setAutoSizeTextTypeUniformWithPresetSizes(int[] iArr, int i4) {
        if (i0.f5160c) {
            super.setAutoSizeTextTypeUniformWithPresetSizes(iArr, i4);
            return;
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            c0688a.j(iArr, i4);
        }
    }

    @Override // android.widget.TextView
    public void setAutoSizeTextTypeWithDefaults(int i4) {
        if (i0.f5160c) {
            super.setAutoSizeTextTypeWithDefaults(i4);
            return;
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            c0688a.k(i4);
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f5118j;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f5118j;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.TextView
    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(S.g.h(callback, this));
    }

    public void setEmojiCompatEnabled(boolean z4) {
        getEmojiTextViewHelper().d(z4);
    }

    @Override // android.widget.TextView
    public void setFilters(InputFilter[] inputFilterArr) {
        super.setFilters(getEmojiTextViewHelper().a(inputFilterArr));
    }

    public void setSupportAllCaps(boolean z4) {
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            c0688a.f4931a.setAllCaps(z4);
        }
    }

    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        C0695d c0695d = this.f5118j;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f5118j;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintList(ColorStateList colorStateList) {
        C0688A c0688a = this.f5119k;
        c0688a.l(colorStateList);
        c0688a.b();
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintMode(PorterDuff.Mode mode) {
        C0688A c0688a = this.f5119k;
        c0688a.m(mode);
        c0688a.b();
    }

    @Override // android.widget.TextView
    public final void setTextAppearance(Context context, int i4) {
        super.setTextAppearance(context, i4);
        C0688A c0688a = this.f5119k;
        if (c0688a != null) {
            c0688a.g(context, i4);
        }
    }

    @Override // android.widget.TextView
    public final void setTextSize(int i4, float f) {
        boolean z4 = i0.f5160c;
        if (z4) {
            super.setTextSize(i4, f);
            return;
        }
        C0688A c0688a = this.f5119k;
        if (c0688a != null && !z4) {
            C0690C c0690c = c0688a.f4938i;
            if (!c0690c.f()) {
                c0690c.g(i4, f);
            }
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0696e(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, i4);
        Y.a(context);
        W.a(getContext(), this);
        C0695d c0695d = new C0695d(this);
        this.f5118j = c0695d;
        c0695d.d(attributeSet, i4);
        C0688A c0688a = new C0688A(this);
        this.f5119k = c0688a;
        c0688a.f(attributeSet, i4);
        c0688a.b();
        getEmojiTextViewHelper().b(attributeSet, i4);
    }
}
