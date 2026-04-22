package w2;

import P2.f;
import android.animation.ValueAnimator;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

/* renamed from: w2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0847a implements ValueAnimator.AnimatorUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ BottomSheetBehavior f6404a;

    public C0847a(BottomSheetBehavior bottomSheetBehavior) {
        this.f6404a = bottomSheetBehavior;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        f fVar = this.f6404a.i;
        if (fVar != null) {
            f.b bVar = fVar.f1820j;
            if (bVar.f1846j != floatValue) {
                bVar.f1846j = floatValue;
                fVar.f1824n = true;
                fVar.invalidateSelf();
            }
        }
    }
}
