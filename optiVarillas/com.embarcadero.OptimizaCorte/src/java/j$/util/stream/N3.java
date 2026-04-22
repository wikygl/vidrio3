package j$.util.stream;

import java.security.AccessController;
import java.security.PrivilegedAction;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
abstract class N3 {

    /* renamed from: a  reason: collision with root package name */
    static final boolean f4343a = ((Boolean) AccessController.doPrivileged((PrivilegedAction<Object>) new Object())).booleanValue();

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void a(Class cls, String str) {
        throw new UnsupportedOperationException(cls + " tripwire tripped but logging not supported: " + str);
    }
}
