package D;

import android.content.res.ColorStateList;
import android.graphics.Shader;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class d {

    /* renamed from: a  reason: collision with root package name */
    public final Shader f514a;

    /* renamed from: b  reason: collision with root package name */
    public final ColorStateList f515b;

    /* renamed from: c  reason: collision with root package name */
    public int f516c;

    public d(Shader shader, ColorStateList colorStateList, int i4) {
        this.f514a = shader;
        this.f515b = colorStateList;
        this.f516c = i4;
    }

    /* JADX WARN: Code restructure failed: missing block: B:85:0x01c8, code lost:
        throw new org.xmlpull.v1.XmlPullParserException(r3.getPositionDescription() + ": <item> tag requires a 'color' attribute and a 'offset' attribute!");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static D.d a(android.content.res.Resources r29, int r30, android.content.res.Resources.Theme r31) {
        /*
            Method dump skipped, instructions count: 659
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: D.d.a(android.content.res.Resources, int, android.content.res.Resources$Theme):D.d");
    }

    public final boolean b() {
        ColorStateList colorStateList;
        if (this.f514a == null && (colorStateList = this.f515b) != null && colorStateList.isStateful()) {
            return true;
        }
        return false;
    }
}
