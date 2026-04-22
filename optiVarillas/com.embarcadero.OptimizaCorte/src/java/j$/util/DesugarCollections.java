package j$.util;

import j$.util.Collection;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Predicate;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class DesugarCollections {

    /* renamed from: a  reason: collision with root package name */
    public static final Class f4077a;

    /* renamed from: b  reason: collision with root package name */
    private static final Field f4078b;

    /* renamed from: c  reason: collision with root package name */
    private static final Field f4079c;

    /* renamed from: d  reason: collision with root package name */
    private static final Constructor f4080d;

    /* renamed from: e  reason: collision with root package name */
    private static final Constructor f4081e;

    static {
        Field field;
        Field field2;
        Constructor<?> constructor;
        Class<?> cls = Collections.synchronizedCollection(new ArrayList()).getClass();
        f4077a = cls;
        Collections.synchronizedList(new LinkedList()).getClass();
        Constructor<?> constructor2 = null;
        try {
            field = cls.getDeclaredField("mutex");
        } catch (NoSuchFieldException unused) {
            field = null;
        }
        f4078b = field;
        if (field != null) {
            field.setAccessible(true);
        }
        try {
            field2 = cls.getDeclaredField("c");
        } catch (NoSuchFieldException unused2) {
            field2 = null;
        }
        f4079c = field2;
        if (field2 != null) {
            field2.setAccessible(true);
        }
        try {
            constructor = Collections.synchronizedSet(new HashSet()).getClass().getDeclaredConstructor(java.util.Set.class, Object.class);
        } catch (NoSuchMethodException unused3) {
            constructor = null;
        }
        f4081e = constructor;
        if (constructor != null) {
            constructor.setAccessible(true);
        }
        try {
            constructor2 = cls.getDeclaredConstructor(java.util.Collection.class, Object.class);
        } catch (NoSuchMethodException unused4) {
        }
        f4080d = constructor2;
        if (constructor2 != null) {
            constructor2.setAccessible(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean c(java.util.Collection collection, Predicate predicate) {
        boolean removeIf;
        Field field = f4078b;
        if (field == null) {
            try {
                java.util.Collection collection2 = (java.util.Collection) f4079c.get(collection);
                return collection2 instanceof Collection ? ((Collection) collection2).removeIf(predicate) : Collection.CC.$default$removeIf(collection2, predicate);
            } catch (IllegalAccessException e4) {
                throw new Error("Runtime illegal access in synchronized collection removeIf fall-back.", e4);
            }
        }
        try {
            synchronized (field.get(collection)) {
                java.util.Collection collection3 = (java.util.Collection) f4079c.get(collection);
                removeIf = collection3 instanceof Collection ? ((Collection) collection3).removeIf(predicate) : Collection.CC.$default$removeIf(collection3, predicate);
            }
            return removeIf;
        } catch (IllegalAccessException e5) {
            throw new Error("Runtime illegal access in synchronized collection removeIf.", e5);
        }
    }

    public static <K, V> java.util.Map<K, V> synchronizedMap(java.util.Map<K, V> map) {
        return new C0504g(map);
    }
}
