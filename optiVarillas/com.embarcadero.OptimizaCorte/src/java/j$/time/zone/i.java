package j$.time.zone;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class i extends j {

    /* renamed from: c  reason: collision with root package name */
    private final Set f4074c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public i() {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (String str : TimeZone.getAvailableIDs()) {
            linkedHashSet.add(str);
        }
        this.f4074c = Collections.unmodifiableSet(linkedHashSet);
    }

    @Override // j$.time.zone.j
    protected final f b(String str) {
        if (this.f4074c.contains(str)) {
            return new f(TimeZone.getTimeZone(str));
        }
        throw new RuntimeException("Not a built-in time zone: " + str);
    }

    @Override // j$.time.zone.j
    protected final Set c() {
        return this.f4074c;
    }
}
