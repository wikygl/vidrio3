package u0;

import android.graphics.Matrix;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class t extends s {
    @Override // u0.p
    public final float a(View view) {
        float transitionAlpha;
        transitionAlpha = view.getTransitionAlpha();
        return transitionAlpha;
    }

    @Override // u0.r, u0.p
    public final void b(View view, int i4, int i5, int i6, int i7) {
        view.setLeftTopRightBottom(i4, i5, i6, i7);
    }

    @Override // u0.p
    public final void c(View view, float f) {
        view.setTransitionAlpha(f);
    }

    @Override // u0.s, u0.p
    public final void d(View view, int i4) {
        view.setTransitionVisibility(i4);
    }

    @Override // u0.q
    public final void e(View view, Matrix matrix) {
        view.transformMatrixToGlobal(matrix);
    }

    @Override // u0.q
    public final void f(View view, Matrix matrix) {
        view.transformMatrixToLocal(matrix);
    }
}
