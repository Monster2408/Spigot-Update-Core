package xyz.mlserver.spigotupdatecore.download;

public interface DownloadCallback {

	void finished();

	void error(Exception exception);

}
