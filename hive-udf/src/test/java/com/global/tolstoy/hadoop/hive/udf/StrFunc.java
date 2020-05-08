/**   
 * @project hive-udf
 * @Title: StrFunc.java 
 * @Package com.global.tolstoy.hadoop.hive.udf 
 * @Description: TODO(用一句话描述该文件做什么) 
 * 
 * @author 	qupengfei@zhixuezhen.com  
 * @since 	2020-05-03
 * @version V1.0   
 */ 
package com.global.tolstoy.hadoop.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;


@Description(
	    name     = "StrFunc",
	    value    = "_FUNC_( value) : Double the value of numeric argument, " +
	               "Concatinate value to itself for string arguments.",
	    extended = "Example:\n" +
	               "    SELECT _FUNC_(salary) FROM customers;\n" +
	               "    (returns 12,000 if the salary was 6,000)\n\n" +
	               "    SELECT _FUNC_(name) FROM customers;\n" +
	               "    (returns \"Tim MayTim May\" if the name was \"Tim May\")\n"
	)

/**
 * Copyright (c) 2020 by Alexander Tolstoy
 * @ClassName:     StrFunc
 * @Description:   TODO(用一句话描述该文件做什么) 
 * 
 * @author:        tolstoy
 * @version:       V1.0  
 * @datetime:      2020-05-03 12:31:48 AM
 */
public class StrFunc extends GenericUDF {

	/* (非 Javadoc)
	 * <p>Title: evaluate</p>
	 * <p>Description: </p>
	 * @param arg0
	 * @return
	 * @throws HiveException
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDF#evaluate(org.apache.hadoop.hive.ql.udf.generic.GenericUDF.DeferredObject[])
	 */
	@Override
	public Object evaluate(DeferredObject[] arg0) throws HiveException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (非 Javadoc)
	 * <p>Title: getDisplayString</p>
	 * <p>Description: </p>
	 * @param arg0
	 * @return
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDF#getDisplayString(java.lang.String[])
	 */
	@Override
	public String getDisplayString(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (非 Javadoc)
	 * <p>Title: initialize</p>
	 * <p>Description: </p>
	 * @param arg0
	 * @return
	 * @throws UDFArgumentException
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDF#initialize(org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector[])
	 */
	@Override
	public ObjectInspector initialize(ObjectInspector[] arg0) throws UDFArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int interval_seq_no(Number... ids) throws Exception {
		Number i = ids[0];

		if (ids.length == 1) {
			return 0;
		}
		int k = 0;// 位置
		for (int j = 1; j < ids.length; j++) {

			boolean a = false;
			if (i instanceof Integer) {
				a = i.intValue() < ids[j].intValue();
			}
			if (i instanceof Double) {
				a = i.doubleValue() < ids[j].doubleValue();
			}

			if (i instanceof Float) {
				a = i.floatValue() < ids[j].floatValue();
			}

			if (i instanceof Long) {
				a = i.longValue() < ids[j].longValue();
			}

			if (i instanceof Byte) {
				a = i.byteValue() < ids[j].byteValue();
			}

			if (i instanceof Short) {
				a = i.shortValue() < ids[j].shortValue();
			}

			if (a) {// 小于则返回位置
				return k;
			} else {
				k++;// 大于或者等于则继续向后比较，并且位置+1
			}

		}
		return k;
	}

	/*
	 * public int interval_seq_no(Number i, Number[] ids) throws Exception { //
	 * Number i = ids[0];
	 * 
	 * if (ids.length == 0) { return 0; } int k = 0;// 位置 for (int j = 0; j <
	 * ids.length; j++) {
	 * 
	 * boolean a = false; if (i instanceof Integer) { a = i.intValue() <
	 * ids[j].intValue(); } if (i instanceof Double) { a = i.doubleValue() <
	 * ids[j].doubleValue(); }
	 * 
	 * if (i instanceof Float) { a = i.floatValue() < ids[j].floatValue(); }
	 * 
	 * if (i instanceof Long) { a = i.longValue() < ids[j].longValue(); }
	 * 
	 * if (i instanceof Byte) { a = i.byteValue() < ids[j].byteValue(); }
	 * 
	 * if (i instanceof Short) { a = i.shortValue() < ids[j].shortValue(); }
	 * 
	 * if (a) {// 小于则返回位置 return k; } else { k++;// 大于或者等于则继续向后比较，并且位置+1 }
	 * 
	 * } return k; }
	 */

}
