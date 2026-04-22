package l;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import d.C0376a;

/* renamed from: l.n  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0705n {

    /* renamed from: a  reason: collision with root package name */
    public final ImageView f5181a;

    /* renamed from: b  reason: collision with root package name */
    public Z f5182b;

    /* renamed from: c  reason: collision with root package name */
    public Z f5183c;

    /* renamed from: d  reason: collision with root package name */
    public int f5184d = 0;

    public C0705n(ImageView imageView) {
        this.f5181a = imageView;
    }

    /* JADX WARN: Type inference failed for: r2v4, types: [l.Z, java.lang.Object] */
    public final void a() {
        ImageView imageView = this.f5181a;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            G.a(drawable);
        }
        if (drawable != null) {
            int i4 = Build.VERSION.SDK_INT;
            if (i4 <= 21 && i4 == 21) {
                if (this.f5183c == null) {
                    this.f5183c = new Object();
                }
                Z z4 = this.f5183c;
                z4.f5087a = null;
                z4.f5090d = false;
                z4.f5088b = null;
                z4.f5089c = false;
                ColorStateList a4 = S.d.a(imageView);
                if (a4 != null) {
                    z4.f5090d = true;
                    z4.f5087a = a4;
                }
                PorterDuff.Mode b4 = S.d.b(imageView);
                if (b4 != null) {
                    z4.f5089c = true;
                    z4.f5088b = b4;
                }
                if (z4.f5090d || z4.f5089c) {
                    C0701j.e(drawable, z4, imageView.getDrawableState());
                    return;
                }
            }
            Z z5 = this.f5182b;
            if (z5 != null) {
                C0701j.e(drawable, z5, imageView.getDrawableState());
            }
        }
    }

    public final void b(AttributeSet attributeSet, int i4) {
        Drawable drawable;
        Drawable drawable2;
        int resourceId;
        ImageView imageView = this.f5181a;
        Context context = imageView.getContext();
        int[] iArr = C0376a.f;
        b0 e4 = b0.e(context, attributeSet, iArr, i4, 0);
        M.O.o(imageView, imageView.getContext(), iArr, attributeSet, e4.f5104b, i4);
        try {
            Drawable drawable3 = imageView.getDrawable();
            TypedArray typedArray = e4.f5104b;
            if (drawable3 == null && (resourceId = typedArray.getResourceId(1, -1)) != -1 && (drawable3 = B2.a.f(imageView.getContext(), resourceId)) != null) {
                imageView.setImageDrawable(drawable3);
            }
            if (drawable3 != null) {
                G.a(drawable3);
            }
            if (typedArray.hasValue(2)) {
                ColorStateList a4 = e4.a(2);
                int i5 = Build.VERSION.SDK_INT;
                S.d.c(imageView, a4);
                if (i5 == 21 && (drawable2 = imageView.getDrawable()) != null && S.d.a(imageView) != null) {
                    if (drawable2.isStateful()) {
                        drawable2.setState(imageView.getDrawableState());
                    }
                    imageView.setImageDrawable(drawable2);
                }
            }
            if (typedArray.hasValue(3)) {
                PorterDuff.Mode c4 = G.c(typedArray.getInt(3, -1), null);
                int i6 = Build.VERSION.SDK_INT;
                S.d.d(imageView, c4);
                if (i6 == 21 && (drawable = imageView.getDrawable()) != null && S.d.a(imageView) != null) {
                    if (drawable.isStateful()) {
                        drawable.setState(imageView.getDrawableState());
                    }
                    imageView.setImageDrawable(drawable);
                }
            }
            e4.f();
        } catch (Throwable th) {
            e4.f();
            throw th;
        }
    }
}
