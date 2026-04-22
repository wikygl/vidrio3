package c;

import android.content.Context;
import android.content.Intent;
import v3.h;

/* renamed from: c.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public abstract class AbstractC0363a<I, O> {

    /* renamed from: c.a$a  reason: collision with other inner class name */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static final class C0039a<T> {

        /* renamed from: a  reason: collision with root package name */
        public final T f2929a;

        public C0039a(T t3) {
            this.f2929a = t3;
        }
    }

    public abstract Intent a(Context context, I i4);

    public C0039a<O> b(Context context, I i4) {
        h.e(context, "context");
        return null;
    }

    public abstract O c(int i4, Intent intent);
}
