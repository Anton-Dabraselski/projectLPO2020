package projectLPO.visitors.evaluation;

public class EvaluatorException extends RuntimeException {

	public EvaluatorException() {
	}

	public EvaluatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EvaluatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public EvaluatorException(String message) {
		super(message);
	}

	public EvaluatorException(Throwable cause) {
		super(cause);
	}
}
