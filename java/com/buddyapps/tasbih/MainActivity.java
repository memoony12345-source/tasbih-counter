package com.buddyapps.tasbih;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * مسبحة: عدّاد تسبيح بسيط وخفيف.
 * واجهة مبنية بالكود، بلا مكتبات خارجية.
 */
public class MainActivity extends Activity {

    private static final String[] DHIKR = {
            "سُبْحَانَ اللَّه",
            "الْحَمْدُ لِلَّه",
            "اللَّهُ أَكْبَر",
            "لَا إِلَهَ إِلَّا اللَّه",
            "أَسْتَغْفِرُ اللَّهَ"
    };

    private static final int TARGET = 33;

    private static final int BG_TOP = 0xFF07231F;
    private static final int BG_BOTTOM = 0xFF0B3B34;
    private static final int ACCENT = 0xFF2BB673;
    private static final int GOLD = 0xFFFFD166;
    private static final int TEXT = 0xFFEAFBF4;
    private static final int MUTED = 0xFF9CC9BC;
    private static final int DARK = 0xFF06201C;

    private SharedPreferences prefs;
    private int count;
    private int rounds;
    private int index;

    private TextView counterView;
    private TextView dhikrView;
    private TextView infoView;
    private TextView circleView;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        prefs = getSharedPreferences("tasbih", MODE_PRIVATE);
        count = prefs.getInt("count", 0);
        rounds = prefs.getInt("rounds", 0);
        index = prefs.getInt("index", 0);
        if (index < 0 || index >= DHIKR.length) index = 0;
        if (count < 0 || count > TARGET) count = 0;

        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG_TOP);
        scroll.addView(buildUi(), new android.widget.FrameLayout.LayoutParams(
                -1, -1));
        setContentView(scroll);
        update();
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        root.setPadding(dp(24), dp(28), dp(24), dp(24));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{BG_TOP, BG_BOTTOM}));

        TextView title = text("مسبحة", 17, MUTED, false);
        title.setLetterSpacing(0.25f);
        root.addView(title, lp(-2, -2, 0));

        dhikrView = text(DHIKR[index], 27, TEXT, true);
        dhikrView.setGravity(Gravity.CENTER);
        dhikrView.setLineSpacing(dp(6), 1f);
        dhikrView.setMinHeight(dp(60));
        root.addView(dhikrView, lp(-1, -2, 28));

        counterView = text("0", 84, TEXT, true);
        counterView.setIncludeFontPadding(false);
        counterView.setGravity(Gravity.CENTER);
        root.addView(counterView, lp(-1, -2, 12));

        infoView = text("", 14, MUTED, false);
        infoView.setGravity(Gravity.CENTER);
        root.addView(infoView, lp(-1, -2, 10));

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(TARGET);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(ACCENT));
        progress.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x33FFFFFF));
        root.addView(progress, lp(-1, dp(6), 22));

        circleView = text("سبّح", 22, DARK, true);
        circleView.setGravity(Gravity.CENTER);
        circleView.setClickable(true);
        circleView.setFocusable(true);
        circleView.setContentDescription("اضغط للتسبيح");
        circleView.setBackground(oval(ACCENT));
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tap();
            }
        });
        LinearLayout.LayoutParams circleLp =
                new LinearLayout.LayoutParams(dp(180), dp(180));
        circleLp.topMargin = dp(26);
        root.addView(circleView, circleLp);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(-1, -2);
        rowLp.topMargin = dp(26);
        root.addView(row, rowLp);

        TextView reset = text("تصفير", 16, TEXT, false);
        reset.setGravity(Gravity.CENTER);
        reset.setPadding(0, dp(13), 0, dp(13));
        reset.setBackground(outline(0x66FFFFFF));
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                buzz(18);
                update();
            }
        });
        LinearLayout.LayoutParams halfLp =
                new LinearLayout.LayoutParams(0, -2, 1f);
        halfLp.setMarginStart(0);
        halfLp.setMarginEnd(dp(5));
        row.addView(reset, halfLp);

        TextView next = text("الذكر التالي", 16, TEXT, false);
        next.setGravity(Gravity.CENTER);
        next.setPadding(0, dp(13), 0, dp(13));
        next.setBackground(outline(0x66FFFFFF));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = (index + 1) % DHIKR.length;
                count = 0;
                buzz(18);
                update();
            }
        });
        LinearLayout.LayoutParams halfLp2 =
                new LinearLayout.LayoutParams(0, -2, 1f);
        halfLp2.setMarginStart(dp(5));
        row.addView(next, halfLp2);

        return root;
    }

    private void tap() {
        count++;
        if (count > TARGET) count = 1;
        if (count == TARGET) {
            rounds++;
            buzz(60);
        } else {
            buzz(12);
        }
        circleView.animate().scaleX(0.93f).scaleY(0.93f).setDuration(60)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        circleView.animate().scaleX(1f).scaleY(1f)
                                .setDuration(90).start();
                    }
                }).start();
        update();
    }

    private void update() {
        counterView.setText(String.valueOf(count));
        dhikrView.setText(DHIKR[index]);
        infoView.setText("الهدف " + TARGET + "  ·  الدورة " + rounds);
        progress.setProgress(count);

        boolean done = count == TARGET;
        circleView.setText(done ? "أحسنت ✓" : "سبّح");
        circleView.setBackground(oval(done ? GOLD : ACCENT));
        circleView.setTextColor(done ? DARK : DARK);
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.edit()
                .putInt("count", count)
                .putInt("rounds", rounds)
                .putInt("index", index)
                .apply();
    }

    private void buzz(int ms) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null || !v.hasVibrator()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(ms);
        }
    }

    private TextView text(String s, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        return t;
    }

    private GradientDrawable oval(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(color);
        return d;
    }

    private GradientDrawable outline(int strokeColor) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(dp(14));
        d.setColor(Color.TRANSPARENT);
        d.setStroke(dp(1), strokeColor);
        return d;
    }

    private LinearLayout.LayoutParams lp(int width, int height, int topMarginDp) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(width, height);
        p.topMargin = dp(topMarginDp);
        return p;
    }

    private int dp(float v) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }
}
