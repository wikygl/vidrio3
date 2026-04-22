package K1;

import A1.W0;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class r extends G3.g {

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ String f1397k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ C0207a f1398l;

    public r(C0207a c0207a, String str) {
        this.f1397k = str;
        this.f1398l = c0207a;
    }

    @Override // G3.g
    public final void E(M1.a aVar) {
        String format;
        String str = this.f1397k;
        W0 w02 = aVar.f1725a;
        String str2 = w02.f97j;
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("paw_id", str);
            jSONObject.put("signal", str2);
            format = String.format("window.postMessage(%1$s, '*');", jSONObject);
        } catch (JSONException unused) {
            format = String.format("window.postMessage({'paw_id': '%1$s', 'signal': '%2$s'}, '*');", str, w02.f97j);
        }
        this.f1398l.f1315b.evaluateJavascript(format, null);
    }

    @Override // G3.g
    public final void z(String str) {
        E1.m.g("Failed to generate query info for the tagging library, error: ".concat(String.valueOf(str)));
        this.f1398l.f1315b.evaluateJavascript(String.format("window.postMessage({'paw_id': '%1$s', 'error': '%2$s'}, '*');", this.f1397k, str), null);
    }
}
