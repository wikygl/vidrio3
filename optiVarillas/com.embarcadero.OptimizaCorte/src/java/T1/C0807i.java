package t1;

import com.google.android.gms.internal.ads.gA;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: t1.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0807i extends gA {

    /* renamed from: o  reason: collision with root package name */
    public final C0811m f5801o;

    public C0807i(int i4, String str, String str2, gA gAVar, C0811m c0811m) {
        super(i4, str, str2, gAVar);
        this.f5801o = c0811m;
    }

    public final JSONObject b() {
        JSONObject b4 = super.b();
        C0811m c0811m = this.f5801o;
        if (c0811m == null) {
            b4.put("Response Info", "null");
        } else {
            b4.put("Response Info", c0811m.a());
        }
        return b4;
    }

    public final String toString() {
        try {
            return b().toString(2);
        } catch (JSONException unused) {
            return "Error forming toString output.";
        }
    }
}
