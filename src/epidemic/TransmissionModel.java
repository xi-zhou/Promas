package epidemic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jep.JepException;
import jep.SharedInterpreter;
import repast.simphony.random.RandomHelper;


/**
 * Between human transmission
 */
public final class TransmissionModel {
  static String res;
  static double prob;

  private TransmissionModel() {}

  static TransmissionModel create() throws JepException {
    return new TransmissionModel();
  }

  public static void loadModel() throws JepException {
    long startTime = System.currentTimeMillis();
    SharedInterpreter interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologFile");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval("result = get_evaluatable()."
        + "create_from(PrologFile('/Users/z.x/eclipse-workspace-2020-06/Promas/misc/infection.pl'))."
        + "evaluate()");
    interp.eval("res = {term2str(k):float(v) for k,v in result.items()}");
    interp.eval("js = json.dumps(res)");
    res = interp.getValue("js", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out
        .println("(infection model) Calculation took " + (endTime - startTime) + " milliseconds");
  }

  /**
   * Take a random subset from problog model by comparing to a random seed.
   * @return
   */
  public static ArrayList<String> getResFromJep() {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, Float>>() {}.getType();
    Map<String, Float> myMap = gson.fromJson(res, type);
    ArrayList<String> infectedPerson = new ArrayList<String>();
    // compare to random seed
    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);
    for (Entry<String, Float> entry : myMap.entrySet()) {
      if (entry.getValue() >= (float) seed) {
        String name[] = entry.getKey().split("'");
        infectedPerson.add(name[1]);
      }
    }
    return infectedPerson;
  }

}


