package S1;

import W1.C0324l;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a {

    /* renamed from: c  reason: collision with root package name */
    public static final ReentrantLock f2302c = new ReentrantLock();

    /* renamed from: d  reason: collision with root package name */
    public static a f2303d;

    /* renamed from: a  reason: collision with root package name */
    public final ReentrantLock f2304a = new ReentrantLock();

    /* renamed from: b  reason: collision with root package name */
    public final SharedPreferences f2305b;

    public a(Context context) {
        this.f2305b = context.getSharedPreferences("com.google.android.gms.signin", 0);
    }

    public static a a(Context context) {
        C0324l.d(context);
        ReentrantLock reentrantLock = f2302c;
        reentrantLock.lock();
        try {
            if (f2303d == null) {
                f2303d = new a(context.getApplicationContext());
            }
            a aVar = f2303d;
            reentrantLock.unlock();
            return aVar;
        } catch (Throwable th) {
            reentrantLock.unlock();
            throw th;
        }
    }

    public final String b(String str) {
        ReentrantLock reentrantLock = this.f2304a;
        reentrantLock.lock();
        try {
            return this.f2305b.getString(str, null);
        } finally {
            reentrantLock.unlock();
        }
    }
}
