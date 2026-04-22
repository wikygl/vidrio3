package T1;

import W1.C0324l;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class k extends androidx.fragment.app.j {

    /* renamed from: s0  reason: collision with root package name */
    public Dialog f2359s0;

    /* renamed from: t0  reason: collision with root package name */
    public DialogInterface.OnCancelListener f2360t0;

    /* renamed from: u0  reason: collision with root package name */
    public AlertDialog f2361u0;

    public final Dialog O() {
        Dialog dialog = this.f2359s0;
        if (dialog == null) {
            ((androidx.fragment.app.j) this).j0 = false;
            if (this.f2361u0 == null) {
                Context h4 = h();
                C0324l.d(h4);
                this.f2361u0 = new AlertDialog.Builder(h4).create();
            }
            return this.f2361u0;
        }
        return dialog;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        DialogInterface.OnCancelListener onCancelListener = this.f2360t0;
        if (onCancelListener != null) {
            onCancelListener.onCancel(dialogInterface);
        }
    }
}
