package ServiceLayer;
// Purpose: Response class for handling the response of the service layer.
public class Response {
    
    // Fields.
    public string ErrorMessage;
    public object ReturnValue;

    // consructor.
    public Response() { }

    public Response(string msg, object returnValue)
    {
        ErrorMessage = msg;
        this.ReturnValue = returnValue;
    }

    // Getter and Setter

    public string getErrorMessage()
    {
        return ErrorMessage;
    }

    public void setErrorMessage(string errorMessage)
    {
        ErrorMessage = errorMessage;
    }

    public object getReturnValue()
    {
        return ReturnValue;
    }

    public void setReturnValue(object returnValue)
    {
        this.ReturnValue = returnValue;
    }
}
