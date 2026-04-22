package j$.util.stream;

import java.util.function.IntConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class V implements IntConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ InterfaceC0599q2 f4426a;

    @Override // java.util.function.IntConsumer
    public final void accept(int i4) {
        this.f4426a.accept(i4);
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }
}
