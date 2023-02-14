package xyz.mlserver.spigotupdatecore;

public interface UpdateCallback {

	/**
	 * Called when a new version was found
	 * <p>
	 * Use {@link SpigotUpdateAbstract#getLatestResourceInfo()} to get all resource details
	 *
	 * @param newVersion      the new version's name
	 * @param downloadUrl     URL to download the update
	 * @param canAutoDownload whether this update can be downloaded automatically
	 */
	void updateAvailable(String newVersion, String downloadUrl, boolean canAutoDownload);

	/**
	 * Called when no update was found
	 */
	void upToDate();

}
