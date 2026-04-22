package S0;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorteContainer;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class P implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2183j;

    public /* synthetic */ P(int i4) {
        this.f2183j = i4;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        switch (this.f2183j) {
            case 0:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                dialogInterface.dismiss();
                return;
            case 1:
                ProgressDialog progressDialog2 = ActivityListaCorte.l0;
                dialogInterface.cancel();
                return;
            default:
                int i5 = ActivityListaCorteContainer.f3041L;
                dialogInterface.cancel();
                return;
        }
    }
}
