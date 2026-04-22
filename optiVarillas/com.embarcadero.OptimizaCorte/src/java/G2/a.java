package G2;

import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class a implements TypeEvaluator<Float> {

    /* renamed from: a  reason: collision with root package name */
    public FloatEvaluator f960a;

    @Override // android.animation.TypeEvaluator
    public final Float evaluate(float f, Float f4, Float f5) {
        float floatValue = this.f960a.evaluate(f, (Number) f4, (Number) f5).floatValue();
        if (floatValue < 0.1f) {
            floatValue = 0.0f;
        }
        return Float.valueOf(floatValue);
    }
}
