package j$.util;

import java.util.function.IntConsumer;

/* renamed from: j$.util.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0506i implements IntConsumer {
    private long count;
    private long sum;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    @Override // java.util.function.IntConsumer
    public final void accept(int i4) {
        this.count++;
        this.sum += i4;
        this.min = Math.min(this.min, i4);
        this.max = Math.max(this.max, i4);
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    public final void b(C0506i c0506i) {
        this.count += c0506i.count;
        this.sum += c0506i.sum;
        this.min = Math.min(this.min, c0506i.min);
        this.max = Math.max(this.max, c0506i.max);
    }

    public final String toString() {
        String simpleName = C0506i.class.getSimpleName();
        Long valueOf = Long.valueOf(this.count);
        Long valueOf2 = Long.valueOf(this.sum);
        Integer valueOf3 = Integer.valueOf(this.min);
        long j4 = this.count;
        return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", simpleName, valueOf, valueOf2, valueOf3, Double.valueOf(j4 > 0 ? this.sum / j4 : 0.0d), Integer.valueOf(this.max));
    }
}
