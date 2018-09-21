package emulationstation;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class GameListParser {

	public static void main(String[] args) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(GameList.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		GameList gameList = (GameList) jaxbUnmarshaller.unmarshal(new File("f:/emudreams/platforms/Atari 2600/games/database/gamelist.xml"));
		GameList newGameList = new GameList();
		for (GameListGame game : gameList.getGames()) {
			System.out.println(game.getName());
			if (game.getName().startsWith("Adventure")) {
				newGameList.addGame(game);
			}
		}
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(newGameList, new File("c:/temp/simple.xml"));
	}
}
