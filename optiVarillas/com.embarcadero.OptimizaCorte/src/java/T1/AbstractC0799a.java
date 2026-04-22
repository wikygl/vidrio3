package t1;

import A1.J0;
import android.os.Bundle;
import com.google.ads.mediation.admob.AdMobAdapter;
import t1.AbstractC0799a;
import t1.C0802d;

/* renamed from: t1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public abstract class AbstractC0799a<T extends AbstractC0799a<T>> {

    /* renamed from: a  reason: collision with root package name */
    public final J0 f5781a;

    public AbstractC0799a() {
        J0 j02 = new J0();
        this.f5781a = j02;
        j02.f41d.add("B3EEABB8EE11C2BE770B684D95219ECB");
    }

    public final C0802d.a a(Bundle bundle) {
        J0 j02 = this.f5781a;
        j02.getClass();
        j02.f39b.putBundle(AdMobAdapter.class.getName(), bundle);
        if (AdMobAdapter.class.equals(AdMobAdapter.class) && bundle.getBoolean("_emulatorLiveAds")) {
            j02.f41d.remove("B3EEABB8EE11C2BE770B684D95219ECB");
        }
        return (C0802d.a) this;
    }
}
