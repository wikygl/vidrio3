package h1;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class h extends AbstractC0434a {

    /* renamed from: a  reason: collision with root package name */
    public final Integer f3541a;

    /* renamed from: b  reason: collision with root package name */
    public final String f3542b;

    /* renamed from: c  reason: collision with root package name */
    public final String f3543c;

    /* renamed from: d  reason: collision with root package name */
    public final String f3544d;

    /* renamed from: e  reason: collision with root package name */
    public final String f3545e;
    public final String f;

    /* renamed from: g  reason: collision with root package name */
    public final String f3546g;

    /* renamed from: h  reason: collision with root package name */
    public final String f3547h;

    /* renamed from: i  reason: collision with root package name */
    public final String f3548i;

    /* renamed from: j  reason: collision with root package name */
    public final String f3549j;

    /* renamed from: k  reason: collision with root package name */
    public final String f3550k;

    /* renamed from: l  reason: collision with root package name */
    public final String f3551l;

    public h(Integer num, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11) {
        this.f3541a = num;
        this.f3542b = str;
        this.f3543c = str2;
        this.f3544d = str3;
        this.f3545e = str4;
        this.f = str5;
        this.f3546g = str6;
        this.f3547h = str7;
        this.f3548i = str8;
        this.f3549j = str9;
        this.f3550k = str10;
        this.f3551l = str11;
    }

    @Override // h1.AbstractC0434a
    public final String a() {
        return this.f3551l;
    }

    @Override // h1.AbstractC0434a
    public final String b() {
        return this.f3549j;
    }

    @Override // h1.AbstractC0434a
    public final String c() {
        return this.f3544d;
    }

    @Override // h1.AbstractC0434a
    public final String d() {
        return this.f3547h;
    }

    @Override // h1.AbstractC0434a
    public final String e() {
        return this.f3543c;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractC0434a)) {
            return false;
        }
        AbstractC0434a abstractC0434a = (AbstractC0434a) obj;
        Integer num = this.f3541a;
        if (num != null ? num.equals(abstractC0434a.l()) : abstractC0434a.l() == null) {
            String str = this.f3542b;
            if (str != null ? str.equals(abstractC0434a.i()) : abstractC0434a.i() == null) {
                String str2 = this.f3543c;
                if (str2 != null ? str2.equals(abstractC0434a.e()) : abstractC0434a.e() == null) {
                    String str3 = this.f3544d;
                    if (str3 != null ? str3.equals(abstractC0434a.c()) : abstractC0434a.c() == null) {
                        String str4 = this.f3545e;
                        if (str4 != null ? str4.equals(abstractC0434a.k()) : abstractC0434a.k() == null) {
                            String str5 = this.f;
                            if (str5 != null ? str5.equals(abstractC0434a.j()) : abstractC0434a.j() == null) {
                                String str6 = this.f3546g;
                                if (str6 != null ? str6.equals(abstractC0434a.g()) : abstractC0434a.g() == null) {
                                    String str7 = this.f3547h;
                                    if (str7 != null ? str7.equals(abstractC0434a.d()) : abstractC0434a.d() == null) {
                                        String str8 = this.f3548i;
                                        if (str8 != null ? str8.equals(abstractC0434a.f()) : abstractC0434a.f() == null) {
                                            String str9 = this.f3549j;
                                            if (str9 != null ? str9.equals(abstractC0434a.b()) : abstractC0434a.b() == null) {
                                                String str10 = this.f3550k;
                                                if (str10 != null ? str10.equals(abstractC0434a.h()) : abstractC0434a.h() == null) {
                                                    String str11 = this.f3551l;
                                                    if (str11 == null) {
                                                        if (abstractC0434a.a() == null) {
                                                            return true;
                                                        }
                                                    } else if (str11.equals(abstractC0434a.a())) {
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override // h1.AbstractC0434a
    public final String f() {
        return this.f3548i;
    }

    @Override // h1.AbstractC0434a
    public final String g() {
        return this.f3546g;
    }

    @Override // h1.AbstractC0434a
    public final String h() {
        return this.f3550k;
    }

    public final int hashCode() {
        int hashCode;
        int hashCode2;
        int hashCode3;
        int hashCode4;
        int hashCode5;
        int hashCode6;
        int hashCode7;
        int hashCode8;
        int hashCode9;
        int hashCode10;
        int hashCode11;
        int i4 = 0;
        Integer num = this.f3541a;
        if (num == null) {
            hashCode = 0;
        } else {
            hashCode = num.hashCode();
        }
        int i5 = (hashCode ^ 1000003) * 1000003;
        String str = this.f3542b;
        if (str == null) {
            hashCode2 = 0;
        } else {
            hashCode2 = str.hashCode();
        }
        int i6 = (i5 ^ hashCode2) * 1000003;
        String str2 = this.f3543c;
        if (str2 == null) {
            hashCode3 = 0;
        } else {
            hashCode3 = str2.hashCode();
        }
        int i7 = (i6 ^ hashCode3) * 1000003;
        String str3 = this.f3544d;
        if (str3 == null) {
            hashCode4 = 0;
        } else {
            hashCode4 = str3.hashCode();
        }
        int i8 = (i7 ^ hashCode4) * 1000003;
        String str4 = this.f3545e;
        if (str4 == null) {
            hashCode5 = 0;
        } else {
            hashCode5 = str4.hashCode();
        }
        int i9 = (i8 ^ hashCode5) * 1000003;
        String str5 = this.f;
        if (str5 == null) {
            hashCode6 = 0;
        } else {
            hashCode6 = str5.hashCode();
        }
        int i10 = (i9 ^ hashCode6) * 1000003;
        String str6 = this.f3546g;
        if (str6 == null) {
            hashCode7 = 0;
        } else {
            hashCode7 = str6.hashCode();
        }
        int i11 = (i10 ^ hashCode7) * 1000003;
        String str7 = this.f3547h;
        if (str7 == null) {
            hashCode8 = 0;
        } else {
            hashCode8 = str7.hashCode();
        }
        int i12 = (i11 ^ hashCode8) * 1000003;
        String str8 = this.f3548i;
        if (str8 == null) {
            hashCode9 = 0;
        } else {
            hashCode9 = str8.hashCode();
        }
        int i13 = (i12 ^ hashCode9) * 1000003;
        String str9 = this.f3549j;
        if (str9 == null) {
            hashCode10 = 0;
        } else {
            hashCode10 = str9.hashCode();
        }
        int i14 = (i13 ^ hashCode10) * 1000003;
        String str10 = this.f3550k;
        if (str10 == null) {
            hashCode11 = 0;
        } else {
            hashCode11 = str10.hashCode();
        }
        int i15 = (i14 ^ hashCode11) * 1000003;
        String str11 = this.f3551l;
        if (str11 != null) {
            i4 = str11.hashCode();
        }
        return i4 ^ i15;
    }

    @Override // h1.AbstractC0434a
    public final String i() {
        return this.f3542b;
    }

    @Override // h1.AbstractC0434a
    public final String j() {
        return this.f;
    }

    @Override // h1.AbstractC0434a
    public final String k() {
        return this.f3545e;
    }

    @Override // h1.AbstractC0434a
    public final Integer l() {
        return this.f3541a;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder("AndroidClientInfo{sdkVersion=");
        sb.append(this.f3541a);
        sb.append(", model=");
        sb.append(this.f3542b);
        sb.append(", hardware=");
        sb.append(this.f3543c);
        sb.append(", device=");
        sb.append(this.f3544d);
        sb.append(", product=");
        sb.append(this.f3545e);
        sb.append(", osBuild=");
        sb.append(this.f);
        sb.append(", manufacturer=");
        sb.append(this.f3546g);
        sb.append(", fingerprint=");
        sb.append(this.f3547h);
        sb.append(", locale=");
        sb.append(this.f3548i);
        sb.append(", country=");
        sb.append(this.f3549j);
        sb.append(", mccMnc=");
        sb.append(this.f3550k);
        sb.append(", applicationBuild=");
        return C.b.c(sb, this.f3551l, "}");
    }
}
