package j$.lang;

import j$.util.Objects;
import java.util.function.Consumer;

/* renamed from: j$.lang.Iterable$-CC  reason: invalid class name */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class Iterable$CC {
    public static void $default$forEach(Iterable iterable, Consumer consumer) {
        Objects.requireNonNull(consumer);
        for (Object obj : iterable) {
            consumer.accept(obj);
        }
    }
}
