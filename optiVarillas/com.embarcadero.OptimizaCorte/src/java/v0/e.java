package v0;

import D.i;
import E.d;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.animation.AnimationUtils;
import java.util.ArrayList;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class e {

    /* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
    public static class a implements TypeEvaluator<d.a[]> {

        /* renamed from: a  reason: collision with root package name */
        public d.a[] f6223a;

        @Override // android.animation.TypeEvaluator
        public final d.a[] evaluate(float f, d.a[] aVarArr, d.a[] aVarArr2) {
            d.a[] aVarArr3 = aVarArr;
            d.a[] aVarArr4 = aVarArr2;
            if (E.d.a(aVarArr3, aVarArr4)) {
                if (!E.d.a(this.f6223a, aVarArr3)) {
                    this.f6223a = E.d.e(aVarArr3);
                }
                for (int i4 = 0; i4 < aVarArr3.length; i4++) {
                    d.a aVar = this.f6223a[i4];
                    d.a aVar2 = aVarArr3[i4];
                    d.a aVar3 = aVarArr4[i4];
                    aVar.getClass();
                    aVar.f808a = aVar2.f808a;
                    int i5 = 0;
                    while (true) {
                        float[] fArr = aVar2.f809b;
                        if (i5 < fArr.length) {
                            aVar.f809b[i5] = (aVar3.f809b[i5] * f) + ((1.0f - f) * fArr[i5]);
                            i5++;
                        }
                    }
                }
                return this.f6223a;
            }
            throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:200:0x039c, code lost:
        if (r31 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:201:0x039e, code lost:
        if (r13 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:202:0x03a0, code lost:
        r1 = new android.animation.Animator[r13.size()];
        r3 = r13.iterator();
        r14 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:204:0x03af, code lost:
        if (r3.hasNext() == false) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:205:0x03b1, code lost:
        r1[r14] = (android.animation.Animator) r3.next();
        r14 = r14 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:206:0x03bd, code lost:
        if (r32 != 0) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x03bf, code lost:
        r31.playTogether(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:208:0x03c3, code lost:
        r31.playSequentially(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x03c6, code lost:
        return r0;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.animation.Animator a(android.content.Context r26, android.content.res.Resources r27, android.content.res.Resources.Theme r28, android.content.res.XmlResourceParser r29, android.util.AttributeSet r30, android.animation.AnimatorSet r31, int r32) {
        /*
            Method dump skipped, instructions count: 967
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: v0.e.a(android.content.Context, android.content.res.Resources, android.content.res.Resources$Theme, android.content.res.XmlResourceParser, android.util.AttributeSet, android.animation.AnimatorSet, int):android.animation.Animator");
    }

    /* JADX WARN: Type inference failed for: r11v26, types: [android.animation.TypeEvaluator, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v3, types: [android.animation.TypeEvaluator, java.lang.Object] */
    public static PropertyValuesHolder b(TypedArray typedArray, int i4, int i5, int i6, String str) {
        boolean z4;
        int i7;
        boolean z5;
        int i8;
        boolean z6;
        f fVar;
        int i9;
        int i10;
        int i11;
        float f;
        PropertyValuesHolder ofFloat;
        float f4;
        float f5;
        PropertyValuesHolder ofObject;
        TypedValue peekValue = typedArray.peekValue(i5);
        if (peekValue != null) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (z4) {
            i7 = peekValue.type;
        } else {
            i7 = 0;
        }
        TypedValue peekValue2 = typedArray.peekValue(i6);
        if (peekValue2 != null) {
            z5 = true;
        } else {
            z5 = false;
        }
        if (z5) {
            i8 = peekValue2.type;
        } else {
            i8 = 0;
        }
        if (i4 == 4) {
            if ((z4 && c(i7)) || (z5 && c(i8))) {
                i4 = 3;
            } else {
                i4 = 0;
            }
        }
        if (i4 == 0) {
            z6 = true;
        } else {
            z6 = false;
        }
        PropertyValuesHolder propertyValuesHolder = null;
        if (i4 == 2) {
            String string = typedArray.getString(i5);
            String string2 = typedArray.getString(i6);
            d.a[] c4 = E.d.c(string);
            d.a[] c5 = E.d.c(string2);
            if (c4 == null && c5 == null) {
                return null;
            }
            if (c4 != null) {
                ?? obj = new Object();
                if (c5 != null) {
                    if (E.d.a(c4, c5)) {
                        ofObject = PropertyValuesHolder.ofObject(str, (TypeEvaluator) obj, c4, c5);
                    } else {
                        throw new InflateException(" Can't morph from " + string + " to " + string2);
                    }
                } else {
                    ofObject = PropertyValuesHolder.ofObject(str, (TypeEvaluator) obj, c4);
                }
                return ofObject;
            } else if (c5 == null) {
                return null;
            } else {
                return PropertyValuesHolder.ofObject(str, (TypeEvaluator) new Object(), c5);
            }
        }
        if (i4 == 3) {
            fVar = f.f6224a;
        } else {
            fVar = null;
        }
        if (z6) {
            if (z4) {
                if (i7 == 5) {
                    f4 = typedArray.getDimension(i5, 0.0f);
                } else {
                    f4 = typedArray.getFloat(i5, 0.0f);
                }
                if (z5) {
                    if (i8 == 5) {
                        f5 = typedArray.getDimension(i6, 0.0f);
                    } else {
                        f5 = typedArray.getFloat(i6, 0.0f);
                    }
                    ofFloat = PropertyValuesHolder.ofFloat(str, f4, f5);
                } else {
                    ofFloat = PropertyValuesHolder.ofFloat(str, f4);
                }
            } else {
                if (i8 == 5) {
                    f = typedArray.getDimension(i6, 0.0f);
                } else {
                    f = typedArray.getFloat(i6, 0.0f);
                }
                ofFloat = PropertyValuesHolder.ofFloat(str, f);
            }
            propertyValuesHolder = ofFloat;
        } else if (z4) {
            if (i7 == 5) {
                i10 = (int) typedArray.getDimension(i5, 0.0f);
            } else if (c(i7)) {
                i10 = typedArray.getColor(i5, 0);
            } else {
                i10 = typedArray.getInt(i5, 0);
            }
            if (z5) {
                if (i8 == 5) {
                    i11 = (int) typedArray.getDimension(i6, 0.0f);
                } else if (c(i8)) {
                    i11 = typedArray.getColor(i6, 0);
                } else {
                    i11 = typedArray.getInt(i6, 0);
                }
                propertyValuesHolder = PropertyValuesHolder.ofInt(str, i10, i11);
            } else {
                propertyValuesHolder = PropertyValuesHolder.ofInt(str, i10);
            }
        } else if (z5) {
            if (i8 == 5) {
                i9 = (int) typedArray.getDimension(i6, 0.0f);
            } else if (c(i8)) {
                i9 = typedArray.getColor(i6, 0);
            } else {
                i9 = typedArray.getInt(i6, 0);
            }
            propertyValuesHolder = PropertyValuesHolder.ofInt(str, i9);
        }
        if (propertyValuesHolder != null && fVar != null) {
            propertyValuesHolder.setEvaluator(fVar);
            return propertyValuesHolder;
        }
        return propertyValuesHolder;
    }

    public static boolean c(int i4) {
        if (i4 >= 28 && i4 <= 31) {
            return true;
        }
        return false;
    }

    public static ValueAnimator d(Context context, Resources resources, Resources.Theme theme, AttributeSet attributeSet, ObjectAnimator objectAnimator, XmlResourceParser xmlResourceParser) {
        ValueAnimator valueAnimator;
        int i4;
        int i5;
        int i6;
        int i7;
        ValueAnimator valueAnimator2;
        TypedArray typedArray;
        TypedArray typedArray2;
        ValueAnimator valueAnimator3;
        PropertyValuesHolder propertyValuesHolder;
        PropertyValuesHolder propertyValuesHolder2;
        boolean z4;
        int i8;
        boolean z5;
        int i9;
        int i10 = 0;
        int i11 = 1;
        TypedArray d4 = i.d(resources, theme, attributeSet, C0828a.f6204g);
        TypedArray d5 = i.d(resources, theme, attributeSet, C0828a.f6208k);
        if (objectAnimator == null) {
            valueAnimator = new ValueAnimator();
        } else {
            valueAnimator = objectAnimator;
        }
        int i12 = 300;
        if (i.c(xmlResourceParser, "duration")) {
            i12 = d4.getInt(1, 300);
        }
        long j4 = i12;
        if (!i.c(xmlResourceParser, "startOffset")) {
            i4 = 0;
        } else {
            i4 = d4.getInt(2, 0);
        }
        long j5 = i4;
        if (!i.c(xmlResourceParser, "valueType")) {
            i5 = 4;
        } else {
            i5 = d4.getInt(7, 4);
        }
        if (i.c(xmlResourceParser, "valueFrom") && i.c(xmlResourceParser, "valueTo")) {
            if (i5 == 4) {
                TypedValue peekValue = d4.peekValue(5);
                if (peekValue != null) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                if (z4) {
                    i8 = peekValue.type;
                } else {
                    i8 = 0;
                }
                TypedValue peekValue2 = d4.peekValue(6);
                if (peekValue2 != null) {
                    z5 = true;
                } else {
                    z5 = false;
                }
                if (z5) {
                    i9 = peekValue2.type;
                } else {
                    i9 = 0;
                }
                if ((z4 && c(i8)) || (z5 && c(i9))) {
                    i5 = 3;
                } else {
                    i5 = 0;
                }
            }
            PropertyValuesHolder b4 = b(d4, i5, 5, 6, "");
            if (b4 != null) {
                valueAnimator.setValues(b4);
            }
        }
        valueAnimator.setDuration(j4);
        valueAnimator.setStartDelay(j5);
        if (!i.c(xmlResourceParser, "repeatCount")) {
            i6 = 0;
        } else {
            i6 = d4.getInt(3, 0);
        }
        valueAnimator.setRepeatCount(i6);
        if (!i.c(xmlResourceParser, "repeatMode")) {
            i7 = 1;
        } else {
            i7 = d4.getInt(4, 1);
        }
        valueAnimator.setRepeatMode(i7);
        if (d5 != null) {
            ObjectAnimator objectAnimator2 = (ObjectAnimator) valueAnimator;
            String b5 = i.b(d5, xmlResourceParser, "pathData", 1);
            if (b5 != null) {
                String b6 = i.b(d5, xmlResourceParser, "propertyXName", 2);
                String b7 = i.b(d5, xmlResourceParser, "propertyYName", 3);
                if (b6 == null && b7 == null) {
                    throw new InflateException(d5.getPositionDescription() + " propertyXName or propertyYName is needed for PathData");
                }
                Path d6 = E.d.d(b5);
                PathMeasure pathMeasure = new PathMeasure(d6, false);
                ArrayList arrayList = new ArrayList();
                arrayList.add(Float.valueOf(0.0f));
                float f = 0.0f;
                while (true) {
                    f += pathMeasure.getLength();
                    arrayList.add(Float.valueOf(f));
                    if (!pathMeasure.nextContour()) {
                        break;
                    }
                    valueAnimator = valueAnimator;
                    i11 = 1;
                }
                PathMeasure pathMeasure2 = new PathMeasure(d6, false);
                int min = Math.min(100, ((int) (f / 0.5f)) + i11);
                float[] fArr = new float[min];
                float[] fArr2 = new float[min];
                float[] fArr3 = new float[2];
                float f4 = f / (min - 1);
                valueAnimator2 = valueAnimator;
                typedArray = d4;
                int i13 = 0;
                int i14 = 0;
                float f5 = 0.0f;
                while (true) {
                    propertyValuesHolder = null;
                    if (i14 >= min) {
                        break;
                    }
                    int i15 = min;
                    pathMeasure2.getPosTan(f5 - ((Float) arrayList.get(i13)).floatValue(), fArr3, null);
                    fArr[i14] = fArr3[0];
                    fArr2[i14] = fArr3[1];
                    f5 += f4;
                    int i16 = i13 + 1;
                    if (i16 < arrayList.size() && f5 > ((Float) arrayList.get(i16)).floatValue()) {
                        pathMeasure2.nextContour();
                        i13 = i16;
                    }
                    i14++;
                    min = i15;
                }
                if (b6 != null) {
                    propertyValuesHolder2 = PropertyValuesHolder.ofFloat(b6, fArr);
                } else {
                    propertyValuesHolder2 = null;
                }
                if (b7 != null) {
                    propertyValuesHolder = PropertyValuesHolder.ofFloat(b7, fArr2);
                }
                if (propertyValuesHolder2 == null) {
                    i10 = 0;
                    objectAnimator2.setValues(propertyValuesHolder);
                } else {
                    i10 = 0;
                    if (propertyValuesHolder == null) {
                        objectAnimator2.setValues(propertyValuesHolder2);
                    } else {
                        objectAnimator2.setValues(propertyValuesHolder2, propertyValuesHolder);
                    }
                }
            } else {
                valueAnimator2 = valueAnimator;
                typedArray = d4;
                objectAnimator2.setPropertyName(i.b(d5, xmlResourceParser, "propertyName", 0));
            }
        } else {
            valueAnimator2 = valueAnimator;
            typedArray = d4;
        }
        if (!i.c(xmlResourceParser, "interpolator")) {
            typedArray2 = typedArray;
        } else {
            typedArray2 = typedArray;
            i10 = typedArray2.getResourceId(i10, i10);
        }
        if (i10 > 0) {
            valueAnimator3 = valueAnimator2;
            valueAnimator3.setInterpolator(AnimationUtils.loadInterpolator(context, i10));
        } else {
            valueAnimator3 = valueAnimator2;
        }
        typedArray2.recycle();
        if (d5 != null) {
            d5.recycle();
        }
        return valueAnimator3;
    }
}
