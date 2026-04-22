package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import java.util.Arrays;
import java.util.function.IntFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class J2 extends AbstractC0522b0 {
    @Override // j$.util.stream.AbstractC0521b
    public final L0 K(AbstractC0521b abstractC0521b, Spliterator spliterator, IntFunction intFunction) {
        if (EnumC0540e3.SORTED.r(abstractC0521b.G())) {
            return abstractC0521b.y(spliterator, false, intFunction);
        }
        int[] iArr = (int[]) ((H0) abstractC0521b.y(spliterator, true, intFunction)).e();
        Arrays.sort(iArr);
        return new C0553h1(iArr);
    }

    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        Objects.requireNonNull(interfaceC0599q2);
        return EnumC0540e3.SORTED.r(i4) ? interfaceC0599q2 : EnumC0540e3.SIZED.r(i4) ? new AbstractC0569k2(interfaceC0599q2) : new AbstractC0569k2(interfaceC0599q2);
    }
}
