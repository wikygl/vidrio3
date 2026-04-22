package l;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public class r {

    /* renamed from: c  reason: collision with root package name */
    public static final int[] f5192c = {16843067, 16843068};

    /* renamed from: a  reason: collision with root package name */
    public final ProgressBar f5193a;

    /* renamed from: b  reason: collision with root package name */
    public Bitmap f5194b;

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
    public static class a {
        public static void a(LayerDrawable layerDrawable, LayerDrawable layerDrawable2, int i4) {
            layerDrawable2.setLayerGravity(i4, layerDrawable.getLayerGravity(i4));
            layerDrawable2.setLayerWidth(i4, layerDrawable.getLayerWidth(i4));
            layerDrawable2.setLayerHeight(i4, layerDrawable.getLayerHeight(i4));
            layerDrawable2.setLayerInsetLeft(i4, layerDrawable.getLayerInsetLeft(i4));
            layerDrawable2.setLayerInsetRight(i4, layerDrawable.getLayerInsetRight(i4));
            layerDrawable2.setLayerInsetTop(i4, layerDrawable.getLayerInsetTop(i4));
            layerDrawable2.setLayerInsetBottom(i4, layerDrawable.getLayerInsetBottom(i4));
            layerDrawable2.setLayerInsetStart(i4, layerDrawable.getLayerInsetStart(i4));
            layerDrawable2.setLayerInsetEnd(i4, layerDrawable.getLayerInsetEnd(i4));
        }
    }

    public r(ProgressBar progressBar) {
        this.f5193a = progressBar;
    }

    public void a(AttributeSet attributeSet, int i4) {
        ProgressBar progressBar = this.f5193a;
        b0 e4 = b0.e(progressBar.getContext(), attributeSet, f5192c, i4, 0);
        Drawable c4 = e4.c(0);
        if (c4 != null) {
            if (c4 instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) c4;
                int numberOfFrames = animationDrawable.getNumberOfFrames();
                AnimationDrawable animationDrawable2 = new AnimationDrawable();
                animationDrawable2.setOneShot(animationDrawable.isOneShot());
                for (int i5 = 0; i5 < numberOfFrames; i5++) {
                    Drawable b4 = b(animationDrawable.getFrame(i5), true);
                    b4.setLevel(10000);
                    animationDrawable2.addFrame(b4, animationDrawable.getDuration(i5));
                }
                animationDrawable2.setLevel(10000);
                c4 = animationDrawable2;
            }
            progressBar.setIndeterminateDrawable(c4);
        }
        Drawable c5 = e4.c(1);
        if (c5 != null) {
            progressBar.setProgressDrawable(b(c5, false));
        }
        e4.f();
    }

    public final Drawable b(Drawable drawable, boolean z4) {
        boolean z5;
        if (drawable instanceof F.c) {
            F.c cVar = (F.c) drawable;
            Drawable b4 = cVar.b();
            if (b4 != null) {
                cVar.a(b(b4, z4));
            }
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            int numberOfLayers = layerDrawable.getNumberOfLayers();
            Drawable[] drawableArr = new Drawable[numberOfLayers];
            for (int i4 = 0; i4 < numberOfLayers; i4++) {
                int id = layerDrawable.getId(i4);
                Drawable drawable2 = layerDrawable.getDrawable(i4);
                if (id != 16908301 && id != 16908303) {
                    z5 = false;
                } else {
                    z5 = true;
                }
                drawableArr[i4] = b(drawable2, z5);
            }
            LayerDrawable layerDrawable2 = new LayerDrawable(drawableArr);
            for (int i5 = 0; i5 < numberOfLayers; i5++) {
                layerDrawable2.setId(i5, layerDrawable.getId(i5));
                if (Build.VERSION.SDK_INT >= 23) {
                    a.a(layerDrawable, layerDrawable2, i5);
                }
            }
            return layerDrawable2;
        } else if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (this.f5194b == null) {
                this.f5194b = bitmap;
            }
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f}, null, null));
            shapeDrawable.getPaint().setShader(new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP));
            shapeDrawable.getPaint().setColorFilter(bitmapDrawable.getPaint().getColorFilter());
            if (z4) {
                return new ClipDrawable(shapeDrawable, 3, 1);
            }
            return shapeDrawable;
        }
        return drawable;
    }
}
