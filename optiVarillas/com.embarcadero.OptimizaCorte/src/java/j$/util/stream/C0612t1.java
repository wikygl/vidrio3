package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.DoubleConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.t1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0612t1 extends AbstractC0630x1 implements InterfaceC0584n2 {

    /* renamed from: h  reason: collision with root package name */
    private final double[] f4588h;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0612t1(Spliterator spliterator, AbstractC0521b abstractC0521b, double[] dArr) {
        super(spliterator, abstractC0521b, dArr.length);
        this.f4588h = dArr;
    }

    C0612t1(C0612t1 c0612t1, Spliterator spliterator, long j4, long j5) {
        super(c0612t1, spliterator, j4, j5, c0612t1.f4588h.length);
        this.f4588h = c0612t1.f4588h;
    }

    @Override // j$.util.stream.AbstractC0630x1, j$.util.stream.InterfaceC0599q2
    public final void accept(double d4) {
        int i4 = this.f;
        if (i4 >= this.f4619g) {
            throw new IndexOutOfBoundsException(Integer.toString(this.f));
        }
        double[] dArr = this.f4588h;
        this.f = i4 + 1;
        dArr[i4] = d4;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        p((Double) obj);
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }

    @Override // j$.util.stream.AbstractC0630x1
    final AbstractC0630x1 b(Spliterator spliterator, long j4, long j5) {
        return new C0612t1(this, spliterator, j4, j5);
    }

    @Override // j$.util.stream.InterfaceC0584n2
    public final /* synthetic */ void p(Double d4) {
        AbstractC0637z0.e(this, d4);
    }
}
