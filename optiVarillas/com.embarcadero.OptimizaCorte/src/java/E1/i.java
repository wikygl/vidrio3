package E1;

import android.util.JsonWriter;
import q0.InterfaceC0768c;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class i implements k, InterfaceC0768c {

    /* renamed from: j  reason: collision with root package name */
    public final String f867j;

    public /* synthetic */ i(String str) {
        this.f867j = str;
    }

    @Override // E1.k
    public void b(JsonWriter jsonWriter) {
        Object obj = l.f870b;
        jsonWriter.name("params").beginObject();
        String str = this.f867j;
        if (str != null) {
            jsonWriter.name("error_description").value(str);
        }
        jsonWriter.endObject();
    }

    @Override // q0.InterfaceC0768c
    public String d() {
        return this.f867j;
    }

    @Override // q0.InterfaceC0768c
    public void a(r0.d dVar) {
    }
}
