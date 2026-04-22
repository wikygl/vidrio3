package C1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.ViewGroup;
import com.google.android.material.bottomappbar.BottomAppBar;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class v extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f408a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ ViewGroup f409b;

    public /* synthetic */ v(ViewGroup viewGroup, int i4) {
        this.f408a = i4;
        this.f409b = viewGroup;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationCancel(Animator animator) {
        switch (this.f408a) {
            case 0:
                x xVar = (x) this.f409b;
                xVar.setEnabled(true);
                xVar.f414j.setEnabled(true);
                return;
            default:
                super.onAnimationCancel(animator);
                return;
        }
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationEnd(Animator animator) {
        switch (this.f408a) {
            case 0:
                x xVar = (x) this.f409b;
                xVar.setEnabled(true);
                xVar.f414j.setEnabled(true);
                return;
            default:
                BottomAppBar bottomAppBar = this.f409b;
                bottomAppBar.getClass();
                bottomAppBar.f0 = null;
                return;
        }
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationStart(Animator animator) {
        switch (this.f408a) {
            case 0:
                x xVar = (x) this.f409b;
                xVar.setEnabled(false);
                xVar.f414j.setEnabled(false);
                return;
            default:
                this.f409b.getClass();
                return;
        }
    }
}
