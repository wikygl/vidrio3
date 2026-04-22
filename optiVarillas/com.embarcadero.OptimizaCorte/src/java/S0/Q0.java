package S0;

import android.content.Intent;
import android.util.Log;
import com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class Q0 extends G3.g {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ StringBuilder f2185k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ ActivityOptimizacion f2186l;

    public Q0(ActivityOptimizacion activityOptimizacion, StringBuilder sb) {
        this.f2186l = activityOptimizacion;
        this.f2185k = sb;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion] */
    @Override // G3.g
    public final void u() {
        int i4 = ActivityOptimizacion.f3061g0;
        ?? r02 = this.f2186l;
        r02.I();
        r02.startActivity(Intent.createChooser(r02.K(this.f2185k.toString()), r02.getString(2131820656)));
        Log.d("TAG", "The ad was dismissed.");
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.embarcadero.OptimizaCorte.Activities.ActivityOptimizacion] */
    @Override // G3.g
    public final void w() {
        int i4 = ActivityOptimizacion.f3061g0;
        ?? r02 = this.f2186l;
        r02.I();
        r02.startActivity(Intent.createChooser(r02.K(this.f2185k.toString()), r02.getString(2131820656)));
        Log.d("TAG", "The ad failed to show.");
    }

    @Override // G3.g
    public final void y() {
        this.f2186l.f3062H = null;
        Log.d("TAG", "The ad was shown.");
    }
}
