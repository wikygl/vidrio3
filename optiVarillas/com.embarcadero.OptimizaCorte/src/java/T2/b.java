package T2;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.google.android.material.tabs.TabLayout;
import r2.C0783a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class b extends com.google.android.material.tabs.a {
    public final void b(TabLayout tabLayout, View view, View view2, float f, Drawable drawable) {
        float b4;
        int i4 = (f > 0.5f ? 1 : (f == 0.5f ? 0 : -1));
        if (i4 >= 0) {
            view = view2;
        }
        RectF a4 = com.google.android.material.tabs.a.a(tabLayout, view);
        if (i4 < 0) {
            b4 = C0783a.b(1.0f, 0.0f, 0.0f, 0.5f, f);
        } else {
            b4 = C0783a.b(0.0f, 1.0f, 0.5f, 1.0f, f);
        }
        drawable.setBounds((int) a4.left, drawable.getBounds().top, (int) a4.right, drawable.getBounds().bottom);
        drawable.setAlpha((int) (b4 * 255.0f));
    }
}
