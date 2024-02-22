package application;

public interface DataModuleListener {
	
	void onSuccess(String msg);
	
	void onFaild(String msg);

	void onError(String msg);
}
