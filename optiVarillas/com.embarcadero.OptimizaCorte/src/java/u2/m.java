package U2;

import A1.P0;
import M.O;
import M.V;
import S0.A;
import S0.M0;
import S0.View$OnClickListenerC0274p;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.android.material.textfield.TextInputLayout;
import java.util.WeakHashMap;
import r2.C0783a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class m extends n {

    /* renamed from: e  reason: collision with root package name */
    public final int f2410e;
    public final int f;

    /* renamed from: g  reason: collision with root package name */
    public final TimeInterpolator f2411g;

    /* renamed from: h  reason: collision with root package name */
    public AutoCompleteTextView f2412h;

    /* renamed from: i  reason: collision with root package name */
    public final View$OnClickListenerC0274p f2413i;

    /* renamed from: j  reason: collision with root package name */
    public final l f2414j;

    /* renamed from: k  reason: collision with root package name */
    public final M0 f2415k;

    /* renamed from: l  reason: collision with root package name */
    public boolean f2416l;

    /* renamed from: m  reason: collision with root package name */
    public boolean f2417m;

    /* renamed from: n  reason: collision with root package name */
    public boolean f2418n;

    /* renamed from: o  reason: collision with root package name */
    public long f2419o;

    /* renamed from: p  reason: collision with root package name */
    public AccessibilityManager f2420p;

    /* renamed from: q  reason: collision with root package name */
    public ValueAnimator f2421q;

    /* renamed from: r  reason: collision with root package name */
    public ValueAnimator f2422r;

    /* JADX WARN: Type inference failed for: r0v1, types: [U2.l] */
    public m(com.google.android.material.textfield.a aVar) {
        super(aVar);
        this.f2413i = new View$OnClickListenerC0274p(2, this);
        this.f2414j = new View.OnFocusChangeListener() { // from class: U2.l
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z4) {
                m mVar = m.this;
                mVar.f2416l = z4;
                mVar.q();
                if (!z4) {
                    mVar.t(false);
                    mVar.f2417m = false;
                }
            }
        };
        this.f2415k = new M0(this);
        this.f2419o = Long.MAX_VALUE;
        this.f = J2.b.c(aVar.getContext(), 2130903817, 67);
        this.f2410e = J2.b.c(aVar.getContext(), 2130903817, 50);
        this.f2411g = J2.b.d(aVar.getContext(), 2130903826, C0783a.f5709a);
    }

    @Override // U2.n
    public final void a() {
        if (this.f2420p.isTouchExplorationEnabled() && P0.b(this.f2412h) && !this.f2426d.hasFocus()) {
            this.f2412h.dismissDropDown();
        }
        this.f2412h.post(new A(6, this));
    }

    @Override // U2.n
    public final int c() {
        return 2131820675;
    }

    @Override // U2.n
    public final int d() {
        return 2131165412;
    }

    @Override // U2.n
    public final View.OnFocusChangeListener e() {
        return this.f2414j;
    }

    @Override // U2.n
    public final View.OnClickListener f() {
        return this.f2413i;
    }

    @Override // U2.n
    public final M0 h() {
        return this.f2415k;
    }

    @Override // U2.n
    public final boolean i(int i4) {
        if (i4 != 0) {
            return true;
        }
        return false;
    }

    @Override // U2.n
    public final boolean j() {
        return this.f2416l;
    }

    @Override // U2.n
    public final boolean l() {
        return this.f2418n;
    }

    @Override // U2.n
    public final void m(EditText editText) {
        if (editText instanceof AutoCompleteTextView) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) editText;
            this.f2412h = autoCompleteTextView;
            autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() { // from class: U2.j
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean z4;
                    m mVar = m.this;
                    mVar.getClass();
                    if (motionEvent.getAction() == 1) {
                        long currentTimeMillis = System.currentTimeMillis() - mVar.f2419o;
                        if (currentTimeMillis >= 0 && currentTimeMillis <= 300) {
                            z4 = false;
                        } else {
                            z4 = true;
                        }
                        if (z4) {
                            mVar.f2417m = false;
                        }
                        mVar.u();
                        mVar.f2417m = true;
                        mVar.f2419o = System.currentTimeMillis();
                    }
                    return false;
                }
            });
            this.f2412h.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() { // from class: U2.k
                @Override // android.widget.AutoCompleteTextView.OnDismissListener
                public final void onDismiss() {
                    m mVar = m.this;
                    mVar.f2417m = true;
                    mVar.f2419o = System.currentTimeMillis();
                    mVar.t(false);
                }
            });
            this.f2412h.setThreshold(0);
            TextInputLayout textInputLayout = this.f2423a;
            textInputLayout.setErrorIconDrawable((Drawable) null);
            if (!P0.b(editText) && this.f2420p.isTouchExplorationEnabled()) {
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                this.f2426d.setImportantForAccessibility(2);
            }
            textInputLayout.setEndIconVisible(true);
            return;
        }
        throw new RuntimeException("EditText needs to be an AutoCompleteTextView if an Exposed Dropdown Menu is being used.");
    }

    @Override // U2.n
    public final void n(N.l lVar) {
        boolean e4;
        boolean b4 = P0.b(this.f2412h);
        AccessibilityNodeInfo accessibilityNodeInfo = lVar.f1743a;
        if (!b4) {
            accessibilityNodeInfo.setClassName(Spinner.class.getName());
        }
        if (Build.VERSION.SDK_INT >= 26) {
            e4 = accessibilityNodeInfo.isShowingHintText();
        } else {
            e4 = lVar.e(4);
        }
        if (e4) {
            lVar.j(null);
        }
    }

    @Override // U2.n
    @SuppressLint({"WrongConstant"})
    public final void o(AccessibilityEvent accessibilityEvent) {
        boolean z4;
        if (this.f2420p.isEnabled() && !P0.b(this.f2412h)) {
            if ((accessibilityEvent.getEventType() == 32768 || accessibilityEvent.getEventType() == 8) && this.f2418n && !this.f2412h.isPopupShowing()) {
                z4 = true;
            } else {
                z4 = false;
            }
            if (accessibilityEvent.getEventType() == 1 || z4) {
                u();
                this.f2417m = true;
                this.f2419o = System.currentTimeMillis();
            }
        }
    }

    @Override // U2.n
    public final void r() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        TimeInterpolator timeInterpolator = this.f2411g;
        ofFloat.setInterpolator(timeInterpolator);
        ofFloat.setDuration(this.f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: U2.i
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                m mVar = m.this;
                mVar.getClass();
                mVar.f2426d.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        this.f2422r = ofFloat;
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(1.0f, 0.0f);
        ofFloat2.setInterpolator(timeInterpolator);
        ofFloat2.setDuration(this.f2410e);
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: U2.i
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                m mVar = m.this;
                mVar.getClass();
                mVar.f2426d.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        this.f2421q = ofFloat2;
        ofFloat2.addListener(new d(this, 1));
        this.f2420p = (AccessibilityManager) this.f2425c.getSystemService("accessibility");
    }

    @Override // U2.n
    @SuppressLint({"ClickableViewAccessibility"})
    public final void s() {
        AutoCompleteTextView autoCompleteTextView = this.f2412h;
        if (autoCompleteTextView != null) {
            autoCompleteTextView.setOnTouchListener(null);
            this.f2412h.setOnDismissListener(null);
        }
    }

    public final void t(boolean z4) {
        if (this.f2418n != z4) {
            this.f2418n = z4;
            this.f2422r.cancel();
            this.f2421q.start();
        }
    }

    public final void u() {
        boolean z4;
        if (this.f2412h == null) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis() - this.f2419o;
        if (currentTimeMillis >= 0 && currentTimeMillis <= 300) {
            z4 = false;
        } else {
            z4 = true;
        }
        if (z4) {
            this.f2417m = false;
        }
        if (!this.f2417m) {
            t(!this.f2418n);
            if (this.f2418n) {
                this.f2412h.requestFocus();
                this.f2412h.showDropDown();
                return;
            }
            this.f2412h.dismissDropDown();
            return;
        }
        this.f2417m = false;
    }
}
