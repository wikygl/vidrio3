package K1;

import android.util.Pair;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class s extends LinkedHashMap {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ t f1399j;

    public s(t tVar) {
        this.f1399j = tVar;
    }

    @Override // java.util.LinkedHashMap
    public final boolean removeEldestEntry(Map.Entry entry) {
        synchronized (this.f1399j) {
            try {
                int size = size();
                t tVar = this.f1399j;
                boolean z4 = false;
                if (size <= tVar.f1400a) {
                    return false;
                }
                tVar.f.add(new Pair((String) entry.getKey(), (String) ((Pair) entry.getValue()).second));
                if (size() > this.f1399j.f1400a) {
                    z4 = true;
                }
                return z4;
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
