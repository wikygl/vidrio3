package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.LongConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.v1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0622v1 extends AbstractC0630x1 implements InterfaceC0594p2 {

    /* renamed from: h  reason: collision with root package name */
    private final long[] f4600h;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0622v1(Spliterator spliterator, AbstractC0521b abstractC0521b, long[] jArr) {
        super(spliterator, abstractC0521b, jArr.length);
        this.f4600h = jArr;
    }

    C0622v1(C0622v1 c0622v1, Spliterator spliterator, long j4, long j5) {
        super(c0622v1, spliterator, j4, j5, c0622v1.f4600h.length);
        this.f4600h = c0622v1.f4600h;
    }

    @Override // j$.util.stream.AbstractC0630x1, j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public final void accept(long j4) {
        int i4 = this.f;
        if (i4 >= this.f4619g) {
            throw new IndexOutOfBoundsException(Integer.toString(this.f));
        }
        long[] jArr = this.f4600h;
        this.f = i4 + 1;
        jArr[i4] = j4;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        j((Long) obj);
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }

    @Override // j$.util.stream.AbstractC0630x1
    final AbstractC0630x1 b(Spliterator spliterator, long j4, long j5) {
        return new C0622v1(this, spliterator, j4, j5);
    }

    @Override // j$.util.stream.InterfaceC0594p2
    public final /* synthetic */ void j(Long l2) {
        AbstractC0637z0.i(this, l2);
    }
}
