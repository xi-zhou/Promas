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

public class TransmissionModel {
	private ArrayList<String> infectedPerson = new ArrayList<String>();

	TransmissionModel() throws JepException{
		
//		JepConfig conf = new JepConfig().setRedirectOutputStreams(true);
//		SharedInterpreter.setConfig(conf);
//		SharedInterpreter interp = new SharedInterpreter();
		SubInterpreter interp = new JepConfig().setRedirectOutputStreams(true).createSubInterpreter();
		interp.eval("from problog.program import PrologString");
        interp.eval("from problog import get_evaluatable");
        interp.eval("from problog.logic import Term, Constant"); 
        interp.eval("from problog.logic import term2str");    
        interp.eval("import json");
        interp.eval("model= \"\"\"\n" + 
        		":- use_module(library(db)).\n" + 
        		":- sqlite_load('/Users/z.x/test.db').\n" + 
        		"P :: infects(PERSONx,PERSONy) :- point(PERSONx, X, Y),\n" + 
        		"point(PERSONy, A, B), PERSONx\\\\=PERSONy,\n" + 
        		"D is sqrt((A-X)^2 + (B-Y)^2), P is 0.1/(D^2), D < 10.\n" + 
        		"ill(PERSONx):-infects(PERSONx,PERSONy), is_ill(PERSONy).\n" + 
        		"ill(PERSONx):-is_ill(PERSONx).\n" + 
        		"query(ill(PERSONx)).\n" + 
        		"\"\"\"");
        interp.eval("result = get_evaluatable().create_from(PrologString(model)).evaluate()");       
        interp.eval("res = {term2str(k):float(v) for k,v in result.items()}");
        interp.eval("js = json.dumps(res)");

       
        String res =interp.getValue("js", String.class);
        interp.close();

        Gson gson = new Gson();        
        Type type = new TypeToken<Map<String, Float>>(){}.getType();
        Map<String, Float> myMap = gson.fromJson(res, type);
                
      for (Entry<String, Float> entry : myMap.entrySet()) {
    	  if(entry.getValue()>=0.5) {  // only infected term
    		  String name[] = entry.getKey().split("'");
              //System.out.println(entry.getKey()+" : "+entry.getValue());
              System.out.println(name[1]+" is infected");  
              this.infectedPerson.add(name[1]);              
    	  }
      }
           

	}

	public ArrayList<String> getInfectedPerson() {
		return infectedPerson;
	}
	
	


	
	
}
