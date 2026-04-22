package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: j$.util.stream.y0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0633y0 extends AbstractC0526c {

    /* renamed from: j  reason: collision with root package name */
    private final C0629x0 f4626j;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0633y0(C0629x0 c0629x0, AbstractC0521b abstractC0521b, Spliterator spliterator) {
        super(abstractC0521b, spliterator);
        this.f4626j = c0629x0;
    }

    C0633y0(C0633y0 c0633y0, Spliterator spliterator) {
        super(c0633y0, spliterator);
        this.f4626j = c0633y0.f4626j;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final Object a() {
        boolean z4;
        AbstractC0521b abstractC0521b = this.f4476a;
        AbstractC0621v0 abstractC0621v0 = (AbstractC0621v0) this.f4626j.f4613b.get();
        abstractC0521b.R(this.f4477b, abstractC0621v0);
        boolean z5 = abstractC0621v0.f4599b;
        z4 = this.f4626j.f4612a.f4607b;
        if (z5 == z4) {
            Boolean valueOf = Boolean.valueOf(z5);
            AtomicReference atomicReference = this.f4461h;
            while (!atomicReference.compareAndSet(null, valueOf) && atomicReference.get() == null) {
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractC0536e
    public final AbstractC0536e e(Spliterator spliterator) {
        return new C0633y0(this, spliterator);
    }

    @Override // j$.util.stream.AbstractC0526c
    protected final Object j() {
        boolean z4;
        z4 = this.f4626j.f4612a.f4607b;
        return Boolean.valueOf(!z4);
    }
}
