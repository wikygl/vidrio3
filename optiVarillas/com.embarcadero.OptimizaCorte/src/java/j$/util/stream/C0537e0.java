package j$.util.stream;

import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.LongBinaryOperator;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: j$.util.stream.e0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/6.dex */
public final /* synthetic */ class C0537e0 implements LongBinaryOperator, Consumer, IntFunction {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f4481a;

    public /* synthetic */ C0537e0(int i4) {
        this.f4481a = i4;
    }

    private final void accept$j$$util$stream$Node$$ExternalSyntheticLambda0(Object obj) {
    }

    private final void accept$j$$util$stream$StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0(Object obj) {
    }

    private final void accept$j$$util$stream$StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda1(Object obj) {
    }

    @Override // java.util.function.Consumer
    public void accept(Object obj) {
        int i4 = this.f4481a;
    }

    @Override // java.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.f4481a) {
            case 2:
                return j$.com.android.tools.r8.a.d(this, consumer);
            case 8:
                return j$.com.android.tools.r8.a.d(this, consumer);
            default:
                return j$.com.android.tools.r8.a.d(this, consumer);
        }
    }

    @Override // java.util.function.IntFunction
    public Object apply(int i4) {
        switch (this.f4481a) {
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                return new Object[i4];
            case 4:
                return new Object[i4];
            case 5:
                return new Integer[i4];
            case 6:
                return new Long[i4];
            default:
                return new Double[i4];
        }
    }

    @Override // java.util.function.LongBinaryOperator
    public long applyAsLong(long j4, long j5) {
        switch (this.f4481a) {
            case 0:
                return Math.max(j4, j5);
            default:
                return j4 + j5;
        }
    }
}
