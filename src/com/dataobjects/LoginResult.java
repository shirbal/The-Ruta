package com.dataobjects;

import com.enums.InputParametersEnum;
import com.enums.LoginResultEnum;

public class LoginResult {

	public LoginResult()
	{
		Message = "";
		Result = LoginResultEnum.Success;
		InputResult = InputParametersEnum.InputValid;
	}
	
	public String Message;
	
	public InputParametersEnum InputResult;
	
	public LoginResultEnum Result;
}
