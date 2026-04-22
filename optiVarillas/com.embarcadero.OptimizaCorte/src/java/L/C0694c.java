package l;

import A1.C0117l0;
import A1.P0;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AutoCompleteTextView;

/* renamed from: l.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class C0694c extends AutoCompleteTextView implements S.j {

    /* renamed from: m  reason: collision with root package name */
    public static final int[] f5106m = {16843126};

    /* renamed from: j  reason: collision with root package name */
    public final C0695d f5107j;

    /* renamed from: k  reason: collision with root package name */
    public final C0688A f5108k;

    /* renamed from: l  reason: collision with root package name */
    public final C0117l0 f5109l;

    public C0694c(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    @Override // android.widget.TextView, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0695d c0695d = this.f5107j;
        if (c0695d != null) {
            c0695d.a();
        }
        C0688A c0688a = this.f5108k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        return S.g.g(super.getCustomSelectionActionModeCallback());
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f5107j;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f5107j;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f5108k.d();
    }

    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f5108k.e();
    }

    @Override // android.widget.TextView, android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        P0.c(onCreateInputConnection, editorInfo, this);
        return this.f5109l.k(onCreateInputConnection, editorInfo);
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f5107j;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f5107j;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f5108k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f5108k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        super.setCustomSelectionActionModeCallback(S.g.h(callback, this));
    }

    @Override // android.widget.AutoCompleteTextView
    public void setDropDownBackgroundResource(int i4) {
        setDropDownBackgroundDrawable(B2.a.f(getContext(), i4));
    }

    public void setEmojiCompatEnabled(boolean z4) {
        this.f5109l.l(z4);
    }

    @Override // android.widget.TextView
    public void setKeyListener(KeyListener keyListener) {
        super.setKeyListener(this.f5109l.i(keyListener));
    }

    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        C0695d c0695d = this.f5107j;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f5107j;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintList(ColorStateList colorStateList) {
        C0688A c0688a = this.f5108k;
        c0688a.l(colorStateList);
        c0688a.b();
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintMode(PorterDuff.Mode mode) {
        C0688A c0688a = this.f5108k;
        c0688a.m(mode);
        c0688a.b();
    }

    @Override // android.widget.TextView
    public final void setTextAppearance(Context context, int i4) {
        super.setTextAppearance(context, i4);
        C0688A c0688a = this.f5108k;
        if (c0688a != null) {
            c0688a.g(context, i4);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0694c(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, 2130903101);
        Y.a(context);
        W.a(getContext(), this);
        b0 e4 = b0.e(getContext(), attributeSet, f5106m, 2130903101, 0);
        if (e4.f5104b.hasValue(0)) {
            setDropDownBackgroundDrawable(e4.b(0));
        }
        e4.f();
        C0695d c0695d = new C0695d(this);
        this.f5107j = c0695d;
        c0695d.d(attributeSet, 2130903101);
        C0688A c0688a = new C0688A(this);
        this.f5108k = c0688a;
        c0688a.f(attributeSet, 2130903101);
        c0688a.b();
        C0117l0 c0117l0 = new C0117l0(this);
        this.f5109l = c0117l0;
        c0117l0.j(attributeSet, 2130903101);
        KeyListener keyListener = getKeyListener();
        if (!(keyListener instanceof NumberKeyListener)) {
            boolean isFocusable = super.isFocusable();
            boolean isClickable = super.isClickable();
            boolean isLongClickable = super.isLongClickable();
            int inputType = super.getInputType();
            KeyListener i5 = c0117l0.i(keyListener);
            if (i5 == keyListener) {
                return;
            }
            super.setKeyListener(i5);
            super.setRawInputType(inputType);
            super.setFocusable(isFocusable);
            super.setClickable(isClickable);
            super.setLongClickable(isLongClickable);
        }
    }
}
