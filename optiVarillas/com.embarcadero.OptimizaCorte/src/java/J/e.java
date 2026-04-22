package J;

import android.util.Base64;
import java.util.List;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class e {

    /* renamed from: a  reason: collision with root package name */
    public final String f1158a;

    /* renamed from: b  reason: collision with root package name */
    public final String f1159b;

    /* renamed from: c  reason: collision with root package name */
    public final String f1160c;

    /* renamed from: d  reason: collision with root package name */
    public final List<List<byte[]>> f1161d;

    /* renamed from: e  reason: collision with root package name */
    public final String f1162e;

    public e(String str, String str2, String str3, List<List<byte[]>> list) {
        str.getClass();
        this.f1158a = str;
        str2.getClass();
        this.f1159b = str2;
        this.f1160c = str3;
        list.getClass();
        this.f1161d = list;
        this.f1162e = str + "-" + str2 + "-" + str3;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FontRequest {mProviderAuthority: " + this.f1158a + ", mProviderPackage: " + this.f1159b + ", mQuery: " + this.f1160c + ", mCertificates:");
        int i4 = 0;
        while (true) {
            List<List<byte[]>> list = this.f1161d;
            if (i4 < list.size()) {
                sb.append(" [");
                List<byte[]> list2 = list.get(i4);
                for (int i5 = 0; i5 < list2.size(); i5++) {
                    sb.append(" \"");
                    sb.append(Base64.encodeToString(list2.get(i5), 0));
                    sb.append("\"");
                }
                sb.append(" ]");
                i4++;
            } else {
                sb.append("}mCertificatesArray: 0");
                return sb.toString();
            }
        }
    }
}
