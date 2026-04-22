package j$.time.zone;

import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract class j {

    /* renamed from: a  reason: collision with root package name */
    private static final CopyOnWriteArrayList f4075a;

    /* renamed from: b  reason: collision with root package name */
    private static final ConcurrentHashMap f4076b;

    static {
        CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
        f4075a = copyOnWriteArrayList;
        f4076b = new ConcurrentHashMap(512, 0.75f, 2);
        ArrayList arrayList = new ArrayList();
        AccessController.doPrivileged(new h(arrayList));
        copyOnWriteArrayList.addAll(arrayList);
    }

    public static f a(String str, boolean z4) {
        Objects.requireNonNull(str, "zoneId");
        ConcurrentHashMap concurrentHashMap = f4076b;
        j jVar = (j) concurrentHashMap.get(str);
        if (jVar == null) {
            if (concurrentHashMap.isEmpty()) {
                throw new RuntimeException("No time-zone data files registered");
            }
            throw new RuntimeException("Unknown time-zone ID: " + str);
        }
        return jVar.b(str);
    }

    public static void d(j jVar) {
        Objects.requireNonNull(jVar, "provider");
        synchronized (j.class) {
            try {
                for (String str : jVar.c()) {
                    Objects.requireNonNull(str, "zoneId");
                    if (((j) f4076b.putIfAbsent(str, jVar)) != null) {
                        throw new RuntimeException("Unable to register zone as one already registered with that ID: " + str + ", currently loading from provider: " + jVar);
                    }
                }
                Collections.unmodifiableSet(new HashSet(f4076b.keySet()));
            } catch (Throwable th) {
                throw th;
            }
        }
        f4075a.add(jVar);
    }

    protected abstract f b(String str);

    protected abstract Set c();
}
