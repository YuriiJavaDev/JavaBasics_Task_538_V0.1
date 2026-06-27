package com.yurii.pavlenko.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CityTranslitUtilTest {

    @Test
    void testConvertToEnglishText_SpecialCases() {
        assertEquals("London", CityTranslitUtil.convertToEnglishText("Лондон"));
        assertEquals("Kyiv", CityTranslitUtil.convertToEnglishText("Київ"));
        assertEquals("Kyiv", CityTranslitUtil.convertToEnglishText("Киев"));
        assertEquals("Rishon LeZion", CityTranslitUtil.convertToEnglishText("Ришон ле-цион"));
        assertEquals("Rishon LeZion", CityTranslitUtil.convertToEnglishText("Rishon"));
    }

    @Test
    void testConvertToEnglishText_Transliteration() {
        assertEquals("Alushta", CityTranslitUtil.convertToEnglishText("Алушта"));
        assertEquals("Dnipro", CityTranslitUtil.convertToEnglishText("Дніпро"));
    }

    @Test
    void testConvertToEnglishText_EmptyOrNull() {
        assertEquals("", CityTranslitUtil.convertToEnglishText(""));
        assertEquals(null, CityTranslitUtil.convertToEnglishText(null));
    }
}