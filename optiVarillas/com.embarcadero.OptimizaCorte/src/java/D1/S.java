package D1;

import android.content.SharedPreferences;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class S implements SharedPreferences.OnSharedPreferenceChangeListener {

    /* renamed from: a  reason: collision with root package name */
    public final String f649a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ T f650b;

    public S(T t3, String str) {
        this.f650b = t3;
        this.f649a = str;
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        synchronized (this.f650b) {
            try {
                Iterator it = this.f650b.f652b.iterator();
                while (it.hasNext()) {
                    String str2 = this.f649a;
                    Map map = ((Q) it.next()).f648a;
                    if (map.containsKey(str2) && ((Set) map.get(str2)).contains(str)) {
                        z1.p.f6575A.f6581g.c().k(false);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }
}
