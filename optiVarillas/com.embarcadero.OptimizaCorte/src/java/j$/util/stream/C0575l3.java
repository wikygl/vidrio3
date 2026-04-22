package j$.util.stream;

import java.util.function.Consumer;

/* renamed from: j$.util.stream.l3  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
final class C0575l3 extends AbstractC0580m3 implements Consumer {

    /* renamed from: b  reason: collision with root package name */
    final Object[] f4538b;

    /* JADX INFO: Access modifiers changed from: package-private */
    public C0575l3(int i4) {
        this.f4538b = new Object[i4];
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        int i4 = this.f4546a;
        this.f4546a = i4 + 1;
        this.f4538b[i4] = obj;
    }

    @Override // java.util.function.Consumer
    public final /* synthetic */ Consumer andThen(Consumer consumer) {
        return j$.com.android.tools.r8.a.d(this, consumer);
    }
}
