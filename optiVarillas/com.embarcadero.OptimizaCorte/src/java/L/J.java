package l;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import k.InterfaceC0683f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class J implements View.OnTouchListener, View.OnAttachStateChangeListener {

    /* renamed from: j  reason: collision with root package name */
    public final float f4999j;

    /* renamed from: k  reason: collision with root package name */
    public final int f5000k;

    /* renamed from: l  reason: collision with root package name */
    public final int f5001l;

    /* renamed from: m  reason: collision with root package name */
    public final View f5002m;

    /* renamed from: n  reason: collision with root package name */
    public a f5003n;

    /* renamed from: o  reason: collision with root package name */
    public b f5004o;

    /* renamed from: p  reason: collision with root package name */
    public boolean f5005p;

    /* renamed from: q  reason: collision with root package name */
    public int f5006q;

    /* renamed from: r  reason: collision with root package name */
    public final int[] f5007r = new int[2];

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class a implements Runnable {
        public a() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            ViewParent parent = J.this.f5002m.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public class b implements Runnable {
        public b() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            J j4 = J.this;
            j4.a();
            View view = j4.f5002m;
            if (view.isEnabled() && !view.isLongClickable() && j4.c()) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                long uptimeMillis = SystemClock.uptimeMillis();
                MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                view.onTouchEvent(obtain);
                obtain.recycle();
                j4.f5005p = true;
            }
        }
    }

    public J(View view) {
        this.f5002m = view;
        view.setLongClickable(true);
        view.addOnAttachStateChangeListener(this);
        this.f4999j = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        int tapTimeout = ViewConfiguration.getTapTimeout();
        this.f5000k = tapTimeout;
        this.f5001l = (ViewConfiguration.getLongPressTimeout() + tapTimeout) / 2;
    }

    public final void a() {
        b bVar = this.f5004o;
        View view = this.f5002m;
        if (bVar != null) {
            view.removeCallbacks(bVar);
        }
        a aVar = this.f5003n;
        if (aVar != null) {
            view.removeCallbacks(aVar);
        }
    }

    public abstract InterfaceC0683f b();

    public abstract boolean c();

    public boolean e() {
        InterfaceC0683f b4 = b();
        if (b4 != null && b4.c()) {
            b4.dismiss();
            return true;
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0059, code lost:
        if (r14 != false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x007b, code lost:
        if (r4 != 3) goto L60;
     */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00fe  */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean onTouch(android.view.View r13, android.view.MotionEvent r14) {
        /*
            Method dump skipped, instructions count: 282
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: l.J.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewDetachedFromWindow(View view) {
        this.f5005p = false;
        this.f5006q = -1;
        a aVar = this.f5003n;
        if (aVar != null) {
            this.f5002m.removeCallbacks(aVar);
        }
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewAttachedToWindow(View view) {
    }
}
