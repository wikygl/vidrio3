package K1;

import android.os.Bundle;
import android.util.JsonReader;
import com.google.android.gms.internal.ads.Ei;
import com.google.android.gms.internal.ads.Gb;
import java.util.HashMap;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class o {

    /* renamed from: a  reason: collision with root package name */
    public final String f1387a;

    /* renamed from: b  reason: collision with root package name */
    public String f1388b;

    /* renamed from: c  reason: collision with root package name */
    public final Ei f1389c;

    /* renamed from: d  reason: collision with root package name */
    public final Bundle f1390d = new Bundle();

    /* renamed from: e  reason: collision with root package name */
    public final long f1391e;
    public final long f;

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public o(JsonReader jsonReader, Ei ei) {
        Ei ei2;
        Bundle bundle;
        char c4;
        this.f1391e = -1L;
        this.f = -1L;
        this.f1389c = ei;
        HashMap hashMap = new HashMap();
        jsonReader.beginObject();
        String str = "";
        while (jsonReader.hasNext()) {
            String nextName = jsonReader.nextName();
            nextName = nextName == null ? "" : nextName;
            switch (nextName.hashCode()) {
                case -1573145462:
                    if (nextName.equals("start_time")) {
                        c4 = 2;
                        break;
                    }
                    c4 = 65535;
                    break;
                case -995427962:
                    if (nextName.equals("params")) {
                        c4 = 0;
                        break;
                    }
                    c4 = 65535;
                    break;
                case -271442291:
                    if (nextName.equals("signal_dictionary")) {
                        c4 = 1;
                        break;
                    }
                    c4 = 65535;
                    break;
                case 1725551537:
                    if (nextName.equals("end_time")) {
                        c4 = 3;
                        break;
                    }
                    c4 = 65535;
                    break;
                default:
                    c4 = 65535;
                    break;
            }
            if (c4 != 0) {
                if (c4 != 1) {
                    if (c4 != 2) {
                        if (c4 != 3) {
                            jsonReader.skipValue();
                        } else {
                            this.f = jsonReader.nextLong();
                        }
                    } else {
                        this.f1391e = jsonReader.nextLong();
                    }
                } else {
                    hashMap = new HashMap();
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()) {
                        hashMap.put(jsonReader.nextName(), jsonReader.nextString());
                    }
                    jsonReader.endObject();
                }
            } else {
                str = jsonReader.nextString();
            }
        }
        this.f1387a = str;
        jsonReader.endObject();
        for (Map.Entry entry : hashMap.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                this.f1390d.putString((String) entry.getKey(), (String) entry.getValue());
            }
        }
        if (((Boolean) A1.r.f168d.f171c.a(Gb.R1)).booleanValue() && (ei2 = this.f1389c) != null && (bundle = ei2.v) != null) {
            bundle.putLong("get-signals-sdkcore-start", this.f1391e);
            this.f1389c.v.putLong("get-signals-sdkcore-end", this.f);
        }
    }
}
