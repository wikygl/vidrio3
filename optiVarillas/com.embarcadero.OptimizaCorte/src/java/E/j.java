package E;

import D.e;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontStyle;
import android.os.ParcelFileDescriptor;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class j extends l {
    public static Font f(FontFamily fontFamily, int i4) {
        int i5;
        int i6;
        if ((i4 & 1) != 0) {
            i5 = 700;
        } else {
            i5 = 400;
        }
        if ((i4 & 2) != 0) {
            i6 = 1;
        } else {
            i6 = 0;
        }
        FontStyle fontStyle = new FontStyle(i5, i6);
        Font font = fontFamily.getFont(0);
        int g4 = g(fontStyle, font.getStyle());
        for (int i7 = 1; i7 < fontFamily.getSize(); i7++) {
            Font font2 = fontFamily.getFont(i7);
            int g5 = g(fontStyle, font2.getStyle());
            if (g5 < g4) {
                font = font2;
                g4 = g5;
            }
        }
        return font;
    }

    public static int g(FontStyle fontStyle, FontStyle fontStyle2) {
        int i4;
        int abs = Math.abs(fontStyle.getWeight() - fontStyle2.getWeight()) / 100;
        if (fontStyle.getSlant() == fontStyle2.getSlant()) {
            i4 = 0;
        } else {
            i4 = 2;
        }
        return abs + i4;
    }

    @Override // E.l
    public final Typeface a(Context context, e.c cVar, Resources resources, int i4) {
        e.d[] dVarArr;
        try {
            FontFamily.Builder builder = null;
            for (e.d dVar : cVar.f517a) {
                try {
                    Font build = new Font.Builder(resources, dVar.f).setWeight(dVar.f519b).setSlant(dVar.f520c ? 1 : 0).setTtcIndex(dVar.f522e).setFontVariationSettings(dVar.f521d).build();
                    if (builder == null) {
                        builder = new FontFamily.Builder(build);
                    } else {
                        builder.addFont(build);
                    }
                } catch (IOException unused) {
                }
            }
            if (builder == null) {
                return null;
            }
            FontFamily build2 = builder.build();
            return new Typeface.CustomFallbackBuilder(build2).setStyle(f(build2, i4).getStyle()).build();
        } catch (Exception unused2) {
            return null;
        }
    }

    @Override // E.l
    public final Typeface b(Context context, J.l[] lVarArr, int i4) {
        ParcelFileDescriptor openFileDescriptor;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            FontFamily.Builder builder = null;
            for (J.l lVar : lVarArr) {
                try {
                    openFileDescriptor = contentResolver.openFileDescriptor(lVar.f1181a, "r", null);
                } catch (IOException unused) {
                }
                if (openFileDescriptor == null) {
                    if (openFileDescriptor == null) {
                    }
                } else {
                    try {
                        Font build = new Font.Builder(openFileDescriptor).setWeight(lVar.f1183c).setSlant(lVar.f1184d ? 1 : 0).setTtcIndex(lVar.f1182b).build();
                        if (builder == null) {
                            builder = new FontFamily.Builder(build);
                        } else {
                            builder.addFont(build);
                        }
                    } catch (Throwable th) {
                        try {
                            openFileDescriptor.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                        break;
                    }
                }
                openFileDescriptor.close();
            }
            if (builder == null) {
                return null;
            }
            FontFamily build2 = builder.build();
            return new Typeface.CustomFallbackBuilder(build2).setStyle(f(build2, i4).getStyle()).build();
        } catch (Exception unused2) {
            return null;
        }
    }

    @Override // E.l
    public final Typeface c(Context context, InputStream inputStream) {
        throw new RuntimeException("Do not use this function in API 29 or later.");
    }

    @Override // E.l
    public final Typeface d(Context context, Resources resources, int i4, String str, int i5) {
        try {
            Font build = new Font.Builder(resources, i4).build();
            return new Typeface.CustomFallbackBuilder(new FontFamily.Builder(build).build()).setStyle(build.getStyle()).build();
        } catch (Exception unused) {
            return null;
        }
    }

    @Override // E.l
    public final J.l e(int i4, J.l[] lVarArr) {
        throw new RuntimeException("Do not use this function in API 29 or later.");
    }
}
