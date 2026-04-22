package v2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import androidx.appcompat.widget.ActionMenuView;
import com.google.android.material.bottomappbar.BottomAppBar;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class b extends AnimatorListenerAdapter {

    /* renamed from: a  reason: collision with root package name */
    public boolean f6290a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ ActionMenuView f6291b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ int f6292c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ boolean f6293d;

    /* renamed from: e  reason: collision with root package name */
    public final /* synthetic */ BottomAppBar f6294e;

    public b(BottomAppBar bottomAppBar, ActionMenuView actionMenuView, int i4, boolean z4) {
        this.f6294e = bottomAppBar;
        this.f6291b = actionMenuView;
        this.f6292c = i4;
        this.f6293d = z4;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationCancel(Animator animator) {
        this.f6290a = true;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public final void onAnimationEnd(Animator animator) {
        boolean z4;
        if (!this.f6290a) {
            BottomAppBar bottomAppBar = this.f6294e;
            int i4 = bottomAppBar.m0;
            if (i4 != 0) {
                z4 = true;
            } else {
                z4 = false;
            }
            if (i4 != 0) {
                bottomAppBar.m0 = 0;
                bottomAppBar.getMenu().clear();
                bottomAppBar.o(i4);
            }
            bottomAppBar.F(this.f6291b, this.f6292c, this.f6293d, z4);
        }
    }
}
