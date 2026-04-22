package i0;

import android.adservices.topics.GetTopicsRequest;
import android.annotation.SuppressLint;

@SuppressLint({"NewApi", "ClassVerificationFailure"})
/* renamed from: i0.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0450f extends C0452h {
    @Override // i0.C0452h
    public final GetTopicsRequest F(C0445a c0445a) {
        GetTopicsRequest.Builder adsSdkName;
        GetTopicsRequest.Builder shouldRecordObservation;
        GetTopicsRequest build;
        v3.h.e(c0445a, "request");
        adsSdkName = N2.a.a().setAdsSdkName(c0445a.f3593a);
        shouldRecordObservation = adsSdkName.setShouldRecordObservation(c0445a.f3594b);
        build = shouldRecordObservation.build();
        v3.h.d(build, "Builder()\n            .s…ion)\n            .build()");
        return build;
    }
}
