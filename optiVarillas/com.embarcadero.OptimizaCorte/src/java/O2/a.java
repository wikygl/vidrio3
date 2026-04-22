package O2;

import android.graphics.Paint;
import android.graphics.Path;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class a {

    /* renamed from: i  reason: collision with root package name */
    public static final int[] f1798i = new int[3];

    /* renamed from: j  reason: collision with root package name */
    public static final float[] f1799j = {0.0f, 0.5f, 1.0f};

    /* renamed from: k  reason: collision with root package name */
    public static final int[] f1800k = new int[4];

    /* renamed from: l  reason: collision with root package name */
    public static final float[] f1801l = {0.0f, 0.0f, 0.5f, 1.0f};

    /* renamed from: a  reason: collision with root package name */
    public final Paint f1802a;

    /* renamed from: b  reason: collision with root package name */
    public final Paint f1803b;

    /* renamed from: c  reason: collision with root package name */
    public final Paint f1804c;

    /* renamed from: d  reason: collision with root package name */
    public int f1805d;

    /* renamed from: e  reason: collision with root package name */
    public int f1806e;
    public int f;

    /* renamed from: g  reason: collision with root package name */
    public final Path f1807g = new Path();

    /* renamed from: h  reason: collision with root package name */
    public final Paint f1808h;

    public a() {
        Paint paint = new Paint();
        this.f1808h = paint;
        Paint paint2 = new Paint();
        this.f1802a = paint2;
        this.f1805d = E.a.d(-16777216, 68);
        this.f1806e = E.a.d(-16777216, 20);
        this.f = E.a.d(-16777216, 0);
        paint2.setColor(this.f1805d);
        paint.setColor(0);
        Paint paint3 = new Paint(4);
        this.f1803b = paint3;
        paint3.setStyle(Paint.Style.FILL);
        this.f1804c = new Paint(paint3);
    }
}
