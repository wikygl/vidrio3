package L1;

import android.net.Uri;
import com.google.android.gms.internal.ads.lc;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class c {

    /* renamed from: a  reason: collision with root package name */
    public final String f1486a = (String) lc.a.e();

    public final String a(Map<String, String> map) {
        Uri.Builder buildUpon = Uri.parse(this.f1486a).buildUpon();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            buildUpon.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return buildUpon.build().toString();
    }
}
