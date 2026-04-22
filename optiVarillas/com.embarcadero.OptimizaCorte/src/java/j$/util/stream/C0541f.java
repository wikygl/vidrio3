package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.IntStream;
import java.util.Iterator;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

/* renamed from: j$.util.stream.f */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0541f implements InterfaceC0551h {

    /* renamed from: a */
    public final /* synthetic */ BaseStream f4503a;

    private /* synthetic */ C0541f(BaseStream baseStream) {
        this.f4503a = baseStream;
    }

    public static /* synthetic */ InterfaceC0551h w(BaseStream baseStream) {
        if (baseStream == null) {
            return null;
        }
        return baseStream instanceof C0546g ? ((C0546g) baseStream).f4509a : baseStream instanceof DoubleStream ? D.w((DoubleStream) baseStream) : baseStream instanceof java.util.stream.IntStream ? IntStream.VivifiedWrapper.convert((java.util.stream.IntStream) baseStream) : baseStream instanceof LongStream ? C0577m0.w((LongStream) baseStream) : baseStream instanceof java.util.stream.Stream ? C0525b3.w((java.util.stream.Stream) baseStream) : new C0541f(baseStream);
    }

    @Override // java.lang.AutoCloseable
    public final /* synthetic */ void close() {
        this.f4503a.close();
    }

    public final /* synthetic */ boolean equals(Object obj) {
        BaseStream baseStream = this.f4503a;
        if (obj instanceof C0541f) {
            obj = ((C0541f) obj).f4503a;
        }
        return baseStream.equals(obj);
    }

    public final /* synthetic */ int hashCode() {
        return this.f4503a.hashCode();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ boolean isParallel() {
        return this.f4503a.isParallel();
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ Iterator iterator() {
        return this.f4503a.iterator();
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ InterfaceC0551h onClose(Runnable runnable) {
        return w(this.f4503a.onClose(runnable));
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0551h parallel() {
        return w(this.f4503a.parallel());
    }

    @Override // j$.util.stream.InterfaceC0551h, j$.util.stream.F
    public final /* synthetic */ InterfaceC0551h sequential() {
        return w(this.f4503a.sequential());
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ Spliterator spliterator() {
        return j$.util.Q.a(this.f4503a.spliterator());
    }

    @Override // j$.util.stream.InterfaceC0551h
    public final /* synthetic */ InterfaceC0551h unordered() {
        return w(this.f4503a.unordered());
    }
}
