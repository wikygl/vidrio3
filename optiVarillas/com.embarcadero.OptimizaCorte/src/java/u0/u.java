package u0;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;
import u0.f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class u extends f {

    /* renamed from: K  reason: collision with root package name */
    public static final String[] f6013K = {"android:visibility:visibility", "android:visibility:parent"};

    /* renamed from: J  reason: collision with root package name */
    public int f6014J = 3;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class a extends AnimatorListenerAdapter implements f.d {

        /* renamed from: a  reason: collision with root package name */
        public final View f6015a;

        /* renamed from: b  reason: collision with root package name */
        public final int f6016b;

        /* renamed from: c  reason: collision with root package name */
        public final ViewGroup f6017c;

        /* renamed from: e  reason: collision with root package name */
        public boolean f6019e;
        public boolean f = false;

        /* renamed from: d  reason: collision with root package name */
        public final boolean f6018d = true;

        public a(View view, int i4) {
            this.f6015a = view;
            this.f6016b = i4;
            this.f6017c = (ViewGroup) view.getParent();
            h(true);
        }

        @Override // u0.f.d
        public final void a(f fVar) {
        }

        @Override // u0.f.d
        public final void b() {
            h(false);
            if (!this.f) {
                o.b(this.f6015a, this.f6016b);
            }
        }

        @Override // u0.f.d
        public final void c() {
            h(true);
            if (!this.f) {
                o.b(this.f6015a, 0);
            }
        }

        @Override // u0.f.d
        public final void d(f fVar) {
            fVar.w(this);
        }

        @Override // u0.f.d
        public final void e(f fVar) {
            fVar.w(this);
        }

        @Override // u0.f.d
        public final void f(f fVar) {
            throw null;
        }

        public final void h(boolean z4) {
            ViewGroup viewGroup;
            if (this.f6018d && this.f6019e != z4 && (viewGroup = this.f6017c) != null) {
                this.f6019e = z4;
                n.a(viewGroup, z4);
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationCancel(Animator animator) {
            this.f = true;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator) {
            if (!this.f) {
                o.b(this.f6015a, this.f6016b);
                ViewGroup viewGroup = this.f6017c;
                if (viewGroup != null) {
                    viewGroup.invalidate();
                }
            }
            h(false);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationStart(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationStart(Animator animator, boolean z4) {
            if (z4) {
                o.b(this.f6015a, 0);
                ViewGroup viewGroup = this.f6017c;
                if (viewGroup != null) {
                    viewGroup.invalidate();
                }
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator, boolean z4) {
            if (z4) {
                return;
            }
            if (!this.f) {
                o.b(this.f6015a, this.f6016b);
                ViewGroup viewGroup = this.f6017c;
                if (viewGroup != null) {
                    viewGroup.invalidate();
                }
            }
            h(false);
        }

        @Override // u0.f.d
        public final void g(f fVar) {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationRepeat(Animator animator) {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public class b extends AnimatorListenerAdapter implements f.d {

        /* renamed from: a  reason: collision with root package name */
        public final ViewGroup f6020a;

        /* renamed from: b  reason: collision with root package name */
        public final View f6021b;

        /* renamed from: c  reason: collision with root package name */
        public final View f6022c;

        /* renamed from: d  reason: collision with root package name */
        public boolean f6023d = true;

        public b(ViewGroup viewGroup, View view, View view2) {
            this.f6020a = viewGroup;
            this.f6021b = view;
            this.f6022c = view2;
        }

        @Override // u0.f.d
        public final void a(f fVar) {
        }

        @Override // u0.f.d
        public final void d(f fVar) {
            fVar.w(this);
        }

        @Override // u0.f.d
        public final void e(f fVar) {
            fVar.w(this);
        }

        @Override // u0.f.d
        public final void f(f fVar) {
            throw null;
        }

        @Override // u0.f.d
        public final void g(f fVar) {
            if (this.f6023d) {
                h();
            }
        }

        public final void h() {
            this.f6022c.setTag(2131231182, null);
            this.f6020a.getOverlay().remove(this.f6021b);
            this.f6023d = false;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator) {
            h();
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorPauseListener
        public final void onAnimationPause(Animator animator) {
            this.f6020a.getOverlay().remove(this.f6021b);
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorPauseListener
        public final void onAnimationResume(Animator animator) {
            View view = this.f6021b;
            if (view.getParent() == null) {
                this.f6020a.getOverlay().add(view);
            } else {
                u.this.c();
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationStart(Animator animator, boolean z4) {
            if (z4) {
                View view = this.f6022c;
                View view2 = this.f6021b;
                view.setTag(2131231182, view2);
                this.f6020a.getOverlay().add(view2);
                this.f6023d = true;
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public final void onAnimationEnd(Animator animator, boolean z4) {
            if (z4) {
                return;
            }
            h();
        }

        @Override // u0.f.d
        public final void b() {
        }

        @Override // u0.f.d
        public final void c() {
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class c {

        /* renamed from: a  reason: collision with root package name */
        public boolean f6025a;

        /* renamed from: b  reason: collision with root package name */
        public boolean f6026b;

        /* renamed from: c  reason: collision with root package name */
        public int f6027c;

        /* renamed from: d  reason: collision with root package name */
        public int f6028d;

        /* renamed from: e  reason: collision with root package name */
        public ViewGroup f6029e;
        public ViewGroup f;
    }

    public static void H(m mVar) {
        int visibility = mVar.f6000b.getVisibility();
        HashMap hashMap = mVar.f5999a;
        hashMap.put("android:visibility:visibility", Integer.valueOf(visibility));
        View view = mVar.f6000b;
        hashMap.put("android:visibility:parent", view.getParent());
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        hashMap.put("android:visibility:screenLocation", iArr);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0059 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:33:0x008c  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0097  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x0035  */
    /* JADX WARN: Type inference failed for: r0v0, types: [u0.u$c, java.lang.Object] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static u0.u.c I(u0.m r8, u0.m r9) {
        /*
            u0.u$c r0 = new u0.u$c
            r0.<init>()
            r1 = 0
            r0.f6025a = r1
            r0.f6026b = r1
            r2 = 0
            r3 = -1
            java.lang.String r4 = "android:visibility:parent"
            java.lang.String r5 = "android:visibility:visibility"
            if (r8 == 0) goto L2f
            java.util.HashMap r6 = r8.f5999a
            boolean r7 = r6.containsKey(r5)
            if (r7 == 0) goto L2f
            java.lang.Object r7 = r6.get(r5)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            r0.f6027c = r7
            java.lang.Object r6 = r6.get(r4)
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            r0.f6029e = r6
            goto L33
        L2f:
            r0.f6027c = r3
            r0.f6029e = r2
        L33:
            if (r9 == 0) goto L52
            java.util.HashMap r6 = r9.f5999a
            boolean r7 = r6.containsKey(r5)
            if (r7 == 0) goto L52
            java.lang.Object r2 = r6.get(r5)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            r0.f6028d = r2
            java.lang.Object r2 = r6.get(r4)
            android.view.ViewGroup r2 = (android.view.ViewGroup) r2
            r0.f = r2
            goto L56
        L52:
            r0.f6028d = r3
            r0.f = r2
        L56:
            r2 = 1
            if (r8 == 0) goto L8a
            if (r9 == 0) goto L8a
            int r8 = r0.f6027c
            int r9 = r0.f6028d
            if (r8 != r9) goto L68
            android.view.ViewGroup r3 = r0.f6029e
            android.view.ViewGroup r4 = r0.f
            if (r3 != r4) goto L68
            return r0
        L68:
            if (r8 == r9) goto L78
            if (r8 != 0) goto L71
            r0.f6026b = r1
            r0.f6025a = r2
            goto L9f
        L71:
            if (r9 != 0) goto L9f
            r0.f6026b = r2
            r0.f6025a = r2
            goto L9f
        L78:
            android.view.ViewGroup r8 = r0.f
            if (r8 != 0) goto L81
            r0.f6026b = r1
            r0.f6025a = r2
            goto L9f
        L81:
            android.view.ViewGroup r8 = r0.f6029e
            if (r8 != 0) goto L9f
            r0.f6026b = r2
            r0.f6025a = r2
            goto L9f
        L8a:
            if (r8 != 0) goto L95
            int r8 = r0.f6028d
            if (r8 != 0) goto L95
            r0.f6026b = r2
            r0.f6025a = r2
            goto L9f
        L95:
            if (r9 != 0) goto L9f
            int r8 = r0.f6027c
            if (r8 != 0) goto L9f
            r0.f6026b = r1
            r0.f6025a = r2
        L9f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: u0.u.I(u0.m, u0.m):u0.u$c");
    }

    @Override // u0.f
    public final void d(m mVar) {
        H(mVar);
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x0046, code lost:
        if (I(n(r3, false), r(r3, false)).f6025a != false) goto L19;
     */
    /* JADX WARN: Removed duplicated region for block: B:78:0x01d8  */
    @Override // u0.f
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.animation.Animator k(android.view.ViewGroup r21, u0.m r22, u0.m r23) {
        /*
            Method dump skipped, instructions count: 723
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: u0.u.k(android.view.ViewGroup, u0.m, u0.m):android.animation.Animator");
    }

    @Override // u0.f
    public final String[] q() {
        return f6013K;
    }

    @Override // u0.f
    public final boolean s(m mVar, m mVar2) {
        if (mVar == null && mVar2 == null) {
            return false;
        }
        if (mVar != null && mVar2 != null && mVar2.f5999a.containsKey("android:visibility:visibility") != mVar.f5999a.containsKey("android:visibility:visibility")) {
            return false;
        }
        c I2 = I(mVar, mVar2);
        if (!I2.f6025a) {
            return false;
        }
        if (I2.f6027c != 0 && I2.f6028d != 0) {
            return false;
        }
        return true;
    }
}
