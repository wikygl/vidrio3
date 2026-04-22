package w2;

import N.p;
import android.view.View;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class c implements p {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f6407a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ BottomSheetBehavior f6408b;

    public c(BottomSheetBehavior bottomSheetBehavior, int i4) {
        this.f6408b = bottomSheetBehavior;
        this.f6407a = i4;
    }

    @Override // N.p
    public final boolean a(View view) {
        this.f6408b.C(this.f6407a);
        return true;
    }
}
