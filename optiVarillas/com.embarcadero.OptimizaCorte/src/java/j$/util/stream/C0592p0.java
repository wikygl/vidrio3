package j$.util.stream;

import java.util.function.Supplier;

/* renamed from: j$.util.stream.p0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0592p0 implements Supplier {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4559a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ EnumC0625w0 f4560b;

    public /* synthetic */ C0592p0(EnumC0625w0 enumC0625w0, int i4) {
        this.f4559a = i4;
        this.f4560b = enumC0625w0;
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        switch (this.f4559a) {
            case 0:
                return new AbstractC0621v0(this.f4560b);
            case 1:
                return new AbstractC0621v0(this.f4560b);
            default:
                return new AbstractC0621v0(this.f4560b);
        }
    }
}
