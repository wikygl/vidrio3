package s2;

import M.O;
import M.V;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.gms.internal.ads.bI;
import java.util.WeakHashMap;

/* renamed from: s2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class AbstractC0796a<V extends View> extends c<V> {

    /* renamed from: c  reason: collision with root package name */
    public RunnableC0069a f5763c;

    /* renamed from: d  reason: collision with root package name */
    public OverScroller f5764d;

    /* renamed from: e  reason: collision with root package name */
    public boolean f5765e;
    public int f;

    /* renamed from: g  reason: collision with root package name */
    public int f5766g;

    /* renamed from: h  reason: collision with root package name */
    public int f5767h;

    /* renamed from: i  reason: collision with root package name */
    public VelocityTracker f5768i;

    /* renamed from: s2.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class RunnableC0069a implements Runnable {

        /* renamed from: j  reason: collision with root package name */
        public final CoordinatorLayout f5769j;

        /* renamed from: k  reason: collision with root package name */
        public final V f5770k;

        public RunnableC0069a(CoordinatorLayout coordinatorLayout, V v4) {
            this.f5769j = coordinatorLayout;
            this.f5770k = v4;
        }

        @Override // java.lang.Runnable
        public final void run() {
            AbstractC0796a abstractC0796a;
            OverScroller overScroller;
            V v4 = this.f5770k;
            if (v4 != null && (overScroller = (abstractC0796a = AbstractC0796a.this).f5764d) != null) {
                boolean computeScrollOffset = overScroller.computeScrollOffset();
                CoordinatorLayout coordinatorLayout = this.f5769j;
                if (computeScrollOffset) {
                    abstractC0796a.A(coordinatorLayout, v4, abstractC0796a.f5764d.getCurrY());
                    WeakHashMap<View, V> weakHashMap = O.f1526a;
                    v4.postOnAnimation(this);
                    return;
                }
                abstractC0796a.y(coordinatorLayout, v4);
            }
        }
    }

    public AbstractC0796a() {
        this.f = -1;
        this.f5767h = -1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void A(CoordinatorLayout coordinatorLayout, View view, int i4) {
        z(coordinatorLayout, view, i4, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public final boolean g(CoordinatorLayout coordinatorLayout, V v4, MotionEvent motionEvent) {
        boolean z4;
        int findPointerIndex;
        if (this.f5767h < 0) {
            this.f5767h = ViewConfiguration.get(coordinatorLayout.getContext()).getScaledTouchSlop();
        }
        if (motionEvent.getActionMasked() == 2 && this.f5765e) {
            int i4 = this.f;
            if (i4 == -1 || (findPointerIndex = motionEvent.findPointerIndex(i4)) == -1) {
                return false;
            }
            int y4 = (int) motionEvent.getY(findPointerIndex);
            if (Math.abs(y4 - this.f5766g) > this.f5767h) {
                this.f5766g = y4;
                return true;
            }
        }
        if (motionEvent.getActionMasked() == 0) {
            this.f = -1;
            int x4 = (int) motionEvent.getX();
            int y5 = (int) motionEvent.getY();
            if (v(v4) && coordinatorLayout.l(v4, x4, y5)) {
                z4 = true;
            } else {
                z4 = false;
            }
            this.f5765e = z4;
            if (z4) {
                this.f5766g = y5;
                this.f = motionEvent.getPointerId(0);
                if (this.f5768i == null) {
                    this.f5768i = VelocityTracker.obtain();
                }
                OverScroller overScroller = this.f5764d;
                if (overScroller != null && !overScroller.isFinished()) {
                    this.f5764d.abortAnimation();
                    return true;
                }
            }
        }
        VelocityTracker velocityTracker = this.f5768i;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x00ce  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00d7  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00de A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:47:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean r(androidx.coordinatorlayout.widget.CoordinatorLayout r20, V r21, android.view.MotionEvent r22) {
        /*
            Method dump skipped, instructions count: 227
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: s2.AbstractC0796a.r(androidx.coordinatorlayout.widget.CoordinatorLayout, android.view.View, android.view.MotionEvent):boolean");
    }

    public boolean v(V v4) {
        return false;
    }

    public int w(V v4) {
        return -v4.getHeight();
    }

    public int x(V v4) {
        return v4.getHeight();
    }

    public int z(CoordinatorLayout coordinatorLayout, V v4, int i4, int i5, int i6) {
        int f;
        int s4 = s();
        if (i5 != 0 && s4 >= i5 && s4 <= i6 && s4 != (f = H.a.f(i4, i5, i6))) {
            bI bIVar = this.f5775a;
            if (bIVar != null) {
                if (bIVar.c != f) {
                    bIVar.c = f;
                    bIVar.a();
                }
            } else {
                this.f5776b = f;
            }
            return s4 - f;
        }
        return 0;
    }

    public AbstractC0796a(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f = -1;
        this.f5767h = -1;
    }

    public void y(CoordinatorLayout coordinatorLayout, V v4) {
    }
}
