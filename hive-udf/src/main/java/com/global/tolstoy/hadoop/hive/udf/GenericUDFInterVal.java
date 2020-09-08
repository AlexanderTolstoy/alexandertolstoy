/**   
 * @project hive-udf
 * @Title: GenericUDFInterVal.java 
 * @Package com.global.tolstoy.hadoop.hive.udf 
 * @Description: TODO GenericUDFInterVal
 * 
 * @author 	qupengfei@zhixuezhen.com  
 * @since 	2020-05-07
 * @version V1.0   
 */
package com.global.tolstoy.hadoop.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils.PrimitiveGrouping;
import org.apache.hadoop.io.IntWritable;


/**
 * 
 * Copyright (c) 2020 by Alexander Tolstoy
 * @ClassName:     GenericUDFInterVal
 * @Description:   TODO GenericUDFInterVal
 * 
 * @author:        tolstoy
 * @version:       V1.0  
 * @since:      2020-05-07 8:44:28 PM 
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2020-05-07     tolstoy           v1.0.0               初始化
 */
@Description(
			name		=	"interval_no", 
			value		=	"_FUNC_(N,N1,N2,N3,...)" +
							"Returns 0 if N < N1, 1 if N < N2 and so on or -1 if N is NULL. All arguments are treated as integers. " +
							"It is required that N1 < N2 < N3 < ... < Nn for this function to work correctly. ", 
			extended 	= 	"Example:\n" +
					   		"  > SELECT INTERVAL(23, 1, 15, 17, 30, 44, 200);\n" +	
							"  		-> 3" +
					   		"  > SELECT INTERVAL(10, 1, 10, 100, 1000);\n"+
							"  		-> 2"
					   		)
public class GenericUDFInterVal extends GenericUDF {

	private transient ObjectInspector[] argumentOIs;
	
	/**
	 * 
	 * @Title: isIntegerType
	 * @Description: TODO 验证输入参数是否为数值型
	 * @param @param ObjectInspector oi
	 * @return boolean    返回类型
	 * @throws
	 * Modification History:
	 * Date         Author          Version            Description
	 *---------------------------------------------------------*
	 *  2020-05-07     tolstoy           v1.0.0               初创
	 */
	protected boolean isNumericType(ObjectInspector oi) {
		if (oi.getCategory() == Category.PRIMITIVE) {
			if (PrimitiveGrouping.NUMERIC_GROUP == PrimitiveObjectInspectorUtils
					.getPrimitiveGrouping(((PrimitiveObjectInspector) oi).getPrimitiveCategory())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (非 Javadoc) <p>Title: initialize</p> <p>Description: </p>
	 * 
	 * @param ObjectInspector[] arguments
	 * 
	 * @return ObjectInspector argumentOIs
	 * 
	 * @throws UDFArgumentException
	 * 
	 * @see
	 * 
	 */
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
		if (arguments.length < 2) {
			throw new UDFArgumentLengthException(
					"The function INTERVAL(N,N1,N2,N3,...) " + "needs at least two arguments.");
		}

		// check if argument is a string or an array of strings
		for (int i = 0; i < arguments.length; i++) {
			switch (arguments[i].getCategory()) {
			case PRIMITIVE:
				if (isNumericType(arguments[i])) {
					arguments[i]=arguments[i];
					break;
				}
			default:
				throw new UDFArgumentTypeException(i,
						"Argument " + (i + 1) + " of function INTERVAL() must be \""
								+ "Numeric Type ,\n" + "but \""
								+ arguments[i].getTypeName() + "\" was found.");
			}
		}

		argumentOIs = arguments;
		return PrimitiveObjectInspectorFactory.writableIntObjectInspector;
	}

	/*
	 * (非 Javadoc) <p>Title: getDisplayString</p> <p>Description: </p>
	 * 
	 * @param String[] children
	 * 
	 * @return
	 * 
	 * @see
	 * org.apache.hadoop.hive.ql.udf.generic.GenericUDF#getDisplayString(java.lang.
	 * String[])
	 */
	@Override
	public String getDisplayString(String[] children) {
		assert (children.length >= 2);
		return getStandardDisplayString("intervale()", children);
	}


	private final IntWritable r = new IntWritable();
	/*
	 * (非 Javadoc) <p>Title: evaluate</p> <p>Description: </p>
	 * 
	 * @param DeferredObject[] arguments
	 * 
	 * @return Integer interval_SeqNo
	 * 
	 * @throws HiveException
	 * 
	 * @see
	 * org.apache.hadoop.hive.ql.udf.generic.GenericUDF#evaluate(org.apache.hadoop.
	 * hive.ql.udf.generic.GenericUDF.DeferredObject[])
	 */
	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {

		if (arguments[0].get() == null) {
			return null;
		}
		double N = PrimitiveObjectInspectorUtils.getDouble(arguments[0].get(), (PrimitiveObjectInspector) argumentOIs[0]);

		int interval_no = 0;

		for (int i = 1; i < arguments.length; i++) {

			if (N < PrimitiveObjectInspectorUtils.getDouble(arguments[i].get(),
					(PrimitiveObjectInspector) argumentOIs[i])) {
				break;
			} else {
				interval_no++;
			}
		}
		r.set(interval_no);
		return  r ;
	}

}
