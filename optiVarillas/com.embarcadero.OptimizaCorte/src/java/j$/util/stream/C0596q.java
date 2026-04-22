package j$.util.stream;

import java.util.function.DoubleConsumer;

/* renamed from: j$.util.stream.q  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0596q implements DoubleConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ InterfaceC0599q2 f4566a;

    @Override // java.util.function.DoubleConsumer
    public final void accept(double d4) {
        this.f4566a.accept(d4);
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }
}
