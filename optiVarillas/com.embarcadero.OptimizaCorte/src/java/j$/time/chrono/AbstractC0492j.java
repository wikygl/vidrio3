package j$.time.chrono;

/* renamed from: j$.time.chrono.j  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
abstract /* synthetic */ class AbstractC0492j {

    /* renamed from: a  reason: collision with root package name */
    static final /* synthetic */ int[] f3883a;

    static {
        int[] iArr = new int[j$.time.temporal.a.values().length];
        f3883a = iArr;
        try {
            iArr[j$.time.temporal.a.INSTANT_SECONDS.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            f3883a[j$.time.temporal.a.OFFSET_SECONDS.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
    }
}
