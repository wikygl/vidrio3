package j$.util;

import java.util.Comparator;
import java.util.function.Function;

/* renamed from: j$.util.Comparator$-EL  reason: invalid class name */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class Comparator$EL {
    public static C0499c a(Comparator comparator, Comparator comparator2) {
        if (!(comparator instanceof InterfaceC0501d)) {
            Objects.requireNonNull(comparator2);
            return new C0499c(comparator, comparator2, 0);
        }
        EnumC0502e enumC0502e = (EnumC0502e) ((InterfaceC0501d) comparator);
        enumC0502e.getClass();
        Objects.requireNonNull(comparator2);
        return new C0499c(enumC0502e, comparator2, 0);
    }

    public static Comparator thenComparing(Comparator comparator, Function function) {
        if (comparator instanceof InterfaceC0501d) {
            EnumC0502e enumC0502e = (EnumC0502e) ((InterfaceC0501d) comparator);
            enumC0502e.getClass();
            return a(enumC0502e, Comparator$CC.comparing(function));
        }
        return a(comparator, Comparator$CC.comparing(function));
    }
}
