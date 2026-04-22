package j$.sun.misc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.misc.Unsafe;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class a {

    /* renamed from: b  reason: collision with root package name */
    private static final a f3834b;

    /* renamed from: a  reason: collision with root package name */
    private final Unsafe f3835a;

    static {
        Field i4 = i();
        i4.setAccessible(true);
        try {
            f3834b = new a((Unsafe) i4.get(null));
        } catch (IllegalAccessException e4) {
            throw new AssertionError("Couldn't get the Unsafe", e4);
        }
    }

    a(Unsafe unsafe) {
        this.f3835a = unsafe;
    }

    public static a h() {
        return f3834b;
    }

    private static Field i() {
        Field[] declaredFields;
        try {
            return Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e4) {
            for (Field field : Unsafe.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && Unsafe.class.isAssignableFrom(field.getType())) {
                    return field;
                }
            }
            throw new AssertionError("Couldn't find the Unsafe", e4);
        }
    }

    public final int a(Class cls) {
        return this.f3835a.arrayBaseOffset(cls);
    }

    public final int b(Class cls) {
        return this.f3835a.arrayIndexScale(cls);
    }

    public final boolean c(Object obj, long j4, int i4, int i5) {
        return this.f3835a.compareAndSwapInt(obj, j4, i4, i5);
    }

    public final boolean d(Object obj, long j4, long j5, long j6) {
        return this.f3835a.compareAndSwapLong(obj, j4, j5, j6);
    }

    public final boolean e(Object obj, long j4, Object obj2) {
        return j$.com.android.tools.r8.a.l(this.f3835a, obj, j4, obj2);
    }

    public final int f(Object obj, long j4) {
        int intVolatile;
        do {
            intVolatile = this.f3835a.getIntVolatile(obj, j4);
        } while (!this.f3835a.compareAndSwapInt(obj, j4, intVolatile, intVolatile - 4));
        return intVolatile;
    }

    public final Object g(Object obj, long j4) {
        return this.f3835a.getObjectVolatile(obj, j4);
    }

    public final long j(Class cls, String str) {
        try {
            return k(cls.getDeclaredField(str));
        } catch (NoSuchFieldException e4) {
            throw new AssertionError("Cannot find field:", e4);
        }
    }

    public final long k(Field field) {
        return this.f3835a.objectFieldOffset(field);
    }

    public final void l(Object obj, long j4, Object obj2) {
        this.f3835a.putObjectVolatile(obj, j4, obj2);
    }
}
