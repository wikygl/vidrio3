package l;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import d.C0376a;
import java.lang.reflect.Method;
import k.InterfaceC0683f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public class L implements InterfaceC0683f {

    /* renamed from: J  reason: collision with root package name */
    public static final Method f5011J;

    /* renamed from: K  reason: collision with root package name */
    public static final Method f5012K;

    /* renamed from: L  reason: collision with root package name */
    public static final Method f5013L;

    /* renamed from: E  reason: collision with root package name */
    public final Handler f5018E;

    /* renamed from: G  reason: collision with root package name */
    public Rect f5020G;

    /* renamed from: H  reason: collision with root package name */
    public boolean f5021H;

    /* renamed from: I  reason: collision with root package name */
    public final C0708q f5022I;

    /* renamed from: j  reason: collision with root package name */
    public final Context f5023j;

    /* renamed from: k  reason: collision with root package name */
    public ListAdapter f5024k;

    /* renamed from: l  reason: collision with root package name */
    public H f5025l;

    /* renamed from: o  reason: collision with root package name */
    public int f5028o;

    /* renamed from: p  reason: collision with root package name */
    public int f5029p;

    /* renamed from: r  reason: collision with root package name */
    public boolean f5031r;

    /* renamed from: s  reason: collision with root package name */
    public boolean f5032s;

    /* renamed from: t  reason: collision with root package name */
    public boolean f5033t;

    /* renamed from: w  reason: collision with root package name */
    public d f5036w;

    /* renamed from: x  reason: collision with root package name */
    public View f5037x;

    /* renamed from: y  reason: collision with root package name */
    public AdapterView.OnItemClickListener f5038y;

    /* renamed from: z  reason: collision with root package name */
    public AdapterView.OnItemSelectedListener f5039z;

    /* renamed from: m  reason: collision with root package name */
    public final int f5026m = -2;

    /* renamed from: n  reason: collision with root package name */
    public int f5027n = -2;

    /* renamed from: q  reason: collision with root package name */
    public final int f5030q = 1002;

    /* renamed from: u  reason: collision with root package name */
    public int f5034u = 0;

    /* renamed from: v  reason: collision with root package name */
    public final int f5035v = Integer.MAX_VALUE;

    /* renamed from: A  reason: collision with root package name */
    public final g f5014A = new g();

    /* renamed from: B  reason: collision with root package name */
    public final f f5015B = new f();

    /* renamed from: C  reason: collision with root package name */
    public final e f5016C = new e();

    /* renamed from: D  reason: collision with root package name */
    public final c f5017D = new c();

    /* renamed from: F  reason: collision with root package name */
    public final Rect f5019F = new Rect();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
        public static int a(PopupWindow popupWindow, View view, int i4, boolean z4) {
            return popupWindow.getMaxAvailableHeight(view, i4, z4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {
        public static void a(PopupWindow popupWindow, Rect rect) {
            popupWindow.setEpicenterBounds(rect);
        }

        public static void b(PopupWindow popupWindow, boolean z4) {
            popupWindow.setIsClippedToScreen(z4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class c implements Runnable {
        public c() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            H h4 = L.this.f5025l;
            if (h4 != null) {
                h4.setListSelectionHidden(true);
                h4.requestLayout();
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class d extends DataSetObserver {
        public d() {
        }

        @Override // android.database.DataSetObserver
        public final void onChanged() {
            L l2 = L.this;
            if (l2.f5022I.isShowing()) {
                l2.a();
            }
        }

        @Override // android.database.DataSetObserver
        public final void onInvalidated() {
            L.this.dismiss();
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class f implements View.OnTouchListener {
        public f() {
        }

        @Override // android.view.View.OnTouchListener
        public final boolean onTouch(View view, MotionEvent motionEvent) {
            C0708q c0708q;
            int action = motionEvent.getAction();
            int x4 = (int) motionEvent.getX();
            int y4 = (int) motionEvent.getY();
            L l2 = L.this;
            if (action == 0 && (c0708q = l2.f5022I) != null && c0708q.isShowing() && x4 >= 0 && x4 < l2.f5022I.getWidth() && y4 >= 0 && y4 < l2.f5022I.getHeight()) {
                l2.f5018E.postDelayed(l2.f5014A, 250L);
                return false;
            } else if (action == 1) {
                l2.f5018E.removeCallbacks(l2.f5014A);
                return false;
            } else {
                return false;
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class g implements Runnable {
        public g() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            L l2 = L.this;
            H h4 = l2.f5025l;
            if (h4 != null && h4.isAttachedToWindow() && l2.f5025l.getCount() > l2.f5025l.getChildCount() && l2.f5025l.getChildCount() <= l2.f5035v) {
                l2.f5022I.setInputMethodMode(2);
                l2.a();
            }
        }
    }

    static {
        if (Build.VERSION.SDK_INT <= 28) {
            try {
                f5011J = PopupWindow.class.getDeclaredMethod("setClipToScreenEnabled", Boolean.TYPE);
            } catch (NoSuchMethodException unused) {
                Log.i("ListPopupWindow", "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
            }
            try {
                f5013L = PopupWindow.class.getDeclaredMethod("setEpicenterBounds", Rect.class);
            } catch (NoSuchMethodException unused2) {
                Log.i("ListPopupWindow", "Could not find method setEpicenterBounds(Rect) on PopupWindow. Oh well.");
            }
        }
        if (Build.VERSION.SDK_INT <= 23) {
            try {
                f5012K = PopupWindow.class.getDeclaredMethod("getMaxAvailableHeight", View.class, Integer.TYPE, Boolean.TYPE);
            } catch (NoSuchMethodException unused3) {
                Log.i("ListPopupWindow", "Could not find method getMaxAvailableHeight(View, int, boolean) on PopupWindow. Oh well.");
            }
        }
    }

    /* JADX WARN: Type inference failed for: r1v9, types: [android.widget.PopupWindow, l.q] */
    public L(Context context, AttributeSet attributeSet, int i4, int i5) {
        Drawable drawable;
        int resourceId;
        this.f5023j = context;
        this.f5018E = new Handler(context.getMainLooper());
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0376a.f3142o, i4, i5);
        this.f5028o = obtainStyledAttributes.getDimensionPixelOffset(0, 0);
        int dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(1, 0);
        this.f5029p = dimensionPixelOffset;
        if (dimensionPixelOffset != 0) {
            this.f5031r = true;
        }
        obtainStyledAttributes.recycle();
        ?? popupWindow = new PopupWindow(context, attributeSet, i4, i5);
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, C0376a.f3146s, i4, i5);
        if (obtainStyledAttributes2.hasValue(2)) {
            S.f.a(popupWindow, obtainStyledAttributes2.getBoolean(2, false));
        }
        if (obtainStyledAttributes2.hasValue(0) && (resourceId = obtainStyledAttributes2.getResourceId(0, 0)) != 0) {
            drawable = B2.a.f(context, resourceId);
        } else {
            drawable = obtainStyledAttributes2.getDrawable(0);
        }
        popupWindow.setBackgroundDrawable(drawable);
        obtainStyledAttributes2.recycle();
        this.f5022I = popupWindow;
        popupWindow.setInputMethodMode(1);
    }

    @Override // k.InterfaceC0683f
    public final void a() {
        int i4;
        boolean z4;
        int a4;
        int makeMeasureSpec;
        int i5;
        int i6;
        boolean z5;
        H h4;
        int i7;
        int i8;
        int i9;
        int i10 = 0;
        H h5 = this.f5025l;
        C0708q c0708q = this.f5022I;
        Context context = this.f5023j;
        if (h5 == null) {
            H q4 = q(context, !this.f5021H);
            this.f5025l = q4;
            q4.setAdapter(this.f5024k);
            this.f5025l.setOnItemClickListener(this.f5038y);
            this.f5025l.setFocusable(true);
            this.f5025l.setFocusableInTouchMode(true);
            this.f5025l.setOnItemSelectedListener(new K(this));
            this.f5025l.setOnScrollListener(this.f5016C);
            AdapterView.OnItemSelectedListener onItemSelectedListener = this.f5039z;
            if (onItemSelectedListener != null) {
                this.f5025l.setOnItemSelectedListener(onItemSelectedListener);
            }
            c0708q.setContentView(this.f5025l);
        } else {
            ViewGroup viewGroup = (ViewGroup) c0708q.getContentView();
        }
        Drawable background = c0708q.getBackground();
        Rect rect = this.f5019F;
        if (background != null) {
            background.getPadding(rect);
            int i11 = rect.top;
            i4 = rect.bottom + i11;
            if (!this.f5031r) {
                this.f5029p = -i11;
            }
        } else {
            rect.setEmpty();
            i4 = 0;
        }
        if (c0708q.getInputMethodMode() == 2) {
            z4 = true;
        } else {
            z4 = false;
        }
        View view = this.f5037x;
        int i12 = this.f5029p;
        if (Build.VERSION.SDK_INT <= 23) {
            Method method = f5012K;
            if (method != null) {
                try {
                    a4 = ((Integer) method.invoke(c0708q, view, Integer.valueOf(i12), Boolean.valueOf(z4))).intValue();
                } catch (Exception unused) {
                    Log.i("ListPopupWindow", "Could not call getMaxAvailableHeightMethod(View, int, boolean) on PopupWindow. Using the public version.");
                }
            }
            a4 = c0708q.getMaxAvailableHeight(view, i12);
        } else {
            a4 = a.a(c0708q, view, i12, z4);
        }
        int i13 = this.f5026m;
        if (i13 == -1) {
            i6 = a4 + i4;
        } else {
            int i14 = this.f5027n;
            if (i14 != -2) {
                if (i14 != -1) {
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i14, 1073741824);
                } else {
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(context.getResources().getDisplayMetrics().widthPixels - (rect.left + rect.right), 1073741824);
                }
            } else {
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(context.getResources().getDisplayMetrics().widthPixels - (rect.left + rect.right), Integer.MIN_VALUE);
            }
            int a5 = this.f5025l.a(makeMeasureSpec, a4);
            if (a5 > 0) {
                i5 = this.f5025l.getPaddingBottom() + this.f5025l.getPaddingTop() + i4;
            } else {
                i5 = 0;
            }
            i6 = a5 + i5;
        }
        if (this.f5022I.getInputMethodMode() == 2) {
            z5 = true;
        } else {
            z5 = false;
        }
        S.f.b(c0708q, this.f5030q);
        if (c0708q.isShowing()) {
            if (!this.f5037x.isAttachedToWindow()) {
                return;
            }
            int i15 = this.f5027n;
            if (i15 == -1) {
                i15 = -1;
            } else if (i15 == -2) {
                i15 = this.f5037x.getWidth();
            }
            if (i13 == -1) {
                if (z5) {
                    i13 = i6;
                } else {
                    i13 = -1;
                }
                if (z5) {
                    if (this.f5027n == -1) {
                        i9 = -1;
                    } else {
                        i9 = 0;
                    }
                    c0708q.setWidth(i9);
                    c0708q.setHeight(0);
                } else {
                    if (this.f5027n == -1) {
                        i10 = -1;
                    }
                    c0708q.setWidth(i10);
                    c0708q.setHeight(-1);
                }
            } else if (i13 == -2) {
                i13 = i6;
            }
            c0708q.setOutsideTouchable(true);
            View view2 = this.f5037x;
            int i16 = this.f5028o;
            int i17 = this.f5029p;
            if (i15 < 0) {
                i7 = -1;
            } else {
                i7 = i15;
            }
            if (i13 < 0) {
                i8 = -1;
            } else {
                i8 = i13;
            }
            c0708q.update(view2, i16, i17, i7, i8);
            return;
        }
        int i18 = this.f5027n;
        if (i18 == -1) {
            i18 = -1;
        } else if (i18 == -2) {
            i18 = this.f5037x.getWidth();
        }
        if (i13 == -1) {
            i13 = -1;
        } else if (i13 == -2) {
            i13 = i6;
        }
        c0708q.setWidth(i18);
        c0708q.setHeight(i13);
        if (Build.VERSION.SDK_INT <= 28) {
            Method method2 = f5011J;
            if (method2 != null) {
                try {
                    method2.invoke(c0708q, Boolean.TRUE);
                } catch (Exception unused2) {
                    Log.i("ListPopupWindow", "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
                }
            }
        } else {
            b.b(c0708q, true);
        }
        c0708q.setOutsideTouchable(true);
        c0708q.setTouchInterceptor(this.f5015B);
        if (this.f5033t) {
            S.f.a(c0708q, this.f5032s);
        }
        if (Build.VERSION.SDK_INT <= 28) {
            Method method3 = f5013L;
            if (method3 != null) {
                try {
                    method3.invoke(c0708q, this.f5020G);
                } catch (Exception e4) {
                    Log.e("ListPopupWindow", "Could not invoke setEpicenterBounds on PopupWindow", e4);
                }
            }
        } else {
            b.a(c0708q, this.f5020G);
        }
        c0708q.showAsDropDown(this.f5037x, this.f5028o, this.f5029p, this.f5034u);
        this.f5025l.setSelection(-1);
        if ((!this.f5021H || this.f5025l.isInTouchMode()) && (h4 = this.f5025l) != null) {
            h4.setListSelectionHidden(true);
            h4.requestLayout();
        }
        if (!this.f5021H) {
            this.f5018E.post(this.f5017D);
        }
    }

    public final int b() {
        return this.f5028o;
    }

    @Override // k.InterfaceC0683f
    public final boolean c() {
        return this.f5022I.isShowing();
    }

    @Override // k.InterfaceC0683f
    public final void dismiss() {
        C0708q c0708q = this.f5022I;
        c0708q.dismiss();
        c0708q.setContentView(null);
        this.f5025l = null;
        this.f5018E.removeCallbacks(this.f5014A);
    }

    public final Drawable e() {
        return this.f5022I.getBackground();
    }

    @Override // k.InterfaceC0683f
    public final H g() {
        return this.f5025l;
    }

    public final void h(Drawable drawable) {
        this.f5022I.setBackgroundDrawable(drawable);
    }

    public final void i(int i4) {
        this.f5029p = i4;
        this.f5031r = true;
    }

    public final void k(int i4) {
        this.f5028o = i4;
    }

    public final int m() {
        if (!this.f5031r) {
            return 0;
        }
        return this.f5029p;
    }

    public void p(ListAdapter listAdapter) {
        d dVar = this.f5036w;
        if (dVar == null) {
            this.f5036w = new d();
        } else {
            ListAdapter listAdapter2 = this.f5024k;
            if (listAdapter2 != null) {
                listAdapter2.unregisterDataSetObserver(dVar);
            }
        }
        this.f5024k = listAdapter;
        if (listAdapter != null) {
            listAdapter.registerDataSetObserver(this.f5036w);
        }
        H h4 = this.f5025l;
        if (h4 != null) {
            h4.setAdapter(this.f5024k);
        }
    }

    public H q(Context context, boolean z4) {
        return new H(context, z4);
    }

    public final void r(int i4) {
        Drawable background = this.f5022I.getBackground();
        if (background != null) {
            Rect rect = this.f5019F;
            background.getPadding(rect);
            this.f5027n = rect.left + rect.right + i4;
            return;
        }
        this.f5027n = i4;
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class e implements AbsListView.OnScrollListener {
        public e() {
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public final void onScrollStateChanged(AbsListView absListView, int i4) {
            if (i4 == 1) {
                L l2 = L.this;
                if (l2.f5022I.getInputMethodMode() != 2 && l2.f5022I.getContentView() != null) {
                    Handler handler = l2.f5018E;
                    g gVar = l2.f5014A;
                    handler.removeCallbacks(gVar);
                    gVar.run();
                }
            }
        }

        @Override // android.widget.AbsListView.OnScrollListener
        public final void onScroll(AbsListView absListView, int i4, int i5, int i6) {
        }
    }
}
