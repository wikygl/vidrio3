package B;

import B.i;
import S0.View$OnClickListenerC0278r0;
import S0.View$OnClickListenerC0290x0;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.widget.Toolbar;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;
import java.lang.reflect.Method;
import org.chromium.support_lib_boundary.WebSettingsBoundaryInterface;

/* renamed from: B.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class RunnableC0145a implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f214j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Object f215k;

    public /* synthetic */ RunnableC0145a(int i4, Object obj) {
        this.f214j = i4;
        this.f215k = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z4;
        Object obj;
        Object obj2 = this.f215k;
        switch (this.f214j) {
            case 0:
                Activity activity = (Activity) obj2;
                if (!activity.isFinishing()) {
                    int i4 = Build.VERSION.SDK_INT;
                    if (i4 >= 28) {
                        Class<?> cls = i.f226a;
                        activity.recreate();
                        return;
                    }
                    Class<?> cls2 = i.f226a;
                    if (i4 != 26 && i4 != 27) {
                        z4 = false;
                    } else {
                        z4 = true;
                    }
                    Method method = i.f;
                    if ((!z4 || method != null) && (i.f230e != null || i.f229d != null)) {
                        try {
                            Object obj3 = i.f228c.get(activity);
                            if (obj3 != null && (obj = i.f227b.get(activity)) != null) {
                                Application application = activity.getApplication();
                                i.a aVar = new i.a(activity);
                                application.registerActivityLifecycleCallbacks(aVar);
                                Handler handler = i.f231g;
                                handler.post(new f(aVar, obj3));
                                if (i4 != 26 && i4 != 27) {
                                    activity.recreate();
                                } else {
                                    Boolean bool = Boolean.FALSE;
                                    method.invoke(obj, obj3, null, null, 0, bool, null, null, bool, bool);
                                }
                                handler.post(new g(application, aVar));
                                return;
                            }
                        } catch (Throwable unused) {
                        }
                    }
                    activity.recreate();
                    return;
                }
                return;
            case 1:
                ActivityListaCorte.f fVar = (ActivityListaCorte.f) obj2;
                ActivityListaCorte activityListaCorte = ActivityListaCorte.this;
                activityListaCorte.f3025e0.f2897x = true;
                activityListaCorte.f3009O.setOnClickListener(new View$OnClickListenerC0278r0(1, fVar));
                activityListaCorte.f3020Z.setVisibility(8);
                Log.d("LC_BILLING_V5_COPY", "onPurchase: ");
                return;
            case 2:
                ActivityListaCorte.g gVar = (ActivityListaCorte.g) obj2;
                gVar.getClass();
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                ActivityListaCorte activityListaCorte2 = ActivityListaCorte.this;
                activityListaCorte2.G();
                activityListaCorte2.f3009O.setImageResource(2131165312);
                activityListaCorte2.f3009O.setOnClickListener(new View$OnClickListenerC0290x0(0, gVar));
                activityListaCorte2.f3025e0.f2897x = false;
                activityListaCorte2.f3020Z.setVisibility(0);
                Log.d("DIA_BILLING_V5_COPY", "onNotPurchase: ");
                return;
            case WebSettingsBoundaryInterface.AttributionBehavior.APP_SOURCE_AND_APP_TRIGGER /* 3 */:
                ActivityOptimizacion.b bVar = (ActivityOptimizacion.b) obj2;
                bVar.getClass();
                int i5 = ActivityOptimizacion.f3061g0;
                ActivityOptimizacion activityOptimizacion = ActivityOptimizacion.this;
                activityOptimizacion.J();
                activityOptimizacion.f3076V.f2897x = false;
                activityOptimizacion.f3064J.setVisibility(0);
                Log.d("DIA_BILLING_V5_COPY", "onNotPurchase: ");
                return;
            default:
                ((Toolbar) obj2).p();
                return;
        }
    }
}
