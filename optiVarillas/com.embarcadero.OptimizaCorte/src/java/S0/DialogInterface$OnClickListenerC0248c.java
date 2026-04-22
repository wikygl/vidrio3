package S0;

import android.content.DialogInterface;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;

/* renamed from: S0.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class DialogInterface$OnClickListenerC0248c implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2224j;

    public /* synthetic */ DialogInterface$OnClickListenerC0248c(int i4) {
        this.f2224j = i4;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        switch (this.f2224j) {
            case 0:
                int i5 = ActivityInicio.f2947A0;
                dialogInterface.cancel();
                return;
            case 1:
                dialogInterface.dismiss();
                return;
            default:
                int i6 = ActivityRetales.f3095b0;
                dialogInterface.cancel();
                return;
        }
    }
}
