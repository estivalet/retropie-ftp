package emulationstation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class EmulationstationTools {
	/**
	 * Create a gamelist.xml file for selected games only. The gamelist.xml full scrapped should be placed in the database folder of GLOG platform structure e.g f:/emudreams/platforms/Atari 2600/games/database
	 * 
	 * @param databaseFolder
	 * @param files
	 * @param fsSubFolder
	 * @param subFolder
	 * @throws Exception
	 */
	public static void createESGameList(String databaseFolder, List<String> files, String subFolder, List<String> fsSubFolder) {
		try {
			// Read gamelist.xml with all games for the system.
			JAXBContext jaxbContext = JAXBContext.newInstance(GameList.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			GameList gameList = (GameList) jaxbUnmarshaller.unmarshal(new File(databaseFolder + "gamelist.xml"));

			// Create a new list with only selected games.
			GameList newGameList = new GameList();
			for (GameListGame game : gameList.getGames()) {
				for (String file : files) {
					// System.out.println("--->" + file + " = " + game.getPath().replace("./", ""));
					if (file.equals(game.getPath().replace("./", ""))) {
						newGameList.addGame(game);
						break;
					}
				}
				if (subFolder != null) {
					for (String file : fsSubFolder) {
						if (file.equals(game.getPath().replace("./", ""))) {
							System.out.println(game.getPath());
							game.setPath("./" + subFolder + "/" + game.getPath().substring(2));
							newGameList.addGame(game);
							break;
						}
					}
				}
			}

			System.out.println("NEW LIST SIZE------------------>" + newGameList.getGames().size());

			// Save list with selected games in C:/temp.
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(newGameList, new File("c:/temp/gamelist.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createESCollection(String destFile, List<String> files) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));
		for (String g : files) {
			writer.write(g + "\n");
		}
		writer.close();

	}
}
