package u0;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import r.C0773b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class g extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ C0773b f5981a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ f f5982b;

    public g(f fVar, C0773b c0773b) {
        this.f5982b = fVar;
        this.f5981a = c0773b;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationEnd(Animator animator) {
        this.f5981a.remove(animator);
        this.f5982b.f5968w.remove(animator);
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationStart(Animator animator) {
        this.f5982b.f5968w.add(animator);
    }
}
