package s0;

import android.content.Context;
import android.os.Bundle;
import android.os.Trace;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import t0.C0798a;

/* renamed from: s0.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0793a {

    /* renamed from: d  reason: collision with root package name */
    public static volatile C0793a f5756d;

    /* renamed from: e  reason: collision with root package name */
    public static final Object f5757e = new Object();

    /* renamed from: c  reason: collision with root package name */
    public final Context f5760c;

    /* renamed from: b  reason: collision with root package name */
    public final HashSet f5759b = new HashSet();

    /* renamed from: a  reason: collision with root package name */
    public final HashMap f5758a = new HashMap();

    public C0793a(Context context) {
        this.f5760c = context.getApplicationContext();
    }

    public static C0793a c(Context context) {
        if (f5756d == null) {
            synchronized (f5757e) {
                try {
                    if (f5756d == null) {
                        f5756d = new C0793a(context);
                    }
                } finally {
                }
            }
        }
        return f5756d;
    }

    public final void a(Bundle bundle) {
        HashSet hashSet;
        String string = this.f5760c.getString(2131820574);
        if (bundle != null) {
            try {
                HashSet hashSet2 = new HashSet();
                Iterator<String> it = bundle.keySet().iterator();
                while (true) {
                    boolean hasNext = it.hasNext();
                    hashSet = this.f5759b;
                    if (!hasNext) {
                        break;
                    }
                    String next = it.next();
                    if (string.equals(bundle.getString(next, null))) {
                        Class<?> cls = Class.forName(next);
                        if (InterfaceC0794b.class.isAssignableFrom(cls)) {
                            hashSet.add(cls);
                        }
                    }
                }
                Iterator it2 = hashSet.iterator();
                while (it2.hasNext()) {
                    b((Class) it2.next(), hashSet2);
                }
            } catch (ClassNotFoundException e4) {
                throw new RuntimeException(e4);
            }
        }
    }

    public final Object b(Class cls, HashSet hashSet) {
        Object obj;
        if (C0798a.a()) {
            try {
                Trace.beginSection(cls.getSimpleName());
            } catch (Throwable th) {
                Trace.endSection();
                throw th;
            }
        }
        if (!hashSet.contains(cls)) {
            HashMap hashMap = this.f5758a;
            if (!hashMap.containsKey(cls)) {
                hashSet.add(cls);
                InterfaceC0794b interfaceC0794b = (InterfaceC0794b) cls.getDeclaredConstructor(null).newInstance(null);
                List<Class<? extends InterfaceC0794b<?>>> a4 = interfaceC0794b.a();
                if (!a4.isEmpty()) {
                    for (Class<? extends InterfaceC0794b<?>> cls2 : a4) {
                        if (!hashMap.containsKey(cls2)) {
                            b(cls2, hashSet);
                        }
                    }
                }
                obj = interfaceC0794b.b(this.f5760c);
                hashSet.remove(cls);
                hashMap.put(cls, obj);
            } else {
                obj = hashMap.get(cls);
            }
            Trace.endSection();
            return obj;
        }
        String name = cls.getName();
        throw new IllegalStateException("Cannot initialize " + name + ". Cycle detected.");
    }
}
