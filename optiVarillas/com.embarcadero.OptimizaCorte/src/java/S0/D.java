package S0;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class D implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2116j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f2117k;

    public /* synthetic */ D(int i4, Object obj) {
        this.f2116j = i4;
        this.f2117k = obj;
    }

    /* JADX WARN: Type inference failed for: r0v4, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas, android.app.Activity] */
    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        Object obj = this.f2117k;
        switch (this.f2116j) {
            case 0:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                androidx.fragment.app.p pVar = (ActivityListaCorte) obj;
                pVar.getClass();
                Toast.makeText((Context) pVar, (CharSequence) "User not logged in Google Play!", 0).show();
                return;
            case 1:
                Toast.makeText((Context) ActivityListaCorte.this, (CharSequence) "User not logged in Google Play!", 0).show();
                return;
            default:
                int i5 = ActivityListasGuardadas.f3047T;
                ?? r02 = (ActivityListasGuardadas) obj;
                r02.B();
                r02.startActivity(r02.f3057Q);
                r02.overridePendingTransition(2130771998, 2130771999);
                r02.finish();
                return;
        }
    }
}
