package j$.util.function;

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class a implements BinaryOperator, Predicate {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4208a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f4209b;

    public /* synthetic */ a(Object obj, int i4) {
        this.f4208a = i4;
        this.f4209b = obj;
    }

    @Override // java.util.function.Predicate
    public /* synthetic */ Predicate and(Predicate predicate) {
        return j$.com.android.tools.r8.a.a(this, predicate);
    }

    @Override // java.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        switch (this.f4208a) {
            case 0:
                return j$.com.android.tools.r8.a.c(this, function);
            default:
                return j$.com.android.tools.r8.a.c(this, function);
        }
    }

    @Override // java.util.function.BiFunction
    public Object apply(Object obj, Object obj2) {
        switch (this.f4208a) {
            case 0:
                return ((Comparator) this.f4209b).compare(obj, obj2) >= 0 ? obj : obj2;
            default:
                return ((Comparator) this.f4209b).compare(obj, obj2) <= 0 ? obj : obj2;
        }
    }

    @Override // java.util.function.Predicate
    public Predicate negate() {
        return new a(this, 2);
    }

    @Override // java.util.function.Predicate
    public /* synthetic */ Predicate or(Predicate predicate) {
        return j$.com.android.tools.r8.a.h(this, predicate);
    }

    @Override // java.util.function.Predicate
    public boolean test(Object obj) {
        return !((Predicate) this.f4209b).test(obj);
    }
}
