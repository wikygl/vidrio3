package S0;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;

/* renamed from: S0.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class DialogInterface$OnClickListenerC0258h implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2248j;

    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        switch (this.f2248j) {
            case 0:
                int i5 = ActivityInicio.f2947A0;
                dialogInterface.cancel();
                return;
            default:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                dialogInterface.dismiss();
                return;
        }
    }
}
