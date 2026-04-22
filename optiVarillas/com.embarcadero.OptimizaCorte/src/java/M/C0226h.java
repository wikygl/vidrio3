package M;

import android.content.ClipData;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContentInfo;
import java.util.Locale;

/* renamed from: M.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class C0226h {

    /* renamed from: a  reason: collision with root package name */
    public final e f1616a;

    /* renamed from: M.h$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class a implements b {

        /* renamed from: a  reason: collision with root package name */
        public final ContentInfo.Builder f1617a;

        public a(ClipData clipData, int i4) {
            this.f1617a = C0223e.e(clipData, i4);
        }

        @Override // M.C0226h.b
        public final C0226h a() {
            ContentInfo build;
            build = this.f1617a.build();
            return new C0226h(new d(build));
        }

        @Override // M.C0226h.b
        public final void b(Bundle bundle) {
            this.f1617a.setExtras(bundle);
        }

        @Override // M.C0226h.b
        public final void c(Uri uri) {
            this.f1617a.setLinkUri(uri);
        }

        @Override // M.C0226h.b
        public final void d(int i4) {
            this.f1617a.setFlags(i4);
        }
    }

    /* renamed from: M.h$b */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface b {
        C0226h a();

        void b(Bundle bundle);

        void c(Uri uri);

        void d(int i4);
    }

    /* renamed from: M.h$c */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class c implements b {

        /* renamed from: a  reason: collision with root package name */
        public ClipData f1618a;

        /* renamed from: b  reason: collision with root package name */
        public int f1619b;

        /* renamed from: c  reason: collision with root package name */
        public int f1620c;

        /* renamed from: d  reason: collision with root package name */
        public Uri f1621d;

        /* renamed from: e  reason: collision with root package name */
        public Bundle f1622e;

        @Override // M.C0226h.b
        public final C0226h a() {
            return new C0226h(new f(this));
        }

        @Override // M.C0226h.b
        public final void b(Bundle bundle) {
            this.f1622e = bundle;
        }

        @Override // M.C0226h.b
        public final void c(Uri uri) {
            this.f1621d = uri;
        }

        @Override // M.C0226h.b
        public final void d(int i4) {
            this.f1620c = i4;
        }
    }

    /* renamed from: M.h$d */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class d implements e {

        /* renamed from: a  reason: collision with root package name */
        public final ContentInfo f1623a;

        public d(ContentInfo contentInfo) {
            contentInfo.getClass();
            this.f1623a = G0.d.d(contentInfo);
        }

        @Override // M.C0226h.e
        public final ClipData a() {
            ClipData clip;
            clip = this.f1623a.getClip();
            return clip;
        }

        @Override // M.C0226h.e
        public final int b() {
            int flags;
            flags = this.f1623a.getFlags();
            return flags;
        }

        @Override // M.C0226h.e
        public final ContentInfo c() {
            return this.f1623a;
        }

        @Override // M.C0226h.e
        public final int d() {
            int source;
            source = this.f1623a.getSource();
            return source;
        }

        public final String toString() {
            return "ContentInfoCompat{" + this.f1623a + "}";
        }
    }

    /* renamed from: M.h$e */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
    public interface e {
        ClipData a();

        int b();

        ContentInfo c();

        int d();
    }

    /* renamed from: M.h$f */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static final class f implements e {

        /* renamed from: a  reason: collision with root package name */
        public final ClipData f1624a;

        /* renamed from: b  reason: collision with root package name */
        public final int f1625b;

        /* renamed from: c  reason: collision with root package name */
        public final int f1626c;

        /* renamed from: d  reason: collision with root package name */
        public final Uri f1627d;

        /* renamed from: e  reason: collision with root package name */
        public final Bundle f1628e;

        public f(c cVar) {
            ClipData clipData = cVar.f1618a;
            clipData.getClass();
            this.f1624a = clipData;
            int i4 = cVar.f1619b;
            if (i4 >= 0) {
                if (i4 <= 5) {
                    this.f1625b = i4;
                    int i5 = cVar.f1620c;
                    if ((i5 & 1) == i5) {
                        this.f1626c = i5;
                        this.f1627d = cVar.f1621d;
                        this.f1628e = cVar.f1622e;
                        return;
                    }
                    throw new IllegalArgumentException("Requested flags 0x" + Integer.toHexString(i5) + ", but only 0x" + Integer.toHexString(1) + " are allowed");
                }
                Locale locale = Locale.US;
                throw new IllegalArgumentException("source is out of range of [0, 5] (too high)");
            }
            Locale locale2 = Locale.US;
            throw new IllegalArgumentException("source is out of range of [0, 5] (too low)");
        }

        @Override // M.C0226h.e
        public final ClipData a() {
            return this.f1624a;
        }

        @Override // M.C0226h.e
        public final int b() {
            return this.f1626c;
        }

        @Override // M.C0226h.e
        public final ContentInfo c() {
            return null;
        }

        @Override // M.C0226h.e
        public final int d() {
            return this.f1625b;
        }

        public final String toString() {
            String str;
            String valueOf;
            Uri uri;
            String str2;
            StringBuilder sb = new StringBuilder("ContentInfoCompat{clip=");
            sb.append(this.f1624a.getDescription());
            sb.append(", source=");
            int i4 = this.f1625b;
            if (i4 != 0) {
                if (i4 != 1) {
                    if (i4 != 2) {
                        if (i4 != 3) {
                            if (i4 != 4) {
                                if (i4 != 5) {
                                    str = String.valueOf(i4);
                                } else {
                                    str = "SOURCE_PROCESS_TEXT";
                                }
                            } else {
                                str = "SOURCE_AUTOFILL";
                            }
                        } else {
                            str = "SOURCE_DRAG_AND_DROP";
                        }
                    } else {
                        str = "SOURCE_INPUT_METHOD";
                    }
                } else {
                    str = "SOURCE_CLIPBOARD";
                }
            } else {
                str = "SOURCE_APP";
            }
            sb.append(str);
            sb.append(", flags=");
            int i5 = this.f1626c;
            if ((i5 & 1) != 0) {
                valueOf = "FLAG_CONVERT_TO_PLAIN_TEXT";
            } else {
                valueOf = String.valueOf(i5);
            }
            sb.append(valueOf);
            String str3 = "";
            if (this.f1627d == null) {
                str2 = "";
            } else {
                str2 = ", hasLinkUri(" + uri.toString().length() + ")";
            }
            sb.append(str2);
            if (this.f1628e != null) {
                str3 = ", hasExtras";
            }
            return C.b.c(sb, str3, "}");
        }
    }

    public C0226h(e eVar) {
        this.f1616a = eVar;
    }

    public final String toString() {
        return this.f1616a.toString();
    }
}
