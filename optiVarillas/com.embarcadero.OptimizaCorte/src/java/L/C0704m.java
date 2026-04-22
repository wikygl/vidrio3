package l;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;

/* renamed from: l.m  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class C0704m extends ImageButton {

    /* renamed from: j  reason: collision with root package name */
    public final C0695d f5178j;

    /* renamed from: k  reason: collision with root package name */
    public final C0705n f5179k;

    /* renamed from: l  reason: collision with root package name */
    public boolean f5180l;

    public C0704m(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 2130903579);
    }

    @Override // android.widget.ImageView, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        C0695d c0695d = this.f5178j;
        if (c0695d != null) {
            c0695d.a();
        }
        C0705n c0705n = this.f5179k;
        if (c0705n != null) {
            c0705n.a();
        }
    }

    public ColorStateList getSupportBackgroundTintList() {
        C0695d c0695d = this.f5178j;
        if (c0695d != null) {
            return c0695d.b();
        }
        return null;
    }

    public PorterDuff.Mode getSupportBackgroundTintMode() {
        C0695d c0695d = this.f5178j;
        if (c0695d != null) {
            return c0695d.c();
        }
        return null;
    }

    public ColorStateList getSupportImageTintList() {
        Z z4;
        C0705n c0705n = this.f5179k;
        if (c0705n == null || (z4 = c0705n.f5182b) == null) {
            return null;
        }
        return z4.f5087a;
    }

    public PorterDuff.Mode getSupportImageTintMode() {
        Z z4;
        C0705n c0705n = this.f5179k;
        if (c0705n == null || (z4 = c0705n.f5182b) == null) {
            return null;
        }
        return z4.f5088b;
    }

    @Override // android.widget.ImageView, android.view.View
    public final boolean hasOverlappingRendering() {
        if ((!(this.f5179k.f5181a.getBackground() instanceof RippleDrawable)) && super.hasOverlappingRendering()) {
            return true;
        }
        return false;
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable drawable) {
        super.setBackgroundDrawable(drawable);
        C0695d c0695d = this.f5178j;
        if (c0695d != null) {
            c0695d.e();
        }
    }

    @Override // android.view.View
    public void setBackgroundResource(int i4) {
        super.setBackgroundResource(i4);
        C0695d c0695d = this.f5178j;
        if (c0695d != null) {
            c0695d.f(i4);
        }
    }

    @Override // android.widget.ImageView
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        C0705n c0705n = this.f5179k;
        if (c0705n != null) {
            c0705n.a();
        }
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        C0705n c0705n = this.f5179k;
        if (c0705n != null && drawable != null && !this.f5180l) {
            c0705n.f5184d = drawable.getLevel();
        }
        super.setImageDrawable(drawable);
        if (c0705n != null) {
            c0705n.a();
            if (!this.f5180l) {
                ImageView imageView = c0705n.f5181a;
                if (imageView.getDrawable() != null) {
                    imageView.getDrawable().setLevel(c0705n.f5184d);
                }
            }
        }
    }

    @Override // android.widget.ImageView
    public void setImageLevel(int i4) {
        super.setImageLevel(i4);
        this.f5180l = true;
    }

    @Override // android.widget.ImageView
    public void setImageResource(int i4) {
        C0705n c0705n = this.f5179k;
        ImageView imageView = c0705n.f5181a;
        if (i4 != 0) {
            Drawable f = B2.a.f(imageView.getContext(), i4);
            if (f != null) {
                G.a(f);
            }
            imageView.setImageDrawable(f);
        } else {
            imageView.setImageDrawable(null);
        }
        c0705n.a();
    }

    @Override // android.widget.ImageView
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        C0705n c0705n = this.f5179k;
        if (c0705n != null) {
            c0705n.a();
        }
    }

    public void setSupportBackgroundTintList(ColorStateList colorStateList) {
        C0695d c0695d = this.f5178j;
        if (c0695d != null) {
            c0695d.h(colorStateList);
        }
    }

    public void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        C0695d c0695d = this.f5178j;
        if (c0695d != null) {
            c0695d.i(mode);
        }
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [l.Z, java.lang.Object] */
    public void setSupportImageTintList(ColorStateList colorStateList) {
        C0705n c0705n = this.f5179k;
        if (c0705n != null) {
            if (c0705n.f5182b == null) {
                c0705n.f5182b = new Object();
            }
            Z z4 = c0705n.f5182b;
            z4.f5087a = colorStateList;
            z4.f5090d = true;
            c0705n.a();
        }
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [l.Z, java.lang.Object] */
    public void setSupportImageTintMode(PorterDuff.Mode mode) {
        C0705n c0705n = this.f5179k;
        if (c0705n != null) {
            if (c0705n.f5182b == null) {
                c0705n.f5182b = new Object();
            }
            Z z4 = c0705n.f5182b;
            z4.f5088b = mode;
            z4.f5089c = true;
            c0705n.a();
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0704m(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, i4);
        Y.a(context);
        this.f5180l = false;
        W.a(getContext(), this);
        C0695d c0695d = new C0695d(this);
        this.f5178j = c0695d;
        c0695d.d(attributeSet, i4);
        C0705n c0705n = new C0705n(this);
        this.f5179k = c0705n;
        c0705n.b(attributeSet, i4);
    }
}
