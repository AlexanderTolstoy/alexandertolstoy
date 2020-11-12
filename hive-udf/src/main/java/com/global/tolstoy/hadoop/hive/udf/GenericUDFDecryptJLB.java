/**
 * 
 */
package com.global.tolstoy.hadoop.hive.udf;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDFUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorConverter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorConverter.StringConverter;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.BaseCharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

/**
 * Copyright (c) 2020 by Alexander Tolstoy
 * @ClassName:     GenericUDFDecryptJLB
 * @Description:   TODO(用一句话描述该文件做什么) 
 * 
 * @author:        tolstoy
 * @version:       V1.0  
 * @since:      2020-11-10 9:10:25 PM
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2020-11-10     tolstoy           v1.0.0               修改原因
 */
@Description(
		name		=	"decrypt_jlb", 
		value		=	"_FUNC_(ciphertext)" +
						"Return plaintext By AES/CBC/PKCS5Padding ,\n " +
						"Only ciphertext is necessary , The key and IV's default value is set by JLB", 
		extended 	= 	"Example:\n" +
				   		"  > SELECT decrypt_jlb('yA6wrcMq7retydx/rkqF1w==');\n"+
						"  		-> HelloWorld"
				   		)
public class GenericUDFDecryptJLB extends GenericUDF {
	
	  private transient PrimitiveObjectInspector argumentOI;
	  private transient StringConverter stringConverter;
	  private transient PrimitiveCategory returnType = PrimitiveCategory.STRING;
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
	    if (arguments.length != 1) {
	        throw new UDFArgumentLengthException(
	            "decrypt_jlb requires 1 argument, got " + arguments.length);
	      }

	      if (arguments[0].getCategory() != Category.PRIMITIVE) {
	        throw new UDFArgumentException(
	            "decrypt_jlb only takes primitive types, got " + argumentOI.getTypeName());
	      }
	      argumentOI = (PrimitiveObjectInspector) arguments[0];

	      stringConverter = new PrimitiveObjectInspectorConverter.StringConverter(argumentOI);
	      PrimitiveCategory inputType = argumentOI.getPrimitiveCategory();
	      ObjectInspector outputOI = null;
	      BaseCharTypeInfo typeInfo;
	      switch (inputType) {
	        case VARCHAR:
	          // return type should have same length as the input.
	          returnType = inputType;
	          typeInfo = TypeInfoFactory.getVarcharTypeInfo(
	              GenericUDFUtils.StringHelper.getFixedStringSizeForType(argumentOI));
	          outputOI = PrimitiveObjectInspectorFactory.getPrimitiveWritableObjectInspector(typeInfo);
	          break;
	        default:
	          returnType = PrimitiveCategory.STRING;
	          outputOI = PrimitiveObjectInspectorFactory.writableStringObjectInspector;
	          break;
	      }
	      returnHelper = new GenericUDFUtils.StringHelper(returnType);
	      return outputOI;
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
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
	    String CipherText = null;
	    String PlainText="";
	    String ENCRYPTION_KEY="b14e25ae02c8e279";
	    String ENCRYPTION_IV="4e5Wa71fYoT7MFEX";
	    if (arguments[0] != null) {
	    	CipherText = (String) stringConverter.convert(arguments[0].get());
	    }
	    if (CipherText == null) {
	      return null;
	    }
	    
        try {
            Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.US_ASCII), "AES"), new IvParameterSpec(ENCRYPTION_IV.getBytes(StandardCharsets.UTF_8)));
            PlainText =  new String(cipher.doFinal(Base64.getDecoder().decode(CipherText)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	    return returnHelper.setReturnValue(PlainText);
	}

	/* (非 Javadoc)
	 * <p>Title: getDisplayString</p>
	 * <p>Description: </p>
	 * @param arg0
	 * @return
	 * @see org.apache.hadoop.hive.ql.udf.generic.GenericUDF#getDisplayString(java.lang.String[])
	 */
	@Override
	public String getDisplayString(String[] children) {
		// TODO Auto-generated method stub
	    return getStandardDisplayString("decrypt_jlb", children);
	}

}
