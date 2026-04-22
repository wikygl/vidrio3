package t0;

import android.annotation.SuppressLint;
import android.os.Trace;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* renamed from: t0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0798a {

    /* renamed from: a  reason: collision with root package name */
    public static long f5779a;

    /* renamed from: b  reason: collision with root package name */
    public static Method f5780b;

    @SuppressLint({"NewApi"})
    public static boolean a() {
        boolean isEnabled;
        try {
            if (f5780b == null) {
                isEnabled = Trace.isEnabled();
                return isEnabled;
            }
        } catch (NoClassDefFoundError | NoSuchMethodError unused) {
        }
        try {
            if (f5780b == null) {
                f5779a = Trace.class.getField("TRACE_TAG_APP").getLong(null);
                f5780b = Trace.class.getMethod("isTagEnabled", Long.TYPE);
            }
            return ((Boolean) f5780b.invoke(null, Long.valueOf(f5779a))).booleanValue();
        } catch (Exception e4) {
            if (e4 instanceof InvocationTargetException) {
                Throwable cause = e4.getCause();
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                }
                throw new RuntimeException(cause);
            }
            Log.v("Trace", "Unable to call isTagEnabled via reflection", e4);
            return false;
        }
    }
}
