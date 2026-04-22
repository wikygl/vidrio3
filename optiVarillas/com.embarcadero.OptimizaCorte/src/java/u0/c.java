package u0;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import u0.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c extends u {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a extends AnimatorListenerAdapter implements f.d {

        /* renamed from: a  reason: collision with root package name */
        public final View f5944a;

        /* renamed from: b  reason: collision with root package name */
        public boolean f5945b = false;

        public a(View view) {
            this.f5944a = view;
        }

        @Override // u0.f.d
        public final void b() {
            float f;
            View view = this.f5944a;
            if (view.getVisibility() == 0) {
                f = o.f6003a.a(view);
            } else {
                f = 0.0f;
            }
            view.setTag(2131231300, Float.valueOf(f));
        }

        @Override // u0.f.d
        public final void c() {
            this.f5944a.setTag(2131231300, null);
        }

        @Override // u0.f.d
        public final void d(f fVar) {
            throw null;
        }

        @Override // u0.f.d
        public final void e(f fVar) {
        }

        @Override // u0.f.d
        public final void f(f fVar) {
            throw null;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationCancel(Animator animator) {
            o.f6003a.c(this.f5944a, 1.0f);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator) {
            onAnimationEnd(animator, false);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationStart(Animator animator) {
            View view = this.f5944a;
            if (view.hasOverlappingRendering() && view.getLayerType() == 0) {
                this.f5945b = true;
                view.setLayerType(2, null);
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator, boolean z4) {
            boolean z5 = this.f5945b;
            View view = this.f5944a;
            if (z5) {
                view.setLayerType(0, null);
            }
            if (z4) {
                return;
            }
            q qVar = o.f6003a;
            qVar.c(view, 1.0f);
            qVar.getClass();
        }

        @Override // u0.f.d
        public final void a(f fVar) {
        }

        @Override // u0.f.d
        public final void g(f fVar) {
        }
    }

    public c(int i4) {
        this.f6014J = i4;
    }

    public static float K(m mVar, float f) {
        Float f4;
        if (mVar != null && (f4 = (Float) mVar.f5999a.get("android:fade:transitionAlpha")) != null) {
            return f4.floatValue();
        }
        return f;
    }

    public final ObjectAnimator J(View view, float f, float f4) {
        if (f == f4) {
            return null;
        }
        o.f6003a.c(view, f);
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, o.f6004b, f4);
        a aVar = new a(view);
        ofFloat.addListener(aVar);
        o().a(aVar);
        return ofFloat;
    }

    @Override // u0.f
    public final void g(m mVar) {
        u.H(mVar);
        View view = mVar.f6000b;
        Float f = (Float) view.getTag(2131231300);
        if (f == null) {
            if (view.getVisibility() == 0) {
                f = Float.valueOf(o.f6003a.a(view));
            } else {
                f = Float.valueOf(0.0f);
            }
        }
        mVar.f5999a.put("android:fade:transitionAlpha", f);
    }
}
