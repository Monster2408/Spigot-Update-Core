package xyz.mlserver.spigotupdatecore;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.mlserver.spigotupdatecore.comparator.VersionComparator;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SpigotUpdateAbstract {

	public static final String RESOURCE_INFO    = "http://api.spigot.org/v2/resources/%s?ut=%s";
	public static final String RESOURCE_VERSION = "http://api.spigot.org/v2/resources/%s/versions/latest?ut=%s";

	protected final int    resourceId;
	protected final String currentVersion;
	protected final Logger log;
	protected String            userAgent         = "spigotResourceUpdater";
	protected VersionComparator versionComparator = VersionComparator.EQUAL;

	protected ResourceInfo latestResourceInfo;

	public SpigotUpdateAbstract(int resourceId, String currentVersion, Logger log) {
		this.resourceId = resourceId;
		this.currentVersion = currentVersion;
		this.log = log;
	}

	public SpigotUpdateAbstract setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public SpigotUpdateAbstract setVersionComparator(VersionComparator comparator) {
		this.versionComparator = comparator;
		return this;
	}

	public ResourceInfo getLatestResourceInfo() {
		return latestResourceInfo;
	}

	protected abstract void dispatch(Runnable runnable);

	public boolean isVersionNewer(String oldVersion, String newVersion) {
		return versionComparator.isNewer(oldVersion, newVersion);
	}

	public void checkForUpdate(final UpdateCallback callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				try {
					HttpURLConnection connection = (HttpURLConnection) new URL(String.format(RESOURCE_INFO, resourceId, System.currentTimeMillis())).openConnection();
					connection.setRequestProperty("User-Agent", getUserAgent());
					JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
					latestResourceInfo = new Gson().fromJson(jsonObject, ResourceInfo.class);

					connection = (HttpURLConnection) new URL(String.format(RESOURCE_VERSION, resourceId, System.currentTimeMillis())).openConnection();
					connection.setRequestProperty("User-Agent", getUserAgent());
					jsonObject = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
					latestResourceInfo.latestVersion = new Gson().fromJson(jsonObject, ResourceVersion.class);

					if (isVersionNewer(currentVersion, latestResourceInfo.latestVersion.name)) {
						callback.updateAvailable(latestResourceInfo.latestVersion.name, "https://spigotmc.org/" + latestResourceInfo.file.url, !latestResourceInfo.external);
					} else {
						callback.upToDate();
					}
				} catch (Exception e) {
					log.log(Level.WARNING, "Failed to get resource info from spigot.org", e);
				}
			}
		});
	}

}
