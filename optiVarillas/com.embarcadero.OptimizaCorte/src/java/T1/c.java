package T1;

import W1.C0324l;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class c extends DialogFragment {

    /* renamed from: j  reason: collision with root package name */
    public Dialog f2345j;

    /* renamed from: k  reason: collision with root package name */
    public DialogInterface.OnCancelListener f2346k;

    /* renamed from: l  reason: collision with root package name */
    public AlertDialog f2347l;

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public final void onCancel(DialogInterface dialogInterface) {
        DialogInterface.OnCancelListener onCancelListener = this.f2346k;
        if (onCancelListener != null) {
            onCancelListener.onCancel(dialogInterface);
        }
    }

    @Override // android.app.DialogFragment
    public final Dialog onCreateDialog(Bundle bundle) {
        Dialog dialog = this.f2345j;
        if (dialog == null) {
            setShowsDialog(false);
            if (this.f2347l == null) {
                Activity activity = getActivity();
                C0324l.d(activity);
                this.f2347l = new AlertDialog.Builder(activity).create();
            }
            return this.f2347l;
        }
        return dialog;
    }
}
