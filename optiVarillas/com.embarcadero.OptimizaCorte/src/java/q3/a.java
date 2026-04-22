package q3;

import java.lang.reflect.Method;
import v3.h;
import w3.c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class a {

    /* renamed from: q3.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class C0063a {

        /* renamed from: a  reason: collision with root package name */
        public static final Method f5633a;

        static {
            Method method;
            Method[] methods = Throwable.class.getMethods();
            h.d(methods, "throwableMethods");
            int length = methods.length;
            int i4 = 0;
            while (true) {
                method = null;
                if (i4 >= length) {
                    break;
                }
                Method method2 = methods[i4];
                if (h.a(method2.getName(), "addSuppressed")) {
                    Class<?>[] parameterTypes = method2.getParameterTypes();
                    h.d(parameterTypes, "it.parameterTypes");
                    if (parameterTypes.length == 1) {
                        method = parameterTypes[0];
                    }
                    if (h.a(method, Throwable.class)) {
                        method = method2;
                        break;
                    }
                }
                i4++;
            }
            f5633a = method;
            int length2 = methods.length;
            for (int i5 = 0; i5 < length2 && !h.a(methods[i5].getName(), "getSuppressed"); i5++) {
            }
        }
    }

    public void a(Throwable th, Throwable th2) {
        h.e(th, "cause");
        h.e(th2, "exception");
        Method method = C0063a.f5633a;
        if (method != null) {
            method.invoke(th, th2);
        }
    }

    public c b() {
        return new w3.b();
    }
}
