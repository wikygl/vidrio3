package j$.util.stream;

import j$.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class O3 extends AbstractC0554h2 {

    /* renamed from: m  reason: collision with root package name */
    final /* synthetic */ Predicate f4352m;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public O3(AbstractC0521b abstractC0521b, int i4, Predicate predicate) {
        super(abstractC0521b, i4, 0);
        this.f4352m = predicate;
    }

    @Override // j$.util.stream.AbstractC0521b
    final L0 K(AbstractC0521b abstractC0521b, Spliterator spliterator, IntFunction intFunction) {
        return (L0) new S3(this, abstractC0521b, spliterator, intFunction).invoke();
    }

    @Override // j$.util.stream.AbstractC0521b
    final Spliterator L(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        return EnumC0540e3.ORDERED.r(abstractC0521b.G()) ? K(abstractC0521b, spliterator, new C0537e0(3)).spliterator() : new T3(abstractC0521b.T(spliterator), this.f4352m, 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        return new C0581n(this, interfaceC0599q2);
    }
}
