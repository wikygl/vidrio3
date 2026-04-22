package D1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class r implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Context f763j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ String f764k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ boolean f765l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ boolean f766m;

    public r(Context context, String str, boolean z4, boolean z5) {
        this.f763j = context;
        this.f764k = str;
        this.f765l = z4;
        this.f766m = z5;
    }

    @Override // java.lang.Runnable
    public final void run() {
        t0 t0Var = z1.p.f6575A.f6578c;
        AlertDialog.Builder i4 = t0.i(this.f763j);
        i4.setMessage(this.f764k);
        if (this.f765l) {
            i4.setTitle("Error");
        } else {
            i4.setTitle("Info");
        }
        if (this.f766m) {
            i4.setNeutralButton("Dismiss", (DialogInterface.OnClickListener) null);
        } else {
            i4.setPositiveButton("Learn More", new DialogInterface$OnClickListenerC0197q(this));
            i4.setNegativeButton("Dismiss", (DialogInterface.OnClickListener) null);
        }
        i4.create().show();
    }
}
