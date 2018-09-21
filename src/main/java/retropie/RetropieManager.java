package retropie;

import static spark.Spark.put;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import emulationstation.EmulationstationTools;
import spark.Request;
import spark.Response;
import spark.Route;
import util.IOUtil;
import util.SecureFTP;

public class RetropieManager {

	class RetroPieMappingWrapper {
		String name;
		String folder;
		String subfolder;
		String type;
		String collection;
		String category;
	}

	private static String getSystemFolder(RetroPieMappingWrapper[] data, String system) {
		for (int i = 0; i < data.length; i++) {
			if (data[i].name.toLowerCase().equals(system.toLowerCase())) {
				return data[i].folder;
			}
		}
		return null;
	}

	private static String getSystemSubFolder(RetroPieMappingWrapper[] data, String system) {
		for (int i = 0; i < data.length; i++) {
			if (data[i].name.toLowerCase().equals(system.toLowerCase())) {
				return data[i].subfolder;
			}
		}
		return null;
	}

	private static String getSystemType(RetroPieMappingWrapper[] data, String system) {
		for (int i = 0; i < data.length; i++) {
			if (data[i].name.equals(system)) {
				return data[i].type;
			}
		}
		return null;
	}

	private static String getCollectionFileName(RetroPieMappingWrapper[] data, String system) {
		for (int i = 0; i < data.length; i++) {
			if (data[i].name.equals(system)) {
				return data[i].collection;
			}
		}
		return null;
	}

	private static String getSystemCategory(RetroPieMappingWrapper[] data, String system) {
		for (int i = 0; i < data.length; i++) {
			if (data[i].name.equals(system)) {
				return data[i].category;
			}
		}
		return null;
	}

