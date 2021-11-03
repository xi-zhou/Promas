package jzombies;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jep.JepConfig;
import jep.JepException;
import jep.SharedInterpreter;
import jep.SubInterpreter;

/**
 * @author z.x
 *
 */
/**
 * @author z.x
 *
 */
public class TransmissionModel {
  private ArrayList<String> infectedPerson = new ArrayList<String>();
  String res;
  SharedInterpreter interp;


  TransmissionModel() {}

  // void setJepConfig() throws JepException {
  // config= new JepConfig().setRedirectOutputStreams(true);
  // SharedInterpreter.setConfig(config);
  //
  // }
  static TransmissionModel create() throws JepException {
    return new TransmissionModel();

  }

  public synchronized void loadModel() throws JepException {
    interp = new SharedInterpreter();
    interp.eval("from jep import redirect_streams");
    interp.eval("redirect_streams.setup()");
    interp.eval("from problog.program import PrologString");
    interp.eval("from problog import get_evaluatable");
    interp.eval("from problog.logic import Term, Constant");
    interp.eval("from problog.logic import term2str");
    interp.eval("import json");
    interp.eval("model= \"\"\"\n" + ":- use_module(library(db)).\n"
        + ":- sqlite_load('/Users/z.x/test.db').\n"
        + "P :: infects(PERSONx,PERSONy) :- point(PERSONx, X, Y),\n"
        + "point(PERSONy, A, B), PERSONx\\\\=PERSONy,\n"
        + "D is sqrt((A-X)^2 + (B-Y)^2),D <10 , D>0,P is 0.1/(D^2).\n" + "\n"
        + "infects(PERSONx,PERSONy) :- point(PERSONx, X, Y),\n" + "point(PERSONy, X, Y).\n"
        + "ill(PERSONx):-infects(PERSONx,PERSONy), is_ill(PERSONy).\n"
        + "ill(PERSONx):-is_ill(PERSONx).\n" + "query(ill(PERSONx)).\n" + "\n" + "\"\"\"");
    interp.eval("result = get_evaluatable().create_from(PrologString(model)).evaluate()");
    interp.eval("res = {term2str(k):float(v) for k,v in result.items()}");
    interp.eval("js = json.dumps(res)");
    res = interp.getValue("js", String.class);
    interp.close();
  }

  public void getResFromJep() {
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, Float>>() {}.getType();
    Map<String, Float> myMap = gson.fromJson(res, type);

    for (Entry<String, Float> entry : myMap.entrySet()) {
      if (entry.getValue() >= 0.05) { // only infected term
        String name[] = entry.getKey().split("'");
        // System.out.println(entry.getKey()+" : "+entry.getValue());
        this.infectedPerson.add(name[1]);
      }
    }
  }

  
  /**
   * Get result from problog model
   * 
   * @return list contains current all infected person
   */
  public ArrayList<String> getInfectedPerson() {
    return infectedPerson;
  }

//  public void closeJep() {
//    try {
//      interp.close();
//    } catch (JepException e) {
//      e.printStackTrace();
//    }
//  }
}


