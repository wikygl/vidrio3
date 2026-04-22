package j$.util.stream;

import j$.util.Objects;
import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;

/* renamed from: j$.util.stream.p  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0591p extends AbstractC0554h2 {
    static P0 U(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        C0571l c0571l = new C0571l(23);
        C0571l c0571l2 = new C0571l(24);
        C0571l c0571l3 = new C0571l(25);
        Objects.requireNonNull(c0571l);
        Objects.requireNonNull(c0571l2);
        Objects.requireNonNull(c0571l3);
        return new P0((Collection) new F1(EnumC0545f3.REFERENCE, (Object) c0571l3, (Object) c0571l2, (Object) c0571l, 3).c(abstractC0521b, spliterator));
    }

    @Override // j$.util.stream.AbstractC0521b
    final L0 K(AbstractC0521b abstractC0521b, Spliterator spliterator, IntFunction intFunction) {
        if (EnumC0540e3.DISTINCT.r(abstractC0521b.G())) {
            return abstractC0521b.y(spliterator, false, intFunction);
        }
        if (EnumC0540e3.ORDERED.r(abstractC0521b.G())) {
            return U(abstractC0521b, spliterator);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        C0597q0 c0597q0 = new C0597q0(2, atomicBoolean, concurrentHashMap);
        Objects.requireNonNull(c0597q0);
        new Q(c0597q0, false).e(abstractC0521b, spliterator);
        Collection keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add(null);
            keySet = hashSet;
        }
        return new P0(keySet);
    }

    @Override // j$.util.stream.AbstractC0521b
    final Spliterator L(AbstractC0521b abstractC0521b, Spliterator spliterator) {
        return EnumC0540e3.DISTINCT.r(abstractC0521b.G()) ? abstractC0521b.T(spliterator) : EnumC0540e3.ORDERED.r(abstractC0521b.G()) ? U(abstractC0521b, spliterator).spliterator() : new C0585n3(abstractC0521b.T(spliterator));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.util.stream.AbstractC0521b
    public final InterfaceC0599q2 N(int i4, InterfaceC0599q2 interfaceC0599q2) {
        Objects.requireNonNull(interfaceC0599q2);
        return EnumC0540e3.DISTINCT.r(i4) ? interfaceC0599q2 : EnumC0540e3.SORTED.r(i4) ? new C0581n(interfaceC0599q2) : new C0586o(interfaceC0599q2);
    }
}
