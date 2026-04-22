package u2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomappbar.BottomAppBar;

/* renamed from: u2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0824a extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f6030a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f6031b;

    public /* synthetic */ C0824a(int i4, Object obj) {
        this.f6030a = i4;
        this.f6031b = obj;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animator) {
        switch (this.f6030a) {
            case 0:
                ((HideBottomViewOnScrollBehavior) this.f6031b).i = null;
                return;
            default:
                super.onAnimationEnd(animator);
                return;
        }
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationStart(Animator animator) {
        switch (this.f6030a) {
            case 1:
                ((BottomAppBar) this.f6031b).getClass();
                throw null;
            default:
                super.onAnimationStart(animator);
                return;
        }
    }
}
