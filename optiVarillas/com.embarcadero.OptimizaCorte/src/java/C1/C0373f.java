package c1;

import D1.l0;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import b1.C0353a;
import b1.C0354b;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/* renamed from: c1.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0373f {
    public static String a(String str) {
        StringBuilder sb = new StringBuilder();
        String replaceAll = str.replaceAll(" \\+ ", "@");
        String str2 = replaceAll.trim() + "@;";
        int i4 = 0;
        while (true) {
            int i5 = 1;
            while (i4 < str2.split("@").length - 1) {
                String str3 = str2.split("@")[i4];
                i4++;
                if (str3.equals(str2.split("@")[i4])) {
                    i5++;
                } else if (i5 > 1) {
                    sb.append(str3);
                    sb.append((char) 215);
                    sb.append(i5);
                    sb.append(" + ");
                } else {
                    sb.append(str3);
                    sb.append(" + ");
                }
            }
            return new StringBuilder(sb.substring(0, sb.lastIndexOf(" + "))).toString();
        }
    }

    public static synchronized ArrayList<b1.e> b(ArrayList<b1.e> arrayList) {
        ArrayList<b1.e> arrayList2;
        synchronized (C0373f.class) {
            try {
                arrayList2 = new ArrayList<>();
                if (arrayList != null) {
                    Iterator it = new HashSet(arrayList).iterator();
                    while (it.hasNext()) {
                        b1.e eVar = (b1.e) it.next();
                        eVar.f2916e = Collections.frequency(arrayList, eVar);
                        arrayList2.add(eVar);
                    }
                }
                arrayList2.trimToSize();
            } catch (Throwable th) {
                throw th;
            }
        }
        return arrayList2;
    }

    public static double c(double d4, double d5) {
        return Math.hypot(d4, Math.tan(Math.toRadians(90.0d - d5)) * d4);
    }

    public static boolean d(C0368a c0368a, ArrayList<Double> arrayList) {
        int i4 = 0;
        boolean z4 = false;
        while (i4 < c0368a.f2936c.size()) {
            ArrayList<Double> arrayList2 = c0368a.f2936c;
            if (Collections.frequency(arrayList, c0368a.f2936c.get(i4)) < Collections.frequency(arrayList2, arrayList2.get(i4))) {
                return false;
            }
            i4++;
            z4 = true;
        }
        return z4;
    }

    public static String e(String str) {
        String trim;
        if (str.contains("[")) {
            trim = str.substring(0, str.lastIndexOf("[")).trim();
        } else {
            trim = str.trim();
        }
        return trim.trim().replaceAll("[\ufeff-\uffff]", "").trim();
    }

    public static l0 f(double d4, ArrayList arrayList) {
        l0 l0Var = new l0(1);
        ArrayList arrayList2 = new ArrayList(arrayList);
        ArrayList arrayList3 = new ArrayList();
        int i4 = 0;
        while (i4 < arrayList2.size()) {
            if (((Double) arrayList2.get(i4)).doubleValue() > d4 || ((Double) arrayList2.get(i4)).doubleValue() == 0.0d) {
                arrayList3.add((Double) arrayList2.get(i4));
                arrayList2.remove(i4);
                i4--;
            }
            i4++;
        }
        l0Var.f736k = arrayList2;
        l0Var.f737l = arrayList3;
        return l0Var;
    }

    public static String g(double d4) {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#.#####");
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        return decimalFormat.format(d4);
    }

    public static double h(String str) {
        double parseInt;
        if (!str.matches("\\d+\\s+\\d+/\\d+") && !str.matches("\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException("Formato de pulgadas no válido");
        }
        if (str.contains("/") && str.contains(" ")) {
            int parseInt2 = Integer.parseInt(str.split("\\s+")[0]);
            String[] split = str.split("\\s+")[1].split("/");
            return (parseInt2 + (Integer.parseInt(split[0]) / Integer.parseInt(split[1]))) * 25.4d;
        }
        if (str.contains(".")) {
            parseInt = Double.parseDouble(str);
        } else {
            parseInt = Integer.parseInt(str);
        }
        return parseInt * 25.4d;
    }

    public static ArrayList<b1.c> i(ArrayList<C0354b> arrayList, C0353a c0353a) {
        ArrayList<C0354b> arrayList2 = arrayList;
        ArrayList<b1.c> arrayList3 = new ArrayList<>();
        if (!arrayList.isEmpty()) {
            char c4 = 6;
            char c5 = 0;
            if (c0353a.f2890q) {
                int i4 = 0;
                while (i4 < arrayList.size()) {
                    int parseInt = Integer.parseInt(arrayList2.get(i4).f2900c.split(" ")[0]);
                    double h4 = h(e(arrayList2.get(i4).f2899b));
                    int i5 = 0;
                    while (i5 < parseInt) {
                        arrayList3.add(new b1.c(arrayList2.get(i4).f2899b, h4, arrayList2.get(i4).b(Boolean.getBoolean(c0353a.f2892s.split("@")[c4])) + h4, arrayList2.get(i4).f));
                        i5++;
                        c4 = 6;
                    }
                    i4++;
                    c4 = 6;
                }
            } else {
                int i6 = 0;
                while (i6 < arrayList.size()) {
                    int parseInt2 = Integer.parseInt(arrayList2.get(i6).f2900c.split(" ")[c5]);
                    int i7 = 0;
                    while (i7 < parseInt2) {
                        arrayList3.add(new b1.c(arrayList2.get(i6).f2899b, Double.parseDouble(e(arrayList2.get(i6).f2899b.replaceAll(",", "."))), arrayList2.get(i6).b(Boolean.getBoolean(c0353a.f2892s.split("@")[6])) + Double.parseDouble(e(arrayList2.get(i6).f2899b.replaceAll(",", "."))), arrayList2.get(i6).f));
                        i7++;
                        arrayList2 = arrayList;
                    }
                    i6++;
                    c5 = 0;
                    arrayList2 = arrayList;
                }
            }
        }
        return arrayList3;
    }

    public static String j(double d4, long j4) {
        double d5 = d4 * 0.0393701d;
        double d6 = (int) d5;
        long round = Math.round((d5 - d6) * j4);
        if (round == 0) {
            return g(d6);
        }
        if (round / j4 == 1) {
            return String.valueOf(Integer.parseInt(g(d6)) + 1);
        }
        return g(d6) + " " + round + "/" + j4;
    }

    public static double k(int i4, ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        double d4 = 0.0d;
        while (it.hasNext()) {
            d4 += ((Double) it.next()).doubleValue() / i4;
        }
        return d4;
    }

    public static void l(long j4, Context context) {
        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= 31) {
            vibrator = L2.f.h(context.getSystemService("vibrator_manager")).getDefaultVibrator();
        } else {
            vibrator = (Vibrator) context.getSystemService("vibrator");
        }
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(j4);
        }
    }
}
