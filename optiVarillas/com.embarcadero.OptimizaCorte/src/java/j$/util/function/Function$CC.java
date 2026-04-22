package j$.util.function;

import j$.util.Objects;
import java.util.function.Function;

/* renamed from: j$.util.function.Function$-CC */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class Function$CC {
    public static Function $default$andThen(Function function, Function function2) {
        Objects.requireNonNull(function2);
        return new c(function, function2, 0);
    }

    public static Function $default$compose(Function function, Function function2) {
        Objects.requireNonNull(function2);
        return new c(function, function2, 1);
    }
}
