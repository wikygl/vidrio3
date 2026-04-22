package g1;

import A3.d;
import f1.C0412b;
import i1.l;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/* renamed from: g1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0423a implements l {

    /* renamed from: c  reason: collision with root package name */
    public static final String f3479c;

    /* renamed from: d  reason: collision with root package name */
    public static final Set<C0412b> f3480d;

    /* renamed from: e  reason: collision with root package name */
    public static final C0423a f3481e;

    /* renamed from: a  reason: collision with root package name */
    public final String f3482a;

    /* renamed from: b  reason: collision with root package name */
    public final String f3483b;

    static {
        String h4 = d.h("hts/frbslgiggolai.o/0clgbthfra=snpoo", "tp:/ieaeogn.ogepscmvc/o/ac?omtjo_rt3");
        f3479c = h4;
        d.h("hts/frbslgigp.ogepscmv/ieo/eaybtho", "tp:/ieaeogn-agolai.o/1frlglgc/aclg");
        d.h("AzSCki82AwsLzKd5O8zo", "IayckHiZRO1EFl1aGoK");
        f3480d = Collections.unmodifiableSet(new HashSet(Arrays.asList(new C0412b("proto"), new C0412b("json"))));
        f3481e = new C0423a(h4, null);
    }

    public C0423a(String str, String str2) {
        this.f3482a = str;
        this.f3483b = str2;
    }

    public static C0423a c(byte[] bArr) {
        String str = new String(bArr, Charset.forName("UTF-8"));
        if (str.startsWith("1$")) {
            String[] split = str.substring(2).split(Pattern.quote("\\"), 2);
            if (split.length == 2) {
                String str2 = split[0];
                if (!str2.isEmpty()) {
                    String str3 = split[1];
                    if (str3.isEmpty()) {
                        str3 = null;
                    }
                    return new C0423a(str2, str3);
                }
                throw new IllegalArgumentException("Missing endpoint in CCTDestination extras");
            }
            throw new IllegalArgumentException("Extra is not a valid encoded LegacyFlgDestination");
        }
        throw new IllegalArgumentException("Version marker missing from extras");
    }

    @Override // i1.l
    public final Set<C0412b> a() {
        return f3480d;
    }

    @Override // i1.l
    public final byte[] b() {
        String str = this.f3482a;
        String str2 = this.f3483b;
        if (str2 == null && str == null) {
            return null;
        }
        if (str2 == null) {
            str2 = "";
        }
        return ("1$" + str + "\\" + str2).getBytes(Charset.forName("UTF-8"));
    }
}
