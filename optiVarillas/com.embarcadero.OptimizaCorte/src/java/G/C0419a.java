package g;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import g.C0420b;
import g.d;
import r.C0778g;
import r.k;

/* renamed from: g.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0419a extends g.d implements F.b {

    /* renamed from: A  reason: collision with root package name */
    public int f3410A = -1;

    /* renamed from: B  reason: collision with root package name */
    public int f3411B = -1;

    /* renamed from: C  reason: collision with root package name */
    public boolean f3412C;

    /* renamed from: y  reason: collision with root package name */
    public b f3413y;

    /* renamed from: z  reason: collision with root package name */
    public f f3414z;

    /* renamed from: g.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class C0047a extends f {

        /* renamed from: a  reason: collision with root package name */
        public final Animatable f3415a;

        public C0047a(Animatable animatable) {
            this.f3415a = animatable;
        }

        @Override // g.C0419a.f
        public final void c() {
            this.f3415a.start();
        }

        @Override // g.C0419a.f
        public final void d() {
            this.f3415a.stop();
        }
    }

    /* renamed from: g.a$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
    public static class b extends d.a {

        /* renamed from: I  reason: collision with root package name */
        public C0778g<Long> f3416I;

        /* renamed from: J  reason: collision with root package name */
        public k<Integer> f3417J;

        public b(b bVar, C0419a c0419a, Resources resources) {
            super(bVar, c0419a, resources);
            if (bVar != null) {
                this.f3416I = bVar.f3416I;
                this.f3417J = bVar.f3417J;
                return;
            }
            this.f3416I = new C0778g<>();
            this.f3417J = new k<>();
        }

        @Override // g.d.a, g.C0420b.c
        public final void e() {
            this.f3416I = this.f3416I.clone();
            this.f3417J = this.f3417J.clone();
        }

        @Override // g.d.a, android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable() {
            return new C0419a(this, null);
        }

        @Override // g.d.a, android.graphics.drawable.Drawable.ConstantState
        public final Drawable newDrawable(Resources resources) {
            return new C0419a(this, resources);
        }
    }

    /* renamed from: g.a$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class c extends f {

        /* renamed from: a  reason: collision with root package name */
        public final v0.d f3418a;

        public c(v0.d dVar) {
            this.f3418a = dVar;
        }

        @Override // g.C0419a.f
        public final void c() {
            this.f3418a.start();
        }

        @Override // g.C0419a.f
        public final void d() {
            this.f3418a.stop();
        }
    }

    /* renamed from: g.a$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class d extends f {

        /* renamed from: a  reason: collision with root package name */
        public final ObjectAnimator f3419a;

        /* renamed from: b  reason: collision with root package name */
        public final boolean f3420b;

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v0, types: [android.animation.TimeInterpolator, java.lang.Object, g.a$e] */
        public d(AnimationDrawable animationDrawable, boolean z4, boolean z5) {
            int i4;
            int i5;
            int numberOfFrames = animationDrawable.getNumberOfFrames();
            int i6 = z4 ? numberOfFrames - 1 : 0;
            if (z4) {
                i4 = 0;
            } else {
                i4 = numberOfFrames - 1;
            }
            ?? obj = new Object();
            int numberOfFrames2 = animationDrawable.getNumberOfFrames();
            obj.f3422b = numberOfFrames2;
            int[] iArr = obj.f3421a;
            if (iArr == null || iArr.length < numberOfFrames2) {
                obj.f3421a = new int[numberOfFrames2];
            }
            int[] iArr2 = obj.f3421a;
            int i7 = 0;
            for (int i8 = 0; i8 < numberOfFrames2; i8++) {
                if (z4) {
                    i5 = (numberOfFrames2 - i8) - 1;
                } else {
                    i5 = i8;
                }
                int duration = animationDrawable.getDuration(i5);
                iArr2[i8] = duration;
                i7 += duration;
            }
            obj.f3423c = i7;
            ObjectAnimator ofInt = ObjectAnimator.ofInt(animationDrawable, "currentIndex", i6, i4);
            ofInt.setAutoCancel(true);
            ofInt.setDuration(obj.f3423c);
            ofInt.setInterpolator(obj);
            this.f3420b = z5;
            this.f3419a = ofInt;
        }

        @Override // g.C0419a.f
        public final boolean a() {
            return this.f3420b;
        }

        @Override // g.C0419a.f
        public final void b() {
            this.f3419a.reverse();
        }

        @Override // g.C0419a.f
        public final void c() {
            this.f3419a.start();
        }

        @Override // g.C0419a.f
        public final void d() {
            this.f3419a.cancel();
        }
    }

    /* renamed from: g.a$e */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class e implements TimeInterpolator {

        /* renamed from: a  reason: collision with root package name */
        public int[] f3421a;

        /* renamed from: b  reason: collision with root package name */
        public int f3422b;

        /* renamed from: c  reason: collision with root package name */
        public int f3423c;

        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            float f4;
            int i4 = (int) ((f * this.f3423c) + 0.5f);
            int i5 = this.f3422b;
            int[] iArr = this.f3421a;
            int i6 = 0;
            while (i6 < i5) {
                int i7 = iArr[i6];
                if (i4 < i7) {
                    break;
                }
                i4 -= i7;
                i6++;
            }
            if (i6 < i5) {
                f4 = i4 / this.f3423c;
            } else {
                f4 = 0.0f;
            }
            return (i6 / i5) + f4;
        }
    }

    public C0419a(b bVar, Resources resources) {
        e(new b(bVar, this, resources));
        onStateChange(getState());
        jumpToCurrentState();
    }

    /* JADX WARN: Code restructure failed: missing block: B:94:0x0260, code lost:
        r5.onStateChange(r5.getState());
     */
    /* JADX WARN: Code restructure failed: missing block: B:95:0x0267, code lost:
        return r5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static g.C0419a g(android.content.Context r23, android.content.res.Resources r24, android.content.res.XmlResourceParser r25, android.util.AttributeSet r26, android.content.res.Resources.Theme r27) {
        /*
            Method dump skipped, instructions count: 646
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: g.C0419a.g(android.content.Context, android.content.res.Resources, android.content.res.XmlResourceParser, android.util.AttributeSet, android.content.res.Resources$Theme):g.a");
    }

    @Override // g.d, g.C0420b
    public final C0420b.c b() {
        return new b(this.f3413y, this, null);
    }

    @Override // g.d, g.C0420b
    public final void e(C0420b.c cVar) {
        super.e(cVar);
        if (cVar instanceof b) {
            this.f3413y = (b) cVar;
        }
    }

    @Override // g.d
    public final d.a f() {
        return new b(this.f3413y, this, null);
    }

    @Override // g.C0420b, android.graphics.drawable.Drawable
    public final void jumpToCurrentState() {
        super.jumpToCurrentState();
        f fVar = this.f3414z;
        if (fVar != null) {
            fVar.d();
            this.f3414z = null;
            d(this.f3410A);
            this.f3410A = -1;
            this.f3411B = -1;
        }
    }

    @Override // g.d, g.C0420b, android.graphics.drawable.Drawable
    public final Drawable mutate() {
        if (!this.f3412C) {
            super.mutate();
            this.f3413y.e();
            this.f3412C = true;
        }
        return this;
    }

    /* JADX WARN: Code restructure failed: missing block: B:50:0x0104, code lost:
        if (d(r3) != false) goto L16;
     */
    @Override // g.d, g.C0420b, android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean onStateChange(int[] r18) {
        /*
            Method dump skipped, instructions count: 273
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: g.C0419a.onStateChange(int[]):boolean");
    }

    @Override // g.C0420b, android.graphics.drawable.Drawable
    public final boolean setVisible(boolean z4, boolean z5) {
        boolean visible = super.setVisible(z4, z5);
        f fVar = this.f3414z;
        if (fVar != null && (visible || z5)) {
            if (z4) {
                fVar.c();
            } else {
                jumpToCurrentState();
            }
        }
        return visible;
    }

    /* renamed from: g.a$f */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class f {
        public boolean a() {
            return false;
        }

        public abstract void c();

        public abstract void d();

        public void b() {
        }
    }
}
