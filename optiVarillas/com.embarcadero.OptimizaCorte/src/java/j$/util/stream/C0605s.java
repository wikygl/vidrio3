package j$.util.stream;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/* renamed from: j$.util.stream.s  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0605s implements BinaryOperator {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4580a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ BiConsumer f4581b;

    public /* synthetic */ C0605s(BiConsumer biConsumer, int i4) {
        this.f4580a = i4;
        this.f4581b = biConsumer;
    }

    @Override // java.util.function.BiFunction
    public final /* synthetic */ BiFunction andThen(Function function) {
        switch (this.f4580a) {
            case 0:
                return j$.com.android.tools.r8.a.c(this, function);
            case 1:
                return j$.com.android.tools.r8.a.c(this, function);
            default:
                return j$.com.android.tools.r8.a.c(this, function);
        }
    }

    @Override // java.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        switch (this.f4580a) {
            case 0:
                this.f4581b.accept(obj, obj2);
                return obj;
            case 1:
                this.f4581b.accept(obj, obj2);
                return obj;
            default:
                this.f4581b.accept(obj, obj2);
                return obj;
        }
    }
}
