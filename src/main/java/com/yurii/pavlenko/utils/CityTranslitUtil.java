//package com.yurii.pavlenko.utils;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Utility class for converting Cyrillic city names to English text using transliteration.
// */
//public class CityTranslitUtil {
//
//    private static final Map<Character, String> TRANSLIT_MAP = new HashMap<>();
//
//    static {
//        String[] cyr = {"А","Б","В","Г","Д","Е","Ё","Ж","З","И","Й","К","Л","М","Н","О","П","Р","С","Т","У","Ф","Х","Ц","Ч","Ш","Щ","Ъ","Ы","Ь","Э","Ю","Я","а","б","в","г","д","е","ё","ж","з","и","й","к","л","м","н","о","п","р","с","т","у","ф","х","ц","ч","ш","щ","ъ","ы","ь","э","ю","я"};
//        String[] lat = {"A","B","V","G","D","E","E","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F","Kh","Ts","Ch","Sh","Shch","","Y","","E","Yu","Ya","a","b","v","g","d","e","e","zh","z","i","y","k","l","m","n","o","p","r","s","t","u","f","kh","ts","ch","sh","shch","","y","","e","yu","ya"};
//        for (int i = 0; i < cyr.length; i++) {
//            TRANSLIT_MAP.put(cyr[i].charAt(0), lat[i]);
//        }
//    }
//
//    /**
//     * Translates or transliterates a city name into English.
//     */
//    public static String convertToEnglishText(String src) {
//        if (src == null || src.isEmpty()) {
//            return src;
//        }
//
//        String lower = src.trim().toLowerCase();
//        switch (lower) {
//            case "лондон" -> { return "London"; }
//            case "париж" -> { return "Paris"; }
//            case "киев", "київ" -> { return "Kyiv"; }
//            case "харьков", "харків" -> { return "Kharkiv"; }
//            case "москва" -> { return "Moscow"; }
//            case "ришон ле-цион", "ришон лецион", "ришон", "Rishon" -> { return "Rishon LeZion"; }
//        }
//
//        StringBuilder sb = new StringBuilder(src.length());
//        for (int i = 0; i < src.length(); i++) {
//            char ch = src.charAt(i);
//            String replacement = TRANSLIT_MAP.get(ch);
//            if (replacement != null) {
//                sb.append(replacement);
//            } else {
//                sb.append(ch);
//            }
//        }
//        return sb.toString();
//    }
//}

package com.yurii.pavlenko.utils;

import java.util.HashMap;
import java.util.Map;

public class CityTranslitUtil {

    private static final Map<Character, String> TRANSLIT_MAP = new HashMap<>();

    static {
        String cyr = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        String[] lat = {"A","B","V","G","D","E","E","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F","Kh","Ts","Ch","Sh","Shch","","Y","","E","Yu","Ya","a","b","v","g","d","e","e","zh","z","i","y","k","l","m","n","o","p","r","s","t","u","f","kh","ts","ch","sh","shch","","y","","e","yu","ya"};

        for (int i = 0; i < cyr.length(); i++) {
            TRANSLIT_MAP.put(cyr.charAt(i), lat[i]);
        }
        TRANSLIT_MAP.put('і', "i");
        TRANSLIT_MAP.put('І', "I");
    }

    public static String convertToEnglishText(String src) {
        if (src == null || src.isEmpty()) {
            return src;
        }

        String lower = src.trim().toLowerCase();

        if (lower.contains("лондон")) return "London";
        if (lower.contains("париж")) return "Paris";
        if (lower.contains("київ") || lower.contains("киев")) return "Kyiv";
        if (lower.contains("харків") || lower.contains("харьков")) return "Kharkiv";
        if (lower.contains("москва")) return "Moscow";
        if (lower.contains("ришон") || lower.contains("rishon")) return "Rishon LeZion";

        StringBuilder sb = new StringBuilder(src.length());
        for (int i = 0; i < src.length(); i++) {
            char ch = src.charAt(i);
            String replacement = TRANSLIT_MAP.get(ch);
            sb.append(replacement != null ? replacement : ch);
        }
        return sb.toString();
    }
}
