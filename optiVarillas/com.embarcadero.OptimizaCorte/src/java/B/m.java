package B;

import M.C0232n;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import androidx.lifecycle.u;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class m extends Activity implements androidx.lifecycle.k, C0232n.a {

    /* renamed from: j  reason: collision with root package name */
    public final androidx.lifecycle.l f239j;

    public m() {
        new r.j();
        this.f239j = new androidx.lifecycle.l(this);
    }

    @Override // M.C0232n.a
    public final boolean d(KeyEvent keyEvent) {
        v3.h.e(keyEvent, "event");
        return super.dispatchKeyEvent(keyEvent);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        v3.h.e(keyEvent, "event");
        View decorView = getWindow().getDecorView();
        v3.h.d(decorView, "window.decorView");
        if (C0232n.a(decorView, keyEvent)) {
            return true;
        }
        return C0232n.b(this, decorView, this, keyEvent);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public final boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
        v3.h.e(keyEvent, "event");
        View decorView = getWindow().getDecorView();
        v3.h.d(decorView, "window.decorView");
        if (C0232n.a(decorView, keyEvent)) {
            return true;
        }
        return super.dispatchKeyShortcutEvent(keyEvent);
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int i4 = androidx.lifecycle.u.k;
        u.b.b(this);
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        v3.h.e(bundle, "outState");
        this.f239j.g();
        super.onSaveInstanceState(bundle);
    }

    public androidx.lifecycle.l r() {
        return this.f239j;
    }
}
