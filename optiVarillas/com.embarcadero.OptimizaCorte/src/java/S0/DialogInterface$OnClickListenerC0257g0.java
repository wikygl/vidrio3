package S0;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas;
import e.C0397f;

/* renamed from: S0.g0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class DialogInterface$OnClickListenerC0257g0 implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2246j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0397f f2247k;

    public /* synthetic */ DialogInterface$OnClickListenerC0257g0(C0397f c0397f, int i4) {
        this.f2246j = i4;
        this.f2247k = c0397f;
    }

    /* JADX WARN: Type inference failed for: r4v2, types: [com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte, java.lang.Object, android.content.ContextWrapper] */
    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        androidx.fragment.app.p pVar = this.f2247k;
        switch (this.f2246j) {
            case 0:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                ?? r4 = (ActivityListaCorte) pVar;
                r4.getClass();
                try {
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("text/plain");
                    r4.f3023c0.F(intent);
                    return;
                } catch (ActivityNotFoundException unused) {
                    Toast.makeText(r4.getBaseContext(), "ERROR: No File Manager Found In Your Device", 1).show();
                    return;
                }
            default:
                int i5 = ActivityListasGuardadas.f3047T;
                androidx.fragment.app.p pVar2 = (ActivityListasGuardadas) pVar;
                Toast.makeText((Context) pVar2, (CharSequence) pVar2.getString(2131820700), 0).show();
                dialogInterface.cancel();
                return;
        }
    }
}
