package G1;

import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.internal.ads.gA;
import java.util.List;
import t1.C0812n;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class a {
    public abstract C0812n getSDKVersionInfo();

    public abstract C0812n getVersionInfo();

    public abstract void initialize(Context context, b bVar, List<B2.a> list);

    public void loadAppOpenAd(f fVar, c<Object, Object> cVar) {
        cVar.e(new gA(7, getClass().getSimpleName().concat(" does not support app open ads."), "com.google.android.gms.ads", (gA) null));
    }

    public void loadBannerAd(g gVar, c<Object, Object> cVar) {
        cVar.e(new gA(7, getClass().getSimpleName().concat(" does not support banner ads."), "com.google.android.gms.ads", (gA) null));
    }

    public void loadInterscrollerAd(g gVar, c<Object, Object> cVar) {
        cVar.e(new gA(7, getClass().getSimpleName().concat(" does not support interscroller ads."), "com.google.android.gms.ads", (gA) null));
    }

    public void loadInterstitialAd(i iVar, c<Object, Object> cVar) {
        cVar.e(new gA(7, getClass().getSimpleName().concat(" does not support interstitial ads."), "com.google.android.gms.ads", (gA) null));
    }

    @Deprecated
    public void loadNativeAd(k kVar, c<com.google.ads.mediation.a, Object> cVar) {
        cVar.e(new gA(7, getClass().getSimpleName().concat(" does not support native ads."), "com.google.android.gms.ads", (gA) null));
    }

    public void loadNativeAdMapper(k kVar, c<Object, Object> cVar) {
        throw new RemoteException("Method is not found");
    }

    public void loadRewardedAd(m mVar, c<Object, Object> cVar) {
        cVar.e(new gA(7, getClass().getSimpleName().concat(" does not support rewarded ads."), "com.google.android.gms.ads", (gA) null));
    }

    public void loadRewardedInterstitialAd(m mVar, c<Object, Object> cVar) {
        cVar.e(new gA(7, getClass().getSimpleName().concat(" does not support rewarded interstitial ads."), "com.google.android.gms.ads", (gA) null));
    }
}
