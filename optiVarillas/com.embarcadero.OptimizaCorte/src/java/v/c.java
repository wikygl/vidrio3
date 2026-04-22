package V;

import M.O;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import java.util.Arrays;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class c {

    /* renamed from: v  reason: collision with root package name */
    public static final a f2501v = new Object();

    /* renamed from: a  reason: collision with root package name */
    public int f2502a;

    /* renamed from: b  reason: collision with root package name */
    public final int f2503b;

    /* renamed from: d  reason: collision with root package name */
    public float[] f2505d;

    /* renamed from: e  reason: collision with root package name */
    public float[] f2506e;
    public float[] f;

    /* renamed from: g  reason: collision with root package name */
    public float[] f2507g;

    /* renamed from: h  reason: collision with root package name */
    public int[] f2508h;

    /* renamed from: i  reason: collision with root package name */
    public int[] f2509i;

    /* renamed from: j  reason: collision with root package name */
    public int[] f2510j;

    /* renamed from: k  reason: collision with root package name */
    public int f2511k;

    /* renamed from: l  reason: collision with root package name */
    public VelocityTracker f2512l;

    /* renamed from: m  reason: collision with root package name */
    public final float f2513m;

    /* renamed from: n  reason: collision with root package name */
    public final float f2514n;

    /* renamed from: o  reason: collision with root package name */
    public final int f2515o;

    /* renamed from: p  reason: collision with root package name */
    public final OverScroller f2516p;

    /* renamed from: q  reason: collision with root package name */
    public final AbstractC0031c f2517q;

    /* renamed from: r  reason: collision with root package name */
    public View f2518r;

    /* renamed from: s  reason: collision with root package name */
    public boolean f2519s;

    /* renamed from: t  reason: collision with root package name */
    public final ViewGroup f2520t;

    /* renamed from: c  reason: collision with root package name */
    public int f2504c = -1;

    /* renamed from: u  reason: collision with root package name */
    public final b f2521u = new b();

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class a implements Interpolator {
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            float f4 = f - 1.0f;
            return (f4 * f4 * f4 * f4 * f4) + 1.0f;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class b implements Runnable {
        public b() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            c.this.n(0);
        }
    }

    public c(Context context, ViewGroup viewGroup, AbstractC0031c abstractC0031c) {
        if (viewGroup != null) {
            if (abstractC0031c != null) {
                this.f2520t = viewGroup;
                this.f2517q = abstractC0031c;
                ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
                this.f2515o = (int) ((context.getResources().getDisplayMetrics().density * 20.0f) + 0.5f);
                this.f2503b = viewConfiguration.getScaledTouchSlop();
                this.f2513m = viewConfiguration.getScaledMaximumFlingVelocity();
                this.f2514n = viewConfiguration.getScaledMinimumFlingVelocity();
                this.f2516p = new OverScroller(context, f2501v);
                return;
            }
            throw new IllegalArgumentException("Callback may not be null");
        }
        throw new IllegalArgumentException("Parent view may not be null");
    }

    public final void a() {
        this.f2504c = -1;
        float[] fArr = this.f2505d;
        if (fArr != null) {
            Arrays.fill(fArr, 0.0f);
            Arrays.fill(this.f2506e, 0.0f);
            Arrays.fill(this.f, 0.0f);
            Arrays.fill(this.f2507g, 0.0f);
            Arrays.fill(this.f2508h, 0);
            Arrays.fill(this.f2509i, 0);
            Arrays.fill(this.f2510j, 0);
            this.f2511k = 0;
        }
        VelocityTracker velocityTracker = this.f2512l;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.f2512l = null;
        }
    }

    public final void b(View view, int i4) {
        ViewParent parent = view.getParent();
        ViewGroup viewGroup = this.f2520t;
        if (parent == viewGroup) {
            this.f2518r = view;
            this.f2504c = i4;
            this.f2517q.e(view, i4);
            n(1);
            return;
        }
        throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + viewGroup + ")");
    }

    public final boolean c(View view, float f, float f4) {
        boolean z4;
        boolean z5;
        if (view == null) {
            return false;
        }
        AbstractC0031c abstractC0031c = this.f2517q;
        if (abstractC0031c.c(view) > 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (abstractC0031c.d() > 0) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z4 && z5) {
            float f5 = f4 * f4;
            int i4 = this.f2503b;
            if (f5 + (f * f) <= i4 * i4) {
                return false;
            }
            return true;
        } else if (z4) {
            if (Math.abs(f) <= this.f2503b) {
                return false;
            }
            return true;
        } else if (!z5 || Math.abs(f4) <= this.f2503b) {
            return false;
        } else {
            return true;
        }
    }

    public final void d(int i4) {
        float[] fArr = this.f2505d;
        if (fArr != null) {
            int i5 = this.f2511k;
            int i6 = 1 << i4;
            if ((i5 & i6) != 0) {
                fArr[i4] = 0.0f;
                this.f2506e[i4] = 0.0f;
                this.f[i4] = 0.0f;
                this.f2507g[i4] = 0.0f;
                this.f2508h[i4] = 0;
                this.f2509i[i4] = 0;
                this.f2510j[i4] = 0;
                this.f2511k = (~i6) & i5;
            }
        }
    }

    public final int e(int i4, int i5, int i6) {
        int width;
        int abs;
        if (i4 == 0) {
            return 0;
        }
        float width2 = this.f2520t.getWidth() / 2;
        float sin = (((float) Math.sin((Math.min(1.0f, Math.abs(i4) / width) - 0.5f) * 0.47123894f)) * width2) + width2;
        int abs2 = Math.abs(i5);
        if (abs2 > 0) {
            abs = Math.round(Math.abs(sin / abs2) * 1000.0f) * 4;
        } else {
            abs = (int) (((Math.abs(i4) / i6) + 1.0f) * 256.0f);
        }
        return Math.min(abs, 600);
    }

    public final boolean f() {
        if (this.f2502a == 2) {
            OverScroller overScroller = this.f2516p;
            boolean computeScrollOffset = overScroller.computeScrollOffset();
            int currX = overScroller.getCurrX();
            int currY = overScroller.getCurrY();
            int left = currX - this.f2518r.getLeft();
            int top = currY - this.f2518r.getTop();
            if (left != 0) {
                O.j(this.f2518r, left);
            }
            if (top != 0) {
                O.k(this.f2518r, top);
            }
            if (left != 0 || top != 0) {
                this.f2517q.g(this.f2518r, currX, currY);
            }
            if (computeScrollOffset && currX == overScroller.getFinalX() && currY == overScroller.getFinalY()) {
                overScroller.abortAnimation();
                computeScrollOffset = false;
            }
            if (!computeScrollOffset) {
                this.f2520t.post(this.f2521u);
            }
        }
        if (this.f2502a != 2) {
            return false;
        }
        return true;
    }

    public final View g(int i4, int i5) {
        ViewGroup viewGroup = this.f2520t;
        for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
            this.f2517q.getClass();
            View childAt = viewGroup.getChildAt(childCount);
            if (i4 >= childAt.getLeft() && i4 < childAt.getRight() && i5 >= childAt.getTop() && i5 < childAt.getBottom()) {
                return childAt;
            }
        }
        return null;
    }

    public final boolean h(int i4, int i5, int i6, int i7) {
        float f;
        float f4;
        float f5;
        float f6;
        int left = this.f2518r.getLeft();
        int top = this.f2518r.getTop();
        int i8 = i4 - left;
        int i9 = i5 - top;
        OverScroller overScroller = this.f2516p;
        if (i8 == 0 && i9 == 0) {
            overScroller.abortAnimation();
            n(0);
            return false;
        }
        View view = this.f2518r;
        int i10 = (int) this.f2514n;
        int i11 = (int) this.f2513m;
        int abs = Math.abs(i6);
        if (abs < i10) {
            i6 = 0;
        } else if (abs > i11) {
            if (i6 > 0) {
                i6 = i11;
            } else {
                i6 = -i11;
            }
        }
        int abs2 = Math.abs(i7);
        if (abs2 < i10) {
            i7 = 0;
        } else if (abs2 > i11) {
            if (i7 > 0) {
                i7 = i11;
            } else {
                i7 = -i11;
            }
        }
        int abs3 = Math.abs(i8);
        int abs4 = Math.abs(i9);
        int abs5 = Math.abs(i6);
        int abs6 = Math.abs(i7);
        int i12 = abs5 + abs6;
        int i13 = abs3 + abs4;
        if (i6 != 0) {
            f = abs5;
            f4 = i12;
        } else {
            f = abs3;
            f4 = i13;
        }
        float f7 = f / f4;
        if (i7 != 0) {
            f5 = abs6;
            f6 = i12;
        } else {
            f5 = abs4;
            f6 = i13;
        }
        float f8 = f5 / f6;
        AbstractC0031c abstractC0031c = this.f2517q;
        overScroller.startScroll(left, top, i8, i9, (int) ((e(i9, i7, abstractC0031c.d()) * f8) + (e(i8, i6, abstractC0031c.c(view)) * f7)));
        n(2);
        return true;
    }

    public final boolean i(int i4) {
        if ((this.f2511k & (1 << i4)) != 0) {
            return true;
        }
        Log.e("ViewDragHelper", "Ignoring pointerId=" + i4 + " because ACTION_DOWN was not received for this pointer before ACTION_MOVE. It likely happened because  ViewDragHelper did not receive all the events in the event stream.");
        return false;
    }

    public final void j(MotionEvent motionEvent) {
        int i4;
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            a();
        }
        if (this.f2512l == null) {
            this.f2512l = VelocityTracker.obtain();
        }
        this.f2512l.addMovement(motionEvent);
        int i5 = 0;
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                AbstractC0031c abstractC0031c = this.f2517q;
                if (actionMasked != 2) {
                    if (actionMasked != 3) {
                        if (actionMasked != 5) {
                            if (actionMasked == 6) {
                                int pointerId = motionEvent.getPointerId(actionIndex);
                                if (this.f2502a == 1 && pointerId == this.f2504c) {
                                    int pointerCount = motionEvent.getPointerCount();
                                    while (true) {
                                        if (i5 < pointerCount) {
                                            int pointerId2 = motionEvent.getPointerId(i5);
                                            if (pointerId2 != this.f2504c) {
                                                View g4 = g((int) motionEvent.getX(i5), (int) motionEvent.getY(i5));
                                                View view = this.f2518r;
                                                if (g4 == view && q(view, pointerId2)) {
                                                    i4 = this.f2504c;
                                                    break;
                                                }
                                            }
                                            i5++;
                                        } else {
                                            i4 = -1;
                                            break;
                                        }
                                    }
                                    if (i4 == -1) {
                                        k();
                                    }
                                }
                                d(pointerId);
                                return;
                            }
                            return;
                        }
                        int pointerId3 = motionEvent.getPointerId(actionIndex);
                        float x4 = motionEvent.getX(actionIndex);
                        float y4 = motionEvent.getY(actionIndex);
                        l(x4, y4, pointerId3);
                        if (this.f2502a == 0) {
                            q(g((int) x4, (int) y4), pointerId3);
                            int i6 = this.f2508h[pointerId3];
                            return;
                        }
                        int i7 = (int) x4;
                        int i8 = (int) y4;
                        View view2 = this.f2518r;
                        if (view2 != null && i7 >= view2.getLeft() && i7 < view2.getRight() && i8 >= view2.getTop() && i8 < view2.getBottom()) {
                            i5 = 1;
                        }
                        if (i5 != 0) {
                            q(this.f2518r, pointerId3);
                            return;
                        }
                        return;
                    }
                    if (this.f2502a == 1) {
                        this.f2519s = true;
                        abstractC0031c.h(this.f2518r, 0.0f, 0.0f);
                        this.f2519s = false;
                        if (this.f2502a == 1) {
                            n(0);
                        }
                    }
                    a();
                    return;
                } else if (this.f2502a == 1) {
                    if (i(this.f2504c)) {
                        int findPointerIndex = motionEvent.findPointerIndex(this.f2504c);
                        float x5 = motionEvent.getX(findPointerIndex);
                        float y5 = motionEvent.getY(findPointerIndex);
                        float[] fArr = this.f;
                        int i9 = this.f2504c;
                        int i10 = (int) (x5 - fArr[i9]);
                        int i11 = (int) (y5 - this.f2507g[i9]);
                        int left = this.f2518r.getLeft() + i10;
                        int top = this.f2518r.getTop() + i11;
                        int left2 = this.f2518r.getLeft();
                        int top2 = this.f2518r.getTop();
                        if (i10 != 0) {
                            left = abstractC0031c.a(this.f2518r, left);
                            O.j(this.f2518r, left - left2);
                        }
                        if (i11 != 0) {
                            top = abstractC0031c.b(this.f2518r, top);
                            O.k(this.f2518r, top - top2);
                        }
                        if (i10 != 0 || i11 != 0) {
                            abstractC0031c.g(this.f2518r, left, top);
                        }
                        m(motionEvent);
                        return;
                    }
                    return;
                } else {
                    int pointerCount2 = motionEvent.getPointerCount();
                    while (i5 < pointerCount2) {
                        int pointerId4 = motionEvent.getPointerId(i5);
                        if (i(pointerId4)) {
                            float x6 = motionEvent.getX(i5);
                            float y6 = motionEvent.getY(i5);
                            float f = x6 - this.f2505d[pointerId4];
                            float f4 = y6 - this.f2506e[pointerId4];
                            Math.abs(f);
                            Math.abs(f4);
                            int i12 = this.f2508h[pointerId4];
                            Math.abs(f4);
                            Math.abs(f);
                            int i13 = this.f2508h[pointerId4];
                            Math.abs(f);
                            Math.abs(f4);
                            int i14 = this.f2508h[pointerId4];
                            Math.abs(f4);
                            Math.abs(f);
                            int i15 = this.f2508h[pointerId4];
                            if (this.f2502a != 1) {
                                View g5 = g((int) x6, (int) y6);
                                if (c(g5, f, f4) && q(g5, pointerId4)) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        i5++;
                    }
                    m(motionEvent);
                    return;
                }
            }
            if (this.f2502a == 1) {
                k();
            }
            a();
            return;
        }
        float x7 = motionEvent.getX();
        float y7 = motionEvent.getY();
        int pointerId5 = motionEvent.getPointerId(0);
        View g6 = g((int) x7, (int) y7);
        l(x7, y7, pointerId5);
        q(g6, pointerId5);
        int i16 = this.f2508h[pointerId5];
    }

    public final void k() {
        VelocityTracker velocityTracker = this.f2512l;
        float f = this.f2513m;
        velocityTracker.computeCurrentVelocity(1000, f);
        float xVelocity = this.f2512l.getXVelocity(this.f2504c);
        float f4 = this.f2514n;
        float abs = Math.abs(xVelocity);
        float f5 = 0.0f;
        if (abs < f4) {
            xVelocity = 0.0f;
        } else if (abs > f) {
            if (xVelocity > 0.0f) {
                xVelocity = f;
            } else {
                xVelocity = -f;
            }
        }
        float yVelocity = this.f2512l.getYVelocity(this.f2504c);
        float abs2 = Math.abs(yVelocity);
        if (abs2 >= f4) {
            if (abs2 > f) {
                if (yVelocity <= 0.0f) {
                    f = -f;
                }
                f5 = f;
            } else {
                f5 = yVelocity;
            }
        }
        this.f2519s = true;
        this.f2517q.h(this.f2518r, xVelocity, f5);
        this.f2519s = false;
        if (this.f2502a == 1) {
            n(0);
        }
    }

    public final void l(float f, float f4, int i4) {
        float[] fArr = this.f2505d;
        int i5 = 0;
        if (fArr == null || fArr.length <= i4) {
            int i6 = i4 + 1;
            float[] fArr2 = new float[i6];
            float[] fArr3 = new float[i6];
            float[] fArr4 = new float[i6];
            float[] fArr5 = new float[i6];
            int[] iArr = new int[i6];
            int[] iArr2 = new int[i6];
            int[] iArr3 = new int[i6];
            if (fArr != null) {
                System.arraycopy(fArr, 0, fArr2, 0, fArr.length);
                float[] fArr6 = this.f2506e;
                System.arraycopy(fArr6, 0, fArr3, 0, fArr6.length);
                float[] fArr7 = this.f;
                System.arraycopy(fArr7, 0, fArr4, 0, fArr7.length);
                float[] fArr8 = this.f2507g;
                System.arraycopy(fArr8, 0, fArr5, 0, fArr8.length);
                int[] iArr4 = this.f2508h;
                System.arraycopy(iArr4, 0, iArr, 0, iArr4.length);
                int[] iArr5 = this.f2509i;
                System.arraycopy(iArr5, 0, iArr2, 0, iArr5.length);
                int[] iArr6 = this.f2510j;
                System.arraycopy(iArr6, 0, iArr3, 0, iArr6.length);
            }
            this.f2505d = fArr2;
            this.f2506e = fArr3;
            this.f = fArr4;
            this.f2507g = fArr5;
            this.f2508h = iArr;
            this.f2509i = iArr2;
            this.f2510j = iArr3;
        }
        float[] fArr9 = this.f2505d;
        this.f[i4] = f;
        fArr9[i4] = f;
        float[] fArr10 = this.f2506e;
        this.f2507g[i4] = f4;
        fArr10[i4] = f4;
        int[] iArr7 = this.f2508h;
        int i7 = (int) f;
        int i8 = (int) f4;
        ViewGroup viewGroup = this.f2520t;
        int left = viewGroup.getLeft();
        int i9 = this.f2515o;
        if (i7 < left + i9) {
            i5 = 1;
        }
        if (i8 < viewGroup.getTop() + i9) {
            i5 |= 4;
        }
        if (i7 > viewGroup.getRight() - i9) {
            i5 |= 2;
        }
        if (i8 > viewGroup.getBottom() - i9) {
            i5 |= 8;
        }
        iArr7[i4] = i5;
        this.f2511k |= 1 << i4;
    }

    public final void m(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        for (int i4 = 0; i4 < pointerCount; i4++) {
            int pointerId = motionEvent.getPointerId(i4);
            if (i(pointerId)) {
                float x4 = motionEvent.getX(i4);
                float y4 = motionEvent.getY(i4);
                this.f[pointerId] = x4;
                this.f2507g[pointerId] = y4;
            }
        }
    }

    public final void n(int i4) {
        this.f2520t.removeCallbacks(this.f2521u);
        if (this.f2502a != i4) {
            this.f2502a = i4;
            this.f2517q.f(i4);
            if (this.f2502a == 0) {
                this.f2518r = null;
            }
        }
    }

    public final boolean o(int i4, int i5) {
        if (this.f2519s) {
            return h(i4, i5, (int) this.f2512l.getXVelocity(this.f2504c), (int) this.f2512l.getYVelocity(this.f2504c));
        }
        throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x00cd, code lost:
        if (r12 != r11) goto L54;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean p(android.view.MotionEvent r18) {
        /*
            Method dump skipped, instructions count: 323
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: V.c.p(android.view.MotionEvent):boolean");
    }

    public final boolean q(View view, int i4) {
        if (view == this.f2518r && this.f2504c == i4) {
            return true;
        }
        if (view != null && this.f2517q.i(view, i4)) {
            this.f2504c = i4;
            b(view, i4);
            return true;
        }
        return false;
    }

    /* renamed from: V.c$c  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static abstract class AbstractC0031c {
        public abstract int a(View view, int i4);

        public abstract int b(View view, int i4);

        public int c(View view) {
            return 0;
        }

        public int d() {
            return 0;
        }

        public abstract void f(int i4);

        public abstract void g(View view, int i4, int i5);

        public abstract void h(View view, float f, float f4);

        public abstract boolean i(View view, int i4);

        public void e(View view, int i4) {
        }
    }
}
