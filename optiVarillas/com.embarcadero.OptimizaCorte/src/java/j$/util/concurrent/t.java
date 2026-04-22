package j$.util.concurrent;

import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class t implements BiConsumer, BiFunction, Consumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4187a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f4188b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ Object f4189c;

    public /* synthetic */ t(int i4, Object obj, Object obj2) {
        this.f4187a = i4;
        this.f4188b = obj;
        this.f4189c = obj2;
    }

    public /* synthetic */ t(BiFunction biFunction, Function function) {
        this.f4187a = 2;
        this.f4189c = biFunction;
        this.f4188b = function;
    }

    @Override // java.util.function.Consumer
    public void accept(Object obj) {
        ((Consumer) this.f4188b).accept(obj);
        ((Consumer) this.f4189c).accept(obj);
    }

    @Override // java.util.function.BiConsumer
    public void accept(Object obj, Object obj2) {
        switch (this.f4187a) {
            case 0:
                break;
            default:
                ((BiConsumer) this.f4188b).accept(obj, obj2);
                ((BiConsumer) this.f4189c).accept(obj, obj2);
                return;
        }
        do {
            Object apply = ((BiFunction) this.f4189c).apply(obj, obj2);
            ConcurrentMap concurrentMap = (ConcurrentMap) this.f4188b;
            if (concurrentMap.replace(obj, obj2, apply)) {
                return;
            }
            obj2 = concurrentMap.get(obj);
        } while (obj2 != null);
    }

    @Override // java.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        switch (this.f4187a) {
            case 0:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
            default:
                return j$.com.android.tools.r8.a.b(this, biConsumer);
        }
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return j$.com.android.tools.r8.a.c(this, function);
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // java.util.function.BiFunction
    public Object apply(Object obj, Object obj2) {
        return ((Function) this.f4188b).apply(((BiFunction) this.f4189c).apply(obj, obj2));
    }
}
