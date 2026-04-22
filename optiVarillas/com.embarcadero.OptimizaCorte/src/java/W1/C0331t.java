package W1;

import android.app.Activity;
import android.content.Intent;

/* renamed from: W1.t  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0331t extends AbstractDialogInterface$OnClickListenerC0333v {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ Intent f2770j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Activity f2771k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ int f2772l = 2;

    public C0331t(Intent intent, Activity activity) {
        this.f2770j = intent;
        this.f2771k = activity;
    }

    @Override // W1.AbstractDialogInterface$OnClickListenerC0333v
    public final void a() {
        Intent intent = this.f2770j;
        if (intent != null) {
            this.f2771k.startActivityForResult(intent, this.f2772l);
        }
    }
}
