package j$.util.stream;

import j$.util.C0503f;
import j$.util.Spliterator;
import j$.util.function.Function$CC;
import j$.util.stream.IntStream;
import j$.util.stream.Stream;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: j$.util.stream.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0516a implements Supplier, Consumer, BooleanSupplier, DoubleFunction, Function, LongFunction {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4445a;

    /* renamed from: b  reason: collision with root package name */
    public Object f4446b;

    public /* synthetic */ C0516a(int i4) {
        this.f4445a = i4;
    }

    public /* synthetic */ C0516a(Object obj, int i4) {
        this.f4445a = i4;
        this.f4446b = obj;
    }

    @Override // java.util.function.Consumer
    public void accept(Object obj) {
        switch (this.f4445a) {
            case 1:
                ((InterfaceC0599q2) this.f4446b).accept((InterfaceC0599q2) obj);
                return;
            default:
                ((List) this.f4446b).add(obj);
                return;
        }
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.f4445a) {
            case 1:
                return j$.com.android.tools.r8.a.d(this, consumer);
            default:
                return j$.com.android.tools.r8.a.d(this, consumer);
        }
    }

    @Override // java.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return Function$CC.$default$andThen(this, function);
    }

    @Override // java.util.function.DoubleFunction
    public Object apply(double d4) {
        Object apply = ((DoubleFunction) this.f4446b).apply(d4);
        if (apply == null) {
            return null;
        }
        if (apply instanceof F) {
            return E.w((F) apply);
        }
        if (apply instanceof DoubleStream) {
            return D.w((DoubleStream) apply);
        }
        C0503f.a("java.util.stream.DoubleStream", apply.getClass());
        throw null;
    }

    @Override // java.util.function.LongFunction
    public Object apply(long j4) {
        Object apply = ((LongFunction) this.f4446b).apply(j4);
        if (apply == null) {
            return null;
        }
        if (apply instanceof InterfaceC0587o0) {
            return C0582n0.w((InterfaceC0587o0) apply);
        }
        if (apply instanceof LongStream) {
            return C0577m0.w((LongStream) apply);
        }
        C0503f.a("java.util.stream.LongStream", apply.getClass());
        throw null;
    }

    @Override // java.util.function.Function
    public Object apply(Object obj) {
        Object apply = ((Function) this.f4446b).apply(obj);
        if (apply == null) {
            return null;
        }
        if (apply instanceof Stream) {
            return Stream.Wrapper.convert((Stream) apply);
        }
        if (apply instanceof java.util.stream.Stream) {
            return C0525b3.w((java.util.stream.Stream) apply);
        }
        if (apply instanceof IntStream) {
            return IntStream.Wrapper.convert((IntStream) apply);
        }
        if (apply instanceof java.util.stream.IntStream) {
            return IntStream.VivifiedWrapper.convert((java.util.stream.IntStream) apply);
        }
        if (apply instanceof F) {
            return E.w((F) apply);
        }
        if (apply instanceof DoubleStream) {
            return D.w((DoubleStream) apply);
        }
        if (apply instanceof InterfaceC0587o0) {
            return C0582n0.w((InterfaceC0587o0) apply);
        }
        if (apply instanceof LongStream) {
            return C0577m0.w((LongStream) apply);
        }
        C0503f.a("java.util.stream.*Stream", apply.getClass());
        throw null;
    }

    @Override // java.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return Function$CC.$default$compose(this, function);
    }

    @Override // java.util.function.Supplier
    public Object get() {
        switch (this.f4445a) {
            case 0:
                return ((AbstractC0521b) this.f4446b).I();
            default:
                return (Spliterator) this.f4446b;
        }
    }

    @Override // java.util.function.BooleanSupplier
    public boolean getAsBoolean() {
        switch (this.f4445a) {
            case 2:
                C0595p3 c0595p3 = (C0595p3) this.f4446b;
                return c0595p3.f4513d.tryAdvance(c0595p3.f4514e);
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                C0604r3 c0604r3 = (C0604r3) this.f4446b;
                return c0604r3.f4513d.tryAdvance(c0604r3.f4514e);
            case 4:
                C0614t3 c0614t3 = (C0614t3) this.f4446b;
                return c0614t3.f4513d.tryAdvance(c0614t3.f4514e);
            default:
                I3 i32 = (I3) this.f4446b;
                return i32.f4513d.tryAdvance(i32.f4514e);
        }
    }
}
