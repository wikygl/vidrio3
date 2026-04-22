package S0;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import e.C0397f;

/* renamed from: S0.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class DialogInterface$OnClickListenerC0256g implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2244j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0397f f2245k;

    public /* synthetic */ DialogInterface$OnClickListenerC0256g(C0397f c0397f, int i4) {
        this.f2244j = i4;
        this.f2245k = c0397f;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio] */
    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        androidx.fragment.app.p pVar = this.f2245k;
        switch (this.f2244j) {
            case 0:
                ?? r02 = (ActivityInicio) pVar;
                r02.f2961U.clear();
                r02.f2961U.trimToSize();
                r02.f2972f0.clear();
                r02.f2972f0.trimToSize();
                r02.f2960T.notifyDataSetChanged();
                Toast.makeText((Context) r02, r02.getString(2131820722), 0).show();
                r02.G();
                return;
            default:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                androidx.fragment.app.p pVar2 = (ActivityListaCorte) pVar;
                pVar2.getClass();
                dialogInterface.cancel();
                Toast.makeText((Context) pVar2, (CharSequence) pVar2.getString(2131820700), 0).show();
                return;
        }
    }
}
