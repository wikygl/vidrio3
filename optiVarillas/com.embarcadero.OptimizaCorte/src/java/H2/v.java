package H2;

import android.annotation.SuppressLint;
import android.widget.ImageButton;

@SuppressLint({"AppCompatCustomView"})
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class v extends ImageButton {

    /* renamed from: j  reason: collision with root package name */
    public int f1127j;

    public final void b(int i4, boolean z4) {
        super.setVisibility(i4);
        if (z4) {
            this.f1127j = i4;
        }
    }

    public final int getUserSetVisibility() {
        return this.f1127j;
    }

    @Override // android.widget.ImageView, android.view.View
    public void setVisibility(int i4) {
        b(i4, true);
    }
}
