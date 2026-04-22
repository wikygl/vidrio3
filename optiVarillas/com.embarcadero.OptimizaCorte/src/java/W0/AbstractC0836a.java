package w0;

import android.os.Parcelable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import r.C0773b;

/* renamed from: w0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0836a {

    /* renamed from: a  reason: collision with root package name */
    public final C0773b<String, Method> f6376a;

    /* renamed from: b  reason: collision with root package name */
    public final C0773b<String, Method> f6377b;

    /* renamed from: c  reason: collision with root package name */
    public final C0773b<String, Class> f6378c;

    public AbstractC0836a(C0773b<String, Method> c0773b, C0773b<String, Method> c0773b2, C0773b<String, Class> c0773b3) {
        this.f6376a = c0773b;
        this.f6377b = c0773b2;
        this.f6378c = c0773b3;
    }

    public abstract b a();

    public final Class b(Class<? extends c> cls) {
        String name = cls.getName();
        C0773b<String, Class> c0773b = this.f6378c;
        Class orDefault = c0773b.getOrDefault(name, null);
        if (orDefault == null) {
            String name2 = cls.getPackage().getName();
            String simpleName = cls.getSimpleName();
            Class<?> cls2 = Class.forName(name2 + "." + simpleName + "Parcelizer", false, cls.getClassLoader());
            c0773b.put(cls.getName(), cls2);
            return cls2;
        }
        return orDefault;
    }

    public final Method c(String str) {
        C0773b<String, Method> c0773b = this.f6376a;
        Method orDefault = c0773b.getOrDefault(str, null);
        if (orDefault == null) {
            System.currentTimeMillis();
            Method declaredMethod = Class.forName(str, true, AbstractC0836a.class.getClassLoader()).getDeclaredMethod("read", AbstractC0836a.class);
            c0773b.put(str, declaredMethod);
            return declaredMethod;
        }
        return orDefault;
    }

    public final Method d(Class cls) {
        String name = cls.getName();
        C0773b<String, Method> c0773b = this.f6377b;
        Method orDefault = c0773b.getOrDefault(name, null);
        if (orDefault == null) {
            Class b4 = b(cls);
            System.currentTimeMillis();
            Method declaredMethod = b4.getDeclaredMethod("write", cls, AbstractC0836a.class);
            c0773b.put(cls.getName(), declaredMethod);
            return declaredMethod;
        }
        return orDefault;
    }

    public abstract boolean e();

    public abstract byte[] f();

    public abstract CharSequence g();

    public abstract boolean h(int i4);

    public abstract int i();

    public abstract <T extends Parcelable> T j();

    public abstract String k();

    public final <T extends c> T l() {
        String k4 = k();
        if (k4 == null) {
            return null;
        }
        try {
            return (T) c(k4).invoke(null, a());
        } catch (ClassNotFoundException e4) {
            throw new RuntimeException("VersionedParcel encountered ClassNotFoundException", e4);
        } catch (IllegalAccessException e5) {
            throw new RuntimeException("VersionedParcel encountered IllegalAccessException", e5);
        } catch (NoSuchMethodException e6) {
            throw new RuntimeException("VersionedParcel encountered NoSuchMethodException", e6);
        } catch (InvocationTargetException e7) {
            if (e7.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e7.getCause());
            }
            throw new RuntimeException("VersionedParcel encountered InvocationTargetException", e7);
        }
    }

    public abstract void m(int i4);

    public abstract void n(boolean z4);

    public abstract void o(byte[] bArr);

    public abstract void p(CharSequence charSequence);

    public abstract void q(int i4);

    public abstract void r(Parcelable parcelable);

    public abstract void s(String str);

    /* JADX WARN: Multi-variable type inference failed */
    public final void t(c cVar) {
        if (cVar == null) {
            s(null);
            return;
        }
        try {
            s(b(cVar.getClass()).getName());
            b a4 = a();
            try {
                d(cVar.getClass()).invoke(null, cVar, a4);
                a4.u();
            } catch (ClassNotFoundException e4) {
                throw new RuntimeException("VersionedParcel encountered ClassNotFoundException", e4);
            } catch (IllegalAccessException e5) {
                throw new RuntimeException("VersionedParcel encountered IllegalAccessException", e5);
            } catch (NoSuchMethodException e6) {
                throw new RuntimeException("VersionedParcel encountered NoSuchMethodException", e6);
            } catch (InvocationTargetException e7) {
                if (e7.getCause() instanceof RuntimeException) {
                    throw ((RuntimeException) e7.getCause());
                }
                throw new RuntimeException("VersionedParcel encountered InvocationTargetException", e7);
            }
        } catch (ClassNotFoundException e8) {
            throw new RuntimeException(cVar.getClass().getSimpleName().concat(" does not have a Parcelizer"), e8);
        }
    }
}
