package D3;

import C3.g0;
import F3.p;
import android.os.Looper;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class a implements p {
    @Override // F3.p
    public String a() {
        return "For tests Dispatchers.setMain from kotlinx-coroutines-test module can be used";
    }

    @Override // F3.p
    public g0 b(List<? extends p> list) {
        Looper mainLooper = Looper.getMainLooper();
        if (mainLooper != null) {
            return new c(e.a(mainLooper));
        }
        throw new IllegalStateException("The main looper is not available");
    }

    @Override // F3.p
    public int c() {
        return 1073741823;
    }
}
