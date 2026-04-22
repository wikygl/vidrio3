package R2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.view.ViewPropertyAnimator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.SnackbarContentLayout;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class c extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ BaseTransientBottomBar f2061a;

    public c(BaseTransientBottomBar baseTransientBottomBar, int i4) {
        this.f2061a = baseTransientBottomBar;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationEnd(Animator animator) {
        this.f2061a.c();
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationStart(Animator animator) {
        BaseTransientBottomBar baseTransientBottomBar = this.f2061a;
        SnackbarContentLayout snackbarContentLayout = baseTransientBottomBar.j;
        int i4 = baseTransientBottomBar.b;
        SnackbarContentLayout snackbarContentLayout2 = snackbarContentLayout;
        snackbarContentLayout2.j.setAlpha(1.0f);
        long j4 = i4;
        ViewPropertyAnimator duration = snackbarContentLayout2.j.animate().alpha(0.0f).setDuration(j4);
        TimeInterpolator timeInterpolator = snackbarContentLayout2.l;
        long j5 = 0;
        duration.setInterpolator(timeInterpolator).setStartDelay(j5).start();
        if (snackbarContentLayout2.k.getVisibility() == 0) {
            snackbarContentLayout2.k.setAlpha(1.0f);
            snackbarContentLayout2.k.animate().alpha(0.0f).setDuration(j4).setInterpolator(timeInterpolator).setStartDelay(j5).start();
        }
    }
}
