package G2;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.floatingactionbutton.d;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class c extends d {

    /* renamed from: I  reason: collision with root package name */
    public StateListAnimator f962I;

    public final float e() {
        return ((d) this).q.getElevation();
    }

    public final void f(Rect rect) {
        boolean z4;
        if (((d) this).r.a.r) {
            super.f(rect);
            return;
        }
        boolean z5 = ((d) this).b;
        FloatingActionButton floatingActionButton = ((d) this).q;
        if (z5 && floatingActionButton.getSizeDimension() < 0) {
            z4 = false;
        } else {
            z4 = true;
        }
        if (!z4) {
            int sizeDimension = (0 - floatingActionButton.getSizeDimension()) / 2;
            rect.set(sizeDimension, sizeDimension, sizeDimension, sizeDimension);
            return;
        }
        rect.set(0, 0, 0, 0);
    }

    public final void h() {
        l();
        throw null;
    }

    public final void i(int[] iArr) {
        if (Build.VERSION.SDK_INT == 21) {
            FloatingActionButton floatingActionButton = ((d) this).q;
            if (floatingActionButton.isEnabled()) {
                floatingActionButton.setElevation(((d) this).d);
                if (floatingActionButton.isPressed()) {
                    floatingActionButton.setTranslationZ(((d) this).f);
                    return;
                } else if (!floatingActionButton.isFocused() && !floatingActionButton.isHovered()) {
                    floatingActionButton.setTranslationZ(0.0f);
                    return;
                } else {
                    floatingActionButton.setTranslationZ(((d) this).e);
                    return;
                }
            }
            floatingActionButton.setElevation(0.0f);
            floatingActionButton.setTranslationZ(0.0f);
        }
    }

    public final void j(float f, float f4, float f5) {
        int i4 = Build.VERSION.SDK_INT;
        FloatingActionButton floatingActionButton = ((d) this).q;
        if (i4 == 21) {
            floatingActionButton.refreshDrawableState();
        } else if (floatingActionButton.getStateListAnimator() == this.f962I) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(d.C, m(f, f5));
            stateListAnimator.addState(d.D, m(f, f4));
            stateListAnimator.addState(d.E, m(f, f4));
            stateListAnimator.addState(d.F, m(f, f4));
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(floatingActionButton, "elevation", f).setDuration(0L));
            if (i4 >= 22 && i4 <= 24) {
                arrayList.add(ObjectAnimator.ofFloat(floatingActionButton, View.TRANSLATION_Z, floatingActionButton.getTranslationZ()).setDuration(100L));
            }
            arrayList.add(ObjectAnimator.ofFloat(floatingActionButton, View.TRANSLATION_Z, 0.0f).setDuration(100L));
            animatorSet.playSequentially((Animator[]) arrayList.toArray(new Animator[0]));
            animatorSet.setInterpolator(d.x);
            stateListAnimator.addState(d.G, animatorSet);
            stateListAnimator.addState(d.H, m(0.0f, 0.0f));
            this.f962I = stateListAnimator;
            floatingActionButton.setStateListAnimator(stateListAnimator);
        }
        if (!n()) {
            return;
        }
        l();
        throw null;
    }

    public final AnimatorSet m(float f, float f4) {
        AnimatorSet animatorSet = new AnimatorSet();
        float[] fArr = {f};
        FloatingActionButton floatingActionButton = ((d) this).q;
        animatorSet.play(ObjectAnimator.ofFloat(floatingActionButton, "elevation", fArr).setDuration(0L)).with(ObjectAnimator.ofFloat(floatingActionButton, View.TRANSLATION_Z, f4).setDuration(100L));
        animatorSet.setInterpolator(d.x);
        return animatorSet;
    }

    public final boolean n() {
        if (!((d) this).r.a.r && (!((d) this).b || ((d) this).q.getSizeDimension() >= 0)) {
            return false;
        }
        return true;
    }

    public final void g() {
    }
}
