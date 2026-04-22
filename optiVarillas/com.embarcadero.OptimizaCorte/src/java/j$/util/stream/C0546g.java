package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.IntStream;
import j$.util.stream.Stream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;

/* renamed from: j$.util.stream.g */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final /* synthetic */ class C0546g implements BaseStream {

    /* renamed from: a */
    public final /* synthetic */ InterfaceC0551h f4509a;

    private /* synthetic */ C0546g(InterfaceC0551h interfaceC0551h) {
        this.f4509a = interfaceC0551h;
    }

    public static /* synthetic */ BaseStream w(InterfaceC0551h interfaceC0551h) {
        if (interfaceC0551h == null) {
            return null;
        }
        return interfaceC0551h instanceof C0541f ? ((C0541f) interfaceC0551h).f4503a : interfaceC0551h instanceof F ? E.w((F) interfaceC0551h) : interfaceC0551h instanceof IntStream ? IntStream.Wrapper.convert((IntStream) interfaceC0551h) : interfaceC0551h instanceof InterfaceC0587o0 ? C0582n0.w((InterfaceC0587o0) interfaceC0551h) : interfaceC0551h instanceof Stream ? Stream.Wrapper.convert((Stream) interfaceC0551h) : new C0546g(interfaceC0551h);
    }

    @Override // java.util.stream.BaseStream, java.lang.AutoCloseable
    public final /* synthetic */ void close() {
        this.f4509a.close();
    }

    public final /* synthetic */ boolean equals(Object obj) {
        InterfaceC0551h interfaceC0551h = this.f4509a;
        if (obj instanceof C0546g) {
            obj = ((C0546g) obj).f4509a;
        }
        return interfaceC0551h.equals(obj);
    }

    public final /* synthetic */ int hashCode() {
        return this.f4509a.hashCode();
    }

    @Override // java.util.stream.BaseStream
    public final /* synthetic */ boolean isParallel() {
        return this.f4509a.isParallel();
    }

    @Override // java.util.stream.BaseStream
    public final /* synthetic */ Iterator iterator() {
        return this.f4509a.iterator();
    }

    @Override // java.util.stream.BaseStream
    public final /* synthetic */ BaseStream onClose(Runnable runnable) {
        return w(this.f4509a.onClose(runnable));
    }

    @Override // java.util.stream.BaseStream
    public final /* synthetic */ BaseStream parallel() {
        return w(this.f4509a.parallel());
    }

    @Override // java.util.stream.BaseStream
    public final /* synthetic */ BaseStream sequential() {
        return w(this.f4509a.sequential());
    }

    @Override // java.util.stream.BaseStream
    public final /* synthetic */ Spliterator spliterator() {
        return Spliterator.Wrapper.convert(this.f4509a.spliterator());
    }

    @Override // java.util.stream.BaseStream
    public final /* synthetic */ BaseStream unordered() {
        return w(this.f4509a.unordered());
    }
}
