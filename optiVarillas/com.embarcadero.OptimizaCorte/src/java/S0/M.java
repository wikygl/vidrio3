package S0;

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityListasGuardadas;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final /* synthetic */ class M implements DialogInterface.OnClickListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f2159j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ KeyEvent.Callback f2160k;

    public /* synthetic */ M(KeyEvent.Callback callback, int i4) {
        this.f2159j = i4;
        this.f2160k = callback;
    }

    /* JADX WARN: Type inference failed for: r4v2, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte] */
    @Override // android.content.DialogInterface.OnClickListener
    public final void onClick(DialogInterface dialogInterface, int i4) {
        KeyEvent.Callback callback = this.f2160k;
        switch (this.f2159j) {
            case 0:
                ?? r4 = (ActivityListaCorte) callback;
                r4.f3019Y.clear();
                r4.f3017W.clear();
                r4.f3025e0.f2883j = new ArrayList<>();
                r4.F();
                Snackbar h4 = Snackbar.h(r4.f3007M, r4.getString(2131820722));
                h4.i(r4.getString(2131820698), new H(r4, 1));
                h4.j();
                return;
            case 1:
                int i5 = ActivityListasGuardadas.f3047T;
                ((ActivityListasGuardadas) callback).onBackPressed();
                return;
            default:
                ((View) callback).callOnClick();
                return;
        }
    }
}
