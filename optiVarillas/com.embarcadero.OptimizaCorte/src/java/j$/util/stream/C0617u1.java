package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.IntConsumer;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.u1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0617u1 extends AbstractC0630x1 implements InterfaceC0589o2 {

    /* renamed from: h  reason: collision with root package name */
    private final int[] f4594h;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0617u1(Spliterator spliterator, AbstractC0521b abstractC0521b, int[] iArr) {
        super(spliterator, abstractC0521b, iArr.length);
        this.f4594h = iArr;
    }

    C0617u1(C0617u1 c0617u1, Spliterator spliterator, long j4, long j5) {
        super(c0617u1, spliterator, j4, j5, c0617u1.f4594h.length);
        this.f4594h = c0617u1.f4594h;
    }

    @Override // j$.util.stream.AbstractC0630x1, j$.util.stream.InterfaceC0599q2
    public final void accept(int i4) {
        int i5 = this.f;
        if (i5 >= this.f4619g) {
            throw new IndexOutOfBoundsException(Integer.toString(this.f));
        }
        int[] iArr = this.f4594h;
        this.f = i5 + 1;
        iArr[i5] = i4;
    }

    @Override // java.util.function.Consumer
    public final /* bridge */ /* synthetic */ void accept(Object obj) {
        m((Integer) obj);
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }

    @Override // j$.util.stream.AbstractC0630x1
    final AbstractC0630x1 b(Spliterator spliterator, long j4, long j5) {
        return new C0617u1(this, spliterator, j4, j5);
    }

    @Override // j$.util.stream.InterfaceC0589o2
    public final /* synthetic */ void m(Integer num) {
        AbstractC0637z0.g(this, num);
    }
}
