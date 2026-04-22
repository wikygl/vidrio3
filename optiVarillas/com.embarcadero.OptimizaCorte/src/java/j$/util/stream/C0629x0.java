package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.Supplier;

/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.x0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final class C0629x0 implements K3 {

    /* renamed from: a  reason: collision with root package name */
    final EnumC0625w0 f4612a;

    /* renamed from: b  reason: collision with root package name */
    final Supplier f4613b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0629x0(EnumC0545f3 enumC0545f3, EnumC0625w0 enumC0625w0, Supplier supplier) {
        this.f4612a = enumC0625w0;
        this.f4613b = supplier;
    }

    @Override // j$.util.stream.K3
    public final Object b(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        AbstractC0621v0 abstractC0621v0 = (AbstractC0621v0) this.f4613b.get();
        abstractC0521b.R(spliterator, abstractC0621v0);
        return Boolean.valueOf(abstractC0621v0.f4599b);
    }

    @Override // j$.util.stream.K3
    public final Object c(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        return (Boolean) new C0633y0(this, abstractC0521b, spliterator).invoke();
    }

    @Override // j$.util.stream.K3
    public final int d() {
        return EnumC0540e3.f4496u | EnumC0540e3.f4493r;
    }
}
