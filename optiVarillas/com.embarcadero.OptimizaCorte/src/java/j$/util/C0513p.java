package j$.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.util.p  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0513p implements DoubleConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Consumer f4241a;

    public /* synthetic */ C0513p(Consumer consumer) {
        this.f4241a = consumer;
    }

    @Override // java.util.function.DoubleConsumer
    public final void accept(double d4) {
        this.f4241a.accept(Double.valueOf(d4));
    }

    @Override // java.util.function.DoubleConsumer
    public final /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return j$.com.android.tools.r8.a.e(this, doubleConsumer);
    }
}
