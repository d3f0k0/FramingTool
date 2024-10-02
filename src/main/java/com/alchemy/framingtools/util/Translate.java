package com.alchemy.framingtools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

// Copied from NomiLabs
@SuppressWarnings("unused")
public class Translate {

    public static String translate(String key, Object... params) {
        if (Side.isDedicatedServer()) return translateServerSide(key, params); // I18n is not available on Dedicated
        // Servers
        try {
            return net.minecraft.client.resources.I18n.format(key, params);
        } catch (Exception e) {
            return translateServerSide(key, params);
        }
    }

    @SuppressWarnings("deprecation")
    private static String translateServerSide(String key, Object... params) {
        try {
            String localTranslated = I18n.translateToLocalFormatted(key, params);
            if (!localTranslated.equals(key)) return localTranslated;

            // Try fallback
            String fallbackTranslated = I18n.translateToFallback(key);
            if (!fallbackTranslated.equals(key) && params.length != 0) {
                try {
                    fallbackTranslated = String.format(fallbackTranslated, params);
                } catch (IllegalFormatException var5) {
                    fallbackTranslated = "Format error: " + fallbackTranslated;
                }
            }
            return fallbackTranslated;
        } catch (Exception e) {
            return key;
        }
    }


    public static String format(String str, TextFormatting... formats) {
        return String.join("", Arrays.stream(formats).map(TextFormatting::toString).toArray(String[]::new)) + str +
                TextFormatting.RESET;
    }


    public static String format(String str, Format... formats) {
        return String.join("", Arrays.stream(formats).map((format) -> format.format).toArray(String[]::new)) + str +
                TextFormatting.RESET;
    }

    public static Translatable translatable(String key, Object... params) {
        return new Translatable(key, params);
    }

    public static Translatable translatableLiteral(String text) {
        return new TranslatableLiteral(text);
    }

    public static Translatable translatableEmpty() {
        return new TranslatableLiteral("");
    }

    public static class Format {

        public final String format;

        private Format(String format) {
            this.format = format;
        }

        public static Format of(TextFormatting format) {
            return new Format(format.toString());
        }

    }

    /**
     * A translatable object, which has its value translated dynamically (allowing for on-the-fly language changes)
     */
    @SuppressWarnings("unused")
    public static class Translatable {

        public final String key;
        public final Object[] params;

        protected List<Format> format;
        protected List<Translatable> appended;

        public Translatable(String key, Object... params) {
            this.key = key;
            this.params = params;
            this.format = new ArrayList<>();
            this.appended = new ArrayList<>();
        }

        public Translatable addFormat(Format format) {
            this.format.add(format);
            return this;
        }

        public Translatable addFormat(TextFormatting format) {
            this.format.add(Format.of(format));
            return this;
        }


        public Translatable append(Translatable append) {
            this.appended.add(append);
            return this;
        }

        public String translate() {
            String translated = translateThis();

            for (Translatable toAppend : appended) {
                translated = translated.concat(toAppend.translate());
            }

            return translated;
        }

        protected String translateThis() {
            if (format.isEmpty()) return Translate.translate(key, params);

            return Translate.format(Translate.translate(key, params), format.toArray(new Format[0]));
        }

        @Override
        public String toString() {
            return translate();
        }
    }

    /**
     * A translatable object with literal text, designed to be appended to a Translatable object.
     */
    public static class TranslatableLiteral extends Translatable {

        public TranslatableLiteral(String text) {
            super(text);
        }

        @Override
        protected String translateThis() {
            if (format.isEmpty()) return key;

            return Translate.format(key, format.toArray(new Format[0]));
        }
    }
}