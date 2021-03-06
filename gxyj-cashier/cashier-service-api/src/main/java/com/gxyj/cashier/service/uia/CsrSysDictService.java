/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.uia;

import java.util.List;

import com.gxyj.cashier.domain.CsrSysDict;

/**
 * 
 * 添加注释说明
 * @author chensj
 */
public interface CsrSysDictService {

	 List<CsrSysDict> findAllByDataName(String dataName);
	    
	 CsrSysDict findByDataNameAndDataCode(CsrSysDict record);
	    
	 List<CsrSysDict> findAllInDataName(List<String> dataNames);
}
