package T2;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.google.android.material.tabs.TabLayout;
import r2.C0783a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class a extends com.google.android.material.tabs.a {
    public final void b(TabLayout tabLayout, View view, View view2, float f, Drawable drawable) {
        float sin;
        float cos;
        RectF a4 = com.google.android.material.tabs.a.a(tabLayout, view);
        RectF a5 = com.google.android.material.tabs.a.a(tabLayout, view2);
        if (a4.left < a5.left) {
            double d4 = (f * 3.141592653589793d) / 2.0d;
            sin = (float) (1.0d - Math.cos(d4));
            cos = (float) Math.sin(d4);
        } else {
            double d5 = (f * 3.141592653589793d) / 2.0d;
            sin = (float) Math.sin(d5);
            cos = (float) (1.0d - Math.cos(d5));
        }
        drawable.setBounds(C0783a.c(sin, (int) a4.left, (int) a5.left), drawable.getBounds().top, C0783a.c(cos, (int) a4.right, (int) a5.right), drawable.getBounds().bottom);
    }
}
