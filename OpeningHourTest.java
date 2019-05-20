// License: GPL. See LICENSE file for details.
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class OpeningHourTest {

    public static final ScriptEngine ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");

    public static void main(String[] args) throws Exception {
        if (args.length==0){
            System.out.println("Please give opening_hours value as first parameter.");
            System.exit(1);
        }
        initialize();

        final Object r = ((Invocable) ENGINE).invokeFunction("oh", args[0], 0 /* oh_mode */);

        for (final Object i : getList(((Invocable) ENGINE).invokeMethod(r, "getErrors"))) {
            System.err.println(i.toString().trim());
        }
        for (final Object i : getList(((Invocable) ENGINE).invokeMethod(r, "getWarnings"))) {
            System.err.println(i.toString().trim());
        }

    }
    public static void initialize() throws Exception {
        if (ENGINE != null) {
            try (Reader reader = new InputStreamReader(
                    new FileInputStream("opening_hours.js"))) {
                ENGINE.eval(
                    "var global=this;var window=this;var process={env:{}};" +
                    "var console={};console.debug=print;console.log=print;console.warn=print;console.error=print;");
                ENGINE.eval(reader);
                // fake country/state to not get errors on holidays
                ENGINE.eval("var nominatimJSON = {address: {state: 'Bayern', country_code: 'de'}};");
                ENGINE.eval(
                        "var oh = function (value, mode) {" +
                        " try {" +
                        "    var r= new opening_hours(value, nominatimJSON, mode);" +
                        "    r.getErrors = function() {return [];};" +
                        "    return r;" +
                        "  } catch(err) {" +
                        "    return {" +
                        "      getWarnings: function() {return [];}," +
                        "      getErrors: function() {return [err.toString()]}" +
                        "    };" +
                        "  }" +
                        "};");
            }
        } else {
            System.err.println("Unable to initialize OpeningHourTest because no JavaScript engine has been found");
        }
    }

    static enum CheckMode {
        TIME_RANGE(0), POINTS_IN_TIME(1), BOTH(2);
        final int code;

        CheckMode(int code) {
            this.code = code;
        }
    }

    protected Object parse(String value, CheckMode mode) throws ScriptException, NoSuchMethodException {
        return ((Invocable) ENGINE).invokeFunction("oh", value, mode.code);
    }

    @SuppressWarnings("unchecked")
    protected static List<Object> getList(Object obj) throws ScriptException, NoSuchMethodException {
        if (obj == null || "".equals(obj)) {
            return Arrays.asList();
        } else if (obj instanceof String) {
            final Object[] strings = ((String) obj).split("\\\\n");
            return Arrays.asList(strings);
        } else if (obj instanceof List) {
            return (List<Object>) obj;
        } else {
            // recursively call getList() with argument converted to newline-separated string
            return getList(((Invocable) ENGINE).invokeMethod(obj, "join", "\\n"));
        }
    }

}
