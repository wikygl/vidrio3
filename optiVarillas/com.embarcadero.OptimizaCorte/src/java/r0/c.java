package R0;

import android.widget.LinearLayout;
import l3.g;
import u3.q;
import v3.i;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c extends i implements q<a, Float, Boolean, g> {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ a f2057k;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public c(a aVar) {
        super(3);
        this.f2057k = aVar;
    }

    @Override // u3.q
    public final void c(Object obj, Float f, Boolean bool) {
        a aVar = (a) obj;
        a aVar2 = this.f2057k;
        LinearLayout linearLayout = (LinearLayout) aVar2.findViewById(2131231017);
        if (linearLayout != null) {
            linearLayout.setVisibility(8);
        }
        LinearLayout linearLayout2 = (LinearLayout) aVar2.findViewById(2131231014);
        if (linearLayout2 != null) {
            linearLayout2.setVisibility(0);
        }
    }
}
