package E1;

import android.util.Base64;
import android.util.JsonWriter;
import com.google.android.gms.internal.ads.H9;
import com.google.android.gms.internal.ads.N9;
import com.google.android.gms.internal.ads.O9;
import com.google.android.gms.internal.ads.P9;
import com.google.android.gms.internal.ads.fb;
import com.google.android.gms.internal.ads.gb;
import com.google.android.gms.internal.ads.mB;
import com.google.android.gms.internal.ads.mG;
import com.google.android.gms.internal.ads.mH;
import com.google.android.gms.internal.ads.mb;
import com.google.android.gms.internal.ads.nU;
import com.google.android.gms.internal.ads.nb;
import com.google.android.gms.internal.ads.oa;
import com.google.android.gms.internal.ads.uG;
import com.google.android.gms.internal.ads.vR;
import com.google.android.gms.internal.ads.wR;
import com.google.android.gms.internal.ads.wz;
import com.google.android.gms.internal.ads.xR;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Map;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final /* synthetic */ class g implements k, H9, mH {

    /* renamed from: j  reason: collision with root package name */
    public Object f861j;

    /* renamed from: k  reason: collision with root package name */
    public Object f862k;

    /* renamed from: l  reason: collision with root package name */
    public Object f863l;

    /* renamed from: m  reason: collision with root package name */
    public Object f864m;

    public /* synthetic */ g() {
        this.f861j = null;
        this.f862k = null;
        this.f863l = null;
        this.f864m = wR.e;
    }

    public void a(int i4) {
        this.f861j = Integer.valueOf(i4);
    }

    @Override // E1.k
    public void b(JsonWriter jsonWriter) {
        jsonWriter.name("params").beginObject();
        jsonWriter.name("firstline").beginObject();
        jsonWriter.name("uri").value((String) this.f861j);
        jsonWriter.name("verb").value((String) this.f862k);
        jsonWriter.endObject();
        l.e(jsonWriter, (Map) this.f863l);
        byte[] bArr = (byte[]) this.f864m;
        if (bArr != null) {
            jsonWriter.name("body").value(Base64.encodeToString(bArr, 0));
        }
        jsonWriter.endObject();
    }

    public void c(mb mbVar) {
        O9 s4 = ((nU) mbVar).k.M().s();
        s4.k();
        P9.H(((nU) s4).k, (N9) this.f863l);
        mbVar.k();
        nb.H(((nU) mbVar).k, s4.i());
        fb s5 = ((nU) mbVar).k.N().s();
        s5.k();
        gb.G(((nU) s5).k, (String) this.f861j);
        s5.k();
        gb.D(((nU) s5).k, (oa) this.f864m);
        mbVar.k();
        nb.G(((nU) mbVar).k, s5.i());
        mbVar.k();
        nb.D(((nU) mbVar).k, (String) this.f862k);
    }

    public Object d(Object obj) {
        Void r4 = (Void) obj;
        return ((mB) this.f861j).b.b((uG) this.f862k, (mG) this.f863l, (wz) this.f864m);
    }

    public void e(int i4) {
        this.f862k = Integer.valueOf(i4);
    }

    public xR f() {
        Integer num = (Integer) this.f861j;
        if (num != null) {
            if (((Integer) this.f862k) != null) {
                if (((vR) this.f863l) != null) {
                    if (((wR) this.f864m) != null) {
                        if (num.intValue() >= 16) {
                            Integer num2 = (Integer) this.f862k;
                            int intValue = num2.intValue();
                            vR vRVar = (vR) this.f863l;
                            if (intValue >= 10) {
                                if (vRVar == vR.b) {
                                    if (intValue > 20) {
                                        throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 20 bytes for SHA1", num2));
                                    }
                                } else if (vRVar == vR.c) {
                                    if (intValue > 28) {
                                        throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 28 bytes for SHA224", num2));
                                    }
                                } else if (vRVar == vR.d) {
                                    if (intValue > 32) {
                                        throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 32 bytes for SHA256", num2));
                                    }
                                } else if (vRVar == vR.e) {
                                    if (intValue > 48) {
                                        throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 48 bytes for SHA384", num2));
                                    }
                                } else if (vRVar == vR.f) {
                                    if (intValue > 64) {
                                        throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; can be at most 64 bytes for SHA512", num2));
                                    }
                                } else {
                                    throw new GeneralSecurityException("unknown hash type; must be SHA256, SHA384 or SHA512");
                                }
                                return new xR(((Integer) this.f861j).intValue(), ((Integer) this.f862k).intValue(), (wR) this.f864m, (vR) this.f863l);
                            }
                            throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d; must be at least 10 bytes", num2));
                        }
                        throw new InvalidAlgorithmParameterException(String.format("Invalid key size in bytes %d; must be at least 16 bytes", (Integer) this.f861j));
                    }
                    throw new GeneralSecurityException("variant is not set");
                }
                throw new GeneralSecurityException("hash type is not set");
            }
            throw new GeneralSecurityException("tag size is not set");
        }
        throw new GeneralSecurityException("key size is not set");
    }

    public /* synthetic */ g(Object obj, Object obj2, Object obj3, Object obj4) {
        this.f861j = obj;
        this.f862k = obj2;
        this.f863l = obj3;
        this.f864m = obj4;
    }
}
