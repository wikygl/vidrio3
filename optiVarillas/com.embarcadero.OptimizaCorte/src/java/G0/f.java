package G0;

import C0.i;
import android.content.ComponentName;
import android.content.Context;
import androidx.work.impl.background.systemjob.SystemJobService;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class f {

    /* renamed from: b  reason: collision with root package name */
    public static final String f953b = i.e("SystemJobInfoConverter");

    /* renamed from: a  reason: collision with root package name */
    public final ComponentName f954a;

    public f(Context context) {
        this.f954a = new ComponentName(context.getApplicationContext(), SystemJobService.class);
    }
}
