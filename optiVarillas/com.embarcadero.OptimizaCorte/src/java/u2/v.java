package U2;

import M.O;
import M.V;
import M2.b;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.internal.CheckableImageButton;
import com.google.android.material.textfield.TextInputLayout;
import java.util.WeakHashMap;
import l.C0689B;
import l.b0;

@SuppressLint({"ViewConstructor"})
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class v extends LinearLayout {

    /* renamed from: j  reason: collision with root package name */
    public final TextInputLayout f2474j;

    /* renamed from: k  reason: collision with root package name */
    public final C0689B f2475k;

    /* renamed from: l  reason: collision with root package name */
    public CharSequence f2476l;

    /* renamed from: m  reason: collision with root package name */
    public final CheckableImageButton f2477m;

    /* renamed from: n  reason: collision with root package name */
    public ColorStateList f2478n;

    /* renamed from: o  reason: collision with root package name */
    public PorterDuff.Mode f2479o;

    /* renamed from: p  reason: collision with root package name */
    public int f2480p;

    /* renamed from: q  reason: collision with root package name */
    public ImageView.ScaleType f2481q;

    /* renamed from: r  reason: collision with root package name */
    public View.OnLongClickListener f2482r;

    /* renamed from: s  reason: collision with root package name */
    public boolean f2483s;

    public v(TextInputLayout textInputLayout, b0 b0Var) {
        super(textInputLayout.getContext());
        CharSequence text;
        Drawable b4;
        this.f2474j = textInputLayout;
        setVisibility(8);
        setOrientation(0);
        setLayoutParams(new FrameLayout.LayoutParams(-2, -1, 8388611));
        CheckableImageButton inflate = LayoutInflater.from(getContext()).inflate(2131427381, (ViewGroup) this, false);
        this.f2477m = inflate;
        if (Build.VERSION.SDK_INT <= 22) {
            int[] iArr = M2.b.f1733a;
            b4 = b.a.b(inflate.getContext(), (int) H2.u.a(inflate.getContext(), 4));
            inflate.setBackground(b4);
        }
        C0689B c0689b = new C0689B(getContext(), null);
        this.f2475k = c0689b;
        if (L2.c.d(getContext())) {
            ((ViewGroup.MarginLayoutParams) inflate.getLayoutParams()).setMarginEnd(0);
        }
        View.OnLongClickListener onLongClickListener = this.f2482r;
        inflate.setOnClickListener(null);
        o.d(inflate, onLongClickListener);
        this.f2482r = null;
        inflate.setOnLongClickListener(null);
        o.d(inflate, null);
        TypedArray typedArray = b0Var.f5104b;
        if (typedArray.hasValue(69)) {
            this.f2478n = L2.c.b(getContext(), b0Var, 69);
        }
        if (typedArray.hasValue(70)) {
            this.f2479o = H2.u.c(typedArray.getInt(70, -1), null);
        }
        if (typedArray.hasValue(66)) {
            b(b0Var.b(66));
            if (typedArray.hasValue(65) && inflate.getContentDescription() != (text = typedArray.getText(65))) {
                inflate.setContentDescription(text);
            }
            inflate.setCheckable(typedArray.getBoolean(64, true));
        }
        int dimensionPixelSize = typedArray.getDimensionPixelSize(67, getResources().getDimensionPixelSize(2131100366));
        if (dimensionPixelSize >= 0) {
            if (dimensionPixelSize != this.f2480p) {
                this.f2480p = dimensionPixelSize;
                inflate.setMinimumWidth(dimensionPixelSize);
                inflate.setMinimumHeight(dimensionPixelSize);
            }
            if (typedArray.hasValue(68)) {
                ImageView.ScaleType b5 = o.b(typedArray.getInt(68, -1));
                this.f2481q = b5;
                inflate.setScaleType(b5);
            }
            c0689b.setVisibility(8);
            c0689b.setId(2131231282);
            c0689b.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            c0689b.setAccessibilityLiveRegion(1);
            S.g.f(c0689b, typedArray.getResourceId(60, 0));
            if (typedArray.hasValue(61)) {
                c0689b.setTextColor(b0Var.a(61));
            }
            CharSequence text2 = typedArray.getText(59);
            this.f2476l = TextUtils.isEmpty(text2) ? null : text2;
            c0689b.setText(text2);
            e();
            addView(inflate);
            addView(c0689b);
            return;
        }
        throw new IllegalArgumentException("startIconSize cannot be less than 0");
    }

    public final int a() {
        int i4;
        CheckableImageButton checkableImageButton = this.f2477m;
        if (checkableImageButton.getVisibility() == 0) {
            i4 = ((ViewGroup.MarginLayoutParams) checkableImageButton.getLayoutParams()).getMarginEnd() + checkableImageButton.getMeasuredWidth();
        } else {
            i4 = 0;
        }
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        return this.f2475k.getPaddingStart() + getPaddingStart() + i4;
    }

    public final void b(Drawable drawable) {
        CheckableImageButton checkableImageButton = this.f2477m;
        checkableImageButton.setImageDrawable(drawable);
        if (drawable != null) {
            ColorStateList colorStateList = this.f2478n;
            PorterDuff.Mode mode = this.f2479o;
            TextInputLayout textInputLayout = this.f2474j;
            o.a(textInputLayout, checkableImageButton, colorStateList, mode);
            c(true);
            o.c(textInputLayout, checkableImageButton, this.f2478n);
            return;
        }
        c(false);
        View.OnLongClickListener onLongClickListener = this.f2482r;
        checkableImageButton.setOnClickListener(null);
        o.d(checkableImageButton, onLongClickListener);
        this.f2482r = null;
        checkableImageButton.setOnLongClickListener(null);
        o.d(checkableImageButton, null);
        if (checkableImageButton.getContentDescription() != null) {
            checkableImageButton.setContentDescription(null);
        }
    }

    public final void c(boolean z4) {
        boolean z5;
        CheckableImageButton checkableImageButton = this.f2477m;
        int i4 = 0;
        if (checkableImageButton.getVisibility() == 0) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z5 != z4) {
            if (!z4) {
                i4 = 8;
            }
            checkableImageButton.setVisibility(i4);
            d();
            e();
        }
    }

    public final void d() {
        int paddingStart;
        EditText editText = this.f2474j.m;
        if (editText == null) {
            return;
        }
        if (this.f2477m.getVisibility() == 0) {
            paddingStart = 0;
        } else {
            WeakHashMap<View, V> weakHashMap = O.f1526a;
            paddingStart = editText.getPaddingStart();
        }
        int compoundPaddingTop = editText.getCompoundPaddingTop();
        int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(2131100236);
        int compoundPaddingBottom = editText.getCompoundPaddingBottom();
        WeakHashMap<View, V> weakHashMap2 = O.f1526a;
        this.f2475k.setPaddingRelative(paddingStart, compoundPaddingTop, dimensionPixelSize, compoundPaddingBottom);
    }

    public final void e() {
        int i4;
        int i5 = 8;
        if (this.f2476l != null && !this.f2483s) {
            i4 = 0;
        } else {
            i4 = 8;
        }
        setVisibility((this.f2477m.getVisibility() == 0 || i4 == 0) ? 0 : 0);
        this.f2475k.setVisibility(i4);
        this.f2474j.q();
    }

    @Override // android.widget.LinearLayout, android.view.View
    public final void onMeasure(int i4, int i5) {
        super.onMeasure(i4, i5);
        d();
    }
}
