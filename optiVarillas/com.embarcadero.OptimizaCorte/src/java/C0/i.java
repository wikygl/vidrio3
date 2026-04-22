package C0;

import android.util.Log;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public abstract class i {

    /* renamed from: a  reason: collision with root package name */
    public static i f322a;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a extends i {

        /* renamed from: b  reason: collision with root package name */
        public final int f323b;

        public a(int i4) {
            this.f323b = i4;
        }

        @Override // C0.i
        public final void a(String str, String str2, Throwable... thArr) {
            if (this.f323b <= 3) {
                if (thArr.length >= 1) {
                    Log.d(str, str2, thArr[0]);
                } else {
                    Log.d(str, str2);
                }
            }
        }

        @Override // C0.i
        public final void b(String str, String str2, Throwable... thArr) {
            if (this.f323b <= 6) {
                if (thArr.length >= 1) {
                    Log.e(str, str2, thArr[0]);
                } else {
                    Log.e(str, str2);
                }
            }
        }

        @Override // C0.i
        public final void d(String str, String str2, Throwable... thArr) {
            if (this.f323b <= 4) {
                if (thArr.length >= 1) {
                    Log.i(str, str2, thArr[0]);
                } else {
                    Log.i(str, str2);
                }
            }
        }

        @Override // C0.i
        public final void f(String str, String str2, Throwable... thArr) {
            if (this.f323b <= 5) {
                if (thArr.length >= 1) {
                    Log.w(str, str2, thArr[0]);
                } else {
                    Log.w(str, str2);
                }
            }
        }
    }

    public static synchronized i c() {
        i iVar;
        synchronized (i.class) {
            try {
                if (f322a == null) {
                    f322a = new a(3);
                }
                iVar = f322a;
            } catch (Throwable th) {
                throw th;
            }
        }
        return iVar;
    }

    public static String e(String str) {
        int length = str.length();
        StringBuilder sb = new StringBuilder(23);
        sb.append("WM-");
        if (length >= 20) {
            sb.append(str.substring(0, 20));
        } else {
            sb.append(str);
        }
        return sb.toString();
    }

    public abstract void a(String str, String str2, Throwable... thArr);

    public abstract void b(String str, String str2, Throwable... thArr);

    public abstract void d(String str, String str2, Throwable... thArr);

    public abstract void f(String str, String str2, Throwable... thArr);
}
