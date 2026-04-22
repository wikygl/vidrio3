package j$.util.stream;

import j$.util.Comparator$CC;
import j$.util.Objects;
import j$.util.Spliterator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class L2 extends AbstractC0554h2 {

    /* renamed from: m  reason: collision with root package name */
    private final boolean f4324m;

    /* renamed from: n  reason: collision with root package name */
    private final Comparator f4325n;

    /* JADX INFO: Access modifiers changed from: package-private */
    public L2(AbstractC0521b abstractC0521b) {
        super(abstractC0521b, EnumC0540e3.f4492q | EnumC0540e3.f4490o, 0);
        this.f4324m = true;
        this.f4325n = Comparator$CC.a();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public L2(AbstractC0521b abstractC0521b, Comparator comparator) {
        super(abstractC0521b, EnumC0540e3.f4492q | EnumC0540e3.f4491p, 0);
        this.f4324m = false;
        this.f4325n = (Comparator) Objects.requireNonNull(comparator);
    }

    @Override // j$.util.stream.AbstractC0521b
    public final L0 K(AbstractC0521b abstractC0521b, Spliterator spliterator, IntFunction intFunction) {
        if (EnumC0540e3.SORTED.r(abstractC0521b.G()) && this.f4324m) {
            return abstractC0521b.y(spliterator, false, intFunction);
        }
        Object[] o4 = abstractC0521b.y(spliterator, true, intFunction).o(intFunction);
        Arrays.sort(o4, this.f4325n);
        return new O0(o4);
    }

    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        Objects.requireNonNull(interfaceC0599q2);
        if (EnumC0540e3.SORTED.r(i4) && this.f4324m) {
            return interfaceC0599q2;
        }
        boolean r4 = EnumC0540e3.SIZED.r(i4);
        Comparator comparator = this.f4325n;
        return r4 ? new E2(interfaceC0599q2, comparator) : new E2(interfaceC0599q2, comparator);
    }
}
