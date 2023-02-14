package xyz.mlserver.spigotupdatecore.comparator;

public abstract class VersionComparator {

	/**
	 * Compares versions by checking if the version strings are equal
	 */
	public static final VersionComparator EQUAL = new VersionComparator() {
		@Override
		public boolean isNewer(String currentVersion, String checkVersion) {
			return !currentVersion.equals(checkVersion);
		}
	};

	/**
	 * Compares versions by their Sematic Version (<code>Major.Minor.Patch</code>, <a href="http://semver.org/">semver.org</a>). Removes dots and compares the resulting Integer values
	 */
	public static final VersionComparator SEM_VER = new VersionComparator() {
		@Override
		public boolean isNewer(String currentVersion, String checkVersion) {
			currentVersion = currentVersion.replace(".", "");
			checkVersion = checkVersion.replace(".", "");

			try {
				int current = Integer.parseInt(currentVersion);
				int check = Integer.parseInt(checkVersion);

				return check > current;
			} catch (NumberFormatException e) {
				System.err.println("[spigotUpdate] Invalid SemVer versions specified [" + currentVersion + "] [" + checkVersion + "]");
			}
			return false;
		}
	};

	/**
	 * Called to check if a version is newer
	 *
	 * @param currentVersion Current version of the plugin
	 * @param checkVersion   Version to check
	 * @return <code>true</code> if the checked version is newer
	 */
	public abstract boolean isNewer(String currentVersion, String checkVersion);

}
