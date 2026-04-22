package l;

import M.O;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import d.C0376a;
import java.util.WeakHashMap;

/* renamed from: l.d  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0695d {

    /* renamed from: a  reason: collision with root package name */
    public final View f5111a;

    /* renamed from: d  reason: collision with root package name */
    public Z f5114d;

    /* renamed from: e  reason: collision with root package name */
    public Z f5115e;
    public Z f;

    /* renamed from: c  reason: collision with root package name */
    public int f5113c = -1;

    /* renamed from: b  reason: collision with root package name */
    public final C0701j f5112b = C0701j.a();

    public C0695d(View view) {
        this.f5111a = view;
    }

    /* JADX WARN: Type inference failed for: r2v3, types: [l.Z, java.lang.Object] */
    public final void a() {
        View view = this.f5111a;
        Drawable background = view.getBackground();
        if (background != null) {
            int i4 = Build.VERSION.SDK_INT;
            if (i4 <= 21 ? i4 == 21 : this.f5114d != null) {
                if (this.f == null) {
                    this.f = new Object();
                }
                Z z4 = this.f;
                z4.f5087a = null;
                z4.f5090d = false;
                z4.f5088b = null;
                z4.f5089c = false;
                WeakHashMap<View, M.V> weakHashMap = M.O.f1526a;
                ColorStateList g4 = O.d.g(view);
                if (g4 != null) {
                    z4.f5090d = true;
                    z4.f5087a = g4;
                }
                PorterDuff.Mode h4 = O.d.h(view);
                if (h4 != null) {
                    z4.f5089c = true;
                    z4.f5088b = h4;
                }
                if (z4.f5090d || z4.f5089c) {
                    C0701j.e(background, z4, view.getDrawableState());
                    return;
                }
            }
            Z z5 = this.f5115e;
            if (z5 != null) {
                C0701j.e(background, z5, view.getDrawableState());
                return;
            }
            Z z6 = this.f5114d;
            if (z6 != null) {
                C0701j.e(background, z6, view.getDrawableState());
            }
        }
    }

    public final ColorStateList b() {
        Z z4 = this.f5115e;
        if (z4 != null) {
            return z4.f5087a;
        }
        return null;
    }

    public final PorterDuff.Mode c() {
        Z z4 = this.f5115e;
        if (z4 != null) {
            return z4.f5088b;
        }
        return null;
    }

    public final void d(AttributeSet attributeSet, int i4) {
        ColorStateList i5;
        View view = this.f5111a;
        Context context = view.getContext();
        int[] iArr = C0376a.f3153z;
        boolean z4 = false;
        b0 e4 = b0.e(context, attributeSet, iArr, i4, 0);
        TypedArray typedArray = e4.f5104b;
        View view2 = this.f5111a;
        M.O.o(view2, view2.getContext(), iArr, attributeSet, e4.f5104b, i4);
        try {
            if (typedArray.hasValue(0)) {
                this.f5113c = typedArray.getResourceId(0, -1);
                C0701j c0701j = this.f5112b;
                Context context2 = view.getContext();
                int i6 = this.f5113c;
                synchronized (c0701j) {
                    i5 = c0701j.f5163a.i(context2, i6);
                }
                if (i5 != null) {
                    g(i5);
                }
            }
            if (typedArray.hasValue(1)) {
                M.O.r(view, e4.a(1));
            }
            if (typedArray.hasValue(2)) {
                PorterDuff.Mode c4 = G.c(typedArray.getInt(2, -1), null);
                int i7 = Build.VERSION.SDK_INT;
                O.d.r(view, c4);
                if (i7 == 21) {
                    Drawable background = view.getBackground();
                    z4 = (O.d.g(view) == null && O.d.h(view) == null) ? true : true;
                    if (background != null && z4) {
                        if (background.isStateful()) {
                            background.setState(view.getDrawableState());
                        }
                        view.setBackground(background);
                    }
                }
            }
            e4.f();
        } catch (Throwable th) {
            e4.f();
            throw th;
        }
    }

    public final void e() {
        this.f5113c = -1;
        g(null);
        a();
    }

    public final void f(int i4) {
        ColorStateList colorStateList;
        this.f5113c = i4;
        C0701j c0701j = this.f5112b;
        if (c0701j != null) {
            Context context = this.f5111a.getContext();
            synchronized (c0701j) {
                colorStateList = c0701j.f5163a.i(context, i4);
            }
        } else {
            colorStateList = null;
        }
        g(colorStateList);
        a();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [l.Z, java.lang.Object] */
    public final void g(ColorStateList colorStateList) {
        if (colorStateList != null) {
            if (this.f5114d == null) {
                this.f5114d = new Object();
            }
            Z z4 = this.f5114d;
            z4.f5087a = colorStateList;
            z4.f5090d = true;
        } else {
            this.f5114d = null;
        }
        a();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [l.Z, java.lang.Object] */
    public final void h(ColorStateList colorStateList) {
        if (this.f5115e == null) {
            this.f5115e = new Object();
        }
        Z z4 = this.f5115e;
        z4.f5087a = colorStateList;
        z4.f5090d = true;
        a();
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [l.Z, java.lang.Object] */
    public final void i(PorterDuff.Mode mode) {
        if (this.f5115e == null) {
            this.f5115e = new Object();
        }
        Z z4 = this.f5115e;
        z4.f5088b = mode;
        z4.f5089c = true;
        a();
    }
}
