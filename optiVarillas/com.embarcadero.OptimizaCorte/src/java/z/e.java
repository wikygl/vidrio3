package Z;

import android.view.ViewGroup;
import androidx.fragment.app.k;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class e extends d {

    /* renamed from: k  reason: collision with root package name */
    public final ViewGroup f2839k;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public e(k kVar, ViewGroup viewGroup) {
        super(kVar, "Attempting to add fragment " + kVar + " to container " + viewGroup + " which is not a FragmentContainerView");
        h.e(kVar, "fragment");
        this.f2839k = viewGroup;
    }
}
