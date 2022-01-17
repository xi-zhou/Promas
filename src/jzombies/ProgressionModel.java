package jzombies;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jep.JepException;
import jep.SharedInterpreter;


public class ProgressionModel {
  static String resQua;
  static String resResis;
  static String resDie;
  static SharedInterpreter interp;

  private ProgressionModel() {}

  static ProgressionModel create() throws JepException {
    return new ProgressionModel();

  }

  public static void quarantine() throws JepException {
    long startTime = System.currentTimeMillis();
    // System.out.println("load quarantine model...");
    interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologFile");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval(
        "resultQua = get_evaluatable().create_from(PrologFile('/Users/z.x/eclipse-workspace-2020-06/JZombies_Demo/misc/quarantine.pl')).evaluate()");
    interp.eval("resQua = {term2str(k):float(v) for k,v in resultQua.items()}");
    interp.eval("jsQua = json.dumps(resQua)");
    resQua = interp.getValue("jsQua", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out
        .println("(quarantine model) Calculation took " + (endTime - startTime) + " milliseconds");
  }

  public static void resistant() throws JepException {
    long startTime = System.currentTimeMillis();
    // System.out.println("load resistance model...");
    interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologFile");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval(
        "resultVacc = get_evaluatable().create_from(PrologFile('/Users/z.x/eclipse-workspace-2020-06/JZombies_Demo/misc/resistant.pl')).evaluate()");
    interp.eval("resultVacc = {term2str(k):float(v) for k,v in resultVacc.items()}");
    interp.eval("jsVacc = json.dumps(resultVacc)");
    resResis = interp.getValue("jsVacc", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out
        .println("(resistance model) Calculation took " + (endTime - startTime) + " milliseconds");
  }


  public static void dies() throws JepException {
    long startTime = System.currentTimeMillis();
    // System.out.println("load death model...");
    interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologFile");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval(
        "result = get_evaluatable().create_from(PrologFile('/Users/z.x/eclipse-workspace-2020-06/JZombies_Demo/misc/death.pl')).evaluate()");
    interp.eval("res = {term2str(k):float(v) for k,v in result.items()}");
    interp.eval("js = json.dumps(res)");
    resDie = interp.getValue("js", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out.println("(death model) Calculation took " + (endTime - startTime) + " milliseconds");
  }


  /**
   * @param trans identifier of result of problog model
   * @return
   */
  public static HashMap<String, Float> getResFromJep(String pattern) {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, Float>>() {}.getType();
    HashMap<String, Float> progression = new HashMap<String, Float>();
    Map<String, Float> myMap = null;
    switch (pattern) {
      case "Quarantine":
        myMap = gson.fromJson(resQua, type);
        break;
      case "Resistant":
        myMap = gson.fromJson(resResis, type);
        break;
      case "Die":
        myMap = gson.fromJson(resDie, type);
        break;
    }

    if (!myMap.keySet().iterator().next().contains("'")) {
      return progression;
    }

    for (Entry<String, Float> entry : myMap.entrySet()) {
      String name[] = entry.getKey().split("'");
      progression.put(name[1], entry.getValue());
    }
    return progression;
  }

}
