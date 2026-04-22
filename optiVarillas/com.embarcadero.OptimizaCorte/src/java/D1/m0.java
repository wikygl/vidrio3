package D1;

import android.content.Context;
import android.content.SharedPreferences;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final /* synthetic */ class m0 implements SharedPreferences.OnSharedPreferenceChangeListener {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ t0 f739a;

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f740b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ String f741c;

    public /* synthetic */ m0(t0 t0Var, Context context, String str) {
        this.f739a = t0Var;
        this.f740b = context;
        this.f741c = str;
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public final void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        this.f739a.f777c.set(C0182d.a(this.f740b, this.f741c));
    }
}
