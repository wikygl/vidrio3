package i1;

import android.util.Base64;
import com.google.auto.value.AutoValue;

@AutoValue
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class s {
    public abstract String a();

    public abstract byte[] b();

    public abstract f1.d c();

    public final String toString() {
        String encodeToString;
        String a4 = a();
        f1.d c4 = c();
        if (b() == null) {
            encodeToString = "";
        } else {
            encodeToString = Base64.encodeToString(b(), 2);
        }
        StringBuilder sb = new StringBuilder("TransportContext(");
        sb.append(a4);
        sb.append(", ");
        sb.append(c4);
        sb.append(", ");
        return C.b.c(sb, encodeToString, ")");
    }
}
