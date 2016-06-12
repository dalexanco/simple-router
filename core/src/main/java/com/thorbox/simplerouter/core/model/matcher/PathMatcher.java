package com.thorbox.simplerouter.core.model.matcher;

import java.util.ArrayList;
import java.util.List;
import com.google.code.regexp.Pattern;
import com.google.code.regexp.Matcher;

/**
 * Created by david on 15/02/2016.
 */
public class PathMatcher {

    // Static patterns
    private final static Pattern PARAMS_INT_PARAMS = Pattern.compile("\\{([a-zA-Z_0-9]*):integer\\}");
    private final static Pattern PARAMS_STRING_PARAMS = Pattern.compile("\\{([a-zA-Z_0-9]*):string\\}");
    private final static Pattern PARAMS_DEFAULT_PARAMS = Pattern.compile("\\{([a-zA-Z_0-9]*)\\}");

    private final static String PATH_PATTERN_INTEGER = "[0-9]+";
    private final static String PATH_PATTERN_STRING = "[a-zA-Z_0-9]+";
    private final static String PATH_PATTERN_PREFIX = "^";
    private final static String PATH_PATTERN_SUFFIX = "(.*)";

    // Instance pattern
    private final String path;
    private String pathRegex;
    private final Pattern pathPattern;
    private List<String> pathParamKeys;

    public PathMatcher(String path) {
        this.path = path;
        this.pathParamKeys = new ArrayList<>();
        this.pathRegex = this.path;
        this.preparePatterns(PARAMS_INT_PARAMS, PATH_PATTERN_INTEGER);
        this.preparePatterns(PARAMS_STRING_PARAMS, PATH_PATTERN_STRING);
        this.preparePatterns(PARAMS_DEFAULT_PARAMS, PATH_PATTERN_STRING);
        this.pathRegex = PATH_PATTERN_PREFIX + this.pathRegex + PATH_PATTERN_SUFFIX;
        this.pathPattern = Pattern.compile(pathRegex);
    }

    private void preparePatterns(Pattern patternFrom, String patternTo) {
        Matcher m = patternFrom.matcher(pathRegex);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String paramName = m.group(1);
            pathParamKeys.add(paramName);
            m.appendReplacement(sb, "(?<" + paramName + ">" + patternTo + ")");
        }
        m.appendTail(sb);
        this.pathRegex = sb.toString();
    }

    public List<String> getParamKeys() {
        return pathParamKeys;
    }

    public Matcher executePattern(String string) {
        return pathPattern.matcher(string);
    }
}
