package S0;

import android.content.Intent;
import android.widget.ImageView;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final /* synthetic */ class P0 implements androidx.activity.result.b {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ ActivityOptimizacion f2184j;

    public final void c(Object obj) {
        androidx.activity.result.a aVar = (androidx.activity.result.a) obj;
        int i4 = ActivityOptimizacion.f3061g0;
        ActivityOptimizacion activityOptimizacion = this.f2184j;
        activityOptimizacion.getClass();
        if (aVar.j == -1) {
            try {
                ImageView imageView = (ImageView) activityOptimizacion.f3079Y.findViewById(2131231004);
                Intent intent = aVar.k;
                if (intent != null) {
                    imageView.setImageURI(intent.getData());
                }
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }
    }
}
