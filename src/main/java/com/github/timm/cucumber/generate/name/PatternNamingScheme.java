package com.github.timm.cucumber.generate.name;

import com.github.timm.cucumber.ModuloCounter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generate a Class Name based on a pattern.
 *
 * <p>
 * No validation is performed to ensure that the generated class name is valid.
 * </p>
 *
 * <p>
 * The following placeholders are supported:
 * </p>
 * <ul>
 * <li>'{f}' - The feature file, converted using supplied naming scheem.</li>
 * <li>'{c}' - A one-up number, formatted to have minimum 2 character width.</li>
 * <li>'{c:n}' - A one-up number modulo n, with no minimum character width.</li>
 * </ul>
 */
public class PatternNamingScheme implements ClassNamingScheme {

    private final String pattern;
    private final Counter counter;
    private final Counter moduloCounter;
    private final ClassNamingScheme featureFileNamingScheme;

    /**
     * Constructor.
     * @param pattern The pattern to use.
     * @param counter Counter to generate one up numbers
     * @param featureFileNamingScheme Naming scheme to use for '{f}' placeholder
     */
    public PatternNamingScheme(final String pattern, final Counter counter,
                    final ClassNamingScheme featureFileNamingScheme) {

        this.pattern = pattern;
        this.counter = counter;
        this.moduloCounter = new ModuloCounter(pattern);
        this.featureFileNamingScheme = featureFileNamingScheme;
    }

    /**
     * Generate a class name using the required pattern and feature file.
     * @param featureFileName The feature file to generate a class name for
     * @return A class name based on the required pattern.
     */
    public String generate(final String featureFileName) {

        String className =
                        pattern.replace("{f}", featureFileNamingScheme.generate(featureFileName));

        className = replaceAll(className, counter.next());
        return className;
    }

    private String replaceAll(String className, int number) {
        String retValue = replaceCounter(className, number);
        retValue = replaceModuloCounter(retValue, number);
        return retValue;
    }

    private String replaceCounter(String pattern, int number) {
        int defaultLen = 2;
        Matcher matcher = Pattern.compile("\\{(\\d*)c}").matcher(pattern);

        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                int len = matcher.start(1) == matcher.end(1) ? defaultLen : Integer.decode(matcher.group(1));
                matcher.appendReplacement(sb, String.format("%0" + len + "d", number));
                result = matcher.find();
            }
            while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return pattern;
    }

    private String replaceModuloCounter(String pattern, int number) {
        int defaultLen = 1;
        Matcher matcher = Pattern.compile("\\{(\\d*)c:(\\d+)}").matcher(pattern);

        boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                int len = matcher.start(1) == matcher.end(1) ? defaultLen : Integer.decode(matcher.group(1));
                int mod = Integer.decode(matcher.group(2));
                matcher.appendReplacement(sb, String.format("%0" + len + "d", number % mod));
                result = matcher.find();
            }
            while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return pattern;
    }

}
