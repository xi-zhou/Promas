package jzombies;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jep.JepConfig;
import jep.JepException;
import jep.SharedInterpreter;
import jep.SubInterpreter;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;


public final class TransmissionModel {
  static String res;
  static double prob;
  
  private TransmissionModel() {}

  static TransmissionModel create() throws JepException {
    return new TransmissionModel();

  }
  
  //@ScheduledMethod(start = 0.5, interval = 1)
  public static void loadModel() throws JepException {
    long startTime = System.currentTimeMillis();
    //System.out.println("load infection model...");
    SharedInterpreter interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologString");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import Term, Constant");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval("infection=\"\"\"\n" + 
        ":- use_module(library(db)).\n" + 
        ":- sqlite_load('/Users/z.x/test.db').\n" + 
        "\n" + 
        "P :: infects(PERSONx,PERSONy) :- point(PERSONx, X, Y),\n" + 
        "point(PERSONy, A, B), PERSONx\\\\=PERSONy,\n" + 
        "D is sqrt((A-X)^2 + (B-Y)^2),D <10 , D>0,P is min(1,0.5/(D^2)).\n" + 
        "\n" + 
        "P :: reinfects(PERSONx,PERSONy) :- point(PERSONx, X, Y),\n" + 
        "point(PERSONy, A, B), PERSONx\\\\=PERSONy,\n" + 
        "D is sqrt((A-X)^2 + (B-Y)^2),D <10 , D>0,P is min(1,0.1/(D^2)).\n" + 
        "\n" + 
        "ill(PERSONx):-is_ill(PERSONy),is_cautious(PERSONx); is_social(PERSONx),infects(PERSONx,PERSONy).\n" + 
        "ill(PERSONx):-is_ill(PERSONx),\\+recovers(PERSONx), \\+in_quarantine(PERSONx).\n" + 
        "\n" + 
        "ill(PERSONx) :- is_ill(PERSONy), is_resistant(PERSONx),reinfects(PERSONx,PERSONy).\n" + 
        "\n" + 
        "query(ill(PERSONx)).\n" + 
        "\"\"\"");
    interp.eval("result = get_evaluatable().create_from(PrologString(infection)).evaluate()");
    interp.eval("res = {term2str(k):float(v) for k,v in result.items()}");
    interp.eval("js = json.dumps(res)");
    res = interp.getValue("js", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out.println("(infection model) Calculation took " + (endTime - startTime) + " milliseconds");
  }

  /**
   * Take a random subset from problog model.
   * @return 
   */
  public static ArrayList<String> getResFromJep() {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, Float>>() {}.getType();
    Map<String, Float> myMap = gson.fromJson(res, type);
    ArrayList<String> infectedPerson = new ArrayList<String>();
    // compare to random seed
    double seed = RandomHelper.nextDoubleFromTo(0.0,1.0);
    for(Entry<String,Float>entry:myMap.entrySet()) {
      if(entry.getValue()>=(float)seed) {
      String name[] = entry.getKey().split("'");
        infectedPerson.add(name[1]);
      }
    }
    return infectedPerson;
  }

}


