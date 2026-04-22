package q3;

import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class b {

    /* renamed from: a  reason: collision with root package name */
    public static final a f5634a;

    static {
        a aVar;
        try {
            Object newInstance = s3.a.class.newInstance();
            h.d(newInstance, "forName(\"kotlin.internal…entations\").newInstance()");
            try {
                try {
                    aVar = (a) newInstance;
                } catch (ClassCastException e4) {
                    ClassLoader classLoader = newInstance.getClass().getClassLoader();
                    ClassLoader classLoader2 = a.class.getClassLoader();
                    if (!h.a(classLoader, classLoader2)) {
                        throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader + ", base type classloader: " + classLoader2, e4);
                    }
                    throw e4;
                }
            } catch (ClassNotFoundException unused) {
                Object newInstance2 = r3.a.class.newInstance();
                h.d(newInstance2, "forName(\"kotlin.internal…entations\").newInstance()");
                try {
                    aVar = (a) newInstance2;
                } catch (ClassCastException e5) {
                    ClassLoader classLoader3 = newInstance2.getClass().getClassLoader();
                    ClassLoader classLoader4 = a.class.getClassLoader();
                    if (!h.a(classLoader3, classLoader4)) {
                        throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader3 + ", base type classloader: " + classLoader4, e5);
                    }
                    throw e5;
                }
            }
        } catch (ClassNotFoundException unused2) {
            Object newInstance3 = Class.forName("kotlin.internal.JRE8PlatformImplementations").newInstance();
            h.d(newInstance3, "forName(\"kotlin.internal…entations\").newInstance()");
            try {
                try {
                    try {
                        aVar = (a) newInstance3;
                    } catch (ClassCastException e6) {
                        ClassLoader classLoader5 = newInstance3.getClass().getClassLoader();
                        ClassLoader classLoader6 = a.class.getClassLoader();
                        if (!h.a(classLoader5, classLoader6)) {
                            throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader5 + ", base type classloader: " + classLoader6, e6);
                        }
                        throw e6;
                    }
                } catch (ClassNotFoundException unused3) {
                    Object newInstance4 = Class.forName("kotlin.internal.JRE7PlatformImplementations").newInstance();
                    h.d(newInstance4, "forName(\"kotlin.internal…entations\").newInstance()");
                    try {
                        aVar = (a) newInstance4;
                    } catch (ClassCastException e7) {
                        ClassLoader classLoader7 = newInstance4.getClass().getClassLoader();
                        ClassLoader classLoader8 = a.class.getClassLoader();
                        if (!h.a(classLoader7, classLoader8)) {
                            throw new ClassNotFoundException("Instance class was loaded from a different classloader: " + classLoader7 + ", base type classloader: " + classLoader8, e7);
                        }
                        throw e7;
                    }
                }
            } catch (ClassNotFoundException unused4) {
                aVar = new a();
            }
        }
        f5634a = aVar;
    }
}
