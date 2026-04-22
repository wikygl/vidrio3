package l;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import g.C0421c;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class H extends ListView {

    /* renamed from: j  reason: collision with root package name */
    public final Rect f4980j;

    /* renamed from: k  reason: collision with root package name */
    public int f4981k;

    /* renamed from: l  reason: collision with root package name */
    public int f4982l;

    /* renamed from: m  reason: collision with root package name */
    public int f4983m;

    /* renamed from: n  reason: collision with root package name */
    public int f4984n;

    /* renamed from: o  reason: collision with root package name */
    public int f4985o;

    /* renamed from: p  reason: collision with root package name */
    public d f4986p;

    /* renamed from: q  reason: collision with root package name */
    public boolean f4987q;

    /* renamed from: r  reason: collision with root package name */
    public final boolean f4988r;

    /* renamed from: s  reason: collision with root package name */
    public boolean f4989s;

    /* renamed from: t  reason: collision with root package name */
    public S.e f4990t;

    /* renamed from: u  reason: collision with root package name */
    public f f4991u;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
        public static void a(View view, float f, float f4) {
            view.drawableHotspotChanged(f, f4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public static final Method f4992a;

        /* renamed from: b  reason: collision with root package name */
        public static final Method f4993b;

        /* renamed from: c  reason: collision with root package name */
        public static final Method f4994c;

        /* renamed from: d  reason: collision with root package name */
        public static final boolean f4995d;

        static {
            try {
                Class cls = Integer.TYPE;
                Class cls2 = Float.TYPE;
                Method declaredMethod = AbsListView.class.getDeclaredMethod("positionSelector", cls, View.class, Boolean.TYPE, cls2, cls2);
                f4992a = declaredMethod;
                declaredMethod.setAccessible(true);
                Method declaredMethod2 = AdapterView.class.getDeclaredMethod("setSelectedPositionInt", cls);
                f4993b = declaredMethod2;
                declaredMethod2.setAccessible(true);
                Method declaredMethod3 = AdapterView.class.getDeclaredMethod("setNextSelectedPositionInt", cls);
                f4994c = declaredMethod3;
                declaredMethod3.setAccessible(true);
                f4995d = true;
            } catch (NoSuchMethodException e4) {
                e4.printStackTrace();
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class c {
        public static boolean a(AbsListView absListView) {
            return absListView.isSelectedChildViewEnabled();
        }

        public static void b(AbsListView absListView, boolean z4) {
            absListView.setSelectedChildViewEnabled(z4);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class d extends C0421c {

        /* renamed from: k  reason: collision with root package name */
        public boolean f4996k;

        @Override // g.C0421c, android.graphics.drawable.Drawable
        public final void draw(Canvas canvas) {
            if (this.f4996k) {
                super.draw(canvas);
            }
        }

        @Override // g.C0421c, android.graphics.drawable.Drawable
        public final void setHotspot(float f, float f4) {
            if (this.f4996k) {
                super.setHotspot(f, f4);
            }
        }

        @Override // g.C0421c, android.graphics.drawable.Drawable
        public final void setHotspotBounds(int i4, int i5, int i6, int i7) {
            if (this.f4996k) {
                super.setHotspotBounds(i4, i5, i6, i7);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public final boolean setState(int[] iArr) {
            if (this.f4996k) {
                return this.f3471j.setState(iArr);
            }
            return false;
        }

        @Override // g.C0421c, android.graphics.drawable.Drawable
        public final boolean setVisible(boolean z4, boolean z5) {
            if (this.f4996k) {
                return super.setVisible(z4, z5);
            }
            return false;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class e {

        /* renamed from: a  reason: collision with root package name */
        public static final Field f4997a;

        static {
            Field field = null;
            try {
                field = AbsListView.class.getDeclaredField("mIsChildViewEnabled");
                field.setAccessible(true);
            } catch (NoSuchFieldException e4) {
                e4.printStackTrace();
            }
            f4997a = field;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class f implements Runnable {
        public f() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            H h4 = H.this;
            h4.f4991u = null;
            h4.drawableStateChanged();
        }
    }

    public H(Context context, boolean z4) {
        super(context, null, 2130903425);
        this.f4980j = new Rect();
        this.f4981k = 0;
        this.f4982l = 0;
        this.f4983m = 0;
        this.f4984n = 0;
        this.f4988r = z4;
        setCacheColorHint(0);
    }

    private void setSelectorEnabled(boolean z4) {
        d dVar = this.f4986p;
        if (dVar != null) {
            dVar.f4996k = z4;
        }
    }

    public final int a(int i4, int i5) {
        int makeMeasureSpec;
        int listPaddingTop = getListPaddingTop();
        int listPaddingBottom = getListPaddingBottom();
        int dividerHeight = getDividerHeight();
        Drawable divider = getDivider();
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            return listPaddingTop + listPaddingBottom;
        }
        int i6 = listPaddingTop + listPaddingBottom;
        dividerHeight = (dividerHeight <= 0 || divider == null) ? 0 : 0;
        int count = adapter.getCount();
        View view = null;
        int i7 = 0;
        for (int i8 = 0; i8 < count; i8++) {
            int itemViewType = adapter.getItemViewType(i8);
            if (itemViewType != i7) {
                view = null;
                i7 = itemViewType;
            }
            view = adapter.getView(i8, view, this);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = generateDefaultLayoutParams();
                view.setLayoutParams(layoutParams);
            }
            int i9 = layoutParams.height;
            if (i9 > 0) {
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i9, 1073741824);
            } else {
                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            }
            view.measure(i4, makeMeasureSpec);
            view.forceLayout();
            if (i8 > 0) {
                i6 += dividerHeight;
            }
            i6 += view.getMeasuredHeight();
            if (i6 >= i5) {
                return i5;
            }
        }
        return i6;
    }

    /* JADX WARN: Removed duplicated region for block: B:80:0x0145 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:83:0x015d  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x0162  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0177  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean b(android.view.MotionEvent r17, int r18) {
        /*
            Method dump skipped, instructions count: 390
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: l.H.b(android.view.MotionEvent, int):boolean");
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.ViewGroup, android.view.View
    public final void dispatchDraw(Canvas canvas) {
        Drawable selector;
        Rect rect = this.f4980j;
        if (!rect.isEmpty() && (selector = getSelector()) != null) {
            selector.setBounds(rect);
            selector.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup, android.view.View
    public final void drawableStateChanged() {
        if (this.f4991u != null) {
            return;
        }
        super.drawableStateChanged();
        setSelectorEnabled(true);
        Drawable selector = getSelector();
        if (selector != null && this.f4989s && isPressed()) {
            selector.setState(getDrawableState());
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public final boolean hasFocus() {
        if (!this.f4988r && !super.hasFocus()) {
            return false;
        }
        return true;
    }

    @Override // android.view.View
    public final boolean hasWindowFocus() {
        if (!this.f4988r && !super.hasWindowFocus()) {
            return false;
        }
        return true;
    }

    @Override // android.view.View
    public final boolean isFocused() {
        if (!this.f4988r && !super.isFocused()) {
            return false;
        }
        return true;
    }

    @Override // android.view.View
    public final boolean isInTouchMode() {
        if ((this.f4988r && this.f4987q) || super.isInTouchMode()) {
            return true;
        }
        return false;
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public final void onDetachedFromWindow() {
        this.f4991u = null;
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        int i4 = Build.VERSION.SDK_INT;
        if (i4 < 26) {
            return super.onHoverEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 10 && this.f4991u == null) {
            f fVar = new f();
            this.f4991u = fVar;
            post(fVar);
        }
        boolean onHoverEvent = super.onHoverEvent(motionEvent);
        if (actionMasked != 9 && actionMasked != 7) {
            setSelection(-1);
        } else {
            int pointToPosition = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
            if (pointToPosition != -1 && pointToPosition != getSelectedItemPosition()) {
                View childAt = getChildAt(pointToPosition - getFirstVisiblePosition());
                if (childAt.isEnabled()) {
                    requestFocus();
                    if (i4 >= 30 && b.f4995d) {
                        try {
                            b.f4992a.invoke(this, Integer.valueOf(pointToPosition), childAt, Boolean.FALSE, -1, -1);
                            b.f4993b.invoke(this, Integer.valueOf(pointToPosition));
                            b.f4994c.invoke(this, Integer.valueOf(pointToPosition));
                        } catch (IllegalAccessException e4) {
                            e4.printStackTrace();
                        } catch (InvocationTargetException e5) {
                            e5.printStackTrace();
                        }
                    } else {
                        setSelectionFromTop(pointToPosition, childAt.getTop() - getTop());
                    }
                }
                Drawable selector = getSelector();
                if (selector != null && this.f4989s && isPressed()) {
                    selector.setState(getDrawableState());
                }
            }
        }
        return onHoverEvent;
    }

    @Override // android.widget.AbsListView, android.view.View
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.f4985o = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
        }
        f fVar = this.f4991u;
        if (fVar != null) {
            H h4 = H.this;
            h4.f4991u = null;
            h4.removeCallbacks(fVar);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setListSelectionHidden(boolean z4) {
        this.f4987q = z4;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [l.H$d, g.c] */
    @Override // android.widget.AbsListView
    public void setSelector(Drawable drawable) {
        d dVar;
        if (drawable != null) {
            ?? c0421c = new C0421c(drawable);
            c0421c.f4996k = true;
            dVar = c0421c;
        } else {
            dVar = null;
        }
        this.f4986p = dVar;
        super.setSelector(dVar);
        Rect rect = new Rect();
        if (drawable != null) {
            drawable.getPadding(rect);
        }
        this.f4981k = rect.left;
        this.f4982l = rect.top;
        this.f4983m = rect.right;
        this.f4984n = rect.bottom;
    }
}
