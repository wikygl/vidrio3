package h3;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* renamed from: h3.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0443a {

    /* renamed from: a  reason: collision with root package name */
    public static final AbstractC0052a f3587a;

    /* renamed from: h3.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static abstract class AbstractC0052a {
        public abstract Method a(Class<?> cls, Field field);

        public abstract <T> Constructor<T> b(Class<T> cls);

        public abstract String[] c(Class<?> cls);

        public abstract boolean d(Class<?> cls);
    }

    /* renamed from: h3.a$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class b extends AbstractC0052a {
        @Override // h3.C0443a.AbstractC0052a
        public final Method a(Class<?> cls, Field field) {
            throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
        }

        @Override // h3.C0443a.AbstractC0052a
        public final <T> Constructor<T> b(Class<T> cls) {
            throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
        }

        @Override // h3.C0443a.AbstractC0052a
        public final String[] c(Class<?> cls) {
            throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
        }

        @Override // h3.C0443a.AbstractC0052a
        public final boolean d(Class<?> cls) {
            return false;
        }
    }

    /* renamed from: h3.a$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static class c extends AbstractC0052a {

        /* renamed from: a  reason: collision with root package name */
        public final Method f3588a = Class.class.getMethod("isRecord", null);

        /* renamed from: b  reason: collision with root package name */
        public final Method f3589b;

        /* renamed from: c  reason: collision with root package name */
        public final Method f3590c;

        /* renamed from: d  reason: collision with root package name */
        public final Method f3591d;

        public c() {
            Method method = Class.class.getMethod("getRecordComponents", null);
            this.f3589b = method;
            Class<?> componentType = method.getReturnType().getComponentType();
            this.f3590c = componentType.getMethod("getName", null);
            this.f3591d = componentType.getMethod("getType", null);
        }

        @Override // h3.C0443a.AbstractC0052a
        public final Method a(Class<?> cls, Field field) {
            try {
                return cls.getMethod(field.getName(), null);
            } catch (ReflectiveOperationException e4) {
                throw new RuntimeException("Unexpected ReflectiveOperationException occurred (Gson 2.10.1). To support Java records, reflection is utilized to read out information about records. All these invocations happens after it is established that records exist in the JVM. This exception is unexpected behavior.", e4);
            }
        }

        @Override // h3.C0443a.AbstractC0052a
        public final <T> Constructor<T> b(Class<T> cls) {
            try {
                Object[] objArr = (Object[]) this.f3589b.invoke(cls, null);
                Class<?>[] clsArr = new Class[objArr.length];
                for (int i4 = 0; i4 < objArr.length; i4++) {
                    clsArr[i4] = (Class) this.f3591d.invoke(objArr[i4], null);
                }
                return cls.getDeclaredConstructor(clsArr);
            } catch (ReflectiveOperationException e4) {
                throw new RuntimeException("Unexpected ReflectiveOperationException occurred (Gson 2.10.1). To support Java records, reflection is utilized to read out information about records. All these invocations happens after it is established that records exist in the JVM. This exception is unexpected behavior.", e4);
            }
        }

        @Override // h3.C0443a.AbstractC0052a
        public final String[] c(Class<?> cls) {
            try {
                Object[] objArr = (Object[]) this.f3589b.invoke(cls, null);
                String[] strArr = new String[objArr.length];
                for (int i4 = 0; i4 < objArr.length; i4++) {
                    strArr[i4] = (String) this.f3590c.invoke(objArr[i4], null);
                }
                return strArr;
            } catch (ReflectiveOperationException e4) {
                throw new RuntimeException("Unexpected ReflectiveOperationException occurred (Gson 2.10.1). To support Java records, reflection is utilized to read out information about records. All these invocations happens after it is established that records exist in the JVM. This exception is unexpected behavior.", e4);
            }
        }

        @Override // h3.C0443a.AbstractC0052a
        public final boolean d(Class<?> cls) {
            try {
                return ((Boolean) this.f3588a.invoke(cls, null)).booleanValue();
            } catch (ReflectiveOperationException e4) {
                throw new RuntimeException("Unexpected ReflectiveOperationException occurred (Gson 2.10.1). To support Java records, reflection is utilized to read out information about records. All these invocations happens after it is established that records exist in the JVM. This exception is unexpected behavior.", e4);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [h3.a$a] */
    /* JADX WARN: Type inference failed for: r0v3 */
    static {
        c cVar;
        try {
            cVar = new c();
        } catch (NoSuchMethodException unused) {
            cVar = new Object();
        }
        f3587a = cVar;
    }

    public static void a(AccessibleObject accessibleObject, StringBuilder sb) {
        Class<?>[] parameterTypes;
        sb.append('(');
        if (accessibleObject instanceof Method) {
            parameterTypes = ((Method) accessibleObject).getParameterTypes();
        } else {
            parameterTypes = ((Constructor) accessibleObject).getParameterTypes();
        }
        for (int i4 = 0; i4 < parameterTypes.length; i4++) {
            if (i4 > 0) {
                sb.append(", ");
            }
            sb.append(parameterTypes[i4].getSimpleName());
        }
        sb.append(')');
    }

    public static String b(Constructor<?> constructor) {
        StringBuilder sb = new StringBuilder(constructor.getDeclaringClass().getName());
        a(constructor, sb);
        return sb.toString();
    }

    public static String c(Field field) {
        return field.getDeclaringClass().getName() + "#" + field.getName();
    }

    public static String d(AccessibleObject accessibleObject, boolean z4) {
        String str;
        if (accessibleObject instanceof Field) {
            str = "field '" + c((Field) accessibleObject) + "'";
        } else if (accessibleObject instanceof Method) {
            Method method = (Method) accessibleObject;
            StringBuilder sb = new StringBuilder(method.getName());
            a(method, sb);
            str = "method '" + method.getDeclaringClass().getName() + "#" + sb.toString() + "'";
        } else if (accessibleObject instanceof Constructor) {
            str = "constructor '" + b((Constructor) accessibleObject) + "'";
        } else {
            str = "<unknown AccessibleObject> " + accessibleObject.toString();
        }
        if (z4 && Character.isLowerCase(str.charAt(0))) {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
        return str;
    }

    public static void e(AccessibleObject accessibleObject) {
        try {
            accessibleObject.setAccessible(true);
        } catch (Exception e4) {
            throw new RuntimeException(C.b.b("Failed making ", d(accessibleObject, false), " accessible; either increase its visibility or write a custom TypeAdapter for its declaring type."), e4);
        }
    }
}
