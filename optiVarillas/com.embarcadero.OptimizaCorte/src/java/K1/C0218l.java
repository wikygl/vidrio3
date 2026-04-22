package K1;

import A1.C0124p;
import a3.InterfaceFutureC0346a;
import android.net.Uri;
import android.text.TextUtils;
import android.util.JsonReader;
import com.google.android.gms.internal.ads.Ei;
import com.google.android.gms.internal.ads.IN;
import com.google.android.gms.internal.ads.Ix;
import com.google.android.gms.internal.ads.Lc;
import com.google.android.gms.internal.ads.RK;
import com.google.android.gms.internal.ads.VN;
import com.google.android.gms.internal.ads.uu;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: K1.l  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class C0218l implements IN {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ int f1381a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Object f1382b;

    public /* synthetic */ C0218l(int i4, Object obj) {
        this.f1381a = i4;
        this.f1382b = obj;
    }

    public final InterfaceFutureC0346a d(Object obj) {
        switch (this.f1381a) {
            case 0:
                Ix ix = (Ix) obj;
                o oVar = new o(new JsonReader(new InputStreamReader(ix.a)), ix.b);
                try {
                    oVar.f1388b = C0124p.f.f161a.g(((Ei) this.f1382b).j).toString();
                } catch (JSONException unused) {
                    oVar.f1388b = "{}";
                }
                return VN.x(oVar);
            default:
                final ArrayList arrayList = (ArrayList) obj;
                final C0208b c0208b = (C0208b) this.f1382b;
                return VN.z(c0208b.D4("google.afma.nativeAds.getPublisherCustomRenderedImpressionSignals"), new RK() { // from class: K1.z
                    public final Object apply(Object obj2) {
                        switch (r2) {
                            case 0:
                                String str = (String) obj2;
                                C0208b c0208b2 = (C0208b) c0208b;
                                c0208b2.getClass();
                                ArrayList arrayList2 = new ArrayList();
                                for (Uri uri : (List) arrayList) {
                                    if (C0208b.I4(uri, c0208b2.f1335J, c0208b2.f1336K) && !TextUtils.isEmpty(str)) {
                                        arrayList2.add(C0208b.J4(uri, "nas", str));
                                    } else {
                                        arrayList2.add(uri);
                                    }
                                }
                                return arrayList2;
                            default:
                                List list = (List) obj2;
                                uu uuVar = (uu) c0208b;
                                uuVar.getClass();
                                Integer num = null;
                                if (list == null || list.isEmpty()) {
                                    return null;
                                }
                                JSONObject jSONObject = (JSONObject) arrayList;
                                String optString = jSONObject.optString("text");
                                Integer d4 = uu.d("bg_color", jSONObject);
                                Integer d5 = uu.d("text_color", jSONObject);
                                int optInt = jSONObject.optInt("text_size", -1);
                                jSONObject.optBoolean("allow_pub_rendering");
                                int optInt2 = jSONObject.optInt("animation_ms", 1000);
                                int optInt3 = jSONObject.optInt("presentation_ms", 4000);
                                if (optInt > 0) {
                                    num = Integer.valueOf(optInt);
                                }
                                return new Lc(optString, list, d4, d5, num, optInt2 + optInt3, uuVar.h.n);
                        }
                    }
                }, c0208b.f1346p);
        }
    }
}
