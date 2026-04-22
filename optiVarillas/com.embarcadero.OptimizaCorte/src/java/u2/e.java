package U2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.google.android.material.bottomappbar.BottomAppBar;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class e extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f2393a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f2394b;

    public /* synthetic */ e(int i4, Object obj) {
        this.f2393a = i4;
        this.f2394b = obj;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationEnd(Animator animator) {
        switch (this.f2393a) {
            case 0:
                ((f) this.f2394b).f2424b.h(false);
                return;
            default:
                BottomAppBar bottomAppBar = (BottomAppBar) this.f2394b;
                bottomAppBar.getClass();
                bottomAppBar.e0 = null;
                return;
        }
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
        switch (this.f2393a) {
            case 1:
                ((BottomAppBar) this.f2394b).getClass();
                return;
            default:
                super.onAnimationStart(animator);
                return;
        }
    }
}
