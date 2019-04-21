package org.ctp.coldstorage.utils.exception;

public class ColdStorageOverMaxException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int current, max;
	private String message;

	public ColdStorageOverMaxException(int current, int max) {
		this.setCurrent(current);
		this.setMax(max);
		setMessage("Cannot insert items into this ColdStorage. Current amount is " + getCurrent() + " and max is " + getMax() + ".");
	}

	public int getCurrent() {
		return current;
	}

	private void setCurrent(int current) {
		this.current = current;
	}

	public int getMax() {
		return max;
	}

	private void setMax(int max) {
		this.max = max;
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}
}
