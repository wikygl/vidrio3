package l;

import M.Q;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class f0 implements View.OnLongClickListener, View.OnHoverListener, View.OnAttachStateChangeListener {

    /* renamed from: t  reason: collision with root package name */
    public static f0 f5125t;

    /* renamed from: u  reason: collision with root package name */
    public static f0 f5126u;

    /* renamed from: j  reason: collision with root package name */
    public final View f5127j;

    /* renamed from: k  reason: collision with root package name */
    public final CharSequence f5128k;

    /* renamed from: l  reason: collision with root package name */
    public final int f5129l;

    /* renamed from: m  reason: collision with root package name */
    public final Q2.g f5130m = new Q2.g(6, this);

    /* renamed from: n  reason: collision with root package name */
    public final S0.A f5131n = new S0.A(7, this);

    /* renamed from: o  reason: collision with root package name */
    public int f5132o;

    /* renamed from: p  reason: collision with root package name */
    public int f5133p;

    /* renamed from: q  reason: collision with root package name */
    public g0 f5134q;

    /* renamed from: r  reason: collision with root package name */
    public boolean f5135r;

    /* renamed from: s  reason: collision with root package name */
    public boolean f5136s;

    public f0(View view, CharSequence charSequence) {
        int scaledTouchSlop;
        this.f5127j = view;
        this.f5128k = charSequence;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(view.getContext());
        Method method = M.Q.f1546a;
        if (Build.VERSION.SDK_INT >= 28) {
            scaledTouchSlop = Q.b.a(viewConfiguration);
        } else {
            scaledTouchSlop = viewConfiguration.getScaledTouchSlop() / 2;
        }
        this.f5129l = scaledTouchSlop;
        this.f5136s = true;
        view.setOnLongClickListener(this);
        view.setOnHoverListener(this);
    }

    public static void b(f0 f0Var) {
        f0 f0Var2 = f5125t;
        if (f0Var2 != null) {
            f0Var2.f5127j.removeCallbacks(f0Var2.f5130m);
        }
        f5125t = f0Var;
        if (f0Var != null) {
            f0Var.f5127j.postDelayed(f0Var.f5130m, ViewConfiguration.getLongPressTimeout());
        }
    }

    public final void a() {
        f0 f0Var = f5126u;
        View view = this.f5127j;
        if (f0Var == this) {
            f5126u = null;
            g0 g0Var = this.f5134q;
            if (g0Var != null) {
                View view2 = g0Var.f5142b;
                if (view2.getParent() != null) {
                    ((WindowManager) g0Var.f5141a.getSystemService("window")).removeView(view2);
                }
                this.f5134q = null;
                this.f5136s = true;
                view.removeOnAttachStateChangeListener(this);
            } else {
                Log.e("TooltipCompatHandler", "sActiveHandler.mPopup == null");
            }
        }
        if (f5125t == this) {
            b(null);
        }
        view.removeCallbacks(this.f5131n);
    }

    public final void c(boolean z4) {
        int height;
        int i4;
        int i5;
        String str;
        int i6;
        String str2;
        int i7;
        long longPressTimeout;
        long j4;
        long j5;
        View view = this.f5127j;
        if (!view.isAttachedToWindow()) {
            return;
        }
        b(null);
        f0 f0Var = f5126u;
        if (f0Var != null) {
            f0Var.a();
        }
        f5126u = this;
        this.f5135r = z4;
        g0 g0Var = new g0(view.getContext());
        this.f5134q = g0Var;
        int i8 = this.f5132o;
        int i9 = this.f5133p;
        boolean z5 = this.f5135r;
        View view2 = g0Var.f5142b;
        ViewParent parent = view2.getParent();
        Context context = g0Var.f5141a;
        if (parent != null && view2.getParent() != null) {
            ((WindowManager) context.getSystemService("window")).removeView(view2);
        }
        g0Var.f5143c.setText(this.f5128k);
        WindowManager.LayoutParams layoutParams = g0Var.f5144d;
        layoutParams.token = view.getApplicationWindowToken();
        int dimensionPixelOffset = context.getResources().getDimensionPixelOffset(2131100462);
        if (view.getWidth() < dimensionPixelOffset) {
            i8 = view.getWidth() / 2;
        }
        if (view.getHeight() >= dimensionPixelOffset) {
            int dimensionPixelOffset2 = context.getResources().getDimensionPixelOffset(2131100461);
            height = i9 + dimensionPixelOffset2;
            i4 = i9 - dimensionPixelOffset2;
        } else {
            height = view.getHeight();
            i4 = 0;
        }
        layoutParams.gravity = 49;
        Resources resources = context.getResources();
        if (z5) {
            i5 = 2131100465;
        } else {
            i5 = 2131100464;
        }
        int dimensionPixelOffset3 = resources.getDimensionPixelOffset(i5);
        View rootView = view.getRootView();
        ViewGroup.LayoutParams layoutParams2 = rootView.getLayoutParams();
        if (!(layoutParams2 instanceof WindowManager.LayoutParams) || ((WindowManager.LayoutParams) layoutParams2).type != 2) {
            Context context2 = view.getContext();
            while (true) {
                if (!(context2 instanceof ContextWrapper)) {
                    break;
                } else if (context2 instanceof Activity) {
                    rootView = ((Activity) context2).getWindow().getDecorView();
                    break;
                } else {
                    context2 = ((ContextWrapper) context2).getBaseContext();
                }
            }
        }
        if (rootView == null) {
            Log.e("TooltipPopup", "Cannot find app view");
            str2 = "window";
        } else {
            Rect rect = g0Var.f5145e;
            rootView.getWindowVisibleDisplayFrame(rect);
            if (rect.left >= 0 || rect.top >= 0) {
                str = "window";
                i6 = 0;
            } else {
                Resources resources2 = context.getResources();
                str = "window";
                int identifier = resources2.getIdentifier("status_bar_height", "dimen", "android");
                if (identifier != 0) {
                    i7 = resources2.getDimensionPixelSize(identifier);
                } else {
                    i7 = 0;
                }
                DisplayMetrics displayMetrics = resources2.getDisplayMetrics();
                i6 = 0;
                rect.set(0, i7, displayMetrics.widthPixels, displayMetrics.heightPixels);
            }
            int[] iArr = g0Var.f5146g;
            rootView.getLocationOnScreen(iArr);
            int[] iArr2 = g0Var.f;
            view.getLocationOnScreen(iArr2);
            int i10 = iArr2[i6] - iArr[i6];
            iArr2[i6] = i10;
            iArr2[1] = iArr2[1] - iArr[1];
            layoutParams.x = (i10 + i8) - (rootView.getWidth() / 2);
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i6, i6);
            view2.measure(makeMeasureSpec, makeMeasureSpec);
            int measuredHeight = view2.getMeasuredHeight();
            int i11 = iArr2[1];
            int i12 = ((i4 + i11) - dimensionPixelOffset3) - measuredHeight;
            int i13 = i11 + height + dimensionPixelOffset3;
            if (z5) {
                if (i12 >= 0) {
                    layoutParams.y = i12;
                } else {
                    layoutParams.y = i13;
                }
            } else if (measuredHeight + i13 <= rect.height()) {
                layoutParams.y = i13;
            } else {
                layoutParams.y = i12;
            }
            str2 = str;
        }
        ((WindowManager) context.getSystemService(str2)).addView(view2, layoutParams);
        view.addOnAttachStateChangeListener(this);
        if (this.f5135r) {
            j5 = 2500;
        } else {
            WeakHashMap<View, M.V> weakHashMap = M.O.f1526a;
            if ((view.getWindowSystemUiVisibility() & 1) == 1) {
                longPressTimeout = ViewConfiguration.getLongPressTimeout();
                j4 = 3000;
            } else {
                longPressTimeout = ViewConfiguration.getLongPressTimeout();
                j4 = 15000;
            }
            j5 = j4 - longPressTimeout;
        }
        S0.A a4 = this.f5131n;
        view.removeCallbacks(a4);
        view.postDelayed(a4, j5);
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0064, code lost:
        if (java.lang.Math.abs(r5 - r3.f5133p) <= r2) goto L17;
     */
    @Override // android.view.View.OnHoverListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean onHover(android.view.View r4, android.view.MotionEvent r5) {
        /*
            r3 = this;
            l.g0 r4 = r3.f5134q
            r0 = 0
            if (r4 == 0) goto La
            boolean r4 = r3.f5135r
            if (r4 == 0) goto La
            return r0
        La:
            android.view.View r4 = r3.f5127j
            android.content.Context r1 = r4.getContext()
            java.lang.String r2 = "accessibility"
            java.lang.Object r1 = r1.getSystemService(r2)
            android.view.accessibility.AccessibilityManager r1 = (android.view.accessibility.AccessibilityManager) r1
            boolean r2 = r1.isEnabled()
            if (r2 == 0) goto L25
            boolean r1 = r1.isTouchExplorationEnabled()
            if (r1 == 0) goto L25
            return r0
        L25:
            int r1 = r5.getAction()
            r2 = 7
            if (r1 == r2) goto L38
            r4 = 10
            if (r1 == r4) goto L31
            goto L6f
        L31:
            r4 = 1
            r3.f5136s = r4
            r3.a()
            goto L6f
        L38:
            boolean r4 = r4.isEnabled()
            if (r4 == 0) goto L6f
            l.g0 r4 = r3.f5134q
            if (r4 != 0) goto L6f
            float r4 = r5.getX()
            int r4 = (int) r4
            float r5 = r5.getY()
            int r5 = (int) r5
            boolean r1 = r3.f5136s
            if (r1 != 0) goto L66
            int r1 = r3.f5132o
            int r1 = r4 - r1
            int r1 = java.lang.Math.abs(r1)
            int r2 = r3.f5129l
            if (r1 > r2) goto L66
            int r1 = r3.f5133p
            int r1 = r5 - r1
            int r1 = java.lang.Math.abs(r1)
            if (r1 <= r2) goto L6f
        L66:
            r3.f5132o = r4
            r3.f5133p = r5
            r3.f5136s = r0
            b(r3)
        L6f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: l.f0.onHover(android.view.View, android.view.MotionEvent):boolean");
    }

    @Override // android.view.View.OnLongClickListener
    public final boolean onLongClick(View view) {
        this.f5132o = view.getWidth() / 2;
        this.f5133p = view.getHeight() / 2;
        c(true);
        return true;
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewDetachedFromWindow(View view) {
        a();
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewAttachedToWindow(View view) {
    }
}
