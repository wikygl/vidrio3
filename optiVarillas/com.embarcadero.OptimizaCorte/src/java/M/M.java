package M;

import M.O;
import android.text.TextUtils;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class M extends O.b<CharSequence> {
    @Override // M.O.b
    public final CharSequence a(View view) {
        return O.j.b(view);
    }

    @Override // M.O.b
    public final void b(View view, CharSequence charSequence) {
        O.j.f(view, charSequence);
    }

    @Override // M.O.b
    public final boolean e(CharSequence charSequence, CharSequence charSequence2) {
        return !TextUtils.equals(charSequence, charSequence2);
    }
}
