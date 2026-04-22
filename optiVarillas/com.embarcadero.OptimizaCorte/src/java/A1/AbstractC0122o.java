package A1;

import android.os.IBinder;
import com.google.android.gms.internal.ads.x8;

/* renamed from: A1.o  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class AbstractC0122o {

    /* renamed from: a  reason: collision with root package name */
    public static final V f160a;

    static {
        V x8Var;
        V v4 = null;
        try {
            Object newInstance = C0120n.class.getClassLoader().loadClass("com.google.android.gms.ads.internal.ClientApi").getDeclaredConstructor(null).newInstance(null);
            if (!(newInstance instanceof IBinder)) {
                E1.m.g("ClientApi class is not an instance of IBinder.");
            } else {
                IBinder iBinder = (IBinder) newInstance;
                if (iBinder != null) {
                    x8 queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.ads.internal.client.IClientApi");
                    if (queryLocalInterface instanceof V) {
                        x8Var = (V) queryLocalInterface;
                    } else {
                        x8Var = new x8(iBinder, "com.google.android.gms.ads.internal.client.IClientApi");
                    }
                    v4 = x8Var;
                }
            }
        } catch (Exception unused) {
            E1.m.g("Failed to instantiate ClientApi class.");
        }
        f160a = v4;
    }

    public abstract Object a();

    public abstract Object b();

    public abstract Object c();

    /* JADX WARN: Removed duplicated region for block: B:21:0x005c  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x00d5  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x007c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:62:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object d(android.content.Context r10, boolean r11) {
        /*
            Method dump skipped, instructions count: 218
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: A1.AbstractC0122o.d(android.content.Context, boolean):java.lang.Object");
    }
}
