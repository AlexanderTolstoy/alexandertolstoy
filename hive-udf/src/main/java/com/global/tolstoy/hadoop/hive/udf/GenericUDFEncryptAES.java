/**
 * 
 */
package com.global.tolstoy.hadoop.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

/**
 * Copyright (c) 2020 by Alexander Tolstoy
 * @ClassName:     GenericUDFEncryptAES
 * @Description:   TODO(用一句话描述该文件做什么) 
 * 
 * @author:        tolstoy
 * @version:       V1.0  
 * @since:      2020-11-10 11:39:42 AM
 * Modification History:
 * Date         Author          Version            Description
 *---------------------------------------------------------*
 * 2020-11-10     tolstoy           v1.0.0               修改原因
 */
public class GenericUDFEncryptAES extends GenericUDF {

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

}
