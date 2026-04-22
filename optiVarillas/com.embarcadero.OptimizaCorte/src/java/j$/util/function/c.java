package j$.util.function;

import java.util.function.Function;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class c implements Function {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4212a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Function f4213b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ Function f4214c;

    public /* synthetic */ c(Function function, Function function2, int i4) {
        this.f4212a = i4;
        this.f4213b = function;
        this.f4214c = function2;
    }

    @Override // java.util.function.Function
    public final /* synthetic */ Function andThen(Function function) {
        switch (this.f4212a) {
            case 0:
                return Function$CC.$default$andThen(this, function);
            default:
                return Function$CC.$default$andThen(this, function);
        }
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        switch (this.f4212a) {
            case 0:
                return this.f4214c.apply(this.f4213b.apply(obj));
            default:
                return this.f4213b.apply(this.f4214c.apply(obj));
        }
    }

    @Override // java.util.function.Function
    public final /* synthetic */ Function compose(Function function) {
        switch (this.f4212a) {
            case 0:
                return Function$CC.$default$compose(this, function);
            default:
                return Function$CC.$default$compose(this, function);
        }
    }
}
