package A1;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

/* renamed from: A1.p  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0124p {
    public static final C0124p f = new C0124p();

    /* renamed from: a  reason: collision with root package name */
    public final E1.f f161a;

    /* renamed from: b  reason: collision with root package name */
    public final C0120n f162b;

    /* renamed from: c  reason: collision with root package name */
    public final String f163c;

    /* renamed from: d  reason: collision with root package name */
    public final E1.a f164d;

    /* renamed from: e  reason: collision with root package name */
    public final Random f165e;

    /* JADX WARN: Type inference failed for: r0v0, types: [E1.f, java.lang.Object] */
    public C0124p() {
        ?? obj = new Object();
        obj.f860a = -1.0f;
        C0120n c0120n = new C0120n(new c2.c("com.google.android.gms.ads.AdManagerCreatorImpl"), new c2.c("com.google.android.gms.ads.AdLoaderBuilderCreatorImpl"), new c2.c("com.google.android.gms.ads.MobileAdsSettingManagerCreatorImpl"), new c2.c("com.google.android.gms.ads.NativeAdViewDelegateCreatorImpl"), new c2.c("com.google.android.gms.ads.AdOverlayCreatorImpl"));
        UUID randomUUID = UUID.randomUUID();
        byte[] byteArray = BigInteger.valueOf(randomUUID.getLeastSignificantBits()).toByteArray();
        byte[] byteArray2 = BigInteger.valueOf(randomUUID.getMostSignificantBits()).toByteArray();
        String bigInteger = new BigInteger(1, byteArray).toString();
        for (int i4 = 0; i4 < 2; i4++) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(byteArray);
                messageDigest.update(byteArray2);
                byte[] bArr = new byte[8];
                System.arraycopy(messageDigest.digest(), 0, bArr, 0, 8);
                bigInteger = new BigInteger(1, bArr).toString();
            } catch (NoSuchAlgorithmException unused) {
            }
        }
        E1.a aVar = new E1.a(0, 241199000, true, false);
        Random random = new Random();
        this.f161a = obj;
        this.f162b = c0120n;
        this.f163c = bigInteger;
        this.f164d = aVar;
        this.f165e = random;
    }
}
