package j$.util.stream;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class Collectors {

    /* renamed from: a  reason: collision with root package name */
    static final Set f4262a;

    static {
        EnumC0556i enumC0556i = EnumC0556i.CONCURRENT;
        EnumC0556i enumC0556i2 = EnumC0556i.UNORDERED;
        EnumC0556i enumC0556i3 = EnumC0556i.IDENTITY_FINISH;
        Collections.unmodifiableSet(EnumSet.of(enumC0556i, enumC0556i2, enumC0556i3));
        Collections.unmodifiableSet(EnumSet.of(enumC0556i, enumC0556i2));
        f4262a = Collections.unmodifiableSet(EnumSet.of(enumC0556i3));
        Collections.unmodifiableSet(EnumSet.of(enumC0556i2, enumC0556i3));
        Collections.emptySet();
        Collections.unmodifiableSet(EnumSet.of(enumC0556i2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void a(double[] dArr, double d4) {
        double d5 = d4 - dArr[1];
        double d6 = dArr[0];
        double d7 = d6 + d5;
        dArr[1] = (d7 - d6) - d5;
        dArr[0] = d7;
    }

    public static <T> Collector<T, ?, List<T>> toList() {
        return new C0576m(new C0571l(19), new C0571l(20), new C0571l(0));
    }
}
