package u0;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b extends u0.f {

    /* renamed from: J  reason: collision with root package name */
    public static final String[] f5930J = {"android:changeBounds:bounds", "android:changeBounds:clip", "android:changeBounds:parent", "android:changeBounds:windowX", "android:changeBounds:windowY"};

    /* renamed from: K  reason: collision with root package name */
    public static final a f5931K = new Property(PointF.class, "topLeft");

    /* renamed from: L  reason: collision with root package name */
    public static final C0072b f5932L = new Property(PointF.class, "bottomRight");

    /* renamed from: M  reason: collision with root package name */
    public static final c f5933M = new Property(PointF.class, "bottomRight");

    /* renamed from: N  reason: collision with root package name */
    public static final d f5934N = new Property(PointF.class, "topLeft");

    /* renamed from: O  reason: collision with root package name */
    public static final e f5935O = new Property(PointF.class, "position");

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class a extends Property<h, PointF> {
        @Override // android.util.Property
        public final /* bridge */ /* synthetic */ PointF get(h hVar) {
            return null;
        }

        @Override // android.util.Property
        public final void set(h hVar, PointF pointF) {
            h hVar2 = hVar;
            PointF pointF2 = pointF;
            hVar2.getClass();
            hVar2.f5938a = Math.round(pointF2.x);
            int round = Math.round(pointF2.y);
            hVar2.f5939b = round;
            int i4 = hVar2.f + 1;
            hVar2.f = i4;
            if (i4 == hVar2.f5943g) {
                o.a(hVar2.f5942e, hVar2.f5938a, round, hVar2.f5940c, hVar2.f5941d);
                hVar2.f = 0;
                hVar2.f5943g = 0;
            }
        }
    }

    /* renamed from: u0.b$b  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class C0072b extends Property<h, PointF> {
        @Override // android.util.Property
        public final /* bridge */ /* synthetic */ PointF get(h hVar) {
            return null;
        }

        @Override // android.util.Property
        public final void set(h hVar, PointF pointF) {
            h hVar2 = hVar;
            PointF pointF2 = pointF;
            hVar2.getClass();
            hVar2.f5940c = Math.round(pointF2.x);
            int round = Math.round(pointF2.y);
            hVar2.f5941d = round;
            int i4 = hVar2.f5943g + 1;
            hVar2.f5943g = i4;
            if (hVar2.f == i4) {
                o.a(hVar2.f5942e, hVar2.f5938a, hVar2.f5939b, hVar2.f5940c, round);
                hVar2.f = 0;
                hVar2.f5943g = 0;
            }
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class c extends Property<View, PointF> {
        @Override // android.util.Property
        public final /* bridge */ /* synthetic */ PointF get(View view) {
            return null;
        }

        @Override // android.util.Property
        public final void set(View view, PointF pointF) {
            View view2 = view;
            PointF pointF2 = pointF;
            o.a(view2, view2.getLeft(), view2.getTop(), Math.round(pointF2.x), Math.round(pointF2.y));
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class d extends Property<View, PointF> {
        @Override // android.util.Property
        public final /* bridge */ /* synthetic */ PointF get(View view) {
            return null;
        }

        @Override // android.util.Property
        public final void set(View view, PointF pointF) {
            View view2 = view;
            PointF pointF2 = pointF;
            o.a(view2, Math.round(pointF2.x), Math.round(pointF2.y), view2.getRight(), view2.getBottom());
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class e extends Property<View, PointF> {
        @Override // android.util.Property
        public final /* bridge */ /* synthetic */ PointF get(View view) {
            return null;
        }

        @Override // android.util.Property
        public final void set(View view, PointF pointF) {
            View view2 = view;
            PointF pointF2 = pointF;
            int round = Math.round(pointF2.x);
            int round2 = Math.round(pointF2.y);
            o.a(view2, round, round2, view2.getWidth() + round, view2.getHeight() + round2);
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public class f extends AnimatorListenerAdapter {
        private final h mViewBounds;

        public f(h hVar) {
            this.mViewBounds = hVar;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class g extends i {

        /* renamed from: a  reason: collision with root package name */
        public boolean f5936a = false;

        /* renamed from: b  reason: collision with root package name */
        public final ViewGroup f5937b;

        public g(ViewGroup viewGroup) {
            this.f5937b = viewGroup;
        }

        @Override // u0.i, u0.f.d
        public final void b() {
            n.a(this.f5937b, false);
        }

        @Override // u0.i, u0.f.d
        public final void c() {
            n.a(this.f5937b, true);
        }

        @Override // u0.f.d
        public final void d(u0.f fVar) {
            if (!this.f5936a) {
                n.a(this.f5937b, false);
            }
            fVar.w(this);
        }

        @Override // u0.i, u0.f.d
        public final void g(u0.f fVar) {
            n.a(this.f5937b, false);
            this.f5936a = true;
        }
    }

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class h {

        /* renamed from: a  reason: collision with root package name */
        public int f5938a;

        /* renamed from: b  reason: collision with root package name */
        public int f5939b;

        /* renamed from: c  reason: collision with root package name */
        public int f5940c;

        /* renamed from: d  reason: collision with root package name */
        public int f5941d;

        /* renamed from: e  reason: collision with root package name */
        public final View f5942e;
        public int f;

        /* renamed from: g  reason: collision with root package name */
        public int f5943g;

        public h(View view) {
            this.f5942e = view;
        }
    }

    public static void H(m mVar) {
        View view = mVar.f6000b;
        if (view.isLaidOut() || view.getWidth() != 0 || view.getHeight() != 0) {
            HashMap hashMap = mVar.f5999a;
            hashMap.put("android:changeBounds:bounds", new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
            hashMap.put("android:changeBounds:parent", mVar.f6000b.getParent());
        }
    }

    @Override // u0.f
    public final void d(m mVar) {
        H(mVar);
    }

    @Override // u0.f
    public final void g(m mVar) {
        H(mVar);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // u0.f
    public final Animator k(ViewGroup viewGroup, m mVar, m mVar2) {
        int i4;
        ObjectAnimator a4;
        if (mVar == null || mVar2 == null) {
            return null;
        }
        HashMap hashMap = mVar.f5999a;
        HashMap hashMap2 = mVar2.f5999a;
        ViewGroup viewGroup2 = (ViewGroup) hashMap.get("android:changeBounds:parent");
        ViewGroup viewGroup3 = (ViewGroup) hashMap2.get("android:changeBounds:parent");
        if (viewGroup2 == null || viewGroup3 == null) {
            return null;
        }
        Rect rect = (Rect) hashMap.get("android:changeBounds:bounds");
        Rect rect2 = (Rect) hashMap2.get("android:changeBounds:bounds");
        int i5 = rect.left;
        int i6 = rect2.left;
        int i7 = rect.top;
        int i8 = rect2.top;
        int i9 = rect.right;
        int i10 = rect2.right;
        int i11 = rect.bottom;
        int i12 = rect2.bottom;
        int i13 = i9 - i5;
        int i14 = i11 - i7;
        int i15 = i10 - i6;
        int i16 = i12 - i8;
        Rect rect3 = (Rect) hashMap.get("android:changeBounds:clip");
        Rect rect4 = (Rect) hashMap2.get("android:changeBounds:clip");
        if ((i13 != 0 && i14 != 0) || (i15 != 0 && i16 != 0)) {
            if (i5 == i6 && i7 == i8) {
                i4 = 0;
            } else {
                i4 = 1;
            }
            if (i9 != i10 || i11 != i12) {
                i4++;
            }
        } else {
            i4 = 0;
        }
        if ((rect3 != null && !rect3.equals(rect4)) || (rect3 == null && rect4 != null)) {
            i4++;
        }
        if (i4 > 0) {
            View view = mVar2.f6000b;
            o.a(view, i5, i7, i9, i11);
            if (i4 == 2) {
                if (i13 == i15 && i14 == i16) {
                    a4 = u0.d.a(view, f5935O, this.f5954E.s(i5, i7, i6, i8));
                } else {
                    h hVar = new h(view);
                    ObjectAnimator a5 = u0.d.a(hVar, f5931K, this.f5954E.s(i5, i7, i6, i8));
                    ObjectAnimator a6 = u0.d.a(hVar, f5932L, this.f5954E.s(i9, i11, i10, i12));
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(a5, a6);
                    animatorSet.addListener(new f(hVar));
                    a4 = animatorSet;
                }
            } else if (i5 == i6 && i7 == i8) {
                a4 = u0.d.a(view, f5933M, this.f5954E.s(i9, i11, i10, i12));
            } else {
                a4 = u0.d.a(view, f5934N, this.f5954E.s(i5, i7, i6, i8));
            }
            if (view.getParent() instanceof ViewGroup) {
                ViewGroup viewGroup4 = (ViewGroup) view.getParent();
                n.a(viewGroup4, true);
                o().a(new g(viewGroup4));
            }
            return a4;
        }
        return null;
    }

    @Override // u0.f
    public final String[] q() {
        return f5930J;
    }
}
