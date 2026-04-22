package M0;

import android.content.Context;
import android.os.PowerManager;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class m {

    /* renamed from: a  reason: collision with root package name */
    public static final String f1687a = C0.i.e("WakeLocks");

    /* renamed from: b  reason: collision with root package name */
    public static final WeakHashMap<PowerManager.WakeLock, String> f1688b = new WeakHashMap<>();

    public static PowerManager.WakeLock a(Context context, String str) {
        String concat = "WorkManager: ".concat(str);
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getApplicationContext().getSystemService("power")).newWakeLock(1, concat);
        WeakHashMap<PowerManager.WakeLock, String> weakHashMap = f1688b;
        synchronized (weakHashMap) {
            weakHashMap.put(newWakeLock, concat);
        }
        return newWakeLock;
    }
}
