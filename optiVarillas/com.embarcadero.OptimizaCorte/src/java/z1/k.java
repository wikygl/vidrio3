package z1;

import android.view.MotionEvent;
import android.view.View;
import com.google.android.gms.internal.ads.s7;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class k implements View.OnTouchListener {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ o f6558j;

    public k(o oVar) {
        this.f6558j = oVar;
    }

    @Override // android.view.View.OnTouchListener
    public final boolean onTouch(View view, MotionEvent motionEvent) {
        s7 s7Var = this.f6558j.f6573q;
        if (s7Var != null) {
            s7Var.b.a(motionEvent);
            return false;
        }
        return false;
    }
}
