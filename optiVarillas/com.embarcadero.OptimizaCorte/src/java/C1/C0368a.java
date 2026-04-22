package c1;

import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: c1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0368a {

    /* renamed from: a  reason: collision with root package name */
    public double f2934a = 0.0d;

    /* renamed from: b  reason: collision with root package name */
    public double f2935b = 0.0d;

    /* renamed from: c  reason: collision with root package name */
    public ArrayList<Double> f2936c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    public String f2937d = "";

    public final double a() {
        ArrayList<Double> arrayList = this.f2936c;
        double d4 = 0.0d;
        if (arrayList != null && !arrayList.isEmpty()) {
            Iterator<Double> it = this.f2936c.iterator();
            while (it.hasNext()) {
                d4 += it.next().doubleValue();
            }
        }
        double d5 = this.f2934a - d4;
        this.f2935b = d5;
        return d5;
    }

    public final String toString() {
        return String.format("BarraOptimizadaDetalles (medidaStock=%s, resto=%s, listaOptimizacion=%s, lineaOptimizadaFinal=%s)", Double.valueOf(this.f2934a), Double.valueOf(this.f2935b), this.f2936c, this.f2937d);
    }
}
