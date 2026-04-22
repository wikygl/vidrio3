package e;

import android.util.Log;
import android.util.LongSparseArray;
import java.lang.reflect.Field;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class w {

    /* renamed from: a  reason: collision with root package name */
    public static Field f3303a;

    /* renamed from: b  reason: collision with root package name */
    public static boolean f3304b;

    /* renamed from: c  reason: collision with root package name */
    public static Class<?> f3305c;

    /* renamed from: d  reason: collision with root package name */
    public static boolean f3306d;

    /* renamed from: e  reason: collision with root package name */
    public static Field f3307e;
    public static boolean f;

    /* renamed from: g  reason: collision with root package name */
    public static Field f3308g;

    /* renamed from: h  reason: collision with root package name */
    public static boolean f3309h;

    public static void a(Object obj) {
        LongSparseArray longSparseArray;
        if (!f3306d) {
            try {
                f3305c = Class.forName("android.content.res.ThemedResourceCache");
            } catch (ClassNotFoundException e4) {
                Log.e("ResourcesFlusher", "Could not find ThemedResourceCache class", e4);
            }
            f3306d = true;
        }
        Class<?> cls = f3305c;
        if (cls == null) {
            return;
        }
        if (!f) {
            try {
                Field declaredField = cls.getDeclaredField("mUnthemedEntries");
                f3307e = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException e5) {
                Log.e("ResourcesFlusher", "Could not retrieve ThemedResourceCache#mUnthemedEntries field", e5);
            }
            f = true;
        }
        Field field = f3307e;
        if (field == null) {
            return;
        }
        try {
            longSparseArray = (LongSparseArray) field.get(obj);
        } catch (IllegalAccessException e6) {
            Log.e("ResourcesFlusher", "Could not retrieve value from ThemedResourceCache#mUnthemedEntries", e6);
            longSparseArray = null;
        }
        if (longSparseArray != null) {
            longSparseArray.clear();
        }
    }
}
