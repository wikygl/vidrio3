package j$.util;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.util.x  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0644x implements LongConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Consumer f4646a;

    public /* synthetic */ C0644x(Consumer consumer) {
        this.f4646a = consumer;
    }

    @Override // java.util.function.LongConsumer
    public final void accept(long j4) {
        this.f4646a.accept(Long.valueOf(j4));
    }

    @Override // java.util.function.LongConsumer
    public final /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return j$.com.android.tools.r8.a.g(this, longConsumer);
    }
}
