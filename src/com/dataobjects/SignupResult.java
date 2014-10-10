package com.dataobjects;

import com.enums.InputParametersEnum;
import com.enums.SignupResultEnum;

public class SignupResult {

	public SignupResult()
	{
		Message = "";
		Result = SignupResultEnum.Success;
		InputResult = InputParametersEnum.InputValid;
	}
	
	public String Message;
	
	public InputParametersEnum InputResult;
	
	public SignupResultEnum Result;
}
