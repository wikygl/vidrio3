package R2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.view.ViewPropertyAnimator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import java.util.ArrayList;
import v0.AbstractC0830c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class b extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f2059a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f2060b;

    public /* synthetic */ b(int i4, Object obj) {
        this.f2059a = i4;
        this.f2060b = obj;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationEnd(Animator animator) {
        switch (this.f2059a) {
            case 0:
                ((BaseTransientBottomBar) this.f2060b).d();
                return;
            default:
                v0.d dVar = (v0.d) this.f2060b;
                ArrayList arrayList = new ArrayList(dVar.f6215n);
                int size = arrayList.size();
                for (int i4 = 0; i4 < size; i4++) {
                    ((AbstractC0830c) arrayList.get(i4)).a(dVar);
                }
                return;
        }
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationStart(Animator animator) {
        switch (this.f2059a) {
            case 0:
                BaseTransientBottomBar baseTransientBottomBar = (BaseTransientBottomBar) this.f2060b;
                SnackbarContentLayout snackbarContentLayout = baseTransientBottomBar.j;
                int i4 = baseTransientBottomBar.c;
                int i5 = baseTransientBottomBar.a;
                int i6 = i4 - i5;
                SnackbarContentLayout snackbarContentLayout2 = snackbarContentLayout;
                snackbarContentLayout2.j.setAlpha(0.0f);
                long j4 = i5;
                ViewPropertyAnimator duration = snackbarContentLayout2.j.animate().alpha(1.0f).setDuration(j4);
                TimeInterpolator timeInterpolator = snackbarContentLayout2.l;
                long j5 = i6;
                duration.setInterpolator(timeInterpolator).setStartDelay(j5).start();
                if (snackbarContentLayout2.k.getVisibility() == 0) {
                    snackbarContentLayout2.k.setAlpha(0.0f);
                    snackbarContentLayout2.k.animate().alpha(1.0f).setDuration(j4).setInterpolator(timeInterpolator).setStartDelay(j5).start();
                    return;
                }
                return;
            default:
                v0.d dVar = (v0.d) this.f2060b;
                ArrayList arrayList = new ArrayList(dVar.f6215n);
                int size = arrayList.size();
                for (int i7 = 0; i7 < size; i7++) {
                    ((AbstractC0830c) arrayList.get(i7)).b(dVar);
                }
                return;
        }
    }
}
