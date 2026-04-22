package U2;

import M.O;
import M.V;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.WeakHashMap;
import l.C0689B;
import r2.C0783a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class p {

    /* renamed from: A  reason: collision with root package name */
    public ColorStateList f2427A;

    /* renamed from: B  reason: collision with root package name */
    public Typeface f2428B;

    /* renamed from: a  reason: collision with root package name */
    public final int f2429a;

    /* renamed from: b  reason: collision with root package name */
    public final int f2430b;

    /* renamed from: c  reason: collision with root package name */
    public final int f2431c;

    /* renamed from: d  reason: collision with root package name */
    public final TimeInterpolator f2432d;

    /* renamed from: e  reason: collision with root package name */
    public final TimeInterpolator f2433e;
    public final TimeInterpolator f;

    /* renamed from: g  reason: collision with root package name */
    public final Context f2434g;

    /* renamed from: h  reason: collision with root package name */
    public final TextInputLayout f2435h;

    /* renamed from: i  reason: collision with root package name */
    public LinearLayout f2436i;

    /* renamed from: j  reason: collision with root package name */
    public int f2437j;

    /* renamed from: k  reason: collision with root package name */
    public FrameLayout f2438k;

    /* renamed from: l  reason: collision with root package name */
    public Animator f2439l;

    /* renamed from: m  reason: collision with root package name */
    public final float f2440m;

    /* renamed from: n  reason: collision with root package name */
    public int f2441n;

    /* renamed from: o  reason: collision with root package name */
    public int f2442o;

    /* renamed from: p  reason: collision with root package name */
    public CharSequence f2443p;

    /* renamed from: q  reason: collision with root package name */
    public boolean f2444q;

    /* renamed from: r  reason: collision with root package name */
    public C0689B f2445r;

    /* renamed from: s  reason: collision with root package name */
    public CharSequence f2446s;

    /* renamed from: t  reason: collision with root package name */
    public int f2447t;

    /* renamed from: u  reason: collision with root package name */
    public int f2448u;

    /* renamed from: v  reason: collision with root package name */
    public ColorStateList f2449v;

    /* renamed from: w  reason: collision with root package name */
    public CharSequence f2450w;

    /* renamed from: x  reason: collision with root package name */
    public boolean f2451x;

    /* renamed from: y  reason: collision with root package name */
    public C0689B f2452y;

    /* renamed from: z  reason: collision with root package name */
    public int f2453z;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class a extends AnimatorListenerAdapter {

        /* renamed from: a  reason: collision with root package name */
        public final /* synthetic */ int f2454a;

        /* renamed from: b  reason: collision with root package name */
        public final /* synthetic */ TextView f2455b;

        /* renamed from: c  reason: collision with root package name */
        public final /* synthetic */ int f2456c;

        /* renamed from: d  reason: collision with root package name */
        public final /* synthetic */ TextView f2457d;

        public a(int i4, TextView textView, int i5, TextView textView2) {
            this.f2454a = i4;
            this.f2455b = textView;
            this.f2456c = i5;
            this.f2457d = textView2;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator) {
            C0689B c0689b;
            int i4 = this.f2454a;
            p pVar = p.this;
            pVar.f2441n = i4;
            pVar.f2439l = null;
            TextView textView = this.f2455b;
            if (textView != null) {
                textView.setVisibility(4);
                if (this.f2456c == 1 && (c0689b = pVar.f2445r) != null) {
                    c0689b.setText((CharSequence) null);
                }
            }
            TextView textView2 = this.f2457d;
            if (textView2 != null) {
                textView2.setTranslationY(0.0f);
                textView2.setAlpha(1.0f);
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationStart(Animator animator) {
            TextView textView = this.f2457d;
            if (textView != null) {
                textView.setVisibility(0);
                textView.setAlpha(0.0f);
            }
        }
    }

    public p(TextInputLayout textInputLayout) {
        Context context = textInputLayout.getContext();
        this.f2434g = context;
        this.f2435h = textInputLayout;
        this.f2440m = context.getResources().getDimensionPixelSize(2131099794);
        this.f2429a = J2.b.c(context, 2130903818, 217);
        this.f2430b = J2.b.c(context, 2130903814, 167);
        this.f2431c = J2.b.c(context, 2130903818, 167);
        this.f2432d = J2.b.d(context, 2130903823, C0783a.f5712d);
        LinearInterpolator linearInterpolator = C0783a.f5709a;
        this.f2433e = J2.b.d(context, 2130903823, linearInterpolator);
        this.f = J2.b.d(context, 2130903826, linearInterpolator);
    }

    public final void a(TextView textView, int i4) {
        if (this.f2436i == null && this.f2438k == null) {
            Context context = this.f2434g;
            LinearLayout linearLayout = new LinearLayout(context);
            this.f2436i = linearLayout;
            linearLayout.setOrientation(0);
            LinearLayout linearLayout2 = this.f2436i;
            TextInputLayout textInputLayout = this.f2435h;
            textInputLayout.addView(linearLayout2, -1, -2);
            this.f2438k = new FrameLayout(context);
            this.f2436i.addView(this.f2438k, new LinearLayout.LayoutParams(0, -2, 1.0f));
            if (textInputLayout.getEditText() != null) {
                b();
            }
        }
        if (i4 != 0 && i4 != 1) {
            this.f2436i.addView(textView, new LinearLayout.LayoutParams(-2, -2));
        } else {
            this.f2438k.setVisibility(0);
            this.f2438k.addView(textView);
        }
        this.f2436i.setVisibility(0);
        this.f2437j++;
    }

    public final void b() {
        if (this.f2436i != null) {
            TextInputLayout textInputLayout = this.f2435h;
            if (textInputLayout.getEditText() != null) {
                EditText editText = textInputLayout.getEditText();
                Context context = this.f2434g;
                boolean d4 = L2.c.d(context);
                LinearLayout linearLayout = this.f2436i;
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                int paddingStart = editText.getPaddingStart();
                if (d4) {
                    paddingStart = context.getResources().getDimensionPixelSize(2131100234);
                }
                int dimensionPixelSize = context.getResources().getDimensionPixelSize(2131100233);
                if (d4) {
                    dimensionPixelSize = context.getResources().getDimensionPixelSize(2131100235);
                }
                int paddingEnd = editText.getPaddingEnd();
                if (d4) {
                    paddingEnd = context.getResources().getDimensionPixelSize(2131100234);
                }
                linearLayout.setPaddingRelative(paddingStart, dimensionPixelSize, paddingEnd, 0);
            }
        }
    }

    public final void c() {
        Animator animator = this.f2439l;
        if (animator != null) {
            animator.cancel();
        }
    }

    public final void d(ArrayList arrayList, boolean z4, TextView textView, int i4, int i5, int i6) {
        boolean z5;
        float f;
        long j4;
        TimeInterpolator timeInterpolator;
        if (textView != null && z4) {
            if (i4 == i6 || i4 == i5) {
                if (i6 == i4) {
                    z5 = true;
                } else {
                    z5 = false;
                }
                if (z5) {
                    f = 1.0f;
                } else {
                    f = 0.0f;
                }
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView, View.ALPHA, f);
                int i7 = this.f2431c;
                if (z5) {
                    j4 = this.f2430b;
                } else {
                    j4 = i7;
                }
                ofFloat.setDuration(j4);
                if (z5) {
                    timeInterpolator = this.f2433e;
                } else {
                    timeInterpolator = this.f;
                }
                ofFloat.setInterpolator(timeInterpolator);
                if (i4 == i6 && i5 != 0) {
                    ofFloat.setStartDelay(i7);
                }
                arrayList.add(ofFloat);
                if (i6 == i4 && i5 != 0) {
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textView, View.TRANSLATION_Y, -this.f2440m, 0.0f);
                    ofFloat2.setDuration(this.f2429a);
                    ofFloat2.setInterpolator(this.f2432d);
                    ofFloat2.setStartDelay(i7);
                    arrayList.add(ofFloat2);
                }
            }
        }
    }

    public final TextView e(int i4) {
        if (i4 != 1) {
            if (i4 != 2) {
                return null;
            }
            return this.f2452y;
        }
        return this.f2445r;
    }

    public final void f() {
        this.f2443p = null;
        c();
        if (this.f2441n == 1) {
            if (this.f2451x && !TextUtils.isEmpty(this.f2450w)) {
                this.f2442o = 2;
            } else {
                this.f2442o = 0;
            }
        }
        i(this.f2441n, this.f2442o, h(this.f2445r, ""));
    }

    public final void g(TextView textView, int i4) {
        FrameLayout frameLayout;
        LinearLayout linearLayout = this.f2436i;
        if (linearLayout == null) {
            return;
        }
        if ((i4 == 0 || i4 == 1) && (frameLayout = this.f2438k) != null) {
            frameLayout.removeView(textView);
        } else {
            linearLayout.removeView(textView);
        }
        int i5 = this.f2437j - 1;
        this.f2437j = i5;
        LinearLayout linearLayout2 = this.f2436i;
        if (i5 == 0) {
            linearLayout2.setVisibility(8);
        }
    }

    public final boolean h(TextView textView, CharSequence charSequence) {
        WeakHashMap<View, V> weakHashMap = O.f1526a;
        TextInputLayout textInputLayout = this.f2435h;
        if (textInputLayout.isLaidOut() && textInputLayout.isEnabled() && (this.f2442o != this.f2441n || textView == null || !TextUtils.equals(textView.getText(), charSequence))) {
            return true;
        }
        return false;
    }

    public final void i(int i4, int i5, boolean z4) {
        TextView e4;
        TextView e5;
        if (i4 == i5) {
            return;
        }
        if (z4) {
            AnimatorSet animatorSet = new AnimatorSet();
            this.f2439l = animatorSet;
            ArrayList arrayList = new ArrayList();
            d(arrayList, this.f2451x, this.f2452y, 2, i4, i5);
            d(arrayList, this.f2444q, this.f2445r, 1, i4, i5);
            B2.a.j(animatorSet, arrayList);
            animatorSet.addListener(new a(i5, e(i4), i4, e(i5)));
            animatorSet.start();
        } else if (i4 != i5) {
            if (i5 != 0 && (e5 = e(i5)) != null) {
                e5.setVisibility(0);
                e5.setAlpha(1.0f);
            }
            if (i4 != 0 && (e4 = e(i4)) != null) {
                e4.setVisibility(4);
                if (i4 == 1) {
                    e4.setText((CharSequence) null);
                }
            }
            this.f2441n = i5;
        }
        TextInputLayout textInputLayout = this.f2435h;
        textInputLayout.r();
        textInputLayout.u(z4, false);
        textInputLayout.x();
    }
}
