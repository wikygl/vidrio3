package u0;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class h extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ f f5983a;

    public h(f fVar) {
        this.f5983a = fVar;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationEnd(Animator animator) {
        this.f5983a.m();
        animator.removeListener(this);
    }
}
