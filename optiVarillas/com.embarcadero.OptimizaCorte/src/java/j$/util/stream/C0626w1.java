package j$.util.stream;

import j$.util.Spliterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.w1  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0626w1 extends AbstractC0630x1 {

    /* renamed from: h  reason: collision with root package name */
    private final Object[] f4608h;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0626w1(Spliterator spliterator, AbstractC0521b abstractC0521b, Object[] objArr) {
        super(spliterator, abstractC0521b, objArr.length);
        this.f4608h = objArr;
    }

    C0626w1(C0626w1 c0626w1, Spliterator spliterator, long j4, long j5) {
        super(c0626w1, spliterator, j4, j5, c0626w1.f4608h.length);
        this.f4608h = c0626w1.f4608h;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        int i4 = this.f;
        if (i4 >= this.f4619g) {
            throw new IndexOutOfBoundsException(Integer.toString(this.f));
        }
        Object[] objArr = this.f4608h;
        this.f = i4 + 1;
        objArr[i4] = obj;
    }

    @Override // j$.util.stream.AbstractC0630x1
    final AbstractC0630x1 b(Spliterator spliterator, long j4, long j5) {
        return new C0626w1(this, spliterator, j4, j5);
    }
}
