package com.neonthread.utils;

import java.util.*;

/**
 * Parser JSON ligero sin dependencias externas.
 * Soporta Objects {}, Arrays [], Strings "", Numbers, Booleans y Null.
 * Usa Recursive Descent parsing pattern.
 */
public class SimpleJsonParser {

    private final String json;
    private int index;

    private SimpleJsonParser(String json) {
        this.json = json;
        this.index = 0;
    }

    public static Object parse(String json) {
        return new SimpleJsonParser(json).parseValue();
    }

    private Object parseValue() {
        skipWhitespace();
        if (index >= json.length()) return null;

        char c = json.charAt(index);
        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();
        if (c == 't') return parseTrue();
        if (c == 'f') return parseFalse();
        if (c == 'n') return parseNull();
        if (Character.isDigit(c) || c == '-') return parseNumber();

        throw new RuntimeException("Invalid JSON at index " + index + ": " + c);
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new HashMap<>();
        consume('{');
        skipWhitespace();
        if (peek() == '}') {
            consume('}');
            return map;
        }

        while (true) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            consume(':');
            Object value = parseValue();
            map.put(key, value);

            skipWhitespace();
            if (peek() == '}') {
                consume('}');
                break;
            }
            consume(',');
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        consume('[');
        skipWhitespace();
        if (peek() == ']') {
            consume(']');
            return list;
        }

        while (true) {
            Object value = parseValue();
            list.add(value);

            skipWhitespace();
            if (peek() == ']') {
                consume(']');
                break;
            }
            consume(',');
        }
        return list;
    }

    private String parseString() {
        consume('"');
        StringBuilder sb = new StringBuilder();
        while (index < json.length()) {
            char c = json.charAt(index++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                char escaped = json.charAt(index++);
                if (escaped == 'n') sb.append('\n');
                else if (escaped == 't') sb.append('\t');
                else if (escaped == 'r') sb.append('\r');
                else if (escaped == '"') sb.append('"');
                else if (escaped == '\\') sb.append('\\');
                else sb.append(escaped);
            } else {
                sb.append(c);
            }
        }
        throw new RuntimeException("Unterminated string");
    }

    private Number parseNumber() {
        int start = index;
        if (peek() == '-') index++;
        while (index < json.length()) {
            char c = json.charAt(index);
            if (!Character.isDigit(c) && c != '.') break;
            index++;
        }
        String numStr = json.substring(start, index);
        return numStr.contains(".") ? Double.parseDouble(numStr) : Integer.parseInt(numStr);
    }

    private Boolean parseTrue() {
        consume("true");
        return Boolean.TRUE;
    }

    private Boolean parseFalse() {
        consume("false");
        return Boolean.FALSE;
    }

    private Object parseNull() {
        consume("null");
        return null;
    }

    private void skipWhitespace() {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
    }

    private void consume(char expected) {
        if (index >= json.length() || json.charAt(index) != expected) {
            throw new RuntimeException("Expected '" + expected + "' at " + index);
        }
        index++;
    }

    private void consume(String expected) {
        if (!json.startsWith(expected, index)) {
            throw new RuntimeException("Expected \"" + expected + "\" at " + index);
        }
        index += expected.length();
    }

    private char peek() {
        if (index >= json.length()) return 0;
        return json.charAt(index);
    }
}
