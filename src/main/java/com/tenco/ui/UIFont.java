package com.tenco.ui;

import java.awt.Font;

/**
 * 플랫폼별 한글 지원 폰트를 제공하는 유틸리티 클래스.
 * macOS: "Apple SD Gothic Neo", Windows: "Malgun Gothic" 우선 사용.
 */
public final class UIFont {

    private static final String FAMILY;

    static {
        String[] candidates = {
            "Apple SD Gothic Neo",  // macOS
            "Malgun Gothic",        // Windows
            "NanumGothic",          // Linux
            "SansSerif"
        };
        String found = "SansSerif";
        for (String name : candidates) {
            Font f = new Font(name, Font.PLAIN, 12);
            if (f.canDisplay('가')) { found = name; break; }
        }
        FAMILY = found;
    }

    private UIFont() {}

    public static Font plain(int size)  { return new Font(FAMILY, Font.PLAIN,  size); }
    public static Font bold(int size)   { return new Font(FAMILY, Font.BOLD,   size); }
}