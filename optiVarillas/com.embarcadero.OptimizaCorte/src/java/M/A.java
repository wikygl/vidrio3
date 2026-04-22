package M;

import android.view.View;
import android.view.ViewTreeObserver;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class A implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {

    /* renamed from: j  reason: collision with root package name */
    public final View f1510j;

    /* renamed from: k  reason: collision with root package name */
    public ViewTreeObserver f1511k;

    /* renamed from: l  reason: collision with root package name */
    public final Runnable f1512l;

    public A(View view, Runnable runnable) {
        this.f1510j = view;
        this.f1511k = view.getViewTreeObserver();
        this.f1512l = runnable;
    }

    public static void a(View view, Runnable runnable) {
        if (view != null) {
            if (runnable != null) {
                A a4 = new A(view, runnable);
                view.getViewTreeObserver().addOnPreDrawListener(a4);
                view.addOnAttachStateChangeListener(a4);
                return;
            }
            throw new NullPointerException("runnable == null");
        }
        throw new NullPointerException("view == null");
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public final boolean onPreDraw() {
        boolean isAlive = this.f1511k.isAlive();
        View view = this.f1510j;
        if (isAlive) {
            this.f1511k.removeOnPreDrawListener(this);
        } else {
            view.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        view.removeOnAttachStateChangeListener(this);
        this.f1512l.run();
        return true;
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewAttachedToWindow(View view) {
        this.f1511k = view.getViewTreeObserver();
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewDetachedFromWindow(View view) {
        boolean isAlive = this.f1511k.isAlive();
        View view2 = this.f1510j;
        if (isAlive) {
            this.f1511k.removeOnPreDrawListener(this);
        } else {
            view2.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        view2.removeOnAttachStateChangeListener(this);
    }
}