	/**
	 * Some conventions need to be set.
	 * 
	 * 1. All media folder names are predefined as: "boxart", "back", "cartart", "flyer", "marquee", "snap", "wheel", "shot"
	 * 
	 * 2. The media folders stay in the roms folder like /home/pi/RetroPie/roms/atari2600/boxart
	 * 
	 * 3. All image media will have extension ".png"
	 * 
	 * 4. All video media will have extension ".mp4"
	 * 
	 * 5. Full emulationstation gamelist.xml file can be found in the "database" folder
	 */
	public static Route ftp = (Request request, Response response) -> {
		// Get parameters.
		String host = request.queryParams("host");
		Integer port = Integer.parseInt(request.queryParams("port"));
		String username = request.queryParams("username");
		String password = request.queryParams("password");
		String jsonFiles = request.queryParams("files");
		String jsonSystems = request.queryParams("systems");
		String systemName = request.queryParams("systemName");
		String retropieMap = request.queryParams("retropieMap");
		String wheel = request.queryParams("wheel");
		String box = request.queryParams("box");
		String cart = request.queryParams("cart");
		String shot = request.queryParams("shot");
		String video = request.queryParams("video");

		System.out.println(jsonFiles);
		// Convert JSON to java List array.
		Type listType = new TypeToken<List<String>>() {
		}.getType();
		List<String> files = new Gson().fromJson(jsonFiles, listType);
		List<String> systems = new Gson().fromJson(jsonSystems, listType);
		Map<String, String> sourceSystem = new HashMap<String, String>();
		boolean overwrite = "on".equals(request.queryParams("overwrite"));

		System.out.println(retropieMap);

		RetroPieMappingWrapper[] data = new Gson().fromJson(retropieMap, RetroPieMappingWrapper[].class);
		System.out.println(data[0].folder);

		SecureFTP retropie = new SecureFTP(host, port, username, password);
		retropie.beginSession();

		// game folder in source file system.
		String folder = "";
		Set<String> updateSystems = new HashSet<String>();
		List<String> collectionFiles = new ArrayList<String>();
		// send files to retropie.
		System.out.println("Total files to send " + files.size());
		for (int i = 0; i < files.size(); i++) {
			String filename = files.get(i).substring(files.get(i).lastIndexOf("/") + 1, files.get(i).lastIndexOf("."));
			folder = files.get(i).substring(0, files.get(i).lastIndexOf("/") + 1);

			String destFolder = getSystemFolder(data, systems.get(i));
			String subFolder = null;
			if (destFolder == null) {
				System.out.println("WARNING! No mapping found for " + systems.get(i));
				continue;
			} else {
				subFolder = getSystemSubFolder(data, systems.get(i));
			}
			// Send rom only if there is a mapping available.
			retropie.changeFolder("/home/" + username);
			retropie.createFolder("/RetroPie/roms/" + destFolder);
			if (subFolder != null) {
				retropie.changeFolder("/home/" + username);
				retropie.createFolder("/RetroPie/roms/" + destFolder + "/" + subFolder);
			}

			sourceSystem.put(systems.get(i), folder);
			destFolder = "/home/" + username + "/RetroPie/roms/" + destFolder + "/";
			String origDestFolder = destFolder;
			if (subFolder != null) {
				destFolder += subFolder + "/";
			}
			System.out.println("sending " + files.get(i) + " to " + destFolder);
			collectionFiles.add(destFolder + files.get(i).substring(files.get(i).lastIndexOf("/") + 1));
			updateSystems.add(systems.get(i));

			retropie.recursiveFolderUpload(files.get(i), destFolder, overwrite);

			// If there is a subfolder must take the media from main system folder
			if (subFolder != null) {
				folder = folder.substring(0, folder.length() - 1);
				folder = folder.substring(0, folder.lastIndexOf("/"));
				folder += "/games/";
				sourceSystem.put(systems.get(i), folder);
				destFolder = origDestFolder;
			}
			// Send wheel if selected.
			retropie.createFolder(destFolder, "media");
			if ("on".equals(wheel)) {
				retropie.createFolder(destFolder + "/media", "logo");
				retropie.recursiveFolderUpload(folder + "media/logo/" + filename + ".png", destFolder + "/media/logo", overwrite);
			}

			if ("on".equals(shot)) {
				retropie.createFolder(destFolder + "/media", "shot");
				if (new File(folder + "media/mix3").exists()) {
					retropie.recursiveFolderUpload(folder + "media/mix3/" + filename + ".png", destFolder + "/media/shot", overwrite);
				} else {
					retropie.recursiveFolderUpload(folder + "media/ingame/" + filename + ".png", destFolder + "/media/shot", overwrite);
				}
			}

			// Send video. In my system videos are in "video" folder but in retropie the default is "snap" folder.
			if ("on".equals(video)) {
				retropie.createFolder(destFolder + "/media", "video");
				retropie.recursiveFolderUpload(folder + "media/video/" + filename + ".mp4", destFolder + "/media/video", overwrite);
			}
		}

		for (String s : updateSystems) {
			String destFolder = "/home/" + username + "/RetroPie/roms/" + getSystemFolder(data, s) + "/";
			String subFolder = getSystemSubFolder(data, s);
			System.out.println("Get files RPi for " + s + " in " + destFolder);
			List<String> fs = retropie.getFiles(destFolder);
			List<String> fsSubFolder = null;
			if (subFolder != null) {
				fsSubFolder = retropie.getFiles(destFolder + subFolder);
			}
			System.out.println("Filter gamelist.xml from " + sourceSystem.get(s) + " " + fs.size());
			EmulationstationTools.createESGameList(sourceSystem.get(s) + "database/", fs, subFolder, fsSubFolder);
			System.out.println("Send gamelist to RPi");
			retropie.recursiveFolderUpload("C:/temp/gamelist.xml", destFolder, true);
		}

		// Only if it is a collection.
		if ("collection".equals(getSystemType(data, systemName))) {
			System.out.println("CREATE AND SEND ES COLLECTION!");
			EmulationstationTools.createESCollection("c:/temp/" + getCollectionFileName(data, systemName), collectionFiles);
			retropie.recursiveFolderUpload("C:/temp/" + getCollectionFileName(data, systemName), "/home/" + username + "/.emulationstation/collections", true);
		}

		// AttractMode stuff...
		String categoryRomList = "/opt/retropie/configs/all/attractmode/romlists/" + getSystemCategory(data, systemName) + ".txt";
		if (retropie.isFileAvailable(categoryRomList)) {
			retropie.getFile(categoryRomList, "c:/temp/" + getSystemCategory(data, systemName) + ".txt");
			String contents = IOUtil.getContents(new File("c:/temp/" + getSystemCategory(data, systemName) + ".txt"));
			if (contents.indexOf(systemName + ";") == -1) {
				contents += systemName + ";" + systemName + ";@;;;;;;;;;0;;;;;\n";
				FileWriter fw = new FileWriter("c:/temp/" + getSystemCategory(data, systemName) + ".txt");
				fw.write(contents);
				fw.close();
			}
		} else {
			// Category file does not exist... create file
			String system = systemName + ";" + systemName + ";@;;;;;;;;;0;;;;;\n";
			FileWriter fw = new FileWriter("c:/temp/" + getSystemCategory(data, systemName) + ".txt");
			fw.write(system);
			fw.close();
		}
		// send category file
		retropie.recursiveFolderUpload("c:/temp/" + getSystemCategory(data, systemName) + ".txt", "/opt/retropie/configs/all/attractmode/romlists/", true);

		// get attract.cfg file
		retropie.getFile("/opt/retropie/configs/all/attractmode/attract.cfg", "c:/temp/attract.cfg");
		String contents = IOUtil.readFully(new FileInputStream(new File("c:/temp/attract.cfg")));
		new File("c:/temp/attract.cfg").delete();
		// check if there is a display for the category
		// System.out.println(contents.indexOf(getSystemCategory(data, systemName)));
		// System.out.println(contents);
		// System.out.println(contents.indexOf("display"));
		// System.out.println(contents.indexOf("display Atari 7800"));
		// System.out.println("display " + getSystemCategory(data, systemName));
		boolean createDisplayCategory = contents.indexOf("display " + getSystemCategory(data, systemName)) > -1 ? false : true;
		boolean createDisplaySystem = contents.indexOf("display " + systemName) > -1 ? false : true;
		if (createDisplayCategory) {
			// category display.
			String d = "\ndisplay " + getSystemCategory(data, systemName) + "\n";
			d += "    layout               HP2-Sub-Menu\n";
			d += "    romlist              " + getSystemCategory(data, systemName) + "\n";
			d += "    in_cycle             no\n";
			d += "    in_menu              yes\n";
			d += "    filter               All\n";
			d += "    filter               Favourites\n";
			d += "            rule                 Favourite equals 1\n\n";
			contents += d;
		}
		if (createDisplaySystem) {
			// system display.
			String d = "\ndisplay " + systemName + "\n";
			d += "    layout               HP2-Systems-Menu\n";
			d += "    romlist              " + systemName + "\n";
			d += "    in_cycle             no\n";
			d += "    in_menu              no\n";
			d += "    global_filter\n";
			d += "            rule                 FileIsAvailable equals 1\n";
			d += "    filter               All\n";
			d += "    filter               Favourites\n";
			d += "            rule                 Favourite equals 1\n\n";
			contents += d;
		}

		FileWriter fw = new FileWriter("c:/temp/attract.cfg");
		fw.write(contents);
		fw.close();

		retropie.recursiveFolderUpload("c:/temp/attract.cfg", "/opt/retropie/configs/all/attractmode/", true);
		retropie.recursiveFolderUpload("F:/EmuDreams/frontends/attractmode/romlists/" + systemName + ".txt", "/opt/retropie/configs/all/attractmode/romlists/", true);

		// close ftp session with rpi.
		retropie.endSession();

		System.out.println("FINISHED FTP");

		return null;
	};

	public static void main(String[] args) {
		put("/retropie/ftp", RetropieManager.ftp);
	}

}