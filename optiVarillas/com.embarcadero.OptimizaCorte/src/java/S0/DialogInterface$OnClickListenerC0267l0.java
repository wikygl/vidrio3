package S0;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;

/* renamed from: S0.l0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class DialogInterface$OnClickListenerC0267l0 implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2258j;

    public /* synthetic */ DialogInterface$OnClickListenerC0267l0(int i4) {
        this.f2258j = i4;
    }

    private final void a(DialogInterface dialogInterface, int i4) {
    }

    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        switch (this.f2258j) {
            case 0:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                dialogInterface.dismiss();
                return;
            case 1:
                return;
            default:
                int i5 = ActivityOptimizacion.f3061g0;
                dialogInterface.cancel();
                return;
        }
    }
}
