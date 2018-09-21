package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/**
 * @author luisoft
 *
 */
public class SecureFTP {
	private String host;
	private int port;
	private String username;
	private String password;
	static ChannelSftp channelSftp = null;
	static Session session = null;
	static Channel channel = null;
	static String PATHSEPARATOR = "/";

	/**
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public SecureFTP(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	/**
	 * @param sourceFolder
	 * @param destFolder
	 * @param overwrite
	 */
	public void sendFolder(String sourceFolder, String destFolder, boolean overwrite) {
		try {
			channelSftp.cd(destFolder); // Change Directory on SFTP Server
			recursiveFolderUpload(sourceFolder, destFolder, overwrite);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * This method is called recursively to Upload the local folder content to SFTP server
	 * 
	 * @param sourcePath
	 * @param destinationPath
	 * @throws SftpException
	 * @throws FileNotFoundException
	 */
	public void recursiveFolderUpload(String sourcePath, String destinationPath, boolean overwrite) throws SftpException, FileNotFoundException {
		File sourceFile = new File(sourcePath);
		if (sourceFile.isFile()) {
			// copy if it is a file
			channelSftp.cd(destinationPath);
			if (!sourceFile.getName().startsWith(".")) {
				if (overwrite) {
					channelSftp.put(new FileInputStream(sourceFile), sourceFile.getName(), ChannelSftp.OVERWRITE);
				} else {
					try {
						channelSftp.lstat(sourceFile.getName());
					} catch (SftpException e) {
						if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
							channelSftp.put(new FileInputStream(sourceFile), sourceFile.getName(), ChannelSftp.OVERWRITE);
						} else {
							throw e;
						}
					}
				}
			}
		} else {
			File[] files = sourceFile.listFiles();
			if (files != null && !sourceFile.getName().startsWith(".")) {
				channelSftp.cd(destinationPath);
				SftpATTRS attrs = null;

				// check if the directory is already existing
				try {
					attrs = channelSftp.stat(destinationPath + "/" + sourceFile.getName());
				} catch (Exception e) {
					System.out.println(destinationPath + "/" + sourceFile.getName() + " not found");
				}

				// else create a directory
				if (attrs != null) {
				} else {
					channelSftp.mkdir(sourceFile.getName());
				}
				for (File f : files) {
					recursiveFolderUpload(f.getAbsolutePath(), destinationPath + "/" + sourceFile.getName(), overwrite);
				}
			}
		}
	}

	/**
	 * @throws JSchException
	 */
	public void beginSession() throws JSchException {
		JSch jsch = new JSch();
		session = jsch.getSession(username, host, port);
		session.setPassword(password);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect(); // Create SFTP Session
		channel = session.openChannel("sftp"); // Open SFTP Channel
		channel.connect();
		channelSftp = (ChannelSftp) channel;
	}

	/**
	 * 
	 */
	public void endSession() {
		if (channelSftp != null) {
			if (channelSftp.isConnected()) {
				channelSftp.disconnect();
			}
		}
		if (channel != null) {
			if (channel.isConnected()) {
				channel.disconnect();
			}
		}
		if (session != null) {
			if (session.isConnected()) {
				session.disconnect();
			}
		}
	}

	public void changeFolder(String folder) throws SftpException {
		channelSftp.cd(folder);
	}

	/**
	 * @param destinationPath
	 * @param folderName
	 * @throws SftpException
	 */
	public void createFolder(String destinationPath, String folderName) throws SftpException {
		try {
			channelSftp.cd(destinationPath);
		} catch (Exception e) {
			channelSftp.mkdir(destinationPath);
		}
		try {
			channelSftp.stat(folderName);
		} catch (Exception e) {
			channelSftp.mkdir(folderName);
		}
	}

	public void createFolder(String path) throws SftpException {
		String[] folders = path.split("/");
		// System.out.println(folders.length);
		for (String folder : folders) {
			String currentDirectory = channelSftp.pwd();
			if (folder.length() > 0) {
				// System.out.println(folder);
				SftpATTRS attrs = null;
				try {
					attrs = channelSftp.stat(currentDirectory + "/" + folder);
				} catch (Exception e) {
					System.out.println(folder + " not found");
				}

				if (attrs != null) {
					// System.out.println("Directory exists IsDir=" + attrs.isDir());
					channelSftp.cd(folder);
				} else {
					System.out.println("Creating dir " + folder);
					try {
						channelSftp.mkdir(folder);
						channelSftp.cd(folder);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @param fileName
	 * @return
	 */
	public boolean isFileAvailable(String fileName) {
		try {
			channelSftp.stat(fileName);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * @param file
	 * @param destFile
	 * @throws SftpException
	 */
	public void getFile(String file, String destFile) throws SftpException {
		channelSftp.get(file, destFile);
	}

	/**
	 * @param folder
	 * @return
	 * @throws SftpException
	 */
	public List<String> getFiles(String folder) throws SftpException {
		Vector filelist = channelSftp.ls(folder);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < filelist.size(); i++) {
			LsEntry entry = (LsEntry) filelist.get(i);
			list.add(entry.getFilename());
			// System.out.println(entry.getFilename());
		}

		return list;
	}
}
