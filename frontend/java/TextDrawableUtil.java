package com.simats.foodstall;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class TextDrawableUtil {

    // A list of nice, soft colors for the backgrounds
    private static final int[] MATERIAL_COLORS = {
            0xFFF06292, 0xFFBA68C8, 0xFF9575CD, 0xFF7986CB, 0xFF64B5F6,
            0xFF4FC3F7, 0xFF4DD0E1, 0xFF4DB6AC, 0xFF81C784, 0xFFAED581,
            0xFFFF8A65, 0xFFFFB74D, 0xFFFFD54F, 0xFFA1887F, 0xFF90A4AE
    };

    public static Drawable getInitialDrawable(String text) {
        if (text == null || text.trim().isEmpty()) {
            text = "?";
        }

        String initials = getInitials(text);
        int color = getColor(text);

        // Create a bitmap and canvas to draw on
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the colored circle background
        Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(color);
        canvas.drawCircle(50, 50, 50, backgroundPaint);

        // Draw the initials text in the center
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Adjust text position for perfect centering
        float yPos = 50 - ((textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(initials, 50, yPos, textPaint);

        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    // UPDATED: Changed from 'private' to 'public'
    public static String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";

        String[] words = name.trim().split("\\s+");
        if (words.length == 0) return "?";

        if (words.length == 1) {
            return words[0].substring(0, Math.min(words[0].length(), 2)).toUpperCase();
        } else {
            String first = String.valueOf(words[0].charAt(0));
            String last = String.valueOf(words[words.length - 1].charAt(0));
            return (first + last).toUpperCase();
        }
    }

    // UPDATED: Changed from 'private' to 'public'
    public static int getColor(String key) {
        // Generate a consistent color based on the stall name
        if (key == null) return MATERIAL_COLORS[0];
        return MATERIAL_COLORS[Math.abs(key.hashCode()) % MATERIAL_COLORS.length];
    }
}