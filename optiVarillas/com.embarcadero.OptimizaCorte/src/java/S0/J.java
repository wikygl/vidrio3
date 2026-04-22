package S0;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityRetales;
import e.C0397f;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class J implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2147j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ C0397f f2148k;

    public /* synthetic */ J(C0397f c0397f, int i4) {
        this.f2147j = i4;
        this.f2148k = c0397f;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        androidx.fragment.app.p pVar = this.f2148k;
        switch (this.f2147j) {
            case 0:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                androidx.fragment.app.p pVar2 = (ActivityListaCorte) pVar;
                pVar2.getClass();
                dialogInterface.dismiss();
                Toast.makeText((Context) pVar2, (CharSequence) pVar2.getString(2131820700), 1).show();
                return;
            default:
                ActivityRetales activityRetales = (ActivityRetales) pVar;
                activityRetales.f3103O.clear();
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.trimToSize();
                activityRetales.f3112X.f2884k = arrayList;
                ArrayList<Double> arrayList2 = activityRetales.f3108T;
                arrayList2.clear();
                activityRetales.f3112X.f2885l = arrayList2;
                return;
        }
    }
}
