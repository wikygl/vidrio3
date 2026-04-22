package j$.util.stream;

/* renamed from: j$.util.stream.a2  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0519a2 extends AbstractC0524b2 {
    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f4459b++;
    }

    @Override // j$.util.stream.V1
    public final void g(V1 v12) {
        this.f4459b += ((AbstractC0524b2) v12).f4459b;
    }

    @Override // j$.util.stream.W1, java.util.function.Supplier
    public final Object get() {
        return Long.valueOf(this.f4459b);
    }
}
