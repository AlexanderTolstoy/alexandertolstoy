/**
 * 
 */
package com.global.tolstoy.hadoop.hive.udf;

import java.nio.charset.StandardCharsets;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorConverter.StringConverter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.io.BytesWritable;

/**
 * Copyright (c) 2020 by Alexander Tolstoy
 * @ClassName:     GenericUDFDecryptAES
 * @Description:   TODO(用一句话描述该文件做什么) 
 * 
 * @author:        tolstoy
 * @version:       V1.0  
 * @since:      2020-11-10 11:43:37 AM
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2020-11-10     tolstoy           v1.0.0               修改原因
 */

@Description(
		name		=	"decrypt_aes", 
		value		=	"_FUNC_(ciphertext,key,IV)" +
						"Return plaintext By AES/CBC/PKCS5Padding ,\n " +
						"Only ciphertext is necessary if encrypted without input parameters include key and IV by default value ", 
		extended 	= 	"Example:\n" +
				   		"  > SELECT decrypt_aes('yA6wrcMq7retydx/rkqF1w==','FiveQu','AlexanderTolstoy');\n" +	
						"  		-> HelloWorld" +
				   		"  > SELECT decrypt_aes('yA6wrcMq7retydx/rkqF1w==');\n"+
						"  		-> HelloWorld"
				   		)
public class GenericUDFDecryptAES extends GenericUDF {
	
	
	
    private String KEY="b14e25ae02c8e279";
    private String IV="4e5Wa71fYoT7MFEX";
    private	String CipherText="";
    private	String PlainText="";
   
    
    
    private transient ObjectInspector[] argumentOIs;
    private transient StringConverter[] stringConverters;
    private transient PrimitiveCategory returnType = PrimitiveCategory.STRING;
    private transient BytesWritable[] bw;
    private transient GenericUDFUtils.StringHelper returnHelper;

	/* (非 Javadoc)
	 * <p>Title: initialize</p>
	 * <p>Description: </p>
	 * @param arg0
	 * @return
	 * @throws UDFArgumentException
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDF#initialize(org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector[])
	 */
	@Override
	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
		
		argumentOIs = arguments;
		
		if (arguments.length >3 || arguments == null  ) {
			throw new UDFArgumentLengthException(
					"The function decrypt_aes(ciphertext,key,IV) " + "needs at least one argument  and most have three arguments.");
		}
		
	    for (int idx = 0; idx < arguments.length; ++idx) {
	      if (arguments[idx].getCategory() != Category.PRIMITIVE) {
	        throw new UDFArgumentException("decrypt_aes only takes primitive arguments");
	      }
	      
	     
	      switch (((PrimitiveObjectInspector) arguments[idx]).getPrimitiveCategory()) {
	        case CHAR:
	        case VARCHAR:
	            if (idx == 2) {
	            	break;
	              }
	          break;
	        default:
				throw new UDFArgumentTypeException(
						idx, "Argument " + " of function INTERVAL() must be \""
								+ "Numeric Type ,\n" + "but \""
								 + "\" was found.");						
	      }
	      
	    }
	   return PrimitiveObjectInspectorFactory.writableStringObjectInspector;
	}

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

}
