package l;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class e0 {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
        public static void a(View view, CharSequence charSequence) {
            view.setTooltipText(charSequence);
        }
    }

    public static void a(View view, CharSequence charSequence) {
        if (Build.VERSION.SDK_INT >= 26) {
            a.a(view, charSequence);
            return;
        }
        f0 f0Var = f0.f5125t;
        if (f0Var != null && f0Var.f5127j == view) {
            f0.b(null);
        }
        if (TextUtils.isEmpty(charSequence)) {
            f0 f0Var2 = f0.f5126u;
            if (f0Var2 != null && f0Var2.f5127j == view) {
                f0Var2.a();
            }
            view.setOnLongClickListener(null);
            view.setLongClickable(false);
            view.setOnHoverListener(null);
            return;
        }
        new f0(view, charSequence);
    }
}
