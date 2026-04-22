package l;

import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.TextView;
import d.C0376a;

/* renamed from: l.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0703l {

    /* renamed from: a  reason: collision with root package name */
    public final TextView f5176a;

    /* renamed from: b  reason: collision with root package name */
    public final X.f f5177b;

    public C0703l(TextView textView) {
        this.f5176a = textView;
        this.f5177b = new X.f(textView);
    }

    public final InputFilter[] a(InputFilter[] inputFilterArr) {
        return this.f5177b.f2794a.a(inputFilterArr);
    }

    public final void b(AttributeSet attributeSet, int i4) {
        TypedArray obtainStyledAttributes = this.f5176a.getContext().obtainStyledAttributes(attributeSet, C0376a.f3136i, i4, 0);
        try {
            boolean z4 = true;
            if (obtainStyledAttributes.hasValue(14)) {
                z4 = obtainStyledAttributes.getBoolean(14, true);
            }
            obtainStyledAttributes.recycle();
            d(z4);
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public final void c(boolean z4) {
        this.f5177b.f2794a.c(z4);
    }

    public final void d(boolean z4) {
        this.f5177b.f2794a.d(z4);
    }
}
