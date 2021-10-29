package jzombies;

import jep.JepException;

public class test {

	public static void main(String[] args) {
		try {
		TransmissionModel trans = TransmissionModel.create();
		trans.loadModel();
		trans.getResFromJep();
		//trans.closeJep();
	} catch (JepException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}

}
