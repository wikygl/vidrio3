package j$.util;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.util.t  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0640t implements IntConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Consumer f4643a;

    public /* synthetic */ C0640t(Consumer consumer) {
        this.f4643a = consumer;
    }

    @Override // java.util.function.IntConsumer
    public final void accept(int i4) {
        this.f4643a.accept(Integer.valueOf(i4));
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return j$.com.android.tools.r8.a.f(this, intConsumer);
    }
}
