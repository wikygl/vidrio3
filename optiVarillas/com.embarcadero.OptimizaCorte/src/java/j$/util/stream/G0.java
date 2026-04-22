package j$.util.stream;

import java.util.function.IntConsumer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class G0 implements IntConsumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4284a;

    public /* synthetic */ G0(int i4) {
        this.f4284a = i4;
    }

    private final void accept$j$$util$stream$Node$OfInt$$ExternalSyntheticLambda0(int i4) {
    }

    private final void accept$j$$util$stream$StreamSpliterators$SliceSpliterator$OfInt$$ExternalSyntheticLambda0(int i4) {
    }

    @Override // java.util.function.IntConsumer
    public final void accept(int i4) {
        int i5 = this.f4284a;
    }

    @Override // java.util.function.IntConsumer
    public final /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        switch (this.f4284a) {
            case 0:
                return j$.com.android.tools.r8.a.f(this, intConsumer);
            default:
                return j$.com.android.tools.r8.a.f(this, intConsumer);
        }
    }
}
