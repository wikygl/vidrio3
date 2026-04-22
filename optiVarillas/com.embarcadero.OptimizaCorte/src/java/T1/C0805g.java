package t1;

import A1.G1;
import A1.N0;
import com.google.android.gms.internal.ads.gA;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: t1.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0805g {

    /* renamed from: a  reason: collision with root package name */
    public final G1 f5798a;

    /* renamed from: b  reason: collision with root package name */
    public final gA f5799b;

    public C0805g(G1 g12) {
        gA h4;
        this.f5798a = g12;
        N0 n02 = g12.f30l;
        if (n02 == null) {
            h4 = null;
        } else {
            h4 = n02.h();
        }
        this.f5799b = h4;
    }

    public final JSONObject a() {
        JSONObject jSONObject = new JSONObject();
        G1 g12 = this.f5798a;
        jSONObject.put("Adapter", g12.f28j);
        jSONObject.put("Latency", g12.f29k);
        String str = g12.f32n;
        if (str == null) {
            jSONObject.put("Ad Source Name", "null");
        } else {
            jSONObject.put("Ad Source Name", str);
        }
        String str2 = g12.f33o;
        if (str2 == null) {
            jSONObject.put("Ad Source ID", "null");
        } else {
            jSONObject.put("Ad Source ID", str2);
        }
        String str3 = g12.f34p;
        if (str3 == null) {
            jSONObject.put("Ad Source Instance Name", "null");
        } else {
            jSONObject.put("Ad Source Instance Name", str3);
        }
        String str4 = g12.f35q;
        if (str4 == null) {
            jSONObject.put("Ad Source Instance ID", "null");
        } else {
            jSONObject.put("Ad Source Instance ID", str4);
        }
        JSONObject jSONObject2 = new JSONObject();
        for (String str5 : g12.f31m.keySet()) {
            jSONObject2.put(str5, g12.f31m.get(str5));
        }
        jSONObject.put("Credentials", jSONObject2);
        gA gAVar = this.f5799b;
        if (gAVar == null) {
            jSONObject.put("Ad Error", "null");
        } else {
            jSONObject.put("Ad Error", gAVar.b());
        }
        return jSONObject;
    }

    public final String toString() {
        try {
            return a().toString(2);
        } catch (JSONException unused) {
            return "Error forming toString output.";
        }
    }
}
