package D1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.media.AudioManager;
import android.text.TextUtils;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.pk;
import java.util.List;
import java.util.Locale;

@TargetApi(28)
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class D0 extends A0 {
    @Override // D1.C0178b
    public final int f(AudioManager audioManager) {
        int streamMinVolume;
        streamMinVolume = audioManager.getStreamMinVolume(3);
        return streamMinVolume;
    }

    @Override // D1.C0178b
    public final void g(final Activity activity) {
        boolean isInMultiWindowMode;
        int i4;
        if (((Boolean) A1.r.f168d.f171c.a(Gb.V0)).booleanValue() && z1.p.f6575A.f6581g.c().v() == null) {
            isInMultiWindowMode = activity.isInMultiWindowMode();
            if (!isInMultiWindowMode) {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                i4 = attributes.layoutInDisplayCutoutMode;
                if (1 != i4) {
                    attributes.layoutInDisplayCutoutMode = 1;
                    window.setAttributes(attributes);
                }
                activity.getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: D1.B0
                    @Override // android.view.View.OnApplyWindowInsetsListener
                    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        int i5;
                        DisplayCutout displayCutout;
                        List<Rect> boundingRects;
                        z1.p pVar = z1.p.f6575A;
                        if (pVar.f6581g.c().v() == null) {
                            displayCutout = windowInsets.getDisplayCutout();
                            pk pkVar = pVar.f6581g;
                            String str = "";
                            if (displayCutout != null) {
                                j0 c4 = pkVar.c();
                                boundingRects = displayCutout.getBoundingRects();
                                for (Rect rect : boundingRects) {
                                    Locale locale = Locale.US;
                                    String str2 = rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom;
                                    if (!TextUtils.isEmpty(str)) {
                                        str = str.concat("|");
                                    }
                                    str = str.concat(str2);
                                }
                                c4.f(str);
                            } else {
                                pkVar.c().f("");
                            }
                        }
                        Window window2 = activity.getWindow();
                        WindowManager.LayoutParams attributes2 = window2.getAttributes();
                        i5 = attributes2.layoutInDisplayCutoutMode;
                        if (2 != i5) {
                            attributes2.layoutInDisplayCutoutMode = 2;
                            window2.setAttributes(attributes2);
                        }
                        return view.onApplyWindowInsets(windowInsets);
                    }
                });
            }
        }
    }
}
