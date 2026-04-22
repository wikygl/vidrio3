package j;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;

/* renamed from: j.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0649c extends ContextWrapper {
    public static Configuration f;

    /* renamed from: a  reason: collision with root package name */
    public int f4651a;

    /* renamed from: b  reason: collision with root package name */
    public Resources.Theme f4652b;

    /* renamed from: c  reason: collision with root package name */
    public LayoutInflater f4653c;

    /* renamed from: d  reason: collision with root package name */
    public Configuration f4654d;

    /* renamed from: e  reason: collision with root package name */
    public Resources f4655e;

    public C0649c() {
        super(null);
    }

    public final void a(Configuration configuration) {
        if (this.f4655e == null) {
            if (this.f4654d == null) {
                this.f4654d = new Configuration(configuration);
                return;
            }
            throw new IllegalStateException("Override configuration has already been set");
        }
        throw new IllegalStateException("getResources() or getAssets() has already been called");
    }

    @Override // android.content.ContextWrapper
    public final void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    public final void b() {
        if (this.f4652b == null) {
            this.f4652b = getResources().newTheme();
            Resources.Theme theme = getBaseContext().getTheme();
            if (theme != null) {
                this.f4652b.setTo(theme);
            }
        }
        this.f4652b.applyStyle(this.f4651a, true);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final AssetManager getAssets() {
        return getResources().getAssets();
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0022, code lost:
        if (r0.equals(j.C0649c.f) != false) goto L13;
     */
    @Override // android.content.ContextWrapper, android.content.Context
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final android.content.res.Resources getResources() {
        /*
            r3 = this;
            android.content.res.Resources r0 = r3.f4655e
            if (r0 != 0) goto L38
            android.content.res.Configuration r0 = r3.f4654d
            if (r0 == 0) goto L32
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 26
            if (r1 < r2) goto L25
            android.content.res.Configuration r1 = j.C0649c.f
            if (r1 != 0) goto L1c
            android.content.res.Configuration r1 = new android.content.res.Configuration
            r1.<init>()
            r2 = 0
            r1.fontScale = r2
            j.C0649c.f = r1
        L1c:
            android.content.res.Configuration r1 = j.C0649c.f
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L25
            goto L32
        L25:
            android.content.res.Configuration r0 = r3.f4654d
            android.content.Context r0 = r3.createConfigurationContext(r0)
            android.content.res.Resources r0 = r0.getResources()
            r3.f4655e = r0
            goto L38
        L32:
            android.content.res.Resources r0 = super.getResources()
            r3.f4655e = r0
        L38:
            android.content.res.Resources r0 = r3.f4655e
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j.C0649c.getResources():android.content.res.Resources");
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final Object getSystemService(String str) {
        if ("layout_inflater".equals(str)) {
            if (this.f4653c == null) {
                this.f4653c = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return this.f4653c;
        }
        return getBaseContext().getSystemService(str);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final Resources.Theme getTheme() {
        Resources.Theme theme = this.f4652b;
        if (theme != null) {
            return theme;
        }
        if (this.f4651a == 0) {
            this.f4651a = 2131886626;
        }
        b();
        return this.f4652b;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final void setTheme(int i4) {
        if (this.f4651a != i4) {
            this.f4651a = i4;
            b();
        }
    }

    public C0649c(Context context, int i4) {
        super(context);
        this.f4651a = i4;
    }
}
