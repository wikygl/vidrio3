package j$.util.stream;

import j$.util.C0503f;
import j$.util.stream.IntStream;
import java.util.function.IntFunction;
import java.util.function.LongFunction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class R0 implements LongFunction, IntFunction {

    /* renamed from: a  reason: collision with root package name */
    public IntFunction f4371a;

    @Override // java.util.function.IntFunction
    public Object apply(int i4) {
        Object apply = this.f4371a.apply(i4);
        if (apply == null) {
            return null;
        }
        if (apply instanceof IntStream) {
            return IntStream.Wrapper.convert((IntStream) apply);
        }
        if (apply instanceof java.util.stream.IntStream) {
            return IntStream.VivifiedWrapper.convert((java.util.stream.IntStream) apply);
        }
        C0503f.a("java.util.stream.IntStream", apply.getClass());
        throw null;
    }

    @Override // java.util.function.LongFunction
    public Object apply(long j4) {
        return AbstractC0637z0.D(j4, this.f4371a);
    }
}
