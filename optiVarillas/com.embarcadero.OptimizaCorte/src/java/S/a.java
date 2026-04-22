package S;

import M.O;
import M.V;
import android.content.res.Resources;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public abstract class a implements View.OnTouchListener {

    /* renamed from: z  reason: collision with root package name */
    public static final int f2069z = ViewConfiguration.getTapTimeout();

    /* renamed from: j  reason: collision with root package name */
    public final C0022a f2070j;

    /* renamed from: k  reason: collision with root package name */
    public final AccelerateInterpolator f2071k;

    /* renamed from: l  reason: collision with root package name */
    public final View f2072l;

    /* renamed from: m  reason: collision with root package name */
    public b f2073m;

    /* renamed from: n  reason: collision with root package name */
    public final float[] f2074n;

    /* renamed from: o  reason: collision with root package name */
    public final float[] f2075o;

    /* renamed from: p  reason: collision with root package name */
    public final int f2076p;

    /* renamed from: q  reason: collision with root package name */
    public final int f2077q;

    /* renamed from: r  reason: collision with root package name */
    public final float[] f2078r;

    /* renamed from: s  reason: collision with root package name */
    public final float[] f2079s;

    /* renamed from: t  reason: collision with root package name */
    public final float[] f2080t;

    /* renamed from: u  reason: collision with root package name */
    public boolean f2081u;

    /* renamed from: v  reason: collision with root package name */
    public boolean f2082v;

    /* renamed from: w  reason: collision with root package name */
    public boolean f2083w;

    /* renamed from: x  reason: collision with root package name */
    public boolean f2084x;

    /* renamed from: y  reason: collision with root package name */
    public boolean f2085y;

    /* renamed from: S.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class C0022a {

        /* renamed from: a  reason: collision with root package name */
        public int f2086a;

        /* renamed from: b  reason: collision with root package name */
        public int f2087b;

        /* renamed from: c  reason: collision with root package name */
        public float f2088c;

        /* renamed from: d  reason: collision with root package name */
        public float f2089d;

        /* renamed from: e  reason: collision with root package name */
        public long f2090e;
        public long f;

        /* renamed from: g  reason: collision with root package name */
        public long f2091g;

        /* renamed from: h  reason: collision with root package name */
        public float f2092h;

        /* renamed from: i  reason: collision with root package name */
        public int f2093i;

        public final float a(long j4) {
            long j5 = this.f2090e;
            if (j4 < j5) {
                return 0.0f;
            }
            long j6 = this.f2091g;
            if (j6 >= 0 && j4 >= j6) {
                float f = this.f2092h;
                return (a.b(((float) (j4 - j6)) / this.f2093i, 0.0f, 1.0f) * f) + (1.0f - f);
            }
            return a.b(((float) (j4 - j5)) / this.f2086a, 0.0f, 1.0f) * 0.5f;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public class b implements Runnable {
        public b() {
        }

        @Override // java.lang.Runnable
        public final void run() {
            a aVar = a.this;
            if (!aVar.f2084x) {
                return;
            }
            boolean z4 = aVar.f2082v;
            C0022a c0022a = aVar.f2070j;
            if (z4) {
                aVar.f2082v = false;
                c0022a.getClass();
                long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
                c0022a.f2090e = currentAnimationTimeMillis;
                c0022a.f2091g = -1L;
                c0022a.f = currentAnimationTimeMillis;
                c0022a.f2092h = 0.5f;
            }
            if ((c0022a.f2091g > 0 && AnimationUtils.currentAnimationTimeMillis() > c0022a.f2091g + c0022a.f2093i) || !aVar.h()) {
                aVar.f2084x = false;
                return;
            }
            boolean z5 = aVar.f2083w;
            View view = aVar.f2072l;
            if (z5) {
                aVar.f2083w = false;
                long uptimeMillis = SystemClock.uptimeMillis();
                MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                view.onTouchEvent(obtain);
                obtain.recycle();
            }
            if (c0022a.f != 0) {
                long currentAnimationTimeMillis2 = AnimationUtils.currentAnimationTimeMillis();
                float a4 = c0022a.a(currentAnimationTimeMillis2);
                c0022a.f = currentAnimationTimeMillis2;
                ListView listView = ((e) aVar).f2097A;
                listView.scrollListBy((int) (((float) (currentAnimationTimeMillis2 - c0022a.f)) * ((a4 * 4.0f) + ((-4.0f) * a4 * a4)) * c0022a.f2089d));
                WeakHashMap<View, V> weakHashMap = O.f1526a;
                view.postOnAnimation(this);
                return;
            }
            throw new RuntimeException("Cannot compute scroll delta before calling start()");
        }
    }

    /* JADX WARN: Type inference failed for: r1v0, types: [S.a$a, java.lang.Object] */
    public a(View view) {
        ?? obj = new Object();
        obj.f2090e = Long.MIN_VALUE;
        obj.f2091g = -1L;
        obj.f = 0L;
        this.f2070j = obj;
        this.f2071k = new AccelerateInterpolator();
        float[] fArr = {0.0f, 0.0f};
        this.f2074n = fArr;
        float[] fArr2 = {Float.MAX_VALUE, Float.MAX_VALUE};
        this.f2075o = fArr2;
        float[] fArr3 = {0.0f, 0.0f};
        this.f2078r = fArr3;
        float[] fArr4 = {0.0f, 0.0f};
        this.f2079s = fArr4;
        float[] fArr5 = {Float.MAX_VALUE, Float.MAX_VALUE};
        this.f2080t = fArr5;
        this.f2072l = view;
        float f = Resources.getSystem().getDisplayMetrics().density;
        float f4 = ((int) ((1575.0f * f) + 0.5f)) / 1000.0f;
        fArr5[0] = f4;
        fArr5[1] = f4;
        float f5 = ((int) ((f * 315.0f) + 0.5f)) / 1000.0f;
        fArr4[0] = f5;
        fArr4[1] = f5;
        this.f2076p = 1;
        fArr2[0] = Float.MAX_VALUE;
        fArr2[1] = Float.MAX_VALUE;
        fArr[0] = 0.2f;
        fArr[1] = 0.2f;
        fArr3[0] = 0.001f;
        fArr3[1] = 0.001f;
        this.f2077q = f2069z;
        obj.f2086a = 500;
        obj.f2087b = 500;
    }

    public static float b(float f, float f4, float f5) {
        if (f > f5) {
            return f5;
        }
        if (f < f4) {
            return f4;
        }
        return f;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003c A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:13:0x003d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final float a(float r4, float r5, float r6, int r7) {
        /*
            r3 = this;
            float[] r0 = r3.f2074n
            r0 = r0[r7]
            float[] r1 = r3.f2075o
            r1 = r1[r7]
            float r0 = r0 * r5
            r2 = 0
            float r0 = b(r0, r2, r1)
            float r1 = r3.c(r4, r0)
            float r5 = r5 - r4
            float r4 = r3.c(r5, r0)
            float r4 = r4 - r1
            android.view.animation.AccelerateInterpolator r5 = r3.f2071k
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 >= 0) goto L26
            float r4 = -r4
            float r4 = r5.getInterpolation(r4)
            float r4 = -r4
            goto L2e
        L26:
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 <= 0) goto L37
            float r4 = r5.getInterpolation(r4)
        L2e:
            r5 = -1082130432(0xffffffffbf800000, float:-1.0)
            r0 = 1065353216(0x3f800000, float:1.0)
            float r4 = b(r4, r5, r0)
            goto L38
        L37:
            r4 = 0
        L38:
            int r5 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r5 != 0) goto L3d
            return r2
        L3d:
            float[] r0 = r3.f2078r
            r0 = r0[r7]
            float[] r1 = r3.f2079s
            r1 = r1[r7]
            float[] r2 = r3.f2080t
            r7 = r2[r7]
            float r0 = r0 * r6
            if (r5 <= 0) goto L54
            float r4 = r4 * r0
            float r4 = b(r4, r1, r7)
            return r4
        L54:
            float r4 = -r4
            float r4 = r4 * r0
            float r4 = b(r4, r1, r7)
            float r4 = -r4
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: S.a.a(float, float, float, int):float");
    }

    public final float c(float f, float f4) {
        if (f4 == 0.0f) {
            return 0.0f;
        }
        int i4 = this.f2076p;
        if (i4 != 0 && i4 != 1) {
            if (i4 == 2 && f < 0.0f) {
                return f / (-f4);
            }
        } else if (f < f4) {
            if (f >= 0.0f) {
                return 1.0f - (f / f4);
            }
            if (this.f2084x && i4 == 1) {
                return 1.0f;
            }
        }
        return 0.0f;
    }

    public final void e() {
        int i4 = 0;
        if (this.f2082v) {
            this.f2084x = false;
            return;
        }
        C0022a c0022a = this.f2070j;
        c0022a.getClass();
        long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        int i5 = (int) (currentAnimationTimeMillis - c0022a.f2090e);
        int i6 = c0022a.f2087b;
        if (i5 > i6) {
            i4 = i6;
        } else if (i5 >= 0) {
            i4 = i5;
        }
        c0022a.f2093i = i4;
        c0022a.f2092h = c0022a.a(currentAnimationTimeMillis);
        c0022a.f2091g = currentAnimationTimeMillis;
    }

    public final boolean h() {
        ListView listView;
        int count;
        C0022a c0022a = this.f2070j;
        float f = c0022a.f2089d;
        int abs = (int) (f / Math.abs(f));
        Math.abs(c0022a.f2088c);
        if (abs == 0 || (count = (listView = ((e) this).f2097A).getCount()) == 0) {
            return false;
        }
        int childCount = listView.getChildCount();
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int i4 = firstVisiblePosition + childCount;
        if (abs > 0) {
            if (i4 >= count && listView.getChildAt(childCount - 1).getBottom() <= listView.getHeight()) {
                return false;
            }
        } else if (abs >= 0) {
            return false;
        } else {
            if (firstVisiblePosition <= 0 && listView.getChildAt(0).getTop() >= 0) {
                return false;
            }
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0013, code lost:
        if (r0 != 3) goto L12;
     */
    @Override // android.view.View.OnTouchListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean onTouch(android.view.View r8, android.view.MotionEvent r9) {
        /*
            r7 = this;
            boolean r0 = r7.f2085y
            r1 = 0
            if (r0 != 0) goto L6
            return r1
        L6:
            int r0 = r9.getActionMasked()
            r2 = 1
            if (r0 == 0) goto L1a
            if (r0 == r2) goto L16
            r3 = 2
            if (r0 == r3) goto L1e
            r8 = 3
            if (r0 == r8) goto L16
            goto L7b
        L16:
            r7.e()
            goto L7b
        L1a:
            r7.f2083w = r2
            r7.f2081u = r1
        L1e:
            float r0 = r9.getX()
            int r3 = r8.getWidth()
            float r3 = (float) r3
            android.view.View r4 = r7.f2072l
            int r5 = r4.getWidth()
            float r5 = (float) r5
            float r0 = r7.a(r0, r3, r5, r1)
            float r9 = r9.getY()
            int r8 = r8.getHeight()
            float r8 = (float) r8
            int r3 = r4.getHeight()
            float r3 = (float) r3
            float r8 = r7.a(r9, r8, r3, r2)
            S.a$a r9 = r7.f2070j
            r9.f2088c = r0
            r9.f2089d = r8
            boolean r8 = r7.f2084x
            if (r8 != 0) goto L7b
            boolean r8 = r7.h()
            if (r8 == 0) goto L7b
            S.a$b r8 = r7.f2073m
            if (r8 != 0) goto L5f
            S.a$b r8 = new S.a$b
            r8.<init>()
            r7.f2073m = r8
        L5f:
            r7.f2084x = r2
            r7.f2082v = r2
            boolean r8 = r7.f2081u
            if (r8 != 0) goto L74
            int r8 = r7.f2077q
            if (r8 <= 0) goto L74
            S.a$b r9 = r7.f2073m
            long r5 = (long) r8
            java.util.WeakHashMap<android.view.View, M.V> r8 = M.O.f1526a
            r4.postOnAnimationDelayed(r9, r5)
            goto L79
        L74:
            S.a$b r8 = r7.f2073m
            r8.run()
        L79:
            r7.f2081u = r2
        L7b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: S.a.onTouch(android.view.View, android.view.MotionEvent):boolean");
    }
}
