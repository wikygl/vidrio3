package U2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class d extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f2391a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ n f2392b;

    public /* synthetic */ d(n nVar, int i4) {
        this.f2391a = i4;
        this.f2392b = nVar;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animator) {
        switch (this.f2391a) {
            case 1:
                m mVar = (m) this.f2392b;
                mVar.q();
                mVar.f2422r.start();
                return;
            default:
                super.onAnimationEnd(animator);
                return;
        }
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
        switch (this.f2391a) {
            case 0:
                ((f) this.f2392b).f2424b.h(true);
                return;
            default:
                super.onAnimationStart(animator);
                return;
        }
    }
}
