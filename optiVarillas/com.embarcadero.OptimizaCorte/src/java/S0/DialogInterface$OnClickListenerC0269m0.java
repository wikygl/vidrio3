package S0;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import c1.C0370c;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;

/* renamed from: S0.m0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class DialogInterface$OnClickListenerC0269m0 implements DialogInterface.OnClickListener {
    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        ProgressDialog progressDialog = ActivityListaCorte.l0;
        C0370c.f2938a = true;
        ActivityListaCorte.l0.dismiss();
    }
}
