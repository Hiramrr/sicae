package mx.uv.sicae.auth.model;

//Formato generico para las respuestas 

public class RespuestaApi<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;

    public RespuestaApi() {
    }

    public RespuestaApi(boolean success, String message, T data, String error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    // Método estático para respuestas exitosas
    public static <T> RespuestaApi<T> ok(String message, T data) {
        return new RespuestaApi<>(true, message, data, null);
    }

    // Método estático para errores
    public static <T> RespuestaApi<T> fail(String message, String error) {
        return new RespuestaApi<>(false, message, null, error);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}