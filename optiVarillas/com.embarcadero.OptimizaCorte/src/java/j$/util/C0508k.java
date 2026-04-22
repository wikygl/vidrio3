package j$.util;

import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/* renamed from: j$.util.k  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0508k implements LongConsumer, IntConsumer {
    private long count;
    private long sum;
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;

    @Override // java.util.function.IntConsumer
    public final void accept(int i4) {
        accept(i4);
    }

    @Override // java.util.function.LongConsumer
    public final void accept(long j4) {
        this.count++;
        this.sum += j4;
        this.min = Math.min(this.min, j4);
        this.max = Math.max(this.max, j4);
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    public final void b(C0508k c0508k) {
        this.count += c0508k.count;
        this.sum += c0508k.sum;
        this.min = Math.min(this.min, c0508k.min);
        this.max = Math.max(this.max, c0508k.max);
    }

    public final String toString() {
        String simpleName = C0508k.class.getSimpleName();
        Long valueOf = Long.valueOf(this.count);
        Long valueOf2 = Long.valueOf(this.sum);
        Long valueOf3 = Long.valueOf(this.min);
        long j4 = this.count;
        return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", simpleName, valueOf, valueOf2, valueOf3, Double.valueOf(j4 > 0 ? this.sum / j4 : 0.0d), Long.valueOf(this.max));
    }
}
