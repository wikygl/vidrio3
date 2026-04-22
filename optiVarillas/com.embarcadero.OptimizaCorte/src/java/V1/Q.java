package V1;

import com.google.android.gms.internal.ads.sT;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class Q extends ThreadLocal {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f2560a;

    @Override // java.lang.ThreadLocal
    public final Object initialValue() {
        switch (this.f2560a) {
            case 0:
                return Boolean.FALSE;
            default:
                try {
                    return (Cipher) sT.b.a.i("AES/CTR/NOPADDING");
                } catch (GeneralSecurityException e4) {
                    throw new IllegalStateException(e4);
                }
        }
    }
}
