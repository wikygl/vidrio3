package j$.util.stream;

import java.util.function.LongConsumer;

/* renamed from: j$.util.stream.d0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0532d0 implements LongConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ InterfaceC0599q2 f4469a;

    @Override // java.util.function.LongConsumer
    public final void accept(long j4) {
        this.f4469a.accept(j4);
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }
}
