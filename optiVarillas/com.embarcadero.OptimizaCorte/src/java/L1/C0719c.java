package l1;

/* renamed from: l1.c  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0719c {

    /* renamed from: a  reason: collision with root package name */
    public final long f5246a;

    /* renamed from: b  reason: collision with root package name */
    public final a f5247b;

    /* renamed from: l1.c$a */
    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
    public enum a implements e3.c {
        f5248k("REASON_UNKNOWN"),
        f5249l("MESSAGE_TOO_OLD"),
        f5250m("CACHE_FULL"),
        f5251n("PAYLOAD_TOO_BIG"),
        f5252o("MAX_RETRIES_REACHED"),
        f5253p("INVALID_PAYLOD"),
        f5254q("SERVER_ERROR");
        

        /* renamed from: j  reason: collision with root package name */
        public final int f5256j;

        a(String str) {
            this.f5256j = r2;
        }

        @Override // e3.c
        public final int a() {
            return this.f5256j;
        }
    }

    public C0719c(long j4, a aVar) {
        this.f5246a = j4;
        this.f5247b = aVar;
    }
}
