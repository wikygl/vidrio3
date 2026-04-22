package E1;

import android.util.JsonWriter;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class j implements k {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f868j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Map f869k;

    public /* synthetic */ j(int i4, Map map) {
        this.f868j = i4;
        this.f869k = map;
    }

    @Override // E1.k
    public final void b(JsonWriter jsonWriter) {
        jsonWriter.name("params").beginObject();
        jsonWriter.name("firstline").beginObject();
        jsonWriter.name("code").value(this.f868j);
        jsonWriter.endObject();
        l.e(jsonWriter, this.f869k);
        jsonWriter.endObject();
    }
}
