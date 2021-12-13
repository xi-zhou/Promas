package jzombies;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jep.JepException;
import jep.SharedInterpreter;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;


public class ProgressionModel {
  static String resQua;
  static String resVacc;
  static String res;
  static SharedInterpreter interp;

  private ProgressionModel() {}

  static ProgressionModel create() throws JepException {
    return new ProgressionModel();

  }
  
  @ScheduledMethod(start = 1.2, interval = 1)
  public static void quarPvacc() throws JepException {
    long startTime = System.currentTimeMillis();
    System.out.println("load quarantine model...");
    interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologString");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import Term, Constant");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval("quarantine=\"\"\"\n" + 
        ":- use_module(library(db)).\n" + 
        ":- sqlite_load('/Users/z.x/test.db').\n" + 
        "quarantine(X) :- in_quarantine(X),+\\dies(X), +\\recovers(X).\n" + 
        "quarantine(X) :- isolation(X).\n" + 
        "Pquarantines :: isolation(X) :- is_ill(X).\n" + 
        "query(quarantine(X)).\n" + 
        "\"\"\"");
    interp.eval("resultQua = get_evaluatable().create_from(PrologString(quarantine)).evaluate()");
    interp.eval("resQua = {term2str(k):float(v) for k,v in resultQua.items()}");
    interp.eval("jsQua = json.dumps(resQua)");
    resQua = interp.getValue("jsQua", String.class);
    
    System.out.println("load vaccination model...");
    interp.eval("vaccination = \"\"\"\n" + 
        ":- use_module(library(db)).\n" + 
        ":- sqlite_load('/Users/z.x/test.db').\n" + 
        "resistant(X) :- is_resistant(X), +\\reinfected(X).\n" + 
        "resistant(X) :- vaccinated(X).\n" + 
        "0.2 :: vaccinated(X) :- is_cautious(X).\n" + 
        "0.1 ::  vaccinated(X) :- is_social(X).\n" + 
        "query(resistant(X)).\n" + 
        "\"\"\"");
    interp.eval("resultVacc = get_evaluatable().create_from(PrologString(vaccination)).evaluate()");
    interp.eval("resultVacc = {term2str(k):float(v) for k,v in resultVacc.items()}");
    interp.eval("jsVacc = json.dumps(resultVacc)");
    resVacc = interp.getValue("jsVacc", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out.println("Calculation took " + (endTime - startTime) + " milliseconds");
  }
  
  
  @ScheduledMethod(start = 1.5, interval = 2)
  public static void dies() throws JepException {
    long startTime = System.currentTimeMillis();
    System.out.println("load death model...");
    interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologString");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import Term, Constant");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval("death = \"\"\"\n" + 
        ":- use_module(library(db)).\n" + 
        ":- sqlite_load('/Users/z.x/test.db').\n" + 
        "0.01:: dies(X) :- in_quarantine(X).\n" + 
        "query(dies(X))\n" + 
        "\"\"\"");
    interp.eval("result = get_evaluatable().create_from(PrologString(death)).evaluate()");
    interp.eval("res = {term2str(k):float(v) for k,v in result.items()}");
    interp.eval("js = json.dumps(res)");
    res = interp.getValue("js", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out.println("Calculation took " + (endTime - startTime) + " milliseconds");
  }

  @ScheduledMethod(start = 2.5, interval = 2)
  public static void recovers() throws JepException {
    long startTime = System.currentTimeMillis();
    System.out.println("load death model...");
    interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologString");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import Term, Constant");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval("recovers = \"\"\"\n" + 
        ":- use_module(library(db)).\n" + 
        ":- sqlite_load('/Users/z.x/test.db').\n" + 
        "resistant(X) :- is_resistant(X), +\\reinfected(X).\n" + 
        "resistant(X) :- recovers(X).\n" + 
        "Pillrecovers ::  recovers(X) :- is_ill(X).\n" + 
        "Pquarrecovers :: recovers(X) :- in_quarantine(X).\n" + 
        "query(resistant(X)).\n" + 
        "\"\"\"");
    interp.eval("result = get_evaluatable().create_from(PrologString(recovers)).evaluate()");
    interp.eval("res = {term2str(k):float(v) for k,v in result.items()}");
    interp.eval("js = json.dumps(res)");
    res = interp.getValue("js", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out.println("Calculation took " + (endTime - startTime) + " milliseconds");
  }
  
  @ScheduledMethod(start = 3.8, interval = 1)
  public static void reinfects() throws JepException {
    long startTime = System.currentTimeMillis();
    System.out.println("load death model...");
    interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologString");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import Term, Constant");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval("reinfects =\"\"\"\n" + 
        ":- use_module(library(db)).\n" + 
        ":- sqlite_load('/Users/z.x/test.db').\n" + 
        "ill(X) :- reinfects(Y,X).\n" + 
        "reinfected(X) :- reinfects(Y,X).\n" + 
        "Preinfects :: reinfects(X,Y) :- is_ill(X), is_resistant(Y),point(X, C, D),\n" + 
        "point(Y, A, B), X\\\\=Y,\n" + 
        "D is max(0.1,sqrt((A-C)^2 + (B-D)^2)),D <10 , D>0,P is min(1,0.1/(D^2)).\n" + 
        "query(ill(X)).\n" + 
        "\"\"\"");
    interp.eval("result = get_evaluatable().create_from(PrologString(reinfects)).evaluate()");
    interp.eval("res = {term2str(k):float(v) for k,v in result.items()}");
    interp.eval("js = json.dumps(res)");
    res = interp.getValue("js", String.class);
    interp.close();
    long endTime = System.currentTimeMillis();
    System.out.println("Calculation took " + (endTime - startTime) + " milliseconds");
  }
  
  /**
   * @param trans identifier of result of problog model
   * @return 
   */
  public static ArrayList<String> getResFromJep(String trans) {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, Float>>() {}.getType();
    ArrayList<String> progression = new ArrayList<String>();
    Map<String, Float> myMap;
    
    switch (trans) {
      case "Quarantine":
        myMap = gson.fromJson(resQua, type);
        break;
      case "Vaccination":
        myMap = gson.fromJson(resVacc, type);
        break;
      default:
        myMap = gson.fromJson(res, type);
    }


    double seed = RandomHelper.nextDoubleFromTo(0.0, 1.0);
    for (Entry<String, Float> entry : myMap.entrySet()) {
      if (entry.getValue() >= (float) seed) {
        String name[] = entry.getKey().split("'");
        progression.add(name[1]);
      }
    }

    return progression;
  }

}
