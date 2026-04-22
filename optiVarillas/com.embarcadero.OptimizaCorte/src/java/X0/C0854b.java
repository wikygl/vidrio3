package x0;

import C.a;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import com.google.android.gms.internal.ads.gI;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Comparator;

/* renamed from: x0.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0854b extends ViewGroup {

    /* renamed from: D  reason: collision with root package name */
    public static final int[] f6436D = {16842931};

    /* renamed from: A  reason: collision with root package name */
    public f f6437A;

    /* renamed from: B  reason: collision with root package name */
    public ArrayList f6438B;

    /* renamed from: C  reason: collision with root package name */
    public int f6439C;

    /* renamed from: j  reason: collision with root package name */
    public Parcelable f6440j;

    /* renamed from: k  reason: collision with root package name */
    public int f6441k;

    /* renamed from: l  reason: collision with root package name */
    public Drawable f6442l;

    /* renamed from: m  reason: collision with root package name */
    public int f6443m;

    /* renamed from: n  reason: collision with root package name */
    public boolean f6444n;

    /* renamed from: o  reason: collision with root package name */
    public boolean f6445o;

    /* renamed from: p  reason: collision with root package name */
    public int f6446p;

    /* renamed from: q  reason: collision with root package name */
    public boolean f6447q;

    /* renamed from: r  reason: collision with root package name */
    public boolean f6448r;

    /* renamed from: s  reason: collision with root package name */
    public int f6449s;

    /* renamed from: t  reason: collision with root package name */
    public float f6450t;

    /* renamed from: u  reason: collision with root package name */
    public float f6451u;

    /* renamed from: v  reason: collision with root package name */
    public float f6452v;

    /* renamed from: w  reason: collision with root package name */
    public int f6453w;

    /* renamed from: x  reason: collision with root package name */
    public VelocityTracker f6454x;

    /* renamed from: y  reason: collision with root package name */
    public boolean f6455y;

    /* renamed from: z  reason: collision with root package name */
    public ArrayList f6456z;

    /* renamed from: x0.b$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a implements Comparator<c> {
        @Override // java.util.Comparator
        public final int compare(c cVar, c cVar2) {
            cVar.getClass();
            cVar2.getClass();
            return 0;
        }
    }

    @Target({ElementType.TYPE})
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    /* renamed from: x0.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public @interface InterfaceC0080b {
    }

    /* renamed from: x0.b$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class c {
    }

    /* renamed from: x0.b$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class d extends ViewGroup.LayoutParams {

        /* renamed from: a  reason: collision with root package name */
        public boolean f6457a;

        /* renamed from: b  reason: collision with root package name */
        public int f6458b;

        public d() {
            super(-1, -1);
        }
    }

    /* renamed from: x0.b$e */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface e {
        void a(C0854b c0854b);
    }

    /* renamed from: x0.b$f */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public interface f {
        void a(int i4);
    }

    /* renamed from: x0.b$g */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class g extends U.a {
        public static final Parcelable.Creator<g> CREATOR = new Object();

        /* renamed from: l  reason: collision with root package name */
        public int f6459l;

        /* renamed from: m  reason: collision with root package name */
        public final Parcelable f6460m;

        /* renamed from: x0.b$g$a */
        /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
        public static class a implements Parcelable.ClassLoaderCreator<g> {
            @Override // android.os.Parcelable.ClassLoaderCreator
            public final g createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new g(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public final Object[] newArray(int i4) {
                return new g[i4];
            }

            @Override // android.os.Parcelable.Creator
            public final Object createFromParcel(Parcel parcel) {
                return new g(parcel, null);
            }
        }

        public g(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            classLoader = classLoader == null ? g.class.getClassLoader() : classLoader;
            this.f6459l = parcel.readInt();
            this.f6460m = parcel.readParcelable(classLoader);
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder("FragmentPager.SavedState{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" position=");
            return gI.a(sb, this.f6459l, "}");
        }

        @Override // U.a, android.os.Parcelable
        public final void writeToParcel(Parcel parcel, int i4) {
            super.writeToParcel(parcel, i4);
            parcel.writeInt(this.f6459l);
            parcel.writeParcelable(this.f6460m, i4);
        }
    }

    public static boolean b(int i4, int i5, int i6, View view, boolean z4) {
        int i7;
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                int i8 = i5 + scrollX;
                if (i8 >= childAt.getLeft() && i8 < childAt.getRight() && (i7 = i6 + scrollY) >= childAt.getTop() && i7 < childAt.getBottom() && b(i4, i8 - childAt.getLeft(), i7 - childAt.getTop(), childAt, true)) {
                    return true;
                }
            }
        }
        if (z4 && view.canScrollHorizontally(-i4)) {
            return true;
        }
        return false;
    }

    private int getClientWidth() {
        return (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
    }

    private void setScrollingCacheEnabled(boolean z4) {
        if (this.f6445o != z4) {
            this.f6445o = z4;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00a3  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00a6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean a(int r6) {
        /*
            r5 = this;
            android.view.View r0 = r5.findFocus()
            r1 = 0
            if (r0 != r5) goto L9
        L7:
            r0 = r1
            goto L60
        L9:
            if (r0 == 0) goto L60
            android.view.ViewParent r2 = r0.getParent()
        Lf:
            boolean r3 = r2 instanceof android.view.ViewGroup
            if (r3 == 0) goto L1b
            if (r2 != r5) goto L16
            goto L60
        L16:
            android.view.ViewParent r2 = r2.getParent()
            goto Lf
        L1b:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.Class r3 = r0.getClass()
            java.lang.String r3 = r3.getSimpleName()
            r2.append(r3)
            android.view.ViewParent r0 = r0.getParent()
        L2f:
            boolean r3 = r0 instanceof android.view.ViewGroup
            if (r3 == 0) goto L48
            java.lang.String r3 = " => "
            r2.append(r3)
            java.lang.Class r3 = r0.getClass()
            java.lang.String r3 = r3.getSimpleName()
            r2.append(r3)
            android.view.ViewParent r0 = r0.getParent()
            goto L2f
        L48:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r3 = "arrowScroll tried to find focus based on non-child current focused view "
            r0.<init>(r3)
            java.lang.String r2 = r2.toString()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "ViewPager"
            android.util.Log.e(r2, r0)
            goto L7
        L60:
            android.view.FocusFinder r1 = android.view.FocusFinder.getInstance()
            android.view.View r1 = r1.findNextFocus(r5, r0, r6)
            r2 = 0
            r3 = 17
            if (r1 == 0) goto La1
            if (r1 == r0) goto La1
            if (r6 != r3) goto L87
            android.graphics.Rect r3 = r5.c(r1)
            int r3 = r3.left
            android.graphics.Rect r4 = r5.c(r0)
            int r4 = r4.left
            if (r0 == 0) goto L82
            if (r3 < r4) goto L82
            goto La4
        L82:
            boolean r2 = r1.requestFocus()
            goto La4
        L87:
            r3 = 66
            if (r6 != r3) goto La4
            android.graphics.Rect r3 = r5.c(r1)
            int r3 = r3.left
            android.graphics.Rect r4 = r5.c(r0)
            int r4 = r4.left
            if (r0 == 0) goto L9c
            if (r3 > r4) goto L9c
            goto La4
        L9c:
            boolean r2 = r1.requestFocus()
            goto La4
        La1:
            if (r6 == r3) goto La4
            r0 = 1
        La4:
            if (r2 == 0) goto Lad
            int r6 = android.view.SoundEffectConstants.getContantForFocusDirection(r6)
            r5.playSoundEffect(r6)
        Lad:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: x0.C0854b.a(int):boolean");
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void addFocusables(ArrayList<View> arrayList, int i4, int i5) {
        int size = arrayList.size();
        int descendantFocusability = getDescendantFocusability();
        if (descendantFocusability != 393216) {
            for (int i6 = 0; i6 < getChildCount(); i6++) {
                if (getChildAt(i6).getVisibility() == 0) {
                    throw null;
                }
            }
        }
        if ((descendantFocusability == 262144 && size != arrayList.size()) || !isFocusable()) {
            return;
        }
        if ((i5 & 1) == 1 && isInTouchMode() && !isFocusableInTouchMode()) {
            return;
        }
        arrayList.add(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void addTouchables(ArrayList<View> arrayList) {
        for (int i4 = 0; i4 < getChildCount(); i4++) {
            if (getChildAt(i4).getVisibility() == 0) {
                throw null;
            }
        }
    }

    @Override // android.view.ViewGroup
    public final void addView(View view, int i4, ViewGroup.LayoutParams layoutParams) {
        boolean z4;
        if (!checkLayoutParams(layoutParams)) {
            layoutParams = new d();
        }
        d dVar = (d) layoutParams;
        boolean z5 = dVar.f6457a;
        if (view.getClass().getAnnotation(InterfaceC0080b.class) != null) {
            z4 = true;
        } else {
            z4 = false;
        }
        boolean z6 = z5 | z4;
        dVar.f6457a = z6;
        if (this.f6444n) {
            if (!z6) {
                addViewInLayout(view, i4, layoutParams);
                return;
            }
            throw new IllegalStateException("Cannot add pager decor view during layout");
        }
        super.addView(view, i4, layoutParams);
    }

    public final Rect c(View view) {
        Rect rect = new Rect();
        if (view == null) {
            rect.set(0, 0, 0, 0);
            return rect;
        }
        rect.left = view.getLeft();
        rect.right = view.getRight();
        rect.top = view.getTop();
        rect.bottom = view.getBottom();
        ViewParent parent = view.getParent();
        while ((parent instanceof ViewGroup) && parent != this) {
            ViewGroup viewGroup = (ViewGroup) parent;
            rect.left = viewGroup.getLeft() + rect.left;
            rect.right = viewGroup.getRight() + rect.right;
            rect.top = viewGroup.getTop() + rect.top;
            rect.bottom = viewGroup.getBottom() + rect.bottom;
            parent = viewGroup.getParent();
        }
        return rect;
    }

    @Override // android.view.View
    public final boolean canScrollHorizontally(int i4) {
        return false;
    }

    @Override // android.view.ViewGroup
    public final boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if ((layoutParams instanceof d) && super.checkLayoutParams(layoutParams)) {
            return true;
        }
        return false;
    }

    @Override // android.view.View
    public final void computeScroll() {
        throw null;
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0055 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean dispatchKeyEvent(android.view.KeyEvent r6) {
        /*
            r5 = this;
            boolean r0 = super.dispatchKeyEvent(r6)
            r1 = 1
            if (r0 != 0) goto L56
            int r0 = r6.getAction()
            r2 = 0
            if (r0 != 0) goto L3c
            int r0 = r6.getKeyCode()
            r3 = 21
            r4 = 2
            if (r0 == r3) goto L45
            r3 = 22
            if (r0 == r3) goto L36
            r3 = 61
            if (r0 == r3) goto L20
            goto L3c
        L20:
            boolean r0 = r6.hasNoModifiers()
            if (r0 == 0) goto L2b
            boolean r6 = r5.a(r4)
            goto L52
        L2b:
            boolean r6 = r6.hasModifiers(r1)
            if (r6 == 0) goto L3c
            boolean r6 = r5.a(r1)
            goto L52
        L36:
            boolean r6 = r6.hasModifiers(r4)
            if (r6 == 0) goto L3e
        L3c:
            r6 = 0
            goto L52
        L3e:
            r6 = 66
            boolean r6 = r5.a(r6)
            goto L52
        L45:
            boolean r6 = r6.hasModifiers(r4)
            if (r6 == 0) goto L4c
            goto L3c
        L4c:
            r6 = 17
            boolean r6 = r5.a(r6)
        L52:
            if (r6 == 0) goto L55
            goto L56
        L55:
            r1 = 0
        L56:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: x0.C0854b.dispatchKeyEvent(android.view.KeyEvent):boolean");
    }

    @Override // android.view.View
    public final boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 4096) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        int childCount = getChildCount();
        for (int i4 = 0; i4 < childCount; i4++) {
            if (getChildAt(i4).getVisibility() == 0) {
                throw null;
            }
        }
        return false;
    }

    @Override // android.view.View
    public final void draw(Canvas canvas) {
        super.draw(canvas);
        if (getOverScrollMode() != 0) {
            throw null;
        }
        throw null;
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.f6442l;
        if (drawable != null && drawable.isStateful()) {
            drawable.setState(getDrawableState());
        }
    }

    @Override // android.view.ViewGroup
    public final ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new d();
    }

    @Override // android.view.ViewGroup
    public final ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new d();
    }

    public AbstractC0853a getAdapter() {
        return null;
    }

    @Override // android.view.ViewGroup
    public final int getChildDrawingOrder(int i4, int i5) {
        throw null;
    }

    public int getCurrentItem() {
        return 0;
    }

    public int getOffscreenPageLimit() {
        return this.f6446p;
    }

    public int getPageMargin() {
        return this.f6441k;
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.f6455y = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void onDetachedFromWindow() {
        removeCallbacks(null);
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public final void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.f6441k > 0 && this.f6442l != null) {
            throw null;
        }
    }

    @Override // android.view.ViewGroup
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        float f4;
        int action = motionEvent.getAction() & 255;
        int i4 = 0;
        if (action != 3 && action != 1) {
            if (action != 0) {
                if (this.f6447q) {
                    return true;
                }
                if (this.f6448r) {
                    return false;
                }
            }
            if (action != 0) {
                if (action != 2) {
                    if (action == 6) {
                        int actionIndex = motionEvent.getActionIndex();
                        if (motionEvent.getPointerId(actionIndex) == this.f6453w) {
                            if (actionIndex == 0) {
                                i4 = 1;
                            }
                            this.f6450t = motionEvent.getX(i4);
                            this.f6453w = motionEvent.getPointerId(i4);
                            VelocityTracker velocityTracker = this.f6454x;
                            if (velocityTracker != null) {
                                velocityTracker.clear();
                            }
                        }
                    }
                } else {
                    int i5 = this.f6453w;
                    if (i5 != -1) {
                        int findPointerIndex = motionEvent.findPointerIndex(i5);
                        float x4 = motionEvent.getX(findPointerIndex);
                        float f5 = x4 - this.f6450t;
                        float abs = Math.abs(f5);
                        float y4 = motionEvent.getY(findPointerIndex);
                        float abs2 = Math.abs(y4 - this.f6452v);
                        int i6 = (f5 > 0.0f ? 1 : (f5 == 0.0f ? 0 : -1));
                        if (i6 != 0) {
                            float f6 = this.f6450t;
                            if ((f6 >= this.f6449s || i6 <= 0) && ((f6 <= getWidth() - this.f6449s || f5 >= 0.0f) && b((int) f5, (int) x4, (int) y4, this, false))) {
                                this.f6450t = x4;
                                this.f6448r = true;
                                return false;
                            }
                        }
                        float f7 = 0;
                        if (abs > f7 && abs * 0.5f > abs2) {
                            this.f6447q = true;
                            ViewParent parent = getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                            setScrollState(1);
                            float f8 = this.f6451u;
                            if (i6 > 0) {
                                f4 = f8 + f7;
                            } else {
                                f4 = f8 - f7;
                            }
                            this.f6450t = f4;
                            setScrollingCacheEnabled(true);
                        } else if (abs2 > f7) {
                            this.f6448r = true;
                        }
                        if (this.f6447q) {
                            this.f6450t = x4;
                            getScrollX();
                            getClientWidth();
                            throw null;
                        }
                    }
                }
                if (this.f6454x == null) {
                    this.f6454x = VelocityTracker.obtain();
                }
                this.f6454x.addMovement(motionEvent);
                return this.f6447q;
            }
            float x5 = motionEvent.getX();
            this.f6451u = x5;
            this.f6450t = x5;
            this.f6452v = motionEvent.getY();
            this.f6453w = motionEvent.getPointerId(0);
            this.f6448r = false;
            throw null;
        }
        this.f6453w = -1;
        this.f6447q = false;
        this.f6448r = false;
        VelocityTracker velocityTracker2 = this.f6454x;
        if (velocityTracker2 != null) {
            velocityTracker2.recycle();
            this.f6454x = null;
        }
        throw null;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0094  */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void onLayout(boolean r18, int r19, int r20, int r21, int r22) {
        /*
            Method dump skipped, instructions count: 218
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: x0.C0854b.onLayout(boolean, int, int, int, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0086  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0090  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x009f  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00a5  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void onMeasure(int r14, int r15) {
        /*
            Method dump skipped, instructions count: 235
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: x0.C0854b.onMeasure(int, int):void");
    }

    @Override // android.view.ViewGroup
    public final boolean onRequestFocusInDescendants(int i4, Rect rect) {
        int i5;
        int i6;
        int i7;
        int childCount = getChildCount();
        if ((i4 & 2) != 0) {
            i6 = 1;
            i7 = childCount;
            i5 = 0;
        } else {
            i5 = childCount - 1;
            i6 = -1;
            i7 = -1;
        }
        while (i5 != i7) {
            if (getChildAt(i5).getVisibility() != 0) {
                i5 += i6;
            } else {
                throw null;
            }
        }
        return false;
    }

    @Override // android.view.View
    public final void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof g)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        g gVar = (g) parcelable;
        super.onRestoreInstanceState(gVar.f2373j);
        this.f6440j = gVar.f6460m;
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [android.os.Parcelable, U.a, x0.b$g] */
    @Override // android.view.View
    public final Parcelable onSaveInstanceState() {
        ?? aVar = new U.a(super.onSaveInstanceState());
        aVar.f6459l = 0;
        return aVar;
    }

    @Override // android.view.View
    public final void onSizeChanged(int i4, int i5, int i6, int i7) {
        super.onSizeChanged(i4, i5, i6, i7);
        if (i4 != i6) {
            if (i6 > 0) {
                throw null;
            }
            throw null;
        }
    }

    @Override // android.view.View
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            motionEvent.getEdgeFlags();
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public final void removeView(View view) {
        if (this.f6444n) {
            removeViewInLayout(view);
        } else {
            super.removeView(view);
        }
    }

    public void setAdapter(AbstractC0853a abstractC0853a) {
        ArrayList arrayList = this.f6438B;
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = this.f6438B.size();
            for (int i4 = 0; i4 < size; i4++) {
                ((e) this.f6438B.get(i4)).a(this);
            }
        }
    }

    public void setCurrentItem(int i4) {
        setScrollingCacheEnabled(false);
    }

    public void setOffscreenPageLimit(int i4) {
        if (i4 < 1) {
            Log.w("ViewPager", "Requested offscreen page limit " + i4 + " too small; defaulting to 1");
            i4 = 1;
        }
        if (i4 != this.f6446p) {
            this.f6446p = i4;
        }
    }

    @Deprecated
    public void setOnPageChangeListener(f fVar) {
        this.f6437A = fVar;
    }

    public void setPageMargin(int i4) {
        this.f6441k = i4;
        if (getWidth() > 0) {
            throw null;
        }
        throw null;
    }

    public void setPageMarginDrawable(Drawable drawable) {
        this.f6442l = drawable;
        if (drawable != null) {
            refreshDrawableState();
        }
        setWillNotDraw(drawable == null);
        invalidate();
    }

    public void setScrollState(int i4) {
        if (this.f6439C == i4) {
            return;
        }
        this.f6439C = i4;
        f fVar = this.f6437A;
        if (fVar != null) {
            fVar.a(i4);
        }
        ArrayList arrayList = this.f6456z;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i5 = 0; i5 < size; i5++) {
                f fVar2 = (f) this.f6456z.get(i5);
                if (fVar2 != null) {
                    fVar2.a(i4);
                }
            }
        }
    }

    @Override // android.view.View
    public final boolean verifyDrawable(Drawable drawable) {
        if (!super.verifyDrawable(drawable) && drawable != this.f6442l) {
            return false;
        }
        return true;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.view.ViewGroup$LayoutParams, x0.b$d] */
    @Override // android.view.ViewGroup
    public final ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        Context context = getContext();
        ?? layoutParams = new ViewGroup.LayoutParams(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, f6436D);
        layoutParams.f6458b = obtainStyledAttributes.getInteger(0, 48);
        obtainStyledAttributes.recycle();
        return layoutParams;
    }

    public void setPageMarginDrawable(int i4) {
        setPageMarginDrawable(a.C0002a.b(getContext(), i4));
    }
}
