package c2;

import android.os.IBinder;
import android.os.IInterface;
import h2.BinderC0439b;
import h2.C0438a;

/* renamed from: c2.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public interface InterfaceC0374a extends IInterface {

    /* renamed from: c2.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public static abstract class AbstractBinderC0042a extends BinderC0439b implements InterfaceC0374a {
        /* JADX WARN: Type inference failed for: r1v1, types: [h2.a, c2.a] */
        public static InterfaceC0374a Z(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.dynamic.IObjectWrapper");
            if (queryLocalInterface instanceof InterfaceC0374a) {
                return (InterfaceC0374a) queryLocalInterface;
            }
            return new C0438a(iBinder, "com.google.android.gms.dynamic.IObjectWrapper");
        }
    }
}
