package J;

import android.app.ProgressDialog;
import com.embarcadero.OptimizaCorte.Activities.ActivityListaCorte;
import java.util.Comparator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class c implements Comparator {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1154j;

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        switch (this.f1154j) {
            case 0:
                byte[] bArr = (byte[]) obj;
                byte[] bArr2 = (byte[]) obj2;
                if (bArr.length != bArr2.length) {
                    return bArr.length - bArr2.length;
                }
                for (int i4 = 0; i4 < bArr.length; i4++) {
                    byte b4 = bArr[i4];
                    byte b5 = bArr2[i4];
                    if (b4 != b5) {
                        return b4 - b5;
                    }
                }
                return 0;
            default:
                ProgressDialog progressDialog = ActivityListaCorte.l0;
                return Double.compare(((b1.e) obj2).f2915d, ((b1.e) obj).f2915d);
        }
    }
}
