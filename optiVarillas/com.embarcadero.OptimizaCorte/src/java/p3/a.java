package p3;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import p3.f;
import v3.h;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public abstract class a implements n3.d<Object>, d, Serializable {

    /* renamed from: j  reason: collision with root package name */
    public final n3.d<Object> f5577j;

    public a(n3.d<Object> dVar) {
        this.f5577j = dVar;
    }

    public n3.d a(n3.d dVar) {
        h.e(dVar, "completion");
        throw new UnsupportedOperationException("create(Any?;Continuation) has not been overridden");
    }

    public final StackTraceElement d() {
        int i4;
        String str;
        Object obj;
        Object obj2;
        Object obj3;
        Integer num;
        int i5;
        e eVar = (e) getClass().getAnnotation(e.class);
        String str2 = null;
        if (eVar == null) {
            return null;
        }
        int v4 = eVar.v();
        if (v4 <= 1) {
            int i6 = -1;
            try {
                Field declaredField = getClass().getDeclaredField("label");
                declaredField.setAccessible(true);
                Object obj4 = declaredField.get(this);
                if (obj4 instanceof Integer) {
                    num = (Integer) obj4;
                } else {
                    num = null;
                }
                if (num != null) {
                    i5 = num.intValue();
                } else {
                    i5 = 0;
                }
                i4 = i5 - 1;
            } catch (Exception unused) {
                i4 = -1;
            }
            if (i4 >= 0) {
                i6 = eVar.l()[i4];
            }
            f.a aVar = f.f5582b;
            f.a aVar2 = f.f5581a;
            if (aVar == null) {
                try {
                    f.a aVar3 = new f.a(Class.class.getDeclaredMethod("getModule", null), getClass().getClassLoader().loadClass("java.lang.Module").getDeclaredMethod("getDescriptor", null), getClass().getClassLoader().loadClass("java.lang.module.ModuleDescriptor").getDeclaredMethod("name", null));
                    f.f5582b = aVar3;
                    aVar = aVar3;
                } catch (Exception unused2) {
                    f.f5582b = aVar2;
                    aVar = aVar2;
                }
            }
            if (aVar != aVar2) {
                Method method = aVar.f5583a;
                if (method != null) {
                    obj = method.invoke(getClass(), null);
                } else {
                    obj = null;
                }
                if (obj != null) {
                    Method method2 = aVar.f5584b;
                    if (method2 != null) {
                        obj2 = method2.invoke(obj, null);
                    } else {
                        obj2 = null;
                    }
                    if (obj2 != null) {
                        Method method3 = aVar.f5585c;
                        if (method3 != null) {
                            obj3 = method3.invoke(obj2, null);
                        } else {
                            obj3 = null;
                        }
                        if (obj3 instanceof String) {
                            str2 = obj3;
                        }
                    }
                }
            }
            if (str2 == null) {
                str = eVar.c();
            } else {
                str = str2 + '/' + eVar.c();
            }
            return new StackTraceElement(str, eVar.m(), eVar.f(), i6);
        }
        throw new IllegalStateException(("Debug metadata version mismatch. Expected: 1, got " + v4 + ". Please update the Kotlin standard library.").toString());
    }

    @Override // p3.d
    public final d h() {
        n3.d<Object> dVar = this.f5577j;
        if (dVar instanceof d) {
            return (d) dVar;
        }
        return null;
    }

    public abstract Object i(Object obj);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // n3.d
    public final void j(Object obj) {
        n3.d dVar = this;
        while (true) {
            a aVar = (a) dVar;
            n3.d dVar2 = aVar.f5577j;
            h.b(dVar2);
            try {
                obj = aVar.i(obj);
                if (obj == o3.a.f5500j) {
                    return;
                }
            } catch (Throwable th) {
                obj = B2.a.a(th);
            }
            aVar.k();
            if (dVar2 instanceof a) {
                dVar = dVar2;
            } else {
                dVar2.j(obj);
                return;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Continuation at ");
        Object d4 = d();
        if (d4 == null) {
            d4 = getClass().getName();
        }
        sb.append(d4);
        return sb.toString();
    }

    public void k() {
    }
}
