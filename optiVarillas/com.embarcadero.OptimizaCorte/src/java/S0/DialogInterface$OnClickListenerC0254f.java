package S0;

import android.content.DialogInterface;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;

/* renamed from: S0.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class DialogInterface$OnClickListenerC0254f implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2238j;

    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        switch (this.f2238j) {
            case 0:
                int i5 = ActivityInicio.f2947A0;
                dialogInterface.cancel();
                return;
            default:
                dialogInterface.dismiss();
                return;
        }
    }
}
