package c2;

import W1.C0324l;
import android.os.IBinder;
import c2.InterfaceC0374a;
import com.google.errorprone.annotations.ResultIgnorabilityUnspecified;
import e0.C0405a;
import java.lang.reflect.Field;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class b<T> extends InterfaceC0374a.AbstractBinderC0042a {

    /* renamed from: j  reason: collision with root package name */
    public final Object f2944j;

    public b(Object obj) {
        super("com.google.android.gms.dynamic.IObjectWrapper");
        this.f2944j = obj;
    }

    @ResultIgnorabilityUnspecified
    public static <T> T p0(InterfaceC0374a interfaceC0374a) {
        if (interfaceC0374a instanceof b) {
            return (T) ((b) interfaceC0374a).f2944j;
        }
        IBinder asBinder = interfaceC0374a.asBinder();
        Field[] declaredFields = asBinder.getClass().getDeclaredFields();
        Field field = null;
        int i4 = 0;
        for (Field field2 : declaredFields) {
            if (!field2.isSynthetic()) {
                i4++;
                field = field2;
            }
        }
        if (i4 == 1) {
            C0324l.d(field);
            if (!field.isAccessible()) {
                field.setAccessible(true);
                try {
                    return (T) field.get(asBinder);
                } catch (IllegalAccessException e4) {
                    throw new IllegalArgumentException("Could not access the field in remoteBinder.", e4);
                } catch (NullPointerException e5) {
                    throw new IllegalArgumentException("Binder object is null.", e5);
                }
            }
            throw new IllegalArgumentException("IObjectWrapper declared field not private!");
        }
        throw new IllegalArgumentException(C0405a.c("Unexpected number of IObjectWrapper declared fields: ", declaredFields.length));
    }
}
