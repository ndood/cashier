/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.exception;

/**
 * 对账异常类
 * 
 * @author Danny
 */
public class ReconciliationException extends CashierServiceException {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2633111365620906027L;

	public ReconciliationException(String errorCode, String errorMsg, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(errorCode, errorMsg, cause, enableSuppression, writableStackTrace);
	}

	public ReconciliationException(String errorCode, String errorMsg, Throwable cause) {
		super(errorCode, errorMsg, cause);
	}

	public ReconciliationException(String errorCode, String errorMsg) {
		super(errorCode, errorMsg);
	}

	



}
