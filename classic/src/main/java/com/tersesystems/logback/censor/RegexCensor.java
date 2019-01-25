package com.tersesystems.logback.censor;

import com.typesafe.config.Config;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexCensor implements Censor {

    private List<Pattern> patterns;

    private String replacementText;

    public RegexCensor(Config config, String regexPath, String replacementTextPath) {
        this.replacementText = config.getString(replacementTextPath);
        List<String> regexes = config.getStringList(regexPath);
        this.patterns = regexes.stream()
                .map(rex -> {
                    int flags = (rex.contains("\n")) ? Pattern.MULTILINE : 0;
                    return Pattern.compile(rex, flags);
                })
                .collect(Collectors.toList());;
    }

    @Override
    public <T> T apply(T original) {
        if (original instanceof String) {
            String acc = (String) original;
            for (Pattern pattern : patterns) {
                acc = pattern.matcher(acc).replaceAll(replacementText);
            }
            return (T) acc;
        }
        return original;
    }

}
