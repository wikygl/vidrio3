package U2;

import S0.View$OnClickListenerC0272o;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.internal.CheckableImageButton;
import r2.C0783a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class f extends n {

    /* renamed from: e  reason: collision with root package name */
    public final int f2395e;
    public final int f;

    /* renamed from: g  reason: collision with root package name */
    public final TimeInterpolator f2396g;

    /* renamed from: h  reason: collision with root package name */
    public final TimeInterpolator f2397h;

    /* renamed from: i  reason: collision with root package name */
    public EditText f2398i;

    /* renamed from: j  reason: collision with root package name */
    public final View$OnClickListenerC0272o f2399j;

    /* renamed from: k  reason: collision with root package name */
    public final a f2400k;

    /* renamed from: l  reason: collision with root package name */
    public AnimatorSet f2401l;

    /* renamed from: m  reason: collision with root package name */
    public ValueAnimator f2402m;

    /* JADX WARN: Type inference failed for: r0v1, types: [U2.a] */
    public f(com.google.android.material.textfield.a aVar) {
        super(aVar);
        this.f2399j = new View$OnClickListenerC0272o(2, this);
        this.f2400k = new View.OnFocusChangeListener() { // from class: U2.a
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z4) {
                f fVar = f.this;
                fVar.t(fVar.u());
            }
        };
        this.f2395e = J2.b.c(aVar.getContext(), 2130903817, 100);
        this.f = J2.b.c(aVar.getContext(), 2130903817, 150);
        this.f2396g = J2.b.d(aVar.getContext(), 2130903826, C0783a.f5709a);
        this.f2397h = J2.b.d(aVar.getContext(), 2130903824, C0783a.f5712d);
    }

    @Override // U2.n
    public final void a() {
        if (this.f2424b.y != null) {
            return;
        }
        t(u());
    }

    @Override // U2.n
    public final int c() {
        return 2131820618;
    }

    @Override // U2.n
    public final int d() {
        return 2131165415;
    }

    @Override // U2.n
    public final View.OnFocusChangeListener e() {
        return this.f2400k;
    }

    @Override // U2.n
    public final View.OnClickListener f() {
        return this.f2399j;
    }

    @Override // U2.n
    public final View.OnFocusChangeListener g() {
        return this.f2400k;
    }

    @Override // U2.n
    public final void m(EditText editText) {
        this.f2398i = editText;
        this.f2423a.setEndIconVisible(u());
    }

    @Override // U2.n
    public final void p(boolean z4) {
        if (this.f2424b.y == null) {
            return;
        }
        t(z4);
    }

    @Override // U2.n
    public final void r() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.8f, 1.0f);
        ofFloat.setInterpolator(this.f2397h);
        ofFloat.setDuration(this.f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: U2.c
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                f fVar = f.this;
                fVar.getClass();
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                CheckableImageButton checkableImageButton = fVar.f2426d;
                checkableImageButton.setScaleX(floatValue);
                checkableImageButton.setScaleY(floatValue);
            }
        });
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        TimeInterpolator timeInterpolator = this.f2396g;
        ofFloat2.setInterpolator(timeInterpolator);
        int i4 = this.f2395e;
        ofFloat2.setDuration(i4);
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: U2.b
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                f fVar = f.this;
                fVar.getClass();
                fVar.f2426d.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        this.f2401l = animatorSet;
        animatorSet.playTogether(ofFloat, ofFloat2);
        this.f2401l.addListener(new d(this, 0));
        ValueAnimator ofFloat3 = ValueAnimator.ofFloat(1.0f, 0.0f);
        ofFloat3.setInterpolator(timeInterpolator);
        ofFloat3.setDuration(i4);
        ofFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: U2.b
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                f fVar = f.this;
                fVar.getClass();
                fVar.f2426d.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        this.f2402m = ofFloat3;
        ofFloat3.addListener(new e(0, this));
    }

    @Override // U2.n
    public final void s() {
        EditText editText = this.f2398i;
        if (editText != null) {
            editText.post(new Q2.g(4, this));
        }
    }

    public final void t(boolean z4) {
        boolean z5;
        if (this.f2424b.d() == z4) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z4 && !this.f2401l.isRunning()) {
            this.f2402m.cancel();
            this.f2401l.start();
            if (z5) {
                this.f2401l.end();
            }
        } else if (!z4) {
            this.f2401l.cancel();
            this.f2402m.start();
            if (z5) {
                this.f2402m.end();
            }
        }
    }

    public final boolean u() {
        EditText editText = this.f2398i;
        if (editText != null && ((editText.hasFocus() || this.f2426d.hasFocus()) && this.f2398i.getText().length() > 0)) {
            return true;
        }
        return false;
    }
}
