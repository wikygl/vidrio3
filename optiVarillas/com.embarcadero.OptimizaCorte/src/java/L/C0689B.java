package l;

import A1.P0;
import K.f;
import S.g;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputFilter;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassifier;
import android.widget.TextView;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import l.C0716z;

/* renamed from: l.B  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class C0689B extends TextView implements S.j {

    /* renamed from: j  reason: collision with root package name */
    public final C0695d f4947j;

    /* renamed from: k  reason: collision with root package name */
    public final C0688A f4948k;

    /* renamed from: l  reason: collision with root package name */
    public final C0716z f4949l;

    /* renamed from: m  reason: collision with root package name */
    public C0703l f4950m;

    /* renamed from: n  reason: collision with root package name */
    public boolean f4951n;

    /* renamed from: o  reason: collision with root package name */
    public b f4952o;

    /* renamed from: p  reason: collision with root package name */
    public Future<K.f> f4953p;

    /* renamed from: l.B$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public interface a {
        void a(int i4);

        void b(int i4);

        void c(int i4, float f);
    }

    /* renamed from: l.B$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class c extends b {
        public c() {
            super();
        }

        @Override // l.C0689B.b, l.C0689B.a
        public final void a(int i4) {
            C0689B.super.setLastBaselineToBottomHeight(i4);
        }

        @Override // l.C0689B.b, l.C0689B.a
        public final void b(int i4) {
            C0689B.super.setFirstBaselineToTopHeight(i4);
        }
    }

    /* renamed from: l.B$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public class d extends c {
        public d() {
            super();
        }

        @Override // l.C0689B.b, l.C0689B.a
        public final void c(int i4, float f) {
            C0689B.super.setLineHeight(i4, f);
        }
    }

    public C0689B(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    private C0703l getEmojiTextViewHelper() {
        if (this.f4950m == null) {
            this.f4950m = new C0703l(this);
        }
        return this.f4950m;
    }

    @Override // android.widget.TextView, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0695d c0695d = this.f4947j;
        if (c0695d != null) {
            c0695d.a();
        }
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public int getAutoSizeMaxTextSize() {
        if (i0.f5160c) {
            return super.getAutoSizeMaxTextSize();
        }
        C0688A c0688a = this.f4948k;
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
        C0688A c0688a = this.f4948k;
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
        C0688A c0688a = this.f4948k;
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
        C0688A c0688a = this.f4948k;
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
        C0688A c0688a = this.f4948k;
        if (c0688a == null) {
            return 0;
        }
        return c0688a.f4938i.f4959a;
    }

    @Override // android.widget.TextView
    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        return S.g.g(super.getCustomSelectionActionModeCallback());
    }

    @Override // android.widget.TextView
    public int getFirstBaselineToTopHeight() {
        return getPaddingTop() - getPaint().getFontMetricsInt().top;
    }

    @Override // android.widget.TextView
    public int getLastBaselineToBottomHeight() {
        return getPaddingBottom() + getPaint().getFontMetricsInt().bottom;
    }

    public a getSuperCaller() {
        if (this.f4952o == null) {
            int i4 = Build.VERSION.SDK_INT;
            if (i4 >= 34) {
                this.f4952o = new d();
            } else if (i4 >= 28) {
                this.f4952o = new c();
            } else if (i4 >= 26) {
                this.f4952o = new b();
            }
        }
        return this.f4952o;
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f4947j;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f4947j;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    public ColorStateList getSupportCompoundDrawablesTintList() {
        return this.f4948k.d();
    }

    public PorterDuff.Mode getSupportCompoundDrawablesTintMode() {
        return this.f4948k.e();
    }

    @Override // android.widget.TextView
    public CharSequence getText() {
        q();
        return super.getText();
    }

    @Override // android.widget.TextView
    public TextClassifier getTextClassifier() {
        C0716z c0716z;
        if (Build.VERSION.SDK_INT < 28 && (c0716z = this.f4949l) != null) {
            TextClassifier textClassifier = c0716z.f5235b;
            if (textClassifier == null) {
                return C0716z.a.a(c0716z.f5234a);
            }
            return textClassifier;
        }
        return super.getTextClassifier();
    }

    public f.a getTextMetricsParamsCompat() {
        return S.g.a(this);
    }

    @Override // android.widget.TextView, android.view.View
    public final InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        this.f4948k.getClass();
        C0688A.h(this, onCreateInputConnection, editorInfo);
        P0.c(onCreateInputConnection, editorInfo, this);
        return onCreateInputConnection;
    }

    @Override // android.view.View
    public final void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 30 && i4 < 33 && onCheckIsTextEditor()) {
            ((InputMethodManager) getContext().getSystemService("input_method")).isActive(this);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public final void onLayout(boolean z4, int i4, int i5, int i6, int i7) {
        super.onLayout(z4, i4, i5, i6, i7);
        C0688A c0688a = this.f4948k;
        if (c0688a != null && !i0.f5160c) {
            c0688a.f4938i.a();
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int i4, int i5) {
        q();
        super.onMeasure(i4, i5);
    }

    @Override // android.widget.TextView
    public final void onTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
        super.onTextChanged(charSequence, i4, i5, i6);
        C0688A c0688a = this.f4948k;
        if (c0688a != null && !i0.f5160c) {
            C0690C c0690c = c0688a.f4938i;
            if (c0690c.f()) {
                c0690c.a();
            }
        }
    }

    public final void q() {
        Future<K.f> future = this.f4953p;
        if (future != null) {
            try {
                this.f4953p = null;
                S.g.e(this, future.get());
            } catch (InterruptedException | ExecutionException unused) {
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
        C0688A c0688a = this.f4948k;
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
        C0688A c0688a = this.f4948k;
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
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.k(i4);
        }
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f4947j;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f4947j;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesWithIntrinsicBounds(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        super.setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.b();
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

    @Override // android.widget.TextView
    public void setFirstBaselineToTopHeight(int i4) {
        if (Build.VERSION.SDK_INT >= 28) {
            getSuperCaller().b(i4);
        } else {
            S.g.b(this, i4);
        }
    }

    @Override // android.widget.TextView
    public void setLastBaselineToBottomHeight(int i4) {
        if (Build.VERSION.SDK_INT >= 28) {
            getSuperCaller().a(i4);
        } else {
            S.g.c(this, i4);
        }
    }

    @Override // android.widget.TextView
    public void setLineHeight(int i4) {
        S.g.d(this, i4);
    }

    public void setPrecomputedText(K.f fVar) {
        S.g.e(this, fVar);
    }

    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        C0695d c0695d = this.f4947j;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f4947j;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintList(ColorStateList colorStateList) {
        C0688A c0688a = this.f4948k;
        c0688a.l(colorStateList);
        c0688a.b();
    }

    @Override // S.j
    public void setSupportCompoundDrawablesTintMode(PorterDuff.Mode mode) {
        C0688A c0688a = this.f4948k;
        c0688a.m(mode);
        c0688a.b();
    }

    @Override // android.widget.TextView
    public void setTextAppearance(Context context, int i4) {
        super.setTextAppearance(context, i4);
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.g(context, i4);
        }
    }

    @Override // android.widget.TextView
    public void setTextClassifier(TextClassifier textClassifier) {
        C0716z c0716z;
        if (Build.VERSION.SDK_INT < 28 && (c0716z = this.f4949l) != null) {
            c0716z.f5235b = textClassifier;
        } else {
            super.setTextClassifier(textClassifier);
        }
    }

    public void setTextFuture(Future<K.f> future) {
        this.f4953p = future;
        if (future != null) {
            requestLayout();
        }
    }

    public void setTextMetricsParamsCompat(f.a aVar) {
        TextDirectionHeuristic textDirectionHeuristic;
        TextDirectionHeuristic textDirectionHeuristic2 = aVar.f1256b;
        TextDirectionHeuristic textDirectionHeuristic3 = TextDirectionHeuristics.FIRSTSTRONG_RTL;
        int i4 = 1;
        if (textDirectionHeuristic2 != textDirectionHeuristic3 && textDirectionHeuristic2 != (textDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_LTR)) {
            if (textDirectionHeuristic2 == TextDirectionHeuristics.ANYRTL_LTR) {
                i4 = 2;
            } else if (textDirectionHeuristic2 == TextDirectionHeuristics.LTR) {
                i4 = 3;
            } else if (textDirectionHeuristic2 == TextDirectionHeuristics.RTL) {
                i4 = 4;
            } else if (textDirectionHeuristic2 == TextDirectionHeuristics.LOCALE) {
                i4 = 5;
            } else if (textDirectionHeuristic2 == textDirectionHeuristic) {
                i4 = 6;
            } else if (textDirectionHeuristic2 == textDirectionHeuristic3) {
                i4 = 7;
            }
        }
        setTextDirection(i4);
        int i5 = Build.VERSION.SDK_INT;
        TextPaint textPaint = aVar.f1255a;
        if (i5 < 23) {
            float textScaleX = textPaint.getTextScaleX();
            getPaint().set(textPaint);
            if (textScaleX == getTextScaleX()) {
                setTextScaleX((textScaleX / 2.0f) + 1.0f);
            }
            setTextScaleX(textScaleX);
            return;
        }
        getPaint().set(textPaint);
        g.a.e(this, aVar.f1257c);
        g.a.h(this, aVar.f1258d);
    }

    @Override // android.widget.TextView
    public final void setTextSize(int i4, float f) {
        boolean z4 = i0.f5160c;
        if (z4) {
            super.setTextSize(i4, f);
            return;
        }
        C0688A c0688a = this.f4948k;
        if (c0688a != null && !z4) {
            C0690C c0690c = c0688a.f4938i;
            if (!c0690c.f()) {
                c0690c.g(i4, f);
            }
        }
    }

    @Override // android.widget.TextView
    public final void setTypeface(Typeface typeface, int i4) {
        Typeface typeface2;
        if (this.f4951n) {
            return;
        }
        if (typeface != null && i4 > 0) {
            Context context = getContext();
            E.l lVar = E.e.f810a;
            if (context != null) {
                typeface2 = Typeface.create(typeface, i4);
            } else {
                throw new IllegalArgumentException("Context cannot be null");
            }
        } else {
            typeface2 = null;
        }
        this.f4951n = true;
        if (typeface2 != null) {
            typeface = typeface2;
        }
        try {
            super.setTypeface(typeface, i4);
        } finally {
            this.f4951n = false;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Type inference failed for: r1v6, types: [l.z, java.lang.Object] */
    public C0689B(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, i4);
        Y.a(context);
        this.f4951n = false;
        this.f4952o = null;
        W.a(getContext(), this);
        C0695d c0695d = new C0695d(this);
        this.f4947j = c0695d;
        c0695d.d(attributeSet, i4);
        C0688A c0688a = new C0688A(this);
        this.f4948k = c0688a;
        c0688a.f(attributeSet, i4);
        c0688a.b();
        ?? obj = new Object();
        obj.f5234a = this;
        this.f4949l = obj;
        getEmojiTextViewHelper().b(attributeSet, i4);
    }

    public final void setLineHeight(int i4, float f) {
        int i5 = Build.VERSION.SDK_INT;
        if (i5 >= 34) {
            getSuperCaller().c(i4, f);
        } else if (i5 >= 34) {
            g.d.a(this, i4, f);
        } else {
            S.g.d(this, Math.round(TypedValue.applyDimension(i4, f, getResources().getDisplayMetrics())));
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesRelativeWithIntrinsicBounds(int i4, int i5, int i6, int i7) {
        Context context = getContext();
        setCompoundDrawablesRelativeWithIntrinsicBounds(i4 != 0 ? B2.a.f(context, i4) : null, i5 != 0 ? B2.a.f(context, i5) : null, i6 != 0 ? B2.a.f(context, i6) : null, i7 != 0 ? B2.a.f(context, i7) : null);
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    @Override // android.widget.TextView
    public final void setCompoundDrawablesWithIntrinsicBounds(int i4, int i5, int i6, int i7) {
        Context context = getContext();
        setCompoundDrawablesWithIntrinsicBounds(i4 != 0 ? B2.a.f(context, i4) : null, i5 != 0 ? B2.a.f(context, i5) : null, i6 != 0 ? B2.a.f(context, i6) : null, i7 != 0 ? B2.a.f(context, i7) : null);
        C0688A c0688a = this.f4948k;
        if (c0688a != null) {
            c0688a.b();
        }
    }

    /* renamed from: l.B$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class b implements a {
        public b() {
        }

        @Override // l.C0689B.a
        public void a(int i4) {
        }

        @Override // l.C0689B.a
        public void b(int i4) {
        }

        @Override // l.C0689B.a
        public void c(int i4, float f) {
        }
    }
}
