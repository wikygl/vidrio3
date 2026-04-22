package r2;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import com.google.android.gms.internal.ads.gI;

/* renamed from: r2.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0787e {

    /* renamed from: a  reason: collision with root package name */
    public long f5720a;

    /* renamed from: c  reason: collision with root package name */
    public TimeInterpolator f5722c = null;

    /* renamed from: d  reason: collision with root package name */
    public int f5723d = 0;

    /* renamed from: e  reason: collision with root package name */
    public int f5724e = 1;

    /* renamed from: b  reason: collision with root package name */
    public long f5721b = 150;

    public C0787e(long j4) {
        this.f5720a = j4;
    }

    public final void a(Animator animator) {
        animator.setStartDelay(this.f5720a);
        animator.setDuration(this.f5721b);
        animator.setInterpolator(b());
        if (animator instanceof ValueAnimator) {
            ValueAnimator valueAnimator = (ValueAnimator) animator;
            valueAnimator.setRepeatCount(this.f5723d);
            valueAnimator.setRepeatMode(this.f5724e);
        }
    }

    public final TimeInterpolator b() {
        TimeInterpolator timeInterpolator = this.f5722c;
        if (timeInterpolator == null) {
            return C0783a.f5710b;
        }
        return timeInterpolator;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof C0787e)) {
            return false;
        }
        C0787e c0787e = (C0787e) obj;
        if (this.f5720a != c0787e.f5720a || this.f5721b != c0787e.f5721b || this.f5723d != c0787e.f5723d || this.f5724e != c0787e.f5724e) {
            return false;
        }
        return b().getClass().equals(c0787e.b().getClass());
    }

    public final int hashCode() {
        long j4 = this.f5720a;
        long j5 = this.f5721b;
        return ((((b().getClass().hashCode() + (((((int) (j4 ^ (j4 >>> 32))) * 31) + ((int) (j5 ^ (j5 >>> 32)))) * 31)) * 31) + this.f5723d) * 31) + this.f5724e;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(C0787e.class.getName());
        sb.append('{');
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" delay: ");
        sb.append(this.f5720a);
        sb.append(" duration: ");
        sb.append(this.f5721b);
        sb.append(" interpolator: ");
        sb.append(b().getClass());
        sb.append(" repeatCount: ");
        sb.append(this.f5723d);
        sb.append(" repeatMode: ");
        return gI.a(sb, this.f5724e, "}\n");
    }
}
