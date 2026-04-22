package M;

import android.view.VelocityTracker;
import j$.util.DesugarCollections;
import java.util.Map;
import java.util.WeakHashMap;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class H {

    /* renamed from: a  reason: collision with root package name */
    public static final Map<VelocityTracker, I> f1520a = DesugarCollections.synchronizedMap(new WeakHashMap());

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public static class a {
        public static float a(VelocityTracker velocityTracker, int i4) {
            return velocityTracker.getAxisVelocity(i4);
        }

        public static float b(VelocityTracker velocityTracker, int i4, int i5) {
            return velocityTracker.getAxisVelocity(i4, i5);
        }

        public static boolean c(VelocityTracker velocityTracker, int i4) {
            return velocityTracker.isAxisSupported(i4);
        }
    }
}
