package j$.util;

import java.util.function.DoubleConsumer;

/* renamed from: j$.util.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0505h implements DoubleConsumer {

    /* renamed from: a  reason: collision with root package name */
    private double f4228a;

    /* renamed from: b  reason: collision with root package name */
    private double f4229b;
    private long count;
    private double sum;
    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;

    private void c(double d4) {
        double d5 = d4 - this.f4228a;
        double d6 = this.sum;
        double d7 = d6 + d5;
        this.f4228a = (d7 - d6) - d5;
        this.sum = d7;
    }

    @Override // java.util.function.DoubleConsumer
    public final void accept(double d4) {
        this.count++;
        this.f4229b += d4;
        c(d4);
        this.min = Math.min(this.min, d4);
        this.max = Math.max(this.max, d4);
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }

    public final void b(C0505h c0505h) {
        this.count += c0505h.count;
        this.f4229b += c0505h.f4229b;
        c(c0505h.sum);
        c(c0505h.f4228a);
        this.min = Math.min(this.min, c0505h.min);
        this.max = Math.max(this.max, c0505h.max);
    }

    public final String toString() {
        double d4;
        String simpleName = C0505h.class.getSimpleName();
        Long valueOf = Long.valueOf(this.count);
        double d5 = this.sum + this.f4228a;
        if (Double.isNaN(d5) && Double.isInfinite(this.f4229b)) {
            d5 = this.f4229b;
        }
        Double valueOf2 = Double.valueOf(d5);
        Double valueOf3 = Double.valueOf(this.min);
        if (this.count > 0) {
            double d6 = this.sum + this.f4228a;
            if (Double.isNaN(d6) && Double.isInfinite(this.f4229b)) {
                d6 = this.f4229b;
            }
            d4 = d6 / this.count;
        } else {
            d4 = 0.0d;
        }
        return String.format("%s{count=%d, sum=%f, min=%f, average=%f, max=%f}", simpleName, valueOf, valueOf2, valueOf3, Double.valueOf(d4), Double.valueOf(this.max));
    }
}
