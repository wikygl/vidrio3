package j$.util.stream;

import java.util.function.Consumer;

/* renamed from: j$.util.stream.v0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
abstract class AbstractC0621v0 implements InterfaceC0599q2 {

    /* renamed from: a  reason: collision with root package name */
    boolean f4598a;

    /* renamed from: b  reason: collision with root package name */
    boolean f4599b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractC0621v0(EnumC0625w0 enumC0625w0) {
        boolean z4;
        z4 = enumC0625w0.f4607b;
        this.f4599b = !z4;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public /* synthetic */ void accept(double d4) {
        AbstractC0637z0.a();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public /* synthetic */ void accept(int i4) {
        AbstractC0637z0.k();
        throw null;
    }

    @Override // j$.util.stream.InterfaceC0599q2, j$.util.stream.InterfaceC0594p2, java.util.function.LongConsumer
    public /* synthetic */ void accept(long j4) {
        AbstractC0637z0.l();
        throw null;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void k() {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final /* synthetic */ void l(long j4) {
    }

    @Override // j$.util.stream.InterfaceC0599q2
    public final boolean n() {
        return this.f4598a;
    }
}
