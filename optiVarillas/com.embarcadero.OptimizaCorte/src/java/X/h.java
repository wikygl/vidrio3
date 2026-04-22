package X;

import android.graphics.Rect;
import android.text.method.TransformationMethod;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class h implements TransformationMethod {

    /* renamed from: a  reason: collision with root package name */
    public final TransformationMethod f2804a;

    public h(TransformationMethod transformationMethod) {
        this.f2804a = transformationMethod;
    }

    @Override // android.text.method.TransformationMethod
    public final CharSequence getTransformation(CharSequence charSequence, View view) {
        if (view.isInEditMode()) {
            return charSequence;
        }
        TransformationMethod transformationMethod = this.f2804a;
        if (transformationMethod != null) {
            charSequence = transformationMethod.getTransformation(charSequence, view);
        }
        if (charSequence != null && androidx.emoji2.text.f.a().b() == 1) {
            androidx.emoji2.text.f a4 = androidx.emoji2.text.f.a();
            a4.getClass();
            return a4.f(charSequence, 0, charSequence.length());
        }
        return charSequence;
    }

    @Override // android.text.method.TransformationMethod
    public final void onFocusChanged(View view, CharSequence charSequence, boolean z4, int i4, Rect rect) {
        TransformationMethod transformationMethod = this.f2804a;
        if (transformationMethod != null) {
            transformationMethod.onFocusChanged(view, charSequence, z4, i4, rect);
        }
    }
}
