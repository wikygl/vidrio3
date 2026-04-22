package S0;

import android.util.Log;
import com.embarcadero.OptimizaCorte.Activities.ActivityInicio;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C extends G3.g {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ ActivityInicio f2113k;

    public C(ActivityInicio activityInicio) {
        this.f2113k = activityInicio;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, android.app.Activity] */
    @Override // G3.g
    public final void u() {
        int i4 = ActivityInicio.f2947A0;
        ?? r02 = this.f2113k;
        r02.F();
        r02.startActivity(r02.f2976j0);
        r02.overridePendingTransition(2130771998, 2130771999);
        Log.d("TAG", "The ad was dismissed.");
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityInicio, android.app.Activity] */
    @Override // G3.g
    public final void w() {
        int i4 = ActivityInicio.f2947A0;
        ?? r02 = this.f2113k;
        r02.F();
        r02.startActivity(r02.f2976j0);
        r02.overridePendingTransition(2130771998, 2130771999);
        Log.d("TAG", "The ad failed to show.");
    }

    @Override // G3.g
    public final void y() {
        this.f2113k.f2979n0 = null;
        Log.d("TAG", "The ad was shown.");
    }
}
