package D0;

import android.content.Context;
import android.text.TextUtils;
import q0.InterfaceC0767b;
import r0.C0780b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class g implements InterfaceC0767b.c {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Context f579a;

    public g(Context context) {
        this.f579a = context;
    }

    @Override // q0.InterfaceC0767b.c
    public final InterfaceC0767b a(InterfaceC0767b.C0062b c0062b) {
        InterfaceC0767b.a aVar = c0062b.f5601c;
        if (aVar != null) {
            Context context = this.f579a;
            if (context != null) {
                String str = c0062b.f5600b;
                if (!TextUtils.isEmpty(str)) {
                    InterfaceC0767b.C0062b c0062b2 = new InterfaceC0767b.C0062b(context, str, aVar, true);
                    return new C0780b(c0062b2.f5599a, c0062b2.f5600b, c0062b2.f5601c, c0062b2.f5602d);
                }
                throw new IllegalArgumentException("Must set a non-null database name to a configuration that uses the no backup directory.");
            }
            throw new IllegalArgumentException("Must set a non-null context to create the configuration.");
        }
        throw new IllegalArgumentException("Must set a callback to create the configuration.");
    }
}
