package j$.util.stream;

import j$.util.Spliterator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
abstract class z3 {

    /* renamed from: a  reason: collision with root package name */
    final long f4638a;

    /* renamed from: b  reason: collision with root package name */
    final long f4639b;

    /* renamed from: c  reason: collision with root package name */
    Spliterator f4640c;

    /* renamed from: d  reason: collision with root package name */
    long f4641d;

    /* renamed from: e  reason: collision with root package name */
    long f4642e;

    /* JADX INFO: Access modifiers changed from: package-private */
    public z3(Spliterator spliterator, long j4, long j5, long j6, long j7) {
        this.f4640c = spliterator;
        this.f4638a = j4;
        this.f4639b = j5;
        this.f4641d = j6;
        this.f4642e = j7;
    }

    protected abstract Spliterator a(Spliterator spliterator, long j4, long j5, long j6, long j7);

    public final int characteristics() {
        return this.f4640c.characteristics();
    }

    public final long estimateSize() {
        long j4 = this.f4642e;
        long j5 = this.f4638a;
        if (j5 < j4) {
            return j4 - Math.max(j5, this.f4641d);
        }
        return 0L;
    }

    public /* bridge */ /* synthetic */ j$.util.G trySplit() {
        return (j$.util.G) m10trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.J m7trySplit() {
        return (j$.util.J) m10trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.M m8trySplit() {
        return (j$.util.M) m10trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public /* bridge */ /* synthetic */ j$.util.P m9trySplit() {
        return (j$.util.P) m10trySplit();
    }

    /* renamed from: trySplit  reason: collision with other method in class */
    public final Spliterator m10trySplit() {
        long j4 = this.f4642e;
        if (this.f4638a >= j4 || this.f4641d >= j4) {
            return null;
        }
        while (true) {
            Spliterator trySplit = this.f4640c.trySplit();
            if (trySplit == null) {
                return null;
            }
            long estimateSize = trySplit.estimateSize() + this.f4641d;
            long min = Math.min(estimateSize, this.f4639b);
            long j5 = this.f4638a;
            if (j5 >= min) {
                this.f4641d = min;
            } else {
                long j6 = this.f4639b;
                if (min < j6) {
                    long j7 = this.f4641d;
                    if (j7 < j5 || estimateSize > j6) {
                        this.f4641d = min;
                        return a(trySplit, j5, j6, j7, min);
                    }
                    this.f4641d = min;
                    return trySplit;
                }
                this.f4640c = trySplit;
                this.f4642e = min;
            }
        }
    }
}
