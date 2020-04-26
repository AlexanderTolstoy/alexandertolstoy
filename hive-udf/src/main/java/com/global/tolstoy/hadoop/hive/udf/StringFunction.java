package com.global.tolstoy.hadoop.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public class StringFunction extends GenericUDF {

	public String getStringBySplitIndex(String strings, String splitchr, int index) {

		String[] stringarray = strings.split(splitchr);

		return stringarray[index];
	}

	@Override
	public Object evaluate(DeferredObject[] arg0) throws HiveException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayString(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectInspector initialize(ObjectInspector[] arg0) throws UDFArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String [] args) {
		StringFunction	test = new StringFunction();
		System.out.print(test.getStringBySplitIndex("张学友,黎明,王河乡,屈鹏飞,协作伟",",",3));
	}


}
