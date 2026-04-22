package j$.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* renamed from: j$.util.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class EnumC0502e implements Comparator, InterfaceC0501d {
    public static final EnumC0502e INSTANCE;

    /* renamed from: a  reason: collision with root package name */
    private static final /* synthetic */ EnumC0502e[] f4203a;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0, types: [java.lang.Enum, j$.util.e] */
    static {
        ?? r12 = new Enum("INSTANCE", 0);
        INSTANCE = r12;
        f4203a = new EnumC0502e[]{r12};
    }

    public static EnumC0502e valueOf(String str) {
        return (EnumC0502e) Enum.valueOf(EnumC0502e.class, str);
    }

    public static EnumC0502e[] values() {
        return (EnumC0502e[]) f4203a.clone();
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return ((Comparable) obj).compareTo((Comparable) obj2);
    }

    @Override // java.util.Comparator
    public final Comparator reversed() {
        return Collections.reverseOrder();
    }

    @Override // java.util.Comparator
    public final Comparator thenComparing(Comparator comparator) {
        Objects.requireNonNull(comparator);
        return new C0499c(this, comparator, 0);
    }

    @Override // java.util.Comparator
    public final Comparator thenComparing(Function function) {
        return Comparator$EL.a(this, Comparator$CC.comparing(function));
    }

    @Override // java.util.Comparator
    public final Comparator thenComparing(Function function, Comparator comparator) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(comparator);
        return Comparator$EL.a(this, new C0499c(comparator, function, 1));
    }

    @Override // java.util.Comparator
    public final Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
        return Comparator$EL.a(this, Comparator$CC.comparingDouble(toDoubleFunction));
    }

    @Override // java.util.Comparator
    public final Comparator thenComparingInt(ToIntFunction toIntFunction) {
        Objects.requireNonNull(toIntFunction);
        return Comparator$EL.a(this, new C0498b(toIntFunction, 0));
    }

    @Override // java.util.Comparator
    public final Comparator thenComparingLong(ToLongFunction toLongFunction) {
        Objects.requireNonNull(toLongFunction);
        return Comparator$EL.a(this, new C0498b(toLongFunction, 3));
    }
}
